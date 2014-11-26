package com.quickplay.tcptrace.ui;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.quickplay.tcptrace.SocketTunnel;
import com.quickplay.tcptrace.SocketTunnel.OnDisconnectListener;

@SuppressWarnings("serial")
public class TunnelList<E> extends JScrollPane implements OnDisconnectListener {

	private static final String[] COLUMN_NAMES = new String[] {
		"Connection", "Status", "Time"
	};
		
	private static JTable table;
	Vector<SocketTunnel> data;

	public TunnelList(Vector<SocketTunnel> data) {
		table = new JTable();
		table.setShowGrid(false);
		table.setBackground(Color.LIGHT_GRAY);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setViewportView(table);
		table.setOpaque(false);
		table.setLayout(null);
		((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);	
		
		setListData(data);
	}
	
	public void setListData(Vector<SocketTunnel> tunnels) {
		this.data = tunnels;
		for(SocketTunnel t : tunnels) {
			t.setOnDisconnectListener(this);
		}
		
		table.setModel(new MyTableModel());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
	}

	public void addListSelectionListener(
			ListSelectionListener listSelectionListener) {
		table.getSelectionModel().addListSelectionListener(listSelectionListener);
	}

	public SocketTunnel getSelectedValue() {
		int index = table.getSelectedRow();
		if (index <0 || index >= data.size()) {
			return null;
		}
		
		System.err.println(index);
		return data.get(index);
	}

	public int getSelectedIndex() {
		return table.getSelectedRow();
	}
	
	static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
	class MyTableModel extends DefaultTableModel {

		
		public MyTableModel() {
			super(data.size(), COLUMN_NAMES.length);
		}
		
		@Override
		public int getRowCount() {
			return data == null ? super.getRowCount() : data.size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return COLUMN_NAMES[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0:
				return data.get(rowIndex).getName();
			case 1:
				return data.get(rowIndex).isConnected() ? "connected" : "";
			case 2:
				return dateFormat.format(data.get(rowIndex).getCreationTime());
			default:
				return null;
			}
		}
	}
	
	@Override
	public void onDisconnect(SocketTunnel t) {
		table.updateUI();
	}

}
