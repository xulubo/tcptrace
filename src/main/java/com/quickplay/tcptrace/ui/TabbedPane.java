package com.quickplay.tcptrace.ui;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class TabbedPane extends JTabbedPane implements OnCloseListener {

	public void addTracePane(String title, TracePane pane) {
		super.addTab(title, pane);
		pane.setOnTraceCloseListener(this);
	}

	@Override
	public void onClose(JComponent component) {
		this.remove(component);
	}

}
