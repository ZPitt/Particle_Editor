import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;


class MyPanel extends JPanel implements ActionListener,MouseListener,MouseMotionListener, MouseWheelListener, ItemListener{
	public Point downPoint;
	public boolean clickDown,mouseOnScreen,nodeSelected,canDragNode;
	public Color neighborColor;
	
	public double scale =1.0;
	public double zMultiplier=1;
	public int Z = 1;
	public int maxZ =1;
	public boolean shiftUp=true;
	public boolean showEdges=true;
	public boolean edgesOnSelected;
	public Point currentPoint = new Point(0,0);
	public Location worldPoint = new Location(0,0);
    public ViewPort viewPort = new ViewPort(new Dimension(0,0));
    public int nodeSize = 100; //in pixels (diameter)
    public int edgeSize = 8; //in pixels (thickness)
    public int eHeight =300;
    public int eWidth =50;
    public int vpEdgeSize;
    public NodeManager nm = new NodeManager(nodeSize);
    public StateManager sm = new StateManager(nm);
    public JPopupMenu popup;
    public JMenuItem optionNone,optionSource,optionGround;
    public String[] hotkeyListA = new String[20];
    public String[] hotkeyListB = new String[20];
    public ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>();
    JCheckBox checkBox;
    JPanel checkBoxPanel;
    JTextArea log;
    JFileChooser fc;
    CsvFileWriter fw;
    public String dir = System.getProperty("user.home")+"\\";
    public String ext = "csv";
    public String packingType;
	static private final String newline = "\n";
    
    public MyPanel(){
    	fw = new CsvFileWriter();
    	fc = new JFileChooser();
    	FileNameExtensionFilter filter = new FileNameExtensionFilter( "CSV files (*csv)", "csv");
    	fc.setFileFilter(filter);
    	
    	FlowLayout experimentLayout = new FlowLayout();
    	experimentLayout.setAlignment(FlowLayout.LEFT);
    	experimentLayout.setHgap(5);
    	experimentLayout.setVgap(55);
    	setLayout(experimentLayout);
    
    	addMouseListener(this);
    	addMouseWheelListener(this);
    	addMouseMotionListener(this);
    	nm.addStateManager(sm);
    	
    	popup = new JPopupMenu();
        optionNone = new JMenuItem("None");
        optionNone.addActionListener(this);
        popup.add(optionNone);
        optionGround = new JMenuItem("Ground");
        optionGround.addActionListener(this);
        popup.add(optionGround);
        optionSource = new JMenuItem("Source");
        optionSource.addActionListener(this);
        popup.add(optionSource);
        
        add(popup);
        MouseListener popupListener = new PopupListener();
        addMouseListener(popupListener);
        
        checkBoxPanel = new JPanel();
        //checkBoxPanel.setPreferredSize(new Dimension(100,300));
        checkBoxPanel.setLayout(new GridLayout(0,1));
        checkBoxPanel.setBackground(Color.GREEN);
        add(checkBoxPanel, BorderLayout.WEST);
        
        neighborColor = new Color(0,0,255,100);
        
        instantiateHotKeyList();
        updateLayerCheckBox();
        sm.setCurrentZ(Z);
    }
    public void instantiateHotKeyList(){
    	hotkeyListA[0]="'DELETE'";
    	hotkeyListA[1]="'SHIFT'";
    	hotkeyListA[2]="'SHIFT-CLICK'";
    	hotkeyListA[3]="'SHIFT-Q'";
    	hotkeyListA[4]="'SHIFT-E'";
    	hotkeyListA[5]="'RIGHT-CLICK'";
    	hotkeyListA[6]="'Q'";
    	hotkeyListA[7]="'E'";
    	hotkeyListA[8]="'D'";
    	hotkeyListA[9]="'A'";
    	hotkeyListA[10]="'Z'";
    	hotkeyListA[11]="'X'";
    	hotkeyListA[12]="'W'";
    	hotkeyListA[13]="'H'";
    	hotkeyListA[14]="'F'";
    	hotkeyListA[15]="'S'";
    	hotkeyListA[16]="'C'";
    	hotkeyListA[17]="'V'";
    	hotkeyListA[18]="'F'";
    	hotkeyListA[19]="'G'";
    	
    	hotkeyListB[0]="-- Deletes selected node";
    	hotkeyListB[1]="-- Pins the ghost node to the closest spot depending on assigned packing type";
    	hotkeyListB[2]="-- Shift-clicking can select multiple nodes in 'Selection' Mode";
    	hotkeyListB[3]="-- *NOT IMPLEMENTED*Disables viewing of lower layers (lower with each sequential press)";
    	hotkeyListB[4]="-- *NOT IMPLEMENTED*Disables viewing of higher layers (higher with each sequential press)";
    	hotkeyListB[5]="-- Right-clicking on node in editor mode to assign ground or source to node";
    	hotkeyListB[6]="-- *NOT IMPLEMENTED*Enables viewing of lower levels (lower with each sequential press)";
    	hotkeyListB[7]="-- *NOT IMPLEMENTED*Enable viewing of higher levels (higher with each sequential press)";
    	hotkeyListB[8]="-- Deletes all edges connecting to selected node. If two nodes are selected, deletes the edge between them.";
    	hotkeyListB[9]="-- Adds an edge between two (shift-click to select multiple nodes) nodes";
    	hotkeyListB[10]="-- Moves down in layers (minimum is 1)";
    	hotkeyListB[11]="-- Moves up in layers (have to the press'Add Layer' button to increase max)";
    	hotkeyListB[12]="-- Clears grounding or sourcing on selected node(s)";
    	hotkeyListB[13]="-- Toggles between the current mode and the Help page";
    	hotkeyListB[14]="-- Toggles displaying the edges between nodes";
    	hotkeyListB[15]="-- Toggles between Selection and Editor Mode";
    	hotkeyListB[16]="-- Toggles between showing edges only on the selected node(s) and showing all the nodes";
    	hotkeyListB[17]="-- Toggle between hiding the edges and showing the edges";
    	hotkeyListB[18]="-- Declares the selected node(s) as sourced";
    	hotkeyListB[19]="-- Declares the selected node(s) as grounded";
    	
    }
    public void assignNodeManager(NodeManager nodeManager){
    	nm = nodeManager;
    }
    public void setViewport(Dimension newSize)
    {
    	viewPort.setSize(newSize);
    	viewPort.updateOriginToCurrent();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawViewport(g);
    }
    public void drawViewport(Graphics g)
    {
    	Dimension size = this.getSize();
    	viewPort.setSize(size);
    	Location focus = viewPort.getFocus();
    	Dimension vpPixel = viewPort.getVPSize();
    	Point vpOrigin = viewPort.getVPOrigin();
    	Node ghostNode = nm.getGhost();
    	
    	//draw origin lines && electrodes
    	if(sm.editor || sm.selection){
	    	g.setColor(Color.BLACK);
	    	g.drawLine(vpOrigin.x, 0, vpOrigin.x, (int)size.getHeight());
	    	g.drawLine(0,vpOrigin.y,(int)size.getWidth(),vpOrigin.y);
	    	
	    	//Draw Electrodes
//	    	Point sourcePoint = viewPort.convertToViewport2(nm.source.getLoc());
//	    	Point groundPoint = viewPort.convertToViewport2(nm.ground.getLoc());
//	    	g.fillRect(groundPoint.x-(int)(eWidth/2/scale), groundPoint.y-(int)(eHeight/2/scale),(int)(eWidth/scale), (int)(eHeight/scale));
//	    	g.setColor(Color.RED);
//	    	g.fillRect(sourcePoint.x-(int)(eWidth/2/scale), sourcePoint.y-(int)(eHeight/2/scale),(int)(eWidth/scale), (int)(eHeight/scale));
//	    	g.setColor(Color.BLACK);
//	    	g.drawRect(groundPoint.x-(int)(eWidth/2/scale), groundPoint.y-(int)(eHeight/2/scale),(int)(eWidth/scale), (int)(eHeight/scale));
//	    	g.drawRect(sourcePoint.x-(int)(eWidth/2/scale), sourcePoint.y-(int)(eHeight/2/scale),(int)(eWidth/scale), (int)(eHeight/scale));
    	}
    	//starts on lowest layer then moves up.
    	for(int k=0;k<sm.getVisibleLayers().size();k++){
    		int drawingLayer = sm.getVisibleLayers().get(k);
    		if(drawingLayer == Z){
    			//draw ghost node
    	    	if(mouseOnScreen && sm.editor){
    	    		Point gPoint = viewPort.convertToViewport2(ghostNode.getLoc());
    		    	g.setColor(Color.GRAY);
    		    	g.fillOval((int)(gPoint.x-nodeSize/scale/2),(int)(gPoint.y-nodeSize/scale/2), 
    		    			(int)(nodeSize/scale),(int)(nodeSize/scale));
    		    	g.setColor(Color.WHITE);
    		    	g.fillOval((int)(gPoint.x-(nodeSize-6)/(scale)/2),(int)(gPoint.y-(nodeSize-6)/(scale)/2), 
    		    			(int)((nodeSize-6)/(scale)),(int)((nodeSize-6)/(scale)));
    		    	drawNeighborInfo(g,size,ghostNode);
    	    	}
    		}
    		if(sm.selection || sm.editor){
        		//draw nodes on drawing layer
        		//node color modifications in selection mode
        		for(int i=0;i<nm.getSize();i++){
        			Node n = nm.getNode(i);
        			if(n.getLayer()==drawingLayer){
	        			Point p = viewPort.convertToViewport2(n.getLoc());
	        			//color of node
	        			if(n.grounded && n.sourced){
		        			
	        				if(drawingLayer<Z){g.setColor((new Color(165,42,42,120)));}
	        				else if(drawingLayer>Z){g.setColor((new Color(165,42,42)));}
	        				else
	        					g.setColor((new Color(165,42,42,200)));
	        				
	        			}
	        			else if(n.grounded){
	        				if(drawingLayer<Z){g.setColor(new Color(50,50,50,175));}
	        				else if(drawingLayer>Z){g.setColor(new Color(100,100,100,150));}
	        				else
	        					g.setColor(Color.BLACK);
	        			}
	        			else if(n.sourced){
	        				if(drawingLayer<Z){g.setColor(new Color(200,100,100,175));}
	        				else if(drawingLayer>Z){g.setColor(new Color(200,140,140,150));}
	        				else
	        					g.setColor(Color.RED);
	        			}
	        			else{
	        				if(drawingLayer<Z){g.setColor(new Color(200,200,200,150));}
	        				else if(drawingLayer>Z){g.setColor(new Color(230,230,230,150));}
	        				else
	        					g.setColor(Color.WHITE);
	        			}
	
	        			g.fillOval((int)(p.x-nodeSize/scale/2),(int)(p.y-nodeSize/scale/2), 
	        					(int)(nodeSize/scale),(int)(nodeSize/scale));
	        			if(n.selected){
	        				g.setColor(new Color(0,255,0,100));//green with alpha of 100/255;
	        				g.fillOval((int)(p.x-nodeSize/scale/2),(int)(p.y-nodeSize/scale/2), 
	        						(int)(nodeSize/scale),(int)(nodeSize/scale));
	        				drawNeighborInfo(g,size,n);
	        				//Selected Node Data
	        				String selectedNodeData = "Selected Node: "+Double.toString(Math.round(n.x*10.0)/10.0)
	        						+",  "+ Double.toString(Math.round((n.y*10.0/10.0))) + ",  "+ Integer.toString(n.getLayer());
	        				g.setColor(Color.BLACK);
	        				g.drawString(selectedNodeData,size.width/2+10,size.height-10);
	        			}
	        			else if(n.neighborToSelected || (sm.editor && n.neighborToGhost))
	        				drawBlueHighlight(g,n,drawingLayer);
	        			
	        			//color of border of node
	        			if(n.hoverOver)
	        				g.setColor(Color.RED);
	        			else
	        				g.setColor(Color.BLACK);
	
	        			g.drawOval((int)(p.x-nodeSize/scale/2),(int)(p.y-nodeSize/scale/2), 
	        					(int)(nodeSize/scale),(int)(nodeSize/scale));
        			}
        		}
        	}
    	}
    	//draws the edges
    	if(showEdges && !sm.helpPage){
	    	for(int i=0;i<nm.getEdges().size();i++){
	    		if(edgesOnSelected){
	    			if(nm.getEdges().get(i).containsSelectedNode()){
	    				drawEdge(g,i);
	    				}
	    		}
	    		else if (nm.getEdges().get(i).isVisible(sm.getVisibleLayers())){
	    			drawEdge(g,i);
	    		}
	    	}
    	}
    	//draw string indicating where the focus is in world coordinates
    	if(sm.editor || sm.selection){
			g.setColor(Color.BLACK);
			String focX = Double.toString(focus.x);
			String focY = Double.toString(focus.y);
			String foc = "FOCUS:"+focX+", "+focY;
			String pix = "FOCUS on SCREEN:"+ Integer.toString(vpPixel.width/2)+", "+Integer.toString(vpPixel.height/2);
			g.drawString(foc, 5, size.height-20);
			g.drawString(pix,5,size.height);
			
			//cursor data strings
			g.setColor(Color.BLACK);
			String mouseVPCoor = "MOUSE VP COOR:"+ Integer.toString(currentPoint.x)+", "+Integer.toString(currentPoint.y);
			String mouseWorldCoor = "MOUSE WORLD COOR:"+Double.toString(viewPort.convertToWorld2(currentPoint).x)
					+", "+Double.toString(viewPort.convertToWorld2(currentPoint).y);
			g.drawString(mouseVPCoor, 5, 10);
			g.drawString(mouseWorldCoor,5,30);
			String mode = "SELECTION";
			g.drawString("SET VISIBLE:",5,50);
			if(!sm.selection)
				mode = "EDITOR";
			String modeIndicator = "MODE: "+mode;
			g.drawString(modeIndicator, size.width-120, 10);
			
			//node and edge counts
			String nodeCount = "Number of Nodes: "+Integer.toString(nm.getSize());
			String edgeCount = "Number of Edges: "+Integer.toString(nm.getEdges().size());
			g.drawString(nodeCount, size.width-120, 30);
			g.drawString(edgeCount, size.width-120, 50);
			
			String zLevel = "Current Layer: "+ Integer.toString(Z) +", Current Height: "+Double.toString(Math.round(Z*zMultiplier * 100.0) / 100.0);
			g.drawString(zLevel,size.width/2-g.getFontMetrics().stringWidth(zLevel)/2,10);
			
			
    	}
    	//test Draw
    	
    	//drawing the help page
    	if(sm.helpPage){
    		g.setColor(Color.BLACK);
    		for(int i=0;i<hotkeyListA.length;i++){
    			g.drawString(hotkeyListA[i],5, 15+20*i);
    			g.drawString(hotkeyListB[i], 100, 15+20*i);
    		}
    		int usefulNotes= hotkeyListA.length*20+25;
    		int sep = 25;
    		g.drawString("USEFUL NOTES",size.width/4-20,usefulNotes);
    		g.drawString("Edges shown in black indicate connection with a node on same plane",5,usefulNotes+sep*1);
    		g.drawString("Edges shown in blue indicate connection with a node on the plane below",5,usefulNotes+sep*2);
    		g.drawString("Edges shown in green indicate connection with a node on the plane above",5,usefulNotes+sep*3);
    		g.fillRect(450,usefulNotes+sep*1-10,100,10);
    		g.setColor(Color.BLUE);
    		g.fillRect(450,usefulNotes+sep*2-10,100,10);
    		g.setColor(Color.GREEN);
    		g.fillRect(450,usefulNotes+sep*3-10,100,10);
    		
    	}
    }
    public void drawEdge(Graphics g, int i){
    	Edge e = nm.getEdges().get(i);
		int connection1 = sm.targetLayerValue(e.getLayer(1));
		int connection2 = sm.targetLayerValue(e.getLayer(2));
		if(connection1==-1 || connection2==-1)
			g.setColor(Color.BLUE);
		else if(connection1==1 || connection2==1)
			g.setColor(Color.GREEN);
		else if(connection1==0 && connection2==0)
			g.setColor(Color.BLACK);
		double rad = (Math.PI-Math.atan2(e.getFirstLoc().y-e.getSecondLoc().y,e.getFirstLoc().x-e.getSecondLoc().x));
	    Location differences = new Location((Math.sin(rad)*edgeSize/2),(Math.cos(rad)*edgeSize/2));
	    Point vpDiff= viewPort.convertSize(differences);
	    Point node1 = viewPort.convertToViewport2(e.getFirstLoc());
	    Point node2 = viewPort.convertToViewport2(e.getSecondLoc());
	    vpEdgeSize = viewPort.convertSize(edgeSize);
	    g.fillOval(node1.x-vpEdgeSize/2, node1.y-vpEdgeSize/2,vpEdgeSize , vpEdgeSize);
	    g.fillOval(node2.x-vpEdgeSize/2, node2.y-vpEdgeSize/2,vpEdgeSize , vpEdgeSize);
	    
	    g.fillPolygon(new int[]{node1.x+vpDiff.x,node1.x-vpDiff.x,node2.x+vpDiff.x,node2.x-vpDiff.x}, 
	    		new int[]{node1.y+vpDiff.y, node1.y-vpDiff.y, node2.y+vpDiff.y,node2.y-vpDiff.y}, 4);
	    g.drawLine(node1.x+vpDiff.x, node1.y+vpDiff.y, node1.x-vpDiff.x, node1.y-vpDiff.y);
	    g.drawLine(node2.x+vpDiff.x, node2.y+vpDiff.y, node2.x-vpDiff.x, node2.y-vpDiff.y);
    }
    public void drawBlueHighlight(Graphics g, Node n, int drawingLayer){
    	g.setColor(neighborColor);
    	Point neighborVPLoc = viewPort.convertToViewport2(n.getLoc());
    	g.fillOval((int)(neighborVPLoc.x-(nodeSize)/scale/2),(int)(neighborVPLoc.y-(nodeSize)/scale/2), 
    			(int)((nodeSize)/scale),(int)((nodeSize)/scale));
    	if(drawingLayer<Z){g.setColor(new Color(200,200,200,150));}
		else if(drawingLayer>Z){g.setColor(new Color(230,230,230,150));}
		else{
			g.setColor(Color.WHITE);
			if(n.grounded && n.sourced)
				g.setColor(new Color(165,42,42,200));
			else if(n.grounded)
				g.setColor(Color.BLACK);
			else if(n.sourced)
				g.setColor(Color.RED);
		}
		g.fillOval((int)(neighborVPLoc.x-(nodeSize-6)/scale/2),(int)(neighborVPLoc.y-(nodeSize-6)/scale/2), 
    			(int)((nodeSize-6)/scale),(int)((nodeSize-6)/scale));
    }
    public void drawNeighborInfo(Graphics g, Dimension size, Node n){
    	for(int j=0;j<n.getNeighborList().size();j++){
			Node neighbor = n.getNeighborList().get(j);
			Point neighborVPLoc = viewPort.convertToViewport2(neighbor.getLoc());
			g.setColor(Color.BLACK);
			String neighLocs = "Neighbor "+Integer.toString(j+1)+" location: "+Integer.toString(neighborVPLoc.x)+
					", "+Integer.toString(neighborVPLoc.y)+", "+Integer.toString(neighbor.getLayer());
			g.drawString(neighLocs, size.width-200, size.height-15*n.getNeighborList().size()+15*(j+1));
		}
    }
    //commands coming mostly from button presses
    public void shift(String state){
    	if(state.compareTo("DOWN")==0){
    		nm.snapping(true);
    		shiftUp=false;
    	}
    	else if(state.compareTo("UP")==0)
    	{
    		nm.snapping(false);
    		shiftUp=true;
    	}
    }
    public void setPackingType(String type){
    	packingType=type;
    }
    public void moveLayer(String direction){
    	if(direction.compareTo("UP")==0){
    		Z+=1;
    		if(Z>maxZ)
    			Z=maxZ;
    	}
    	if(direction.compareTo("DOWN")==0){
    		Z-=1;
    		if(Z<1)
    			Z=1;
    	}
    	sm.setCurrentZ(Z);
    	repaint();
    }
    public void removeLayer(){
    	maxZ-=1;
    	if(maxZ<1)
    		maxZ=1;
    	else{
    		nm.removeNodesAtLayer(maxZ+1);
    	}
    	if(Z>maxZ){    		
    		Z=maxZ;	
    		sm.setCurrentZ(Z);
    	}
    	
    	updateLayerCheckBox();
    	repaint();
    }
    public void addLayer(){
    	maxZ+=1;
    	if(Z==(maxZ-1)){
    		Z=maxZ;
    		updateLayerCheckBox();
    		sm.setVisible(Z);
    		}
    	updateLayerCheckBox();
    	sm.setCurrentZ(Z);
    	repaint();
    }
    public void updateLayerCheckBox(){
    	if(checkBoxList.size()<maxZ){
    		for(int i=0;i<maxZ-checkBoxList.size();i++){
    			String checkBoxName = "Level "+ Integer.toString(checkBoxList.size()+i+1);
    			checkBox = new JCheckBox(checkBoxName);
    			checkBoxPanel.add(checkBox);
    			checkBoxList.add(checkBox);
    			checkBox.addItemListener(this);
    			if(checkBoxList.size()==1){
    				checkBox.setSelected(true);
    			}
    			checkBoxPanel.revalidate();
    			validate();
    		}
    	}
    	if(checkBoxList.size()>maxZ){
    		for(int i=checkBoxList.size();i>maxZ;i--){
    			remove(checkBoxList.get(i-1));
    			checkBoxPanel.remove(checkBoxList.get(i-1));
    			checkBoxList.remove(i-1);
    			checkBoxPanel.revalidate();
    			validate();
    		}
    	}
    	sm.updateVisibleLayers(checkBoxList);
    	repaint();
    		
    }
    
    //mostly functions triggered by key strokes from here
    public void save(String name){
    	String fileName = dir + name;
    	fc.setSelectedFile(new File(fileName));
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String newPath = fixPath(file.getAbsolutePath());
            System.out.println("Write CSV file: "+ file.getAbsolutePath());
    		fw.writeCsvFile(newPath,nm.getEdges(),nm.getNodes());
            //This is where a real application would save the file.
            //log.append("Saved: " + file.getName() + "." + newline);
        } else {
            //log.append("Save cancelled." + newline);
        }
        	//log.setCaretPosition(log.getDocument().getLength());
    }
    public String fixPath(String path){
    	
    	int i = path.lastIndexOf('.');
		if (i > 0) {
		   String extension = path.substring(i+1);
		   if(!extension.equals(ext)){
			   path=path.substring(0, i+1)+ext;
		   }
		}
		if(i==-1){
			path=path+"."+ext;
		}
		i = path.lastIndexOf("\\");
		dir = path.substring(0,i+1);
    	return path;
    }
    public void layerView(String s){
    	if(s.equals("DOWN")){
    		sm.viewLowerLevels(shiftUp);
    	}
    	if(s.equals("UP")){
    		sm.viewUpperLevels(shiftUp);
    	}
    }
    public void toggleEdgesOnSelected(){
    	edgesOnSelected= !edgesOnSelected;
    	repaint();
    }
    public void addEdges(){
    	nm.addEdgeToSelected();
    	repaint();
    }
    public void removeEdges(){
    	nm.removeEdgesFromSelected();
    	repaint();
    }
    public void toggleEdgeView(){
    	showEdges =!showEdges;
    	repaint();
    }
    public void toggleHelpPage(){
    	sm.toggleHelpPage();
    	checkBoxPanel.setVisible((!sm.helpPage));
    	repaint();
    }
    public void toggleSelection(){
    	sm.toggleSelection();
    	if(!sm.selection)
    		nm.clearNodeSelection();
    	repaint();
    }
    
    public void deleteSelected(){
    	nm.deleteSelected();
    	repaint();
    }
    public void updateDrag()
    {
    	viewPort.updateFocus(currentPoint);
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {
		if(sm.editor&& SwingUtilities.isLeftMouseButton(e))
			nm.addNode(Z);
		repaint();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseOnScreen=true;
	}
	@Override
	public void mouseExited(MouseEvent e) {
		mouseOnScreen=false;
		repaint();
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		downPoint = e.getPoint();
		viewPort.setDownWorldCoord(downPoint);
		clickDown = true;
		
		if(nm.checkHover()&&SwingUtilities.isLeftMouseButton(e)){
			nm.selectNode(!shiftUp);
		}
		canDragNode = nm.checkSelectHover(worldPoint);
		repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		clickDown = false;
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		viewPort.incrementZoom(0.05*e.getWheelRotation());
		scale = viewPort.getZoom();
		repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
    	currentPoint = e.getPoint();
    	worldPoint = viewPort.convertToWorld2(currentPoint);
    	if(sm.editor)
    		nm.updateGhost(worldPoint);
		if(canDragNode){
			nm.dragNode(worldPoint);
		}
		else
			updateDrag();
    	repaint();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		currentPoint = e.getPoint();
		worldPoint = viewPort.convertToWorld2(currentPoint);
		if(sm.selection){
			nm.mouseOverEffects(worldPoint);
		}
		else{
			nm.updateGhost(worldPoint);
		}
		repaint();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if(s==optionNone){
			nm.setHoveredTo(nm.NONE);
		}
		else if(s==optionSource){
			nm.setHoveredTo(nm.SOURCE);
		}
		else if(s==optionGround){
			nm.setHoveredTo(nm.GROUND);
		}
		repaint();
		
	}
	public class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	    	if(sm.selection&&nm.checkHover())
	    		maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	    	if(sm.selection&&nm.checkHover())
	    		maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	        	nm.setMenuLocation(viewPort.convertToWorld2(currentPoint));
	            popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		sm.updateVisibleLayers(checkBoxList);
		repaint();
	}
	
}
