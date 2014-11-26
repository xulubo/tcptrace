package com.quickplay.tcptrace.exception;

@SuppressWarnings("serial")
public class ConfigurationException extends RuntimeException {

	public ConfigurationException(String msg, Exception e) {
		super(msg, e);
	}
	
}
