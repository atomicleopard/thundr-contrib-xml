/*
 * This file is a community contributed library for use with thundr.
 * Read more: http://3wks.github.io/thundr/
 *
 * Copyright (C) 2014 Atomic Leopard, <nick@atomicleopard.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomicleopard.thundr.xml.jaxb;

import static com.atomicleopard.expressive.Expressive.list;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.atomicleopard.thundr.xml.jaxb.test.XmlPojo;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojoWithNamedRootElement;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojoWithNoRoot;

import jodd.io.StringOutputStream;

public class JaxbTest {

	private Jaxb jaxb = new Jaxb();

	@Test
	public void shouldWriteXmlForPojo() {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo>"));
	}

	@Test
	public void shouldWriteXmlForPojoToOutputStream() {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).to(new StringOutputStream()).toString();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo>"));

		xml = new String(jaxb.write(xmlPojo).to(new ByteArrayOutputStream()).toByteArray());
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo>"));
	}

	@Test
	public void shouldWriteXmlForPojoToWriter() {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).to(new StringWriter()).toString();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo>"));
	}

	@Test
	public void shouldWriteFormattedXmlForPojo() {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).format().string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<xmlPojo>\n    <amount>1.23</amount>\n    <id>Id</id>\n    <name>Name</name>\n</xmlPojo>\n"));
	}

	@Test
	public void shouldWriteEncodedXmlForPojo() {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).format().encoding("ISO-8859-1").string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>\n<xmlPojo>\n    <amount>1.23</amount>\n    <id>Id</id>\n    <name>Name</name>\n</xmlPojo>\n"));
	}

	@Test
	public void shouldWriteWithDifferentEncodingXmlForPojo() throws UnsupportedEncodingException {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).encoding("ISO-8859-1").string();
		String expected = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo>";
		assertThat(xml, is(expected));

		byte[] data = jaxb.write(xmlPojo).encoding("ISO-8859-1").to(new ByteArrayOutputStream()).toByteArray();
		assertThat(data, is(expected.getBytes("ISO-8859-1")));
	}

	@Test
	public void shouldWriteXmlForNamedRootPojo() {
		XmlPojoWithNamedRootElement xmlPojo = xmlPojoWithNamedRoot();

		String xml = jaxb.write(list(xmlPojo)).rootElement("root").string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><XmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></XmlPojo></root>"));
	}

	@Test
	public void shouldWriteXmlForNamedRootPojoToWriter() {
		XmlPojoWithNamedRootElement xmlPojo = xmlPojoWithNamedRoot();

		String xml = jaxb.write(list(xmlPojo)).rootElement("root").to(new StringWriter()).toString();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><XmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></XmlPojo></root>"));
	}

	@Test
	public void shouldWriteXmlForNamedRootPojoToOutputStream() {
		XmlPojoWithNamedRootElement xmlPojo = xmlPojoWithNamedRoot();

		String xml = jaxb.write(list(xmlPojo)).rootElement("root").to(new StringOutputStream()).toString();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><XmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></XmlPojo></root>"));
	}

	@Test
	public void shouldWriteXmlForNonRootPojo() {
		XmlPojoWithNoRoot xmlPojo = xmlPojoWithNoRoot();

		String xml = jaxb.write(list(xmlPojo)).rootElement("root").string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><xmlPojoWithNoRoot><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojoWithNoRoot></root>"));
	}
	
	@Test
	public void shouldWriteXmlForNonRootPojoIterator() {
		XmlPojoWithNoRoot xmlPojo = xmlPojoWithNoRoot();
		XmlPojoWithNoRoot xmlPojo2 = xmlPojoWithNoRoot();
		
		String xml = jaxb.write(list(xmlPojo, xmlPojo2).iterator()).rootElement("root").string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><xmlPojoWithNoRoot><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojoWithNoRoot><xmlPojoWithNoRoot><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojoWithNoRoot></root>"));
	}
	
	@Test
	public void shouldWriteXmlForNamedRootPojoIterator() {
		XmlPojoWithNamedRootElement xmlPojo = xmlPojoWithNamedRoot();
		XmlPojoWithNamedRootElement xmlPojo2 = xmlPojoWithNamedRoot();
		
		String xml = jaxb.write(list(xmlPojo, xmlPojo2).iterator()).rootElement("root").string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><XmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></XmlPojo><XmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></XmlPojo></root>"));
	}
	
	@Test
	public void shouldWriteXmlForPojoIterator() {
		XmlPojo xmlPojo = xmlPojo();
		XmlPojo xmlPojo2 = xmlPojo();
		
		String xml = jaxb.write(list(xmlPojo, xmlPojo2).iterator()).rootElement("root").string();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo><xmlPojo><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojo></root>"));
	}

	@Test
	public void shouldWriteXmlForNonRootPojoToWriter() {
		XmlPojoWithNoRoot xmlPojo = xmlPojoWithNoRoot();

		String xml = jaxb.write(list(xmlPojo)).rootElement("root").to(new StringWriter()).toString();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><xmlPojoWithNoRoot><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojoWithNoRoot></root>"));
	}

	@Test
	public void shouldWriteXmlForNonRootPojoToOutputStream() {
		XmlPojoWithNoRoot xmlPojo = xmlPojoWithNoRoot();

		String xml = jaxb.write(list(xmlPojo)).rootElement("root").to(new StringOutputStream()).toString();
		assertThat(xml, is("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><xmlPojoWithNoRoot><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojoWithNoRoot></root>"));
	}

	@Test
	public void shouldWriteFormattedEncodedXmlForNonRootPojoToOutputStream() throws UnsupportedEncodingException {
		XmlPojoWithNoRoot xmlPojo = xmlPojoWithNoRoot();

		byte[] data = jaxb.write(list(xmlPojo)).format().encoding("ISO-8859-1").rootElement("root").to(new ByteArrayOutputStream()).toByteArray();
		String expected = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><root><xmlPojoWithNoRoot><amount>1.23</amount><id>Id</id><name>Name</name></xmlPojoWithNoRoot></root>";
		assertThat(data, is(expected.getBytes("ISO-8859-1")));
	}

	@Test
	public void shouldReadXmlIntoPojo() {
		XmlPojo xmlPojo = xmlPojo();
		String xml = jaxb.write(xmlPojo).string();

		XmlPojo hydrated = jaxb.read(XmlPojo.class).from(xml).one();
		assertThat(hydrated, is(notNullValue()));
		assertThat(hydrated.getId(), is("Id"));
		assertThat(hydrated.getName(), is("Name"));
		assertThat(hydrated.getAmount(), is(new BigDecimal("1.23")));
	}

	@Test
	public void shouldReadXmlIntoPojoWithDifferentEncoding() {
		XmlPojo xmlPojo = xmlPojo();

		String xml = jaxb.write(xmlPojo).encoding("ISO-8859-1").string();

		XmlPojo hydrated = jaxb.read(XmlPojo.class).from(xml, "ISO-8859-1").one();
		assertThat(hydrated, is(notNullValue()));
		assertThat(hydrated.getId(), is("Id"));
		assertThat(hydrated.getName(), is("Name"));
		assertThat(hydrated.getAmount(), is(new BigDecimal("1.23")));
	}

	@Test
	public void shouldReadManyUnrootedPojosInFromXml() throws UnsupportedEncodingException {
		XmlPojoWithNoRoot pojo1 = xmlPojoWithNoRoot(1);
		XmlPojoWithNoRoot pojo2 = xmlPojoWithNoRoot(2);
		XmlPojoWithNoRoot pojo3 = xmlPojoWithNoRoot(3);

		String xml = jaxb.write(list(pojo1, pojo2, pojo3)).rootElement("root").format().string();
		List<XmlPojoWithNoRoot> hydrated = jaxb.read(XmlPojoWithNoRoot.class).from(xml, "UTF-8").list();
		assertThat(hydrated.size(), is(3));
		assertThat(hydrated.get(0).getId(), is("Id1"));
		assertThat(hydrated.get(0).getName(), is("Name1"));
		assertThat(hydrated.get(0).getAmount(), is(new BigDecimal("1.23")));
		assertThat(hydrated.get(1).getId(), is("Id2"));
		assertThat(hydrated.get(1).getName(), is("Name2"));
		assertThat(hydrated.get(1).getAmount(), is(new BigDecimal("2.23")));
		assertThat(hydrated.get(2).getId(), is("Id3"));
		assertThat(hydrated.get(2).getName(), is("Name3"));
		assertThat(hydrated.get(2).getAmount(), is(new BigDecimal("3.23")));
	}

	@Test
	public void shouldReadManyRootedPojosInFromXml() throws UnsupportedEncodingException {
		XmlPojo pojo1 = xmlPojo(1);
		XmlPojo pojo2 = xmlPojo(2);
		XmlPojo pojo3 = xmlPojo(3);

		String xml = jaxb.write(list(pojo1, pojo2, pojo3)).rootElement("root").format().string();
		List<XmlPojoWithNoRoot> hydrated = jaxb.read(XmlPojoWithNoRoot.class).from(xml, "UTF-8").list();
		assertThat(hydrated.size(), is(3));
		assertThat(hydrated.get(0).getId(), is("Id1"));
		assertThat(hydrated.get(0).getName(), is("Name1"));
		assertThat(hydrated.get(0).getAmount(), is(new BigDecimal("1.23")));
		assertThat(hydrated.get(1).getId(), is("Id2"));
		assertThat(hydrated.get(1).getName(), is("Name2"));
		assertThat(hydrated.get(1).getAmount(), is(new BigDecimal("2.23")));
		assertThat(hydrated.get(2).getId(), is("Id3"));
		assertThat(hydrated.get(2).getName(), is("Name3"));
		assertThat(hydrated.get(2).getAmount(), is(new BigDecimal("3.23")));
	}

	@Test
	public void shouldReadManyNamedRootedPojosInFromXml() throws UnsupportedEncodingException {
		XmlPojoWithNamedRootElement pojo1 = xmlPojoWithNamedRoot(1);
		XmlPojoWithNamedRootElement pojo2 = xmlPojoWithNamedRoot(2);
		XmlPojoWithNamedRootElement pojo3 = xmlPojoWithNamedRoot(3);

		String xml = jaxb.write(list(pojo1, pojo2, pojo3)).rootElement("root").format().string();
		List<XmlPojoWithNamedRootElement> hydrated = jaxb.read(XmlPojoWithNamedRootElement.class).from(xml, "UTF-8").list();
		assertThat(hydrated.size(), is(3));
		assertThat(hydrated.get(0).getId(), is("Id1"));
		assertThat(hydrated.get(0).getName(), is("Name1"));
		assertThat(hydrated.get(0).getAmount(), is(new BigDecimal("1.23")));
		assertThat(hydrated.get(1).getId(), is("Id2"));
		assertThat(hydrated.get(1).getName(), is("Name2"));
		assertThat(hydrated.get(1).getAmount(), is(new BigDecimal("2.23")));
		assertThat(hydrated.get(2).getId(), is("Id3"));
		assertThat(hydrated.get(2).getName(), is("Name3"));
		assertThat(hydrated.get(2).getAmount(), is(new BigDecimal("3.23")));
	}

	private XmlPojo xmlPojo() {
		return xmlPojo(null);
	}

	private XmlPojoWithNoRoot xmlPojoWithNoRoot() {
		return xmlPojoWithNoRoot(null);
	}

	private XmlPojoWithNamedRootElement xmlPojoWithNamedRoot() {
		return xmlPojoWithNamedRoot(null);
	}

	private XmlPojoWithNamedRootElement xmlPojoWithNamedRoot(Integer i) {
		String iStr = i == null ? "" : "" + i;
		BigDecimal amount = new BigDecimal((i == null ? "1" : i) + ".23");
		return new XmlPojoWithNamedRootElement("Id" + iStr, "Name" + iStr, amount);
	}

	private XmlPojoWithNoRoot xmlPojoWithNoRoot(Integer i) {
		String iStr = i == null ? "" : "" + i;
		BigDecimal amount = new BigDecimal((i == null ? "1" : i) + ".23");
		return new XmlPojoWithNoRoot("Id" + iStr, "Name" + iStr, amount);
	}

	private XmlPojo xmlPojo(Integer i) {
		String iStr = i == null ? "" : "" + i;
		BigDecimal amount = new BigDecimal((i == null ? "1" : i) + ".23");
		return new XmlPojo("Id" + iStr, "Name" + iStr, amount);
	}
}
