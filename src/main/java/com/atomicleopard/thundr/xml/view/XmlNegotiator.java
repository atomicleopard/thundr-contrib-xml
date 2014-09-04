package com.atomicleopard.thundr.xml.view;

import com.threewks.thundr.view.negotiating.NegotiatingView;
import com.threewks.thundr.view.negotiating.Negotiator;

/**
 * {@link Negotiator} which produces an {@link XmlView} for a {@link NegotiatingView}
 * 
 * @author nick
 * 
 */
public class XmlNegotiator implements Negotiator<XmlView> {

	@Override
	public XmlView create(NegotiatingView view) {
		return new XmlView(view);
	}

}
