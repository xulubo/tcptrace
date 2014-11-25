package com.quickplay.tcptrace.ui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class ToggleButton extends JToggleButton {

	private boolean state;
	private ImageIcon iconOn;
	private ImageIcon iconOff;
	
	public ToggleButton(ImageIcon on, ImageIcon off, Boolean defaultState) {
		iconOn = on;
		iconOff = off;
		setAction(action);
		this.setEnabled(true);
		setState(defaultState);
	}
	
	public void setState(boolean state) {
		this.state = state;
		setIcon(state ? iconOff : iconOn);
	}
	
	Action action = new Action() {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.err.println(state);
			setState(!state);
		}

		@Override
		public Object getValue(String key) {
			return null;
		}

		@Override
		public void putValue(String key, Object value) {
			
		}

		@Override
		public void setEnabled(boolean b) {
			setIcon(b ? iconOff : iconOn);		
			System.err.print("b=" + b);
		}

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			
		}
		
	};
}
