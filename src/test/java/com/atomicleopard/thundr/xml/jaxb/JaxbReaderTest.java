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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.atomicleopard.thundr.xml.XmlException;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojo;

public class JaxbReaderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private Jaxb jaxb = new Jaxb();

	@Test
	public void shouldRetainJaxbAndType() {
		JaxbReader<XmlPojo> jaxbReader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		assertThat(jaxbReader.jaxb, is(jaxb));
		assertThat(jaxbReader.type == XmlPojo.class, is(true));
		assertThat(jaxbReader.stream, is(nullValue()));
		assertThat(jaxbReader.validate, is(false));
		assertThat(jaxbReader.batchSize, is(200));
	}

	@Test
	public void shouldChangeValidateAndReturnNewInstance() {
		JaxbReader<XmlPojo> original = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);

		JaxbReader<XmlPojo> reader = original.validate(true);
		assertThat(reader.validate, is(true));
		assertThat(reader, is(not(sameInstance(original))));

		reader = reader.validate(false);
		assertThat(reader.validate, is(false));
		assertThat(reader, is(not(sameInstance(original))));
	}

	@Test
	public void shouldChangeBathSizeAndReturnNewInstance() {
		JaxbReader<XmlPojo> original = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);

		JaxbReader<XmlPojo> reader = original.batchSize(1);
		assertThat(reader.batchSize, is(1));
		assertThat(reader, is(not(sameInstance(original))));

		reader = reader.batchSize(10);
		assertThat(reader.batchSize, is(10));
		assertThat(reader, is(not(sameInstance(original))));
	}

	@Test
	public void shouldChangeInputStreamAndReturnNewInstance() {
		JaxbReader<XmlPojo> original = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		assertThat(original.stream, is(nullValue()));

		JaxbReader<XmlPojo> reader = original.from(new byte[] { 1, 2, 3 });
		assertThat(reader.stream, is(notNullValue()));
		assertThat(reader, is(not(sameInstance(original))));

		InputStream inputStream = new ByteArrayInputStream(new byte[] { 1, 2, 3 });
		reader = reader.from(inputStream);
		assertThat(reader.stream, is(inputStream));
		assertThat(reader, is(not(sameInstance(original))));

		reader = reader.from("string");
		assertThat(reader.stream, is(notNullValue()));
		assertThat(reader.stream, is(not(inputStream)));
		assertThat(reader, is(not(sameInstance(original))));
	}

	@Test
	public void shouldThrowExceptionWhenReadOneIfNoStreamSpecified() {
		thrown.expect(XmlException.class);
		thrown.expectMessage("You have not specified a data source, such as an InputStream, String or byte[]");

		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		reader.one();
	}

	@Test
	public void shouldThrowExceptionWhenListIfNoStreamSpecified() {
		thrown.expect(XmlException.class);
		thrown.expectMessage("You have not specified a data source, such as an InputStream, String or byte[]");

		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		reader.list();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldThrowExceptionWhenStreamIfNoStreamSpecified() {
		thrown.expect(XmlException.class);
		thrown.expectMessage("You have not specified a data source, such as an InputStream, String or byte[]");

		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		Batch<XmlPojo, Integer> batch = mock(Batch.class);
		reader.stream(batch);
	}

	@Test
	public void shouldReadOneFromXml() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>1.23</amount><id>id</id><name>name</name></xmlPojo>";
		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		XmlPojo result = reader.from(xml).one();
		assertThat(result, is(notNullValue()));
		assertThat(result.getId(), is("id"));
		assertThat(result.getName(), is("name"));
		assertThat(result.getAmount(), is(new BigDecimal("1.23")));
	}

	@Test
	public void shouldListFromXml() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Root><xmlPojo><amount>1.23</amount><id>id1</id><name>name1</name></xmlPojo><xmlPojo><amount>3.21</amount><id>id2</id><name>name2</name></xmlPojo></Root>";
		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		List<XmlPojo> result = reader.from(xml).list();
		assertThat(result, is(notNullValue()));
		assertThat(result.size(), is(2));
		assertThat(result.get(0).getId(), is("id1"));
		assertThat(result.get(0).getName(), is("name1"));
		assertThat(result.get(0).getAmount(), is(new BigDecimal("1.23")));
		assertThat(result.get(1).getId(), is("id2"));
		assertThat(result.get(1).getName(), is("name2"));
		assertThat(result.get(1).getAmount(), is(new BigDecimal("3.21")));
	}

	@Test
	public void shouldStreamFromXmlReducing() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Root><xmlPojo><amount>1.23</amount><id>id1</id><name>name1</name></xmlPojo><xmlPojo><amount>3.21</amount><id>id2</id><name>name2</name></xmlPojo></Root>";
		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		final List<XmlPojo> results = new ArrayList<XmlPojo>();
		Integer result = reader.from(xml).stream(new Batch<XmlPojo, Integer>() {
			@Override
			public Integer process(Integer result, List<XmlPojo> batch) {
				results.addAll(batch);
				return batch.size() + (result == null ? 0 : result);
			}
		});
		assertThat(result, is(2));
		assertThat(results.size(), is(2));
		assertThat(results.get(0).getId(), is("id1"));
		assertThat(results.get(0).getName(), is("name1"));
		assertThat(results.get(0).getAmount(), is(new BigDecimal("1.23")));
		assertThat(results.get(1).getId(), is("id2"));
		assertThat(results.get(1).getName(), is("name2"));
		assertThat(results.get(1).getAmount(), is(new BigDecimal("3.21")));
	}

	@Test
	public void shouldStreamMultipleBatches() {
		String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Root>";
		String tail = "</Root>";
		String fragment = "<xmlPojo><amount>1.23</amount><id>id1</id><name>name1</name></xmlPojo>";
		String xml = head;
		for (int i = 0; i < 20; i++) {
			xml += fragment;
		}
		xml += tail;

		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		final List<List<XmlPojo>> batches = new ArrayList<>();
		Integer result = reader.from(xml).batchSize(1).stream(new Batch<XmlPojo, Integer>() {
			@Override
			public Integer process(Integer result, List<XmlPojo> batch) {
				batches.add(batch);
				return batch.size() + (result == null ? 0 : result);
			}
		});
		assertThat(result, is(20));
		assertThat(batches.size(), is(20));
		assertThat(batches.get(0).get(0).getId(), is(notNullValue()));
		assertThat(batches.get(19).get(0).getId(), is(notNullValue()));
	}

	@Test
	public void shouldRestrictBatchSizeToPositiveInteger() {
		JaxbReader<XmlPojo> reader = new JaxbReader<XmlPojo>(jaxb, XmlPojo.class);
		assertThat(reader.batchSize(-1).batchSize, is(1));
		assertThat(reader.batchSize(Integer.MIN_VALUE).batchSize, is(1));
		assertThat(reader.batchSize(0).batchSize, is(1));
		assertThat(reader.batchSize(1).batchSize, is(1));
	}
}
