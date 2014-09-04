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
