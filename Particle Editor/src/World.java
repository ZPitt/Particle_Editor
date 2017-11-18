import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class World extends JPanel implements ActionListener, DocumentListener
{
	static Toolkit tk = Toolkit.getDefaultToolkit();  
	private final static int HEIGHT = (int)tk.getScreenSize().getHeight();
	private final static int WIDTH  = (int)tk.getScreenSize().getWidth();
	private final static int OPTIONS_WIDTH = 150;
	public JButton addLayer,removeLayer,save,helpPage,moveDown,moveUp,edgeDisplay,
		edgeOnSelect,ground,source,clear;
	public JTextField autoName;
	public JComboBox<String> packingBox;
	public MyPanel graphicsPanel;
	String[] packingOptions = {"None","Cubic", "BCC", "FCC"};
	
	
	
    public static void main(String[] args) {
    	try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { } 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                constructGUI();
            }
        });
    }

    private static void constructGUI() {
        MyFrame frame = new MyFrame("Particle Editor");
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        MyPanel centerPanel = new MyPanel();
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setPreferredSize(new Dimension(WIDTH-OPTIONS_WIDTH-15,HEIGHT-300));
        
        frame.add(centerPanel, BorderLayout.CENTER);
        
        World eastPanel = new World(centerPanel);
        eastPanel.setPreferredSize(new Dimension(OPTIONS_WIDTH,HEIGHT-300));
        frame.add(eastPanel, BorderLayout.WEST);
        frame.addWorld(eastPanel);
        frame.pack();
        frame.setVisible(true);
        
        centerPanel.setViewport(centerPanel.getSize());
        centerPanel.setFocusable(true);
        frame.addTargetPanel(centerPanel);
    }

    public World(MyPanel newPanel) {
    	
    	
    	graphicsPanel = newPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
       
        save = new JButton("Save");
        //button.setPreferredSize(...);
        save.setMaximumSize(new Dimension(Integer.MAX_VALUE,save.getMinimumSize().height));
        
        save.addActionListener(this);
        add(save);
        
        addLayer = new JButton("Add Layer");
        //button.setPreferredSize(...);
        addLayer.setMaximumSize(new Dimension(Integer.MAX_VALUE, addLayer.getMinimumSize().height));
        addLayer.addActionListener(this);
        add(addLayer);

        removeLayer = new JButton("Remove Top Layer");
        //button.setPreferredSize(...);
        removeLayer.setMaximumSize(new Dimension(Integer.MAX_VALUE, removeLayer.getMinimumSize().height));
        removeLayer.addActionListener(this);
        add(removeLayer);

        helpPage = new JButton("Help Page");
        //label.setPreferredSize(...);
        helpPage.setMaximumSize(new Dimension(Integer.MAX_VALUE, helpPage.getMinimumSize().height));
        helpPage.addActionListener(this);
        add(helpPage);
        
        moveUp = new JButton("Up a Layer");
        //label.setPreferredSize(...);
        moveUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, moveUp.getMinimumSize().height));
        moveUp.addActionListener(this);
        add(moveUp);
        
        moveDown = new JButton("Down a Layer");
        //label.setPreferredSize(...);
        moveDown.setMaximumSize(new Dimension(Integer.MAX_VALUE,moveDown.getMinimumSize().height));
        moveDown.addActionListener(this);
        add(moveDown);
        
        JLabel label = new JLabel("SELECT PACKING:");
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE,label.getMinimumSize().height));
        add(label);
        
        packingBox = new JComboBox<String>(packingOptions);
        packingBox.setSelectedIndex(1);
        newPanel.sm.setPackingType((String)packingBox.getSelectedItem());
        packingBox.setMaximumSize(new Dimension(200, packingBox.getMinimumSize().height));
        packingBox.setAlignmentX(LEFT_ALIGNMENT);
        packingBox.addActionListener(this);
        add(packingBox);
        
        label = new JLabel("File Name:");
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE,label.getMinimumSize().height));
        add(label);
        
        autoName = new JTextField();
        //textField.setPreferredSize(...);
        autoName.setText("particle1.csv");
        autoName.setMaximumSize(new Dimension(Integer.MAX_VALUE, autoName.getMinimumSize().height));
        add(autoName);
        
        source = new JButton("Source Left of Selected");
        //label.setPreferredSize(...);
        source.setMaximumSize(new Dimension(Integer.MAX_VALUE,source.getMinimumSize().height));
        source.addActionListener(this);
        add(source);
        
        ground = new JButton("Ground Right of Selected");
        //label.setPreferredSize(...);
        ground.setMaximumSize(new Dimension(Integer.MAX_VALUE,ground.getMinimumSize().height));
        ground.addActionListener(this);
        add(ground);
        
        clear = new JButton("Clear all Nodes");
        //label.setPreferredSize(...);
        clear.setMaximumSize(new Dimension(Integer.MAX_VALUE,clear.getMinimumSize().height));
        clear.addActionListener(this);
        add(clear);
        
//        label = new JLabel("SAVING LOG:");
//        label.setMaximumSize(new Dimension(Integer.MAX_VALUE,label.getMinimumSize().height));
//        add(label); 
//        log = new JTextArea(); 
//        log.setMaximumSize(new Dimension(Integer.MAX_VALUE, log.getMinimumSize().height));
//        log.setBackground(Color.LIGHT_GRAY);
//        log.setEditable(false);
//        log.setAlignmentX(LEFT_ALIGNMENT);
//        add(log);

        add(Box.createVerticalGlue());
        edgeDisplay = new JButton("Hide Edges");
        //label.setPreferredSize(...);
        edgeDisplay.setMaximumSize(new Dimension(Integer.MAX_VALUE,edgeDisplay.getMinimumSize().height));
        edgeDisplay.addActionListener(this);
        add(edgeDisplay);
        
        edgeOnSelect = new JButton("Edges on Selected");
        //label.setPreferredSize(...);
        edgeOnSelect.setMaximumSize(new Dimension(Integer.MAX_VALUE,edgeOnSelect.getMinimumSize().height));
        edgeOnSelect.addActionListener(this);
        add(edgeOnSelect);
        
        
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		if(s.equals(addLayer)){
			graphicsPanel.addLayer();
		}
		else if(s.equals(removeLayer)){
			graphicsPanel.removeLayer();
		}
		else if(s.equals(save)){
			
		}
		else if(s.equals(helpPage)){
			graphicsPanel.toggleHelpPage();
		}
		else if(s.equals(moveUp)){
			graphicsPanel.moveLayer("UP");
		}
		else if(s.equals(moveDown)){
			graphicsPanel.moveLayer("DOWN");
		}
		else if(s.equals(edgeDisplay)){
			if(edgeDisplay.getText().equals("Show Edges")){
				edgeDisplay.setText("Hide Edges");
			}
			else{
				edgeDisplay.setText("Show Edges");
			}
			graphicsPanel.toggleEdgeView();
		}
		else if(s.equals(edgeOnSelect)){
			if(edgeOnSelect.getText().equals("Edges on Selected")){
				edgeOnSelect.setText("Show All Edges");
			}
			else{
				edgeOnSelect.setText("Edges on Selected");
			}
			graphicsPanel.toggleEdgesOnSelected();
		}
		//save file
		//Handle save button action.
		if (e.getSource() == save) {
        	graphicsPanel.save(autoName.getText());
		}
		else if(s.equals(source)){
			graphicsPanel.nm.sourceLeft();
			graphicsPanel.repaint();
		}
		else if(s.equals(ground)){
			graphicsPanel.nm.groundRight();
			graphicsPanel.repaint();
		}
		else if(s.equals(clear)){
			graphicsPanel.nm.clearAllNodes();
			graphicsPanel.repaint();
		}
		else if(s.equals(packingBox)){
			graphicsPanel.sm.setPackingType((String)packingBox.getSelectedItem());
		}
	}
	

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
class MyFrame extends JFrame {
	public MyPanel panel;
	public boolean shiftUp = true;
	public World w;
	public void addWorld(World world){
		w=world;
	}
    private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
            	if(e.getKeyCode()==KeyEvent.VK_S)
        		{
        			panel.toggleSelection();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_A)
        		{
        			panel.addEdges();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_D){
            		panel.removeEdges();
            	}
            	if(e.getKeyCode()==KeyEvent.VK_DELETE)
        		{
        			panel.deleteSelected();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_H)
        		{
        			panel.toggleHelpPage();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_X)
        		{
        			panel.moveLayer("UP");
        		}
            	if(e.getKeyCode()==KeyEvent.VK_Z)
        		{
        			panel.moveLayer("DOWN");
        		}
            	if(e.getKeyCode()==KeyEvent.VK_C)
        		{
            		if(w.edgeOnSelect.getText().equals("Edges on Selected")){
        				w.edgeOnSelect.setText("Show All Edges");
        			}
        			else{
        				w.edgeOnSelect.setText("Edges on Selected");
        			}
        			panel.toggleEdgesOnSelected();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_V)
        		{
            		if(w.edgeDisplay.getText().equals("Show Edges")){
    				w.edgeDisplay.setText("Hide Edges");
    			}
    			else{
    				w.edgeDisplay.setText("Show Edges");
    			}
            		panel.toggleEdgeView();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_SHIFT){
            		if(shiftUp){
            			shiftUp=false;
            			panel.shift("DOWN");
            		}
            			
            	}
            	if(e.getKeyCode()==KeyEvent.VK_Q)
        		{
        			panel.layerView("DOWN");
        		}
            	if(e.getKeyCode()==KeyEvent.VK_E)
        		{
        			panel.layerView("DOWN");
        		}
            	if(e.getKeyCode()==KeyEvent.VK_W)
        		{
        			panel.nm.clearNode();
        			panel.repaint();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_F)
        		{
        			panel.nm.sourceNode();
        			panel.repaint();
        		}
            	if(e.getKeyCode()==KeyEvent.VK_G)
        		{
        			panel.nm.groundNode();
        			panel.repaint();
        		}
            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            	if(e.getKeyCode()==KeyEvent.VK_SHIFT){
            		shiftUp=true;
            		panel.shift("UP");
            	}
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
            	
            }
            return false;
        }
    }
    public MyFrame(String title) {
    	super.setTitle(title);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
    }
    public void addTargetPanel(MyPanel p)
    {
    	panel =p;
    }
}
