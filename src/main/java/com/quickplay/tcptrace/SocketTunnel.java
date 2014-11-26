package com.quickplay.tcptrace;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

import com.quickplay.tcptrace.exception.TunnelDisconnectedException;
import com.quickplay.tcptrace.util.ByteUtils;

public class SocketTunnel {
	final Date creationTime = new Date();
	final SocketChannel remoteChannel;
	final SocketChannel localChannel;
	OnDataReceivedListener listener;
	OnDisconnectListener onDisconnectListener;
	String name;
	
	StringBuilder localBuf = new StringBuilder();
	StringBuilder remoteBuf = new StringBuilder();
	
	public SocketTunnel(SocketChannel local, SocketChannel remote) {
		this.remoteChannel = remote;
		this.localChannel = local;

		SocketAddress addr = this.localChannel.socket().getRemoteSocketAddress();
		if (addr instanceof InetSocketAddress) {
			
			this.name = ((InetSocketAddress)addr).getHostString();
		}
		else {
			this.name = addr.toString();		
		}
	}

	public void setOnDataReceivedListener(OnDataReceivedListener l) {
		this.listener = l;
	}
	
	public SocketChannel getRemoteChannel() {
		return remoteChannel;
	}


	public SocketChannel getLocalChannel() {
		return localChannel;
	}

	public boolean isConnected() {
		return this.localChannel.isConnected() && this.remoteChannel.isConnected();
	}

	public void relay(SocketChannel channel)  {
		ByteBuffer buf = ByteBuffer.allocate(40960);
		try {
			int cnt = channel.read(buf);
			if (cnt > 0) {
				buf.flip();
				if (channel == this.localChannel){
					cnt = this.remoteChannel.write(buf);
					String str = ByteUtils.ascii(ByteUtils.consume(buf));
					this.localBuf.append(str);
					this.listener.onLocalDataReceived(this, str);
				}
				else if (channel == this.remoteChannel) {
					cnt = this.localChannel.write(buf);
					String str = ByteUtils.ascii(ByteUtils.consume(buf));
					this.remoteBuf.append(str);
					this.listener.onRemoteDataReceived(this, str);
				}
				else {
					System.err.println("xxxxxxxxxxx----------------------------");
				}
			}

			if (cnt == -1) {
				disconnect();
				throw new TunnelDisconnectedException();
			}
		}
		catch(IOException e) {
			disconnect();
			throw new TunnelDisconnectedException();
		}
	}
	
	public void disconnect() {
		try {
			this.localChannel.close();
			this.remoteChannel.close();		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (onDisconnectListener != null) {
			onDisconnectListener.onDisconnect(this);
		}
	}

	public static SocketTunnel createTunnel(SocketChannel localeChannel, Trace trace) throws IOException {
		SocketChannel remoteChannel = SocketChannel.open();
		remoteChannel.connect(new InetSocketAddress(trace.getRemoteAddress(), trace.getRemotePort()));
		SocketTunnel tunnel = new SocketTunnel(localeChannel, remoteChannel);

		return tunnel;
	}
	
	public static interface OnDataReceivedListener {
		public void onLocalDataReceived(SocketTunnel t, String data);
		public void onRemoteDataReceived(SocketTunnel t, String data);
	}

	public String getLocalBuffer() {
		return localBuf.toString();
	}
	
	public String getRemoteBuffer() {
		return remoteBuf.toString();
	}

	public Object getCreationTime() {
		return creationTime;
	}
	
	public void setOnDisconnectListener(OnDisconnectListener l) {
		this.onDisconnectListener = l;
	}
	
	public static interface OnDisconnectListener {
		void onDisconnect(SocketTunnel t);
	}

	public Object getName() {
		return name;
	}
}
