/*
 * This file is a component of thundr, a software library from 3wks.
 * Read more: http://www.3wks.com.au/thundr
 * Copyright (C) 2013 3wks, <thundr@3wks.com.au>
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

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atomicleopard.expressive.Expressive;
import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.threewks.thundr.action.method.bind.ActionMethodBinder;
import com.threewks.thundr.action.method.bind.BindException;
import com.threewks.thundr.action.method.bind.path.PathVariableBinder;
import com.threewks.thundr.action.method.bind.request.CookieBinder;
import com.threewks.thundr.action.method.bind.request.RequestClassBinder;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.introspection.ParameterDescription;

public class JaxbBinder implements ActionMethodBinder {
	/**
	 * When trying to bind to a Pojo/DTO, we know there is a specific set of objects that we shouldn't bother trying with. This list contains those types.
	 */
	public static final List<Class<?>> NonBindableTypes = Expressive.list(PathVariableBinder.PathVariableTypes).addItems(RequestClassBinder.BoundTypes).addItems(CookieBinder.BoundTypes)
			.addItems(Boolean.class, boolean.class);

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
		if (!bindings.isEmpty() && bindings.containsValue(null)) {
			if (canBind(req.getContentType())) {
				ParameterDescription xmlParameterDescription = findParameterDescriptionForXmlParameter(bindings);
				if (xmlParameterDescription != null) {
					bindToSingleParameter(bindings, req, xmlParameterDescription);
				}
			}
		}
	}

	private void bindToSingleParameter(Map<ParameterDescription, Object> bindings, HttpServletRequest req, ParameterDescription xmlParameterDescription) {
		Class<?> type = xmlParameterDescription.classType();
		try {
			BufferedReader reader = req.getReader();
			if (reader != null) {
				Object converted = jaxb.read(type).validate(false).one();
				bindings.put(xmlParameterDescription, converted);
			}
		} catch (Exception e) {
			throw new BindException(e, "Failed to bind parameter '%s' as %s using JAXB: %s", xmlParameterDescription.name(), type, e.getMessage());
		}
	}

	private ParameterDescription findParameterDescriptionForXmlParameter(Map<ParameterDescription, Object> bindings) {
		for (Map.Entry<ParameterDescription, Object> bindingEntry : bindings.entrySet()) {
			ParameterDescription parameterDescription = bindingEntry.getKey();
			if (bindingEntry.getValue() == null && !NonBindableTypes.contains(parameterDescription.type())) {
				return parameterDescription;
			}
		}
		return null;
	}
}
