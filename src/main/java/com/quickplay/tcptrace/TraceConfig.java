package com.quickplay.tcptrace;

public class TraceConfig {
	int localPort;
	int remotePort;
	String remoteAddress;

	
	public TraceConfig(int localPort, String remoteAddress, int remotePort) {
		this.localPort = localPort;
		this.remotePort = remotePort;
		this.remoteAddress = remoteAddress;
	}
	
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	public int getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	public String getRemoteAddress() {
		return remoteAddress;
	}
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
		
}
