package com.atomicleopard.thundr.xml.jaxb;

import static com.atomicleopard.expressive.Expressive.list;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.mockito.Mockito;

import com.atomicleopard.thundr.xml.jaxb.test.XmlPojo;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojoWithNamedRootElement;

public class JaxbBatchWriterTest {

	private Jaxb jaxb = new Jaxb();
	private XmlPojo pojo1 = new XmlPojo();;
	private XmlPojo pojo2 = new XmlPojo();;
	private Iterator<XmlPojo> pojos = Arrays.asList(pojo1, pojo2).iterator();

	@Test
	public void shouldRetainJaxbAndInput() {
		JaxbBatchWriter<XmlPojo> original = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		assertThat(original.jaxb, is(jaxb));
		assertThat(original.output.next(), is(pojo1));
		assertThat(original.output.next(), is(pojo2));
	}

	@Test
	public void shouldRetainEncoding() {
		JaxbBatchWriter<XmlPojo> original = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		JaxbBatchWriter<XmlPojo> writer = original;
		assertThat(writer.encoding, is("UTF-8"));

		writer = writer.encoding("UTF-16");
		assertThat(writer, is(not(sameInstance(original))));
		assertThat(writer.encoding, is("UTF-16"));

		writer = writer.encoding(Charset.defaultCharset());
		assertThat(writer, is(not(sameInstance(original))));
		assertThat(writer.encoding, is(Charset.defaultCharset().name()));
	}

	@Test
	public void shouldRetainFormat() {
		JaxbBatchWriter<XmlPojo> original = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		JaxbBatchWriter<XmlPojo> writer = original;

		assertThat(writer.format, is(false));

		writer = writer.format();
		assertThat(writer, not(sameInstance(original)));
		assertThat(writer.format, is(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldWriteToStringUsingJaxb() {
		jaxb = spy(jaxb);
		JaxbBatchWriter<XmlPojo> original = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		String xml = original.encoding("UTF-16").format().string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-16\"?><Root><xmlPojo></xmlPojo><xmlPojo></xmlPojo></Root>"));
		verify(jaxb).writeMany(Mockito.any(Iterator.class), eq("Root"), eq("UTF-16"), eq(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldWriteToStreamUsingJaxb() throws UnsupportedEncodingException {
		jaxb = spy(jaxb);
		JaxbBatchWriter<XmlPojo> original = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayOutputStream os = original.encoding("UTF-16").format().to(baos);
		assertThat(os, is(baos));
		assertThat(new String(baos.toByteArray(), "UTF-16"), is("<?xml version=\"1.0\" encoding=\"UTF-16\"?><Root><xmlPojo></xmlPojo><xmlPojo></xmlPojo></Root>"));
		verify(jaxb).writeMany(Mockito.any(Iterator.class), eq(baos), eq("Root"), eq("UTF-16"), eq(true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldWriteToWriterUsingJaxb() throws UnsupportedEncodingException {
		jaxb = spy(jaxb);
		JaxbBatchWriter<XmlPojo> original = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		StringWriter writer = new StringWriter();
		StringWriter out = original.encoding("UTF-16").format().to(writer);
		assertThat(out, is(writer));
		assertThat(writer.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-16\"?><Root><xmlPojo></xmlPojo><xmlPojo></xmlPojo></Root>"));
		verify(jaxb).writeMany(Mockito.any(Iterator.class), eq(writer), eq("Root"), eq("UTF-16"), eq(true));
	}

	@Test
	public void shouldWriteXmlWithDefaultConfig() {
		pojos = list(new XmlPojo("id", "name", new BigDecimal("1.23")), new XmlPojo("id2", "name2", new BigDecimal("3.21"))).iterator();
		JaxbBatchWriter<XmlPojo> batchWriter = new JaxbBatchWriter<XmlPojo>(jaxb, pojos);
		String xml = batchWriter.string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Root><xmlPojo><amount>1.23</amount><id>id</id><name>name</name></xmlPojo><xmlPojo><amount>3.21</amount><id>id2</id><name>name2</name></xmlPojo></Root>"));
	}

	@Test
	public void shouldRespectRootElementName() {
		Iterator<XmlPojoWithNamedRootElement> pojos = list(new XmlPojoWithNamedRootElement("id", "name", new BigDecimal("1.23")),
				new XmlPojoWithNamedRootElement("id2", "name2", new BigDecimal("3.21"))).iterator();
		JaxbBatchWriter<XmlPojoWithNamedRootElement> batchWriter = new JaxbBatchWriter<XmlPojoWithNamedRootElement>(jaxb, pojos);

		String xml = batchWriter.string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Root><XmlPojo><amount>1.23</amount><id>id</id><name>name</name></XmlPojo><XmlPojo><amount>3.21</amount><id>id2</id><name>name2</name></XmlPojo></Root>"));
	}
}
