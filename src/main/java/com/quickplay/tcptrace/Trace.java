package com.quickplay.tcptrace;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Vector;

public class Trace {
	SelectionKey serverSelectionKey;
	private final Vector<SocketTunnel> tunnels = new Vector<SocketTunnel>();
	private TraceConfig cfg;
	
	public int getLocalPort() {
		return cfg.getLocalPort();
	}


	public int getRemotePort() {
		return cfg.getRemotePort();
	}


	public String getRemoteAddress() {
		return cfg.getRemoteAddress();
	}


	public Trace(TraceConfig cfg) {
		this.cfg = cfg;
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
		return "" + cfg.getLocalPort() + "->" + cfg.getRemoteAddress() + ":" + cfg.getRemotePort();
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
