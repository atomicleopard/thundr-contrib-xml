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
