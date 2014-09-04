package com.atomicleopard.thundr.xml.view;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojo;
import com.threewks.thundr.http.Cookies;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;
import com.threewks.thundr.test.mock.servlet.MockHttpServletResponse;
import com.threewks.thundr.view.ViewResolutionException;

public class JaxbViewResolverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MockHttpServletRequest req = new MockHttpServletRequest();
	private MockHttpServletResponse resp = new MockHttpServletResponse();

	private Jaxb jaxb = new Jaxb();
	private JaxbViewResolver resolver = new JaxbViewResolver(jaxb);

	@Test
	public void shouldResolveByWritingXmlToOutputStream() throws IOException {
		XmlView viewResult = new XmlView(new XmlPojo("id", "name", new BigDecimal("98.76")));
		resolver.resolve(req, resp, viewResult);
		assertThat(resp.status(), is(HttpServletResponse.SC_OK));
		assertThat(resp.content(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>"));
		assertThat(resp.getCharacterEncoding(), is("UTF-8"));
	}

	@Test
	public void shouldThrowViewResolutionExceptionWhenFailedToWriteXmlToOutputStream() throws IOException {
		thrown.expect(ViewResolutionException.class);
		thrown.expectMessage("Failed to generate XML output for object 'string'");

		resp = spy(resp);
		when(resp.getOutputStream()).thenThrow(new RuntimeException("fail"));
		XmlView viewResult = new XmlView("string");
		resolver.resolve(req, resp, viewResult);
	}

	@Test
	public void shouldReturnClassNameForToString() {
		assertThat(new JaxbViewResolver().toString(), is("JaxbViewResolver"));
	}

	@Test
	public void shouldSetXmlContentType() {
		XmlView viewResult = new XmlView(new XmlPojo());
		resolver.resolve(req, resp, viewResult);
		assertThat(resp.getContentType(), is("application/xml"));
	}

	@Test
	public void shouldRespectExtendedViewValues() {
		XmlView view = new XmlView(new XmlPojo("id", "name", new BigDecimal("98.76")));
		Cookie cookie = Cookies.build("cookie").withValue("value2").build();
		view.withContentType("content/type").withCharacterEncoding("UTF-16").withHeader("header", "value1").withCookie(cookie);

		resolver.resolve(req, resp, view);
		assertThat(resp.getContentType(), is("content/type"));
		assertThat(resp.getCharacterEncoding(), is("UTF-16"));
		assertThat(resp.<String> header("header"), is("value1"));
		assertThat(resp.getCookies(), hasItem(cookie));
	}

	@Test
	public void shouldAllowAccessToInternalJaxb() {
		assertThat(resolver.getJaxb(), is(jaxb));
	}

}
