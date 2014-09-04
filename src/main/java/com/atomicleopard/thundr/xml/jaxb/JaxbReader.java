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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.atomicleopard.thundr.xml.XmlException;

import jodd.util.StringPool;

/**
 * {@link JaxbReader} is a builder for read state when using the {@link Jaxb#read(Class)} interface.
 * They are immutable builders with a fluent api. As such, you must retain the reference to subsequent readers
 * created by each call.
 */
public class JaxbReader<T> {
	protected Jaxb jaxb;
	protected Class<T> type;
	protected boolean validate = false;
	protected InputStream stream = null;
	protected int batchSize = 200;

	protected JaxbReader(Jaxb service, Class<T> type) {
		this.jaxb = service;
		this.type = type;
	}

	protected JaxbReader(Jaxb service, Class<T> type, InputStream stream, boolean validate, int batchSize) {
		this.jaxb = service;
		this.type = type;
		this.validate = validate;
		this.stream = stream;
		this.batchSize = batchSize;
	}

	/**
	 * Controls whether validation is performed during reading of xml to JAXB objects. Defaults to false.
	 * 
	 * @param validate
	 * @return
	 */
	public JaxbReader<T> validate(boolean validate) {
		return new JaxbReader<T>(jaxb, type, stream, validate, batchSize);
	}

	/**
	 * Controls the number of elements processed at a time when using the streaming interfaces. Can be used to control
	 * memory consumption and the number of javabeans processed at a time.
	 * Defaults to 200
	 * 
	 * @param batchSize
	 * @return
	 */
	public JaxbReader<T> batchSize(int batchSize) {
		return new JaxbReader<T>(jaxb, type, stream, validate, Math.max(batchSize, 1));
	}

	/**
	 * Specifies the source of xml to read
	 * 
	 * @param stream
	 * @return
	 */
	public JaxbReader<T> from(InputStream stream) {
		return new JaxbReader<T>(jaxb, type, stream, validate, batchSize);
	}

	/**
	 * Specifies the source of xml to read
	 * 
	 * @param string
	 * @return
	 */
	public JaxbReader<T> from(String string) {
		return from(string, StringPool.UTF_8);
	}

	/**
	 * Specifies the source of xml to read, including the encoding of the string
	 * 
	 * @param string
	 * @param encoding
	 * @return
	 */
	public JaxbReader<T> from(String string, String encoding) {
		return new JaxbReader<T>(jaxb, type, stream(string, encoding), validate, batchSize);
	}

	/**
	 * Specifies the source of xml to read
	 * 
	 * @param data
	 * @return
	 */
	public JaxbReader<T> from(byte[] data) {
		return new JaxbReader<T>(jaxb, type, stream(data), validate, batchSize);
	}

	/**
	 * Transforms the previously specified data source from xml into one instance of the expected type.
	 * The implication is that the root element of the xml document matches the expected type.
	 * 
	 * <pre>
	 * <code>
	 * &lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;
	 * &lt;Javabean&gt;
	 * 	&hellip;
	 * &lt;/Javabean&gt;
	 * </code>
	 * </pre>
	 * 
	 * @return
	 */
	public T one() {
		if (stream == null) {
			throw new XmlException("You have not specified a data source, such as an InputStream, String or byte[]");
		}
		return jaxb.readOne(stream, type, validate);
	}

	/**
	 * Transforms the previously specified data source from xml into many instances of the expected type.
	 * The implication is that the root element contains a list of xml fragments that match the expected type.
	 * 
	 * <pre>
	 * <code>
	 * &lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;
	 * &lt;Root&gt;
	 * 	&lt;Javabean&gt;
	 * 		&hellip;
	 * 	&lt;/Javabean&gt;
	 * 	&lt;Javabean&gt;
	 * 		&hellip;
	 * 	&lt;/Javabean&gt;
	 * &lt;/Root&gt;
	 * </code>
	 * </pre>
	 * 
	 * @return
	 */
	public List<T> list() {
		return stream(new GatherBatch<T>());
	}

	/**
	 * Transforms the previously specified data source from xml into many instances of the expected type, processing them in batches.
	 * The implication is that the root element contains a list of xml fragments that match the expected type.
	 * 
	 * <pre>
	 * <code>
	 * &lt;?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?&gt;
	 * &lt;Root&gt;
	 * 	&lt;Javabean&gt;
	 * 		&hellip;
	 * 	&lt;/Javabean&gt;
	 * 	&lt;Javabean&gt;
	 * 		&hellip;
	 * 	&lt;/Javabean&gt;
	 * &lt;/Root&gt;
	 * </code>
	 * </pre>
	 * 
	 * The provided {@link Batch} will be invoked once for each batch to be processed (the batch size being controlled by {@link #batchSize(int)}).
	 * You can utilise the result of the batch to aggregate a result across all batches.
	 * 
	 * @param batchProcessor
	 * @return the result of execution across all batches
	 */
	public <R> R stream(Batch<T, R> batchProcessor) {
		if (stream == null) {
			throw new XmlException("You have not specified a data source, such as an InputStream, String or byte[]");
		}
		return jaxb.readMany(stream, type, validate, batchSize, batchProcessor);
	}

	private InputStream stream(String string, String encoding) {
		try {
			return stream(string.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream stream(byte[] data) {
		return new ByteArrayInputStream(data);
	}
}
