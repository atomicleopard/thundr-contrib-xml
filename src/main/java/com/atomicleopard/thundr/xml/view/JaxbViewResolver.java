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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.threewks.thundr.view.BaseView;
import com.threewks.thundr.view.ViewResolutionException;
import com.threewks.thundr.view.ViewResolver;

/**
 * A {@link ViewResolver} that produces Xml output using JAXB.
 * 
 * @see Jaxb
 * @see XmlView
 */
public class JaxbViewResolver implements ViewResolver<XmlView> {
	private Jaxb jaxb;

	public JaxbViewResolver() {
		this(new Jaxb());
	}

	public JaxbViewResolver(Jaxb jaxb) {
		this.jaxb = jaxb;
	}

	public Jaxb getJaxb() {
		return jaxb;
	}

	@Override
	public void resolve(HttpServletRequest req, HttpServletResponse resp, XmlView viewResult) {
		Object output = viewResult.getOutput();
		try {
			String encoding = viewResult.getCharacterEncoding();
			BaseView.applyToResponse(viewResult, resp);
			jaxb.write(output).encoding(encoding).to(resp.getOutputStream());
		} catch (Exception e) {
			throw new ViewResolutionException(e, "Failed to generate XML output for object '%s': %s", output.toString(), e.getMessage());
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}

}
