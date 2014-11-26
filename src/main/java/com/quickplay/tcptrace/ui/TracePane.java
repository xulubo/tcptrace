package com.quickplay.tcptrace.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quickplay.tcptrace.SocketTunnel;
import com.quickplay.tcptrace.SocketTunnel.OnDataReceivedListener;
import com.quickplay.tcptrace.Trace;


@SuppressWarnings("serial")
public class TracePane extends JPanel{
	private static Logger logger = LoggerFactory.getLogger(TracePane.class);
	private final JTextArea request = new JTextArea("");
	private final JTextArea response = new JTextArea("");
	private final TunnelList<SocketTunnel> uiTunnelList;
	private final Font  TEXT_FONT 			= new Font("Courier New", Font.BOLD, 16);
	private final Color BACKGROUND_COLOR 	= new Color(242,242,242);
	private final Trace trace;
	private OnCloseListener onCloseListener;
	
	public TracePane(Trace trace) {
		this.trace = trace;
		uiTunnelList = new TunnelList<SocketTunnel>(trace.getTunnels());
		this.setLayout(new BorderLayout());
		this.add(createToolBar(), BorderLayout.NORTH);
		this.add(createWorkPane());
		this.updateUI();
	}

	public void addSocketChannel(SocketTunnel t) {
		trace.addSocketTunnel(t);
		uiTunnelList.setListData(trace.getTunnels());
		this.repaint();
	
		t.setOnDataReceivedListener(onDataReceivedListener);
	}

	public void setOnTraceCloseListener(OnCloseListener l) {
		this.onCloseListener = l;
	}
	
	private JSplitPane createWorkPane() {
		uiTunnelList.setPreferredSize(new Dimension(235, 500));
		uiTunnelList.setBackground(BACKGROUND_COLOR);
		request.setFont(TEXT_FONT);
		response.setFont(TEXT_FONT);
		request.setBackground(BACKGROUND_COLOR);
		response.setBackground(BACKGROUND_COLOR);
		JScrollPane requestScrollPane = new JScrollPane(request);
		requestScrollPane.setPreferredSize(new Dimension(500, 200));

		JSplitPane outputPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
				requestScrollPane, 
				new JScrollPane(response));

		JSplitPane workPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, uiTunnelList, outputPanel);

		outputPanel.setDividerLocation(0.5);

		uiTunnelList.addListSelectionListener(listSelectionListener);

		return workPane;
	}
	

	
	private JToolBar createToolBar() {
		ImageIcon filterIcon = new ImageIcon("images/filter.png");
		ImageIcon startIcon = new ImageIcon("images/start.png");
		ImageIcon stopIcon = new ImageIcon("images/stop.png");

		JToolBar toolbar = new JToolBar();
		
		JButton filterButton = new JButton(filterIcon);
		ToggleButton startButton = new ToggleButton(startIcon, stopIcon, true);
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(clearActionListener);
		JButton disconnectButton = new JButton("Disconnect All Connections");
		disconnectButton.addActionListener(disconnectAllActionListener);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(closeActionListener);
		
		toolbar.add(filterButton);
		toolbar.add(startButton);
		toolbar.add(clearButton);
		toolbar.add(disconnectButton);
		toolbar.add(closeButton);

		return toolbar;
	}
	
	private boolean isSelected(SocketTunnel t) {
		return this.uiTunnelList.getSelectedValue() == t;
	}

	private ActionListener disconnectAllActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
    		trace.disconnectAll();
    		TracePane.this.repaint();    		
        }
    };
    
	private ActionListener clearActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
        	trace.removeDisconnected();
    		uiTunnelList.setListData(trace.getTunnels());
    		TracePane.this.repaint();    		
        }
    };
	
	private ActionListener closeActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
    		trace.close();
    		if (TracePane.this.onCloseListener != null) {
    			TracePane.this.onCloseListener.onClose(TracePane.this);
    		}
        }
    };
	
	private ListSelectionListener listSelectionListener = new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if (!e.getValueIsAdjusting()) {
				int i  = uiTunnelList.getSelectedIndex();
				if (i>=0 && i<trace.getTunnels().size()) {
					SocketTunnel t = trace.getTunnels().get(i);
					logger.debug("selected index {} name: {}", i, t.toString());					
					request.setText(t.getLocalBuffer());
					response.setText(t.getRemoteBuffer());
					request.setSelectionStart(0);
					request.setSelectionEnd(0);
					response.setSelectionStart(0);
					response.setSelectionEnd(0);
				}
			}
		}

	};
	
	private OnDataReceivedListener onDataReceivedListener = new OnDataReceivedListener(){
		
		@Override
		public void onLocalDataReceived(SocketTunnel t, String data) {
			if (isSelected(t)) {
				request.append(data);
			}
		}

		@Override
		public void onRemoteDataReceived(SocketTunnel t, String data) {
			if (isSelected(t)) {
				response.append(data);
			}
		}

	};
}
