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
