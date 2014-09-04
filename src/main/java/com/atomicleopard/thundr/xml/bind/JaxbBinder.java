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
package com.atomicleopard.thundr.xml.bind;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.threewks.thundr.bind.BindException;
import com.threewks.thundr.bind.Binder;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.introspection.TypeIntrospector;

public class JaxbBinder implements Binder {
	private Jaxb jaxb;

	public JaxbBinder(Jaxb jaxb) {
		this.jaxb = jaxb;
	}

	public Jaxb getJaxb() {
		return jaxb;
	}

	public boolean canBind(String contentType) {
		return ContentType.ApplicationXml.matches(contentType);
	}

	@Override
	public void bindAll(Map<ParameterDescription, Object> bindings, HttpServletRequest req, HttpServletResponse resp, Map<String, String> pathVariables) {
		boolean shouldBind = bindings.containsValue(null);
		boolean canBind = canBind(req.getContentType());

		if (shouldBind && canBind) {
			ParameterDescription xmlParameterDescription = findParameterDescriptionForXmlParameter(bindings);
			if (xmlParameterDescription != null) {
				Object bound = bindToParameter(req, xmlParameterDescription);
				bindings.put(xmlParameterDescription, bound);
			}
		}
	}

	Object bindToParameter(HttpServletRequest req, ParameterDescription xmlParameterDescription) {
		Class<?> type = xmlParameterDescription.classType();
		try {
			return jaxb.read(type).validate(false).from(req.getInputStream()).one();
		} catch (Exception e) {
			throw new BindException(e, "Failed to bind parameter '%s' as %s using JAXB: %s", xmlParameterDescription.name(), type.getSimpleName(), e.getMessage());
		}
	}

	ParameterDescription findParameterDescriptionForXmlParameter(Map<ParameterDescription, Object> bindings) {
		for (Map.Entry<ParameterDescription, Object> bindingEntry : bindings.entrySet()) {
			ParameterDescription parameterDescription = bindingEntry.getKey();
			if (bindingEntry.getValue() == null && TypeIntrospector.isAJavabean(parameterDescription.classType())) {
				return parameterDescription;
			}
		}
		return null;
	}
}
