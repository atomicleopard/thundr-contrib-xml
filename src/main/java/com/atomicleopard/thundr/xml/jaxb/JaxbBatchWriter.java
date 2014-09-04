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

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * {@link JaxbBatchWriter} is a builder for write state when using the {@link Jaxb#write(Iterable)} and {@link Jaxb#write(Iterator)} interfaces.
 * They are immutable builders with a fluent api. As such, you must retain the reference to subsequent writers
 * created by each call.
 */
public class JaxbBatchWriter<T> {
	protected Jaxb jaxb;
	protected Iterator<T> output;
	protected boolean format = false;
	protected String encoding = "UTF-8";
	protected String rootElement = "Root";

	protected JaxbBatchWriter(Jaxb jaxb, Iterator<T> outputs) {
		this.output = outputs;
		this.jaxb = jaxb;
	}

	protected JaxbBatchWriter(Jaxb jaxb, Iterator<T> output, String rootElement, String encoding, boolean format) {
		this.jaxb = jaxb;
		this.output = output;
		this.format = format;
		this.encoding = encoding;
		this.rootElement = rootElement;
	}

	/**
	 * When invoked, causes the xml output to be formatted. This has no effect on streamed output.
	 * 
	 * @return
	 */
	public JaxbBatchWriter<T> format() {
		return new JaxbBatchWriter<T>(jaxb, output, rootElement, encoding, true);
	}

	/**
	 * Specifies the encoding of the output xml
	 * 
	 * @param encoding
	 * @return
	 */
	public JaxbBatchWriter<T> encoding(String encoding) {
		return new JaxbBatchWriter<T>(jaxb, output, rootElement, encoding, format);
	}

	/**
	 * Specifies the encoding of the output xml
	 * 
	 * @param encoding
	 * @return
	 */
	public JaxbBatchWriter<T> encoding(Charset encoding) {
		return new JaxbBatchWriter<T>(jaxb, output, rootElement, encoding.name(), format);
	}

	/**
	 * Specifies the name of the root element that will wrap the fragments produced for each javabean
	 * Defaults to "Root"
	 * 
	 * @param rootElement
	 * @return
	 */
	public JaxbBatchWriter<T> rootElement(String rootElement) {
		return new JaxbBatchWriter<T>(jaxb, output, rootElement, encoding, format);
	}

	/**
	 * Writes the previously given objects to the given output stream.
	 * 
	 * @param os
	 * @return the given outputstream with the xml output written to it
	 */
	public <O extends OutputStream> O to(O os) {
		return jaxb.writeMany(output, os, rootElement, encoding, format);
	}

	/**
	 * Writes the previously given objects to the given writer.
	 * 
	 * @param writer
	 * @return the given writer with the xml output written to it
	 */
	public <W extends Writer> W to(W writer) {
		return jaxb.writeMany(output, writer, rootElement, encoding, format);
	}

	/**
	 * Writes the previously given objects to an xml string
	 * 
	 * @return an xml string with the output
	 */
	public String string() {
		return jaxb.writeMany(output, rootElement, encoding, format);
	}

}
