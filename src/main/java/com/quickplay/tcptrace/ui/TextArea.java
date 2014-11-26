package com.quickplay.tcptrace.ui;

import java.awt.KeyboardFocusManager;

import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class TextArea extends JTextArea {

	public TextArea(String text) {
		super(text);
		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);	
	}
	

}
