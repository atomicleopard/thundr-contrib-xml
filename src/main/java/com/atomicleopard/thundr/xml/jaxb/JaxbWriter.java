package com.atomicleopard.thundr.xml.jaxb;

import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * {@link JaxbWriter} is a builder for write state when using the {@link Jaxb#write(Object)} interface.
 * They are immutable builders with a fluent api. As such, you must retain the reference to subsequent writers
 * created by each call.
 */
public class JaxbWriter<T> {
	protected Jaxb jaxb;
	protected T output;
	protected boolean format = false;
	protected String encoding = "UTF-8";

	protected JaxbWriter(Jaxb jaxb, T output) {
		this.output = output;
		this.jaxb = jaxb;
	}

	protected JaxbWriter(Jaxb jaxb, T output, String encoding, boolean format) {
		this.jaxb = jaxb;
		this.output = output;
		this.format = format;
		this.encoding = encoding;
	}

	/**
	 * When invoked, causes the xml output to be formatted. This has no effect on streamed output.
	 * 
	 * @return
	 */
	public JaxbWriter<T> format() {
		return new JaxbWriter<T>(jaxb, output, encoding, true);
	}

	/**
	 * Specifies the encoding of the output xml
	 * 
	 * @param encoding
	 * @return
	 */
	public JaxbWriter<T> encoding(String encoding) {
		return new JaxbWriter<T>(jaxb, output, encoding, format);
	}

	/**
	 * Specifies the encoding of the output xml
	 * 
	 * @param encoding
	 * @return
	 */
	public JaxbWriter<T> encoding(Charset encoding) {
		return new JaxbWriter<T>(jaxb, output, encoding.name(), format);
	}

	/**
	 * Writes the previously given object to the given output stream.
	 * 
	 * @param os
	 * @return the given outputstream with the xml output written to it
	 */
	public <O extends OutputStream> O to(O os) {
		return jaxb.writeOne(output, os, encoding, format);
	}

	/**
	 * Writes the previously given object to the given writer.
	 * 
	 * @param writer
	 * @return the given writer with the xml output written to it
	 */
	public <W extends Writer> W to(W writer) {
		return jaxb.writeOne(output, writer, encoding, format);
	}

	/**
	 * Writes the previously given object to an xml string
	 * 
	 * @return an xml string with the output
	 */
	public String string() {
		return jaxb.writeOne(output, encoding, format);
	}

}
