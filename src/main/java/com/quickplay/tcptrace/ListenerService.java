package com.quickplay.tcptrace;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.quickplay.tcptrace.exception.TunnelDisconnectedException;

public class ListenerService extends Thread {

	private static ListenerService instance;
	
	private Selector selector;
	private List<Trace> pendingTraces;
	private Map<Integer, Trace> traceMap;
	private Map<SelectableChannel, SocketTunnel> tunnelMap;
	private OnAcceptListener listener;
	private boolean running;
	
	public static ListenerService getInstance() throws IOException {
		if (instance == null) {
			instance = new ListenerService();
		}
		
		return instance;
	}
	
	private ListenerService() throws IOException {
		this.running = false;
		this.selector = Selector.open();
		this.traceMap = new HashMap<Integer, Trace>();
		this.pendingTraces = new LinkedList<Trace>();
		this.tunnelMap = new HashMap<SelectableChannel, SocketTunnel>();
		start();
	}
	
	public void shutdown() throws InterruptedException {
		this.running = false;
		selector.wakeup();
		this.interrupt();
		this.join();
	}
	
	public void createTunnelServer(Trace cfg)  {
		synchronized(pendingTraces) {
			pendingTraces.add(cfg);
		}
		
		selector.wakeup();
	}
	
	
	public void addOnAcceptListener(OnAcceptListener l) {
		this.listener = l;
	}
	
	@Override
	public void run() {
		try {
			this.running = true;
			startSelector();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startSelector() throws IOException {
		while(this.running) {

		  int readyChannels = selector.select();

		  synchronized(pendingTraces) {
			  for(Trace cfg : pendingTraces) {
					ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
					serverSocketChannel.socket().bind(new InetSocketAddress(Inet4Address.getByName("127.0.0.1"), cfg.getLocalPort()));
					serverSocketChannel.socket().setReuseAddress(true);
					serverSocketChannel.configureBlocking(false);
					traceMap.put(serverSocketChannel.socket().getLocalPort(), cfg);
					SelectionKey serverKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
					cfg.setServerSelectionKey(serverKey);
			  }
			  
			  pendingTraces.clear();
		  }
		  
		  if(readyChannels == 0) continue;


		  Set<SelectionKey> selectedKeys = selector.selectedKeys();

		  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

		  while(keyIterator.hasNext()) {

		    SelectionKey key = keyIterator.next();
		    if (!key.isValid()) {
		    	continue;
		    }
		    
		    if(key.isAcceptable()) {
		    	ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
		    	accept(serverSocketChannel);


		    } else if (key.isConnectable()) {
		        // a connection was established with a remote server.

		    } else if (key.isReadable()) {
	    		SocketTunnel t = tunnelMap.get(key.channel());
		    	try {
		    		t.relay((SocketChannel)key.channel());
		    	} catch(TunnelDisconnectedException e) {
		    		tunnelMap.remove(t.getLocalChannel());
		    		tunnelMap.remove(t.getRemoteChannel());
		    		key.cancel();
		    	}
		    	
		    } else if (key.isWritable()) {
		        // a channel is ready for writing
		    }

		    keyIterator.remove();
		  }
		}
	}

	
	private void accept(ServerSocketChannel serverSocketChannel) {

		try {
			SocketChannel socketChannel = serverSocketChannel.accept();
			Integer port = serverSocketChannel.socket().getLocalPort();
			Trace cfg = traceMap.get(port);
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
			SocketTunnel t = SocketTunnel.createTunnel(socketChannel, cfg);
			t.getRemoteChannel().configureBlocking(false);
			t.getRemoteChannel().register(selector, SelectionKey.OP_READ);
			
			tunnelMap.put(socketChannel, t);
			tunnelMap.put(t.getRemoteChannel(), t);

			if (this.listener != null) {
				this.listener.onAccept(cfg, t);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public interface OnAcceptListener {
		void onAccept(Trace context, SocketTunnel t);
	}
}
