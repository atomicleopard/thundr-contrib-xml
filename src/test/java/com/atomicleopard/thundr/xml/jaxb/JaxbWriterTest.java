package com.atomicleopard.thundr.xml.jaxb;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.junit.Test;

import com.atomicleopard.thundr.xml.jaxb.test.XmlPojo;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojoWithNamedRootElement;

public class JaxbWriterTest {

	private Jaxb jaxb = new Jaxb();
	private XmlPojo pojo = new XmlPojo();

	@Test
	public void shouldRetainJaxbAndInput() {
		JaxbWriter<XmlPojo> original = new JaxbWriter<XmlPojo>(jaxb, pojo);
		assertThat(original.jaxb, is(jaxb));
		assertThat(original.output, is(pojo));
	}

	@Test
	public void shouldRetainEncoding() {
		JaxbWriter<XmlPojo> original = new JaxbWriter<XmlPojo>(jaxb, pojo);
		JaxbWriter<XmlPojo> writer = original;
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
		JaxbWriter<XmlPojo> original = new JaxbWriter<XmlPojo>(jaxb, pojo);
		JaxbWriter<XmlPojo> writer = original;

		assertThat(writer.format, is(false));

		writer = writer.format();
		assertThat(writer, not(sameInstance(original)));
		assertThat(writer.format, is(true));
	}

	@Test
	public void shouldWriteToStringUsingJaxb() {
		jaxb = spy(jaxb);
		JaxbWriter<XmlPojo> original = new JaxbWriter<XmlPojo>(jaxb, pojo);
		String xml = original.encoding("UTF-16").format().string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"yes\"?>\n<xmlPojo/>\n"));
		verify(jaxb).writeOne(pojo, "UTF-16", true);
	}

	@Test
	public void shouldWriteToStreamUsingJaxb() throws UnsupportedEncodingException {
		jaxb = spy(jaxb);
		JaxbWriter<XmlPojo> original = new JaxbWriter<XmlPojo>(jaxb, pojo);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayOutputStream os = original.encoding("UTF-16").format().to(baos);
		assertThat(os, is(baos));
		assertThat(new String(baos.toByteArray(), "UTF-16"), is("<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"yes\"?>\n<xmlPojo/>\n"));
		verify(jaxb).writeOne(pojo, baos, "UTF-16", true);
	}

	@Test
	public void shouldWriteToWriterUsingJaxb() throws UnsupportedEncodingException {
		jaxb = spy(jaxb);
		JaxbWriter<XmlPojo> original = new JaxbWriter<XmlPojo>(jaxb, pojo);
		StringWriter writer = new StringWriter();
		StringWriter out = original.encoding("UTF-16").format().to(writer);
		assertThat(out, is(writer));
		assertThat(writer.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"yes\"?>\n<xmlPojo/>\n"));
		verify(jaxb).writeOne(pojo, writer, "UTF-16", true);
	}

	@Test
	public void shouldWriteWithDefaultConfig() {
		String xml = new JaxbWriter<XmlPojo>(jaxb, new XmlPojo("id", "name", new BigDecimal("1.23"))).string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>id</id><name>name</name></xmlPojo>"));
	}
	
	@Test
	public void shouldRespectRootElementName() {
		String xml = new JaxbWriter<XmlPojoWithNamedRootElement>(jaxb, new XmlPojoWithNamedRootElement("id", "name", new BigDecimal("1.23"))).string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><XmlPojo><amount>1.23</amount><id>id</id><name>name</name></XmlPojo>"));
	}
}
