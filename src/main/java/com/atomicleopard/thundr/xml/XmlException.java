package com.atomicleopard.thundr.xml;

import com.threewks.thundr.exception.BaseException;

/**
 * {@link XmlException} is a generic exception type for exceptions during thundr xml manipulations.
 */
public class XmlException extends BaseException {
	private static final long serialVersionUID = 1L;

	public XmlException(String format, Object... formatArgs) {
		super(format, formatArgs);
	}

	public XmlException(Throwable cause, String format, Object... formatArgs) {
		super(cause, format, formatArgs);
	}

}
