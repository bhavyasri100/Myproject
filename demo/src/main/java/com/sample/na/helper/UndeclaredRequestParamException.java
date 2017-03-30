package com.sample.na.helper;

import java.util.Set;

@SuppressWarnings("serial")
public class UndeclaredRequestParamException extends IllegalArgumentException {
	public UndeclaredRequestParamException(String param, Set<String> allowedParams) {
		super("Parameter '" + param + "' is not supported. Supported parameters " +
			"are " + allowedParams + ".");
	}
}
