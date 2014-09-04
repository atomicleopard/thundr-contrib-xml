package com.atomicleopard.thundr.xml;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class XmlExceptionTest {

	@Test
	public void shouldRetainMessage() {
		XmlException exception = new XmlException("Format %s", "message");
		assertThat(exception.getMessage(), is("Format message"));
		assertThat(exception.getCause(), is(nullValue()));
	}

	@Test
	public void shouldRetainMessageAndCause() {
		Throwable cause = new RuntimeException();
		XmlException exception = new XmlException(cause, "Format %s", "message");
		assertThat(exception.getMessage(), is("Format message"));
		assertThat(exception.getCause(), is(cause));
	}
}
