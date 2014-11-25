package com.quickplay.tcptrace.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.quickplay.tcptrace.ListenerService;
import com.quickplay.tcptrace.ListenerService.OnAcceptListener;
import com.quickplay.tcptrace.SocketTunnel;
import com.quickplay.tcptrace.Trace;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private		TabbedPane tabbedPane;
	Map<Trace, TracePane> tracePaneMap = new HashMap<Trace, TracePane>();
	List<Trace> configurations = new LinkedList<Trace>();
	File configurationFile = null;
	
    public MainFrame() throws IOException {
       initUI();     
    }
    
    private void initUI() {
		setJMenuBar(createMenuBar());
		add(createToolBar(), BorderLayout.NORTH);
		tabbedPane = new TabbedPane();
        add(tabbedPane);        
        pack();
    
        
        setTitle("TCP Trace");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menubar = new JMenuBar();


        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenu viewm = new JMenu("View");
        JMenu toolsm = new JMenu("Tools");
        JMenu helpm = new JMenu("Help");
        
        JMenu imp = new JMenu("Import");
        imp.setMnemonic(KeyEvent.VK_M);

        JMenuItem newsf = new JMenuItem("Import newsfeed list...");
        JMenuItem bookm = new JMenuItem("Import bookmarks...");
        JMenuItem mail = new JMenuItem("Import mail...");

        imp.add(newsf);
        imp.add(bookm);
        imp.add(mail);

        JMenuItem fileNew = new JMenuItem("New", Icons.get("new"));
        fileNew.setMnemonic(KeyEvent.VK_N);
        fileNew.addActionListener(newFileActionListener);
        
        
        JMenuItem fileOpen = new JMenuItem("Open", Icons.get("open"));
        fileNew.setMnemonic(KeyEvent.VK_O);

        JMenuItem fileSave = new JMenuItem("Save", Icons.get("save"));
        fileSave.setMnemonic(KeyEvent.VK_S);

        JMenuItem fileExit = new JMenuItem("Exit", Icons.get("exit"));
        fileExit.setMnemonic(KeyEvent.VK_C);
        fileExit.setToolTipText("Exit application");
        fileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,
            ActionEvent.CTRL_MASK));

        fileExit.addActionListener(exitListener);

        fileSave.addActionListener(fileSaveListener);
        
        fileOpen.addActionListener(fileOpenListener);
        
        file.add(fileNew);
        file.add(fileOpen);
        file.add(fileSave);
        file.addSeparator();
        file.add(imp);
        file.addSeparator();
        file.add(fileExit);

        menubar.add(file);
        menubar.add(viewm);
        menubar.add(toolsm);
        menubar.add(helpm);
        
        return menubar;
    }

    public JToolBar createToolBar() {
    	JToolBar toolbar = new JToolBar();
        JButton btnNew = new JButton("New");
        JButton btnOpen = new JButton("Open");
        JButton btnExit = new JButton("Exit");

        btnNew.addActionListener(newFileActionListener);
        btnExit.addActionListener(exitListener);
        toolbar.add(btnNew);
        toolbar.add(btnOpen);
        toolbar.add(btnExit);
        
        return toolbar;
    }
    
    NewTraceDialog.NewTunnelServerListener newTunnelListener = new NewTraceDialog.NewTunnelServerListener() {

		@Override
		public void onNewTunnelServer(Trace cfg) {
			startNewServer(cfg);
		}
    };

    private void startNewServer(Trace trace) {
		configurations.add(trace);
		final TracePane pane = new TracePane(trace);
		tabbedPane.addTracePane( trace.getName(), pane );
		tracePaneMap.put(trace, pane);
		
		try {
			ListenerService.getInstance().createTunnelServer(trace);
			ListenerService.getInstance().addOnAcceptListener(onAcceptListener);    
		}
		catch(Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
    }
    
    public void open() {
        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("configuration files", ".json");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Open");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            try {
				byte[] bytes = Files.readAllBytes(file.toPath());
				Trace[] configurations = new Gson().fromJson(new String(bytes), Trace[].class);
				if (configurations != null) {
					for(Trace c: configurations) {
						startNewServer(c);
					}
				}
				this.configurationFile = file;
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
        }

    }
    
    public void saveAs() {
        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("configuration files", ".json");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Save");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            saveAs(file);
            configurationFile = file;
        }    	
    }
    
    public void saveAs(File file) {
        try {
			FileWriter w = new FileWriter(file);
			w.append(new Gson().toJson(configurations));
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}	
    }
    
    private ActionListener newFileActionListener = new ActionListener() {

        public void actionPerformed(ActionEvent event) {
            NewTraceDialog ad = new NewTraceDialog(newTunnelListener);
            ad.setVisible(true);
        }
    };
    
    private ActionListener fileOpenListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			open();
		}
    };
    
    private ActionListener fileSaveListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (configurationFile == null) {
				saveAs();
			} else {
				saveAs(configurationFile);
			}
		}
    };
    
    private ActionListener fileSaveAsListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs();
		}
    	
    };
    
    private ActionListener exitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    };
    
    private OnAcceptListener onAcceptListener = new ListenerService.OnAcceptListener() {
		
		@Override
		public void onAccept(Trace tunnel, SocketTunnel t) {
			tracePaneMap.get(tunnel).addSocketChannel(t);
		}
	};
	

}