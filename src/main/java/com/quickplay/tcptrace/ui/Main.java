package com.quickplay.tcptrace.ui;

import java.awt.EventQueue;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
    	MainFrame ex;
		try {
			ex = new MainFrame();
            ex.setVisible(true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
    }
    
    public static void main1(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            	//main1();
            }
        });
    }
}
