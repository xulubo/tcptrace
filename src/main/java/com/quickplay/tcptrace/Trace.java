package com.quickplay.tcptrace;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Vector;

public class Trace {
	int localPort;
	int remotePort;
	String remoteAddress;
	SelectionKey serverSelectionKey;
	private final Vector<SocketTunnel> tunnels = new Vector<SocketTunnel>();

	public Trace() {
		
	}
	
	public Trace(int localPort, String remoteAddress, int remotePort) {
		super();
		this.localPort = localPort;
		this.remotePort = remotePort;
		this.remoteAddress = remoteAddress;
	}
	
	public void disconnectAll() {
		Iterator<SocketTunnel> iter = tunnels.iterator();
		while(iter.hasNext()) {
			SocketTunnel t = iter.next();
			t.disconnect();
		}
	}
	
	public void close() {
		disconnectAll();
		
		if (serverSelectionKey != null) {
			try {
				serverSelectionKey.channel().close();
				serverSelectionKey.cancel();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	
	public SelectionKey getServerSelectionKey() {
		return serverSelectionKey;
	}

	public void setServerSelectionKey(SelectionKey serverSelectionKey) {
		this.serverSelectionKey = serverSelectionKey;
	}

	public void addSocketTunnel(SocketTunnel tunnel) {
		this.tunnels.add(tunnel);
	}
	
	public Vector<SocketTunnel> getTunnels() {
		return tunnels;
	}

	public String getName() {
		return "" + getLocalPort() + "->" + getRemoteAddress() + ":" + getRemotePort();
	}

	public void removeDisconnected() {
		Iterator<SocketTunnel> iter = tunnels.iterator();
		while(iter.hasNext()) {
			if (!iter.next().isConnected()) {
				iter.remove();
			}
		}
	}
	
}
