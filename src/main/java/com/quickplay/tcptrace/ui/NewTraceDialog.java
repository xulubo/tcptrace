package com.quickplay.tcptrace.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.quickplay.tcptrace.Trace;
import com.quickplay.tcptrace.TraceConfig;

@SuppressWarnings("serial")
public class NewTraceDialog  extends JDialog {

	NewTunnelServerListener listener = null;
	
    public NewTraceDialog(Component parent, NewTunnelServerListener listener) {
    	this.listener = listener;
        initUI();
        setLocationRelativeTo(parent);
    }

    public final void initUI() {

        this.setLayout(new BorderLayout());
        this.setResizable(false);
        
        
        final JPanel panel = new JPanel(new GridLayout(3,1,0,0));
    	final JTextArea localPort = createTextArea("8080");
    	final JTextArea remoteAddress = createTextArea("san-dev-qpmtvx54-app-02");
    	final JTextArea remotePort = createTextArea("8083");
        
    	panel.add(createConfigRow("Listen Port:", localPort));
    	panel.add(createConfigRow("Remote Address:", remoteAddress));
    	panel.add(createConfigRow("Remote Port:", remotePort));
        panel.setAlignmentY(BOTTOM_ALIGNMENT);
        panel.setPreferredSize(new Dimension(500, 150));
        panel.setMaximumSize(new Dimension(500, 150));
        
        final JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setAlignmentY(CENTER_ALIGNMENT);
        main.add(Box.createVerticalGlue());
        main.add(panel);
        main.add(Box.createVerticalGlue());
        
    	add(main);
    	
        JButton cancel = createButton("Cancel");
        JButton start = createButton("Start");
        start.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
            	if (listener != null) {
            		Integer.parseInt(localPort.getText());
            		Integer.parseInt(remotePort.getText());
            		listener.onNewTunnelServer(new TraceConfig(
            				Integer.parseInt(localPort.getText()),
            				remoteAddress.getText(),
                    		Integer.parseInt(remotePort.getText())
            				));
            	}
                dispose();
            }
        });
        
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });        

        start.setAlignmentX(0.5f);
        
        JPanel bottom = new JPanel();
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.add(buttonPanel);
        
        buttonPanel.add(start);
        buttonPanel.add(cancel);
        buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
        bottom.setPreferredSize(new Dimension(200, 50));
        bottom.setBackground(Color.YELLOW);
        add(bottom, BorderLayout.SOUTH);
        
        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("New TCP Tunnel");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 250);
    }
    
    public void setTunnelAddedListener(NewTunnelServerListener l) {
    	this.listener = l;
    }
    
    public interface NewTunnelServerListener {
    	public void onNewTunnelServer(TraceConfig cfg);
    }
    
    private JPanel createConfigRow(String label, JTextArea text) {
    	JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
    	panel.add(createLabel(label));
    	panel.add(text);
    	//panel.setBackground(Color.YELLOW);
    	//panel.setPreferredSize(new Dimension(500, 40));
    	panel.setMaximumSize(new Dimension(500, 50));
    	return panel;
    }
    
    private JLabel createLabel(String text) {
    	Font font = new Font("Verdana", Font.BOLD, 16);
    	JLabel label = new JLabel(text);
    	label.setHorizontalAlignment(SwingConstants.RIGHT);
    	label.setFont(font);
    	//label.setMinimumSize(new Dimension(180, 30));
    	label.setPreferredSize(new Dimension(180, 30));
   
    	return label;
    }
    
    private JTextArea createTextArea(String text) {
    	Font font = new Font("Verdana", Font.BOLD, 16);
    	final TextArea area = new TextArea(text);
    	area.setMargin(new Insets(7, 0, 0, 0));
    	area.setFont(font);
    	area.setPreferredSize(new Dimension(260, 30));
    	area.setBorder(BorderFactory.createBevelBorder(1));
    	return area;
    }
    
    private JButton createButton(String text) {
    	Font font = new Font("Verdana", Font.PLAIN, 18);
    	JButton button = new JButton(text);
    	button.setPreferredSize(new Dimension(120, 40));
    	button.setFont(font);
    	return button;
    }
}
