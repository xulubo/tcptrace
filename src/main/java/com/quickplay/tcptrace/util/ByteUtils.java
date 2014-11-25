package com.quickplay.tcptrace.util;

import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;

public class ByteUtils {
	public static byte[] consume(ByteBuffer bbuf) {
		bbuf.flip();
		byte[] bytes = new byte[bbuf.limit()];
		bbuf.get(bytes);
		return bytes;
	}
	
	public static String ascii(byte[] bytes) {
		if (!StringUtils.isAsciiPrintable(new String(bytes))) {
			new String("[binary]\r\n");
		}
		return new String(bytes);
	}
}
