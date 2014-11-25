package com.quickplay.tcptrace.ui;

import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class Icons {

	public static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
	
	static {
        icons.put("new", 	new ImageIcon("new.png"));
        icons.put("open", 	new ImageIcon("open.png"));
        icons.put("save", 	new ImageIcon("save.png"));
        icons.put("exit", 	new ImageIcon("exit.png"));
	}
	
	public static ImageIcon get(String name) {
		return icons.get(name);
	}
}
