package com.atomicleopard.thundr.xml.bind;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.atomicleopard.thundr.xml.jaxb.test.XmlPojo;
import com.threewks.thundr.bind.BindException;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.introspection.ParameterDescription;
import com.threewks.thundr.route.HttpMethod;
import com.threewks.thundr.test.mock.servlet.MockHttpServletRequest;
import com.threewks.thundr.test.mock.servlet.MockHttpServletResponse;

public class JaxbBinderTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private ParameterDescription parameterDescription = new ParameterDescription("xml", XmlPojo.class);
	private Map<ParameterDescription, Object> bindings = new LinkedHashMap<>();
	private MockHttpServletRequest req = new MockHttpServletRequest();
	private MockHttpServletResponse resp = new MockHttpServletResponse();

	private Jaxb jaxb = new Jaxb();
	private JaxbBinder binder = new JaxbBinder(jaxb);

	@Test
	public void shouldRetainJaxBInstance() {
		assertThat(binder.getJaxb(), is(jaxb));
	}

	@Test
	public void shouldReturnTrueForCanBindForApplicationXml() {
		assertThat(binder.canBind(null), is(false));
		assertThat(binder.canBind(""), is(false));
		assertThat(binder.canBind("junk"), is(false));
		assertThat(binder.canBind("text/plain"), is(false));
		assertThat(binder.canBind("text/plain; charset=US-ASCII"), is(false));

		assertThat(binder.canBind("application/xml"), is(true));
		assertThat(binder.canBind("application/xml; charset=US-ASCII"), is(true));
		assertThat(binder.canBind("APPLICATION/XML"), is(true));
	}

	@Test
	public void shouldConvertRequestContentToParameter() {
		req.content("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>");

		Object result = binder.bindToParameter(req, parameterDescription);
		assertThat(result, is(notNullValue()));
		assertThat(result, instanceOf(XmlPojo.class));
		XmlPojo xmlPojo = (XmlPojo) result;
		assertThat(xmlPojo.getId(), is("id"));
		assertThat(xmlPojo.getName(), is("name"));
		assertThat(xmlPojo.getAmount(), is(new BigDecimal("98.76")));
	}

	@Test
	public void shouldConvertRequestContentToParameterWithoutProlog() {
		req.content("<xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>");

		Object result = binder.bindToParameter(req, parameterDescription);
		assertThat(result, is(notNullValue()));
		assertThat(result, instanceOf(XmlPojo.class));
		XmlPojo xmlPojo = (XmlPojo) result;
		assertThat(xmlPojo.getId(), is("id"));
		assertThat(xmlPojo.getName(), is("name"));
		assertThat(xmlPojo.getAmount(), is(new BigDecimal("98.76")));
	}

	@Test
	public void shouldFindFirstUnboundParameterWhichIsAJavabean() {
		assertThat(binder.findParameterDescriptionForXmlParameter(bindings), is(nullValue()));

		bindings.put(new ParameterDescription("one", String.class), null);

		assertThat(binder.findParameterDescriptionForXmlParameter(bindings), is(nullValue()));

		bindings.put(new ParameterDescription("two", int.class), null);
		bindings.put(new ParameterDescription("three", BigDecimal.class), null);
		bindings.put(new ParameterDescription("four", HttpMethod.class), null);
		bindings.put(new ParameterDescription("bound", XmlPojo.class), new XmlPojo());

		assertThat(binder.findParameterDescriptionForXmlParameter(bindings), is(nullValue()));

		bindings.put(new ParameterDescription("xml", XmlPojo.class), null);

		assertThat(binder.findParameterDescriptionForXmlParameter(bindings).name(), is("xml"));
	}

	@Test
	public void shouldBindWhenBindAll() {
		bindings.put(parameterDescription, null);

		req.contentType(ContentType.ApplicationXml);
		req.content("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>");

		binder.bindAll(bindings, req, resp, null);

		Object value = bindings.get(parameterDescription);
		assertThat(value, is(notNullValue()));
		assertThat(value, instanceOf(XmlPojo.class));
		XmlPojo xmlPojo = (XmlPojo) value;
		assertThat(xmlPojo.getId(), is("id"));
		assertThat(xmlPojo.getName(), is("name"));
		assertThat(xmlPojo.getAmount(), is(new BigDecimal("98.76")));
	}

	@Test
	public void shouldOnlyBindForRightContentType() throws IOException {
		bindings.put(parameterDescription, null);

		req.contentType("text/plain");
		req.content("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>");

		binder.bindAll(bindings, req, resp, null);

		assertThat(bindings.get(parameterDescription), is(nullValue()));
		// this fails on the second invocation, so this should check if the stream has been consumed
		req.getInputStream();
	}

	@Test
	public void shouldOnlyBindIfNotAlreadyBound() throws IOException {
		bindings.put(parameterDescription, "string");

		req.contentType(ContentType.ApplicationXml);
		req.content("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>");

		binder.bindAll(bindings, req, resp, null);

		assertThat(bindings.get(parameterDescription), is((Object) "string"));
		// this fails on the second invocation, so this should check if the stream has been consumed
		req.getInputStream();
	}

	@Test
	public void shouldOnlyBindIfBindableParameterFound() throws IOException {
		bindings.put(new ParameterDescription("name", String.class), null);

		req.contentType(ContentType.ApplicationXml);
		req.content("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><xmlPojo><amount>98.76</amount><id>id</id><name>name</name></xmlPojo>");

		binder.bindAll(bindings, req, resp, null);

		assertThat(bindings.get(parameterDescription), is(nullValue()));
		// this fails on the second invocation, so this should check if the stream has been consumed
		req.getInputStream();
	}

	@Test
	public void shouldThrowBindExceptionWhenBindingFails() {
		thrown.expect(BindException.class);
		thrown.expectMessage("Failed to bind parameter 'xml' as XmlPojo using JAXB: ");

		ParameterDescription parameterDescription = new ParameterDescription("xml", XmlPojo.class);
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.content("Not xml");

		binder.bindToParameter(req, parameterDescription);
	}
}
