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
package com.atomicleopard.thundr.xml.view;

import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.http.StatusCode;
import com.threewks.thundr.view.DataView;

import jodd.util.StringPool;

public class XmlView extends DataView<XmlView> {

	public XmlView(Object output) {
		super(output);
		withContentType(ContentType.ApplicationXml.value());
		withStatusCode(StatusCode.OK);
		withCharacterEncoding(StringPool.UTF_8);
	}

	protected XmlView(DataView<?> other) {
		super(other);
	}

}
