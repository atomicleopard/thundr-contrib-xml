package com.atomicleopard.thundr.xml.view;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.threewks.thundr.view.negotiating.NegotiatingView;

public class XmlNegotiatorTest {

	@Test
	public void shouldCreateXmlView() {
		NegotiatingView negotiatingView = new NegotiatingView("output");
		XmlView view = new XmlNegotiator().create(negotiatingView);
		assertThat(view.getOutput(), is((Object)"output"));
	}
}
