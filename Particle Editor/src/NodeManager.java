import java.awt.Dimension;
import java.util.ArrayList;


public class NodeManager {
	public ArrayList<Node> nodeList = new ArrayList<Node>();
	public ArrayList<Edge>	edgeList = new ArrayList<Edge>();
	public ArrayList<Electrode>electrodeList = new ArrayList<Electrode>();
	public boolean nodeSelected;
	public boolean activeGhost = true;
	public ViewPort vp = new ViewPort(new Dimension(0,0));
	public int nodeSize,dragNodeIndex,totalSelected;
	public Location wPoint,desiredLoc,ghostPoint,menuLocation;
	public Node ghostNode;
	public StateManager sm;
	public final String GROUND = "ground";
	public final String SOURCE = "source";
	public final String NONE = "none";
	
	public NodeManager(int ns){
		nodeSize = ns;
		ghostNode = new Node(0,0,0);
		
		
	}
	public void addStateManager(StateManager stateManager){
		sm=stateManager;
	}
	public void makeGhostNode(Location startPoint){
		ghostNode = new Node(startPoint.x,startPoint.y, 0);
	}
	public Node getGhost(){
		return ghostNode;
	}
	public void updateGhost(Location cursorLoc){
		ghostPoint = cursorLoc;
    	desiredLoc = new Location(ghostNode.newLocation(cursorLoc));
		if(validMoveGhost(desiredLoc)){
			ghostNode.setLoc(desiredLoc);
			updateNeighborList(ghostNode);
		}
		else if(validMoveGhost(cursorLoc)){
			ghostNode.setLoc(cursorLoc);
			ghostNode.setDragOffset(ghostNode.getLoc());
			activeGhost = true; //prevents from adding nodes after already putting one down in same location
		}
	}
	public boolean validMoveGhost(Location l){
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).getLoc().distanceTo(l)<nodeSize && nodeList.get(i).getLayer()==sm.currentZ){
    			ghostNode.setDragOffset(ghostPoint);
    			return false;
    		}
    	}
    	ghostNode.setDragOffset(ghostNode.getLoc());
    	return true;
    }
    public boolean checkHover(){
    	//checks to see if any nodes are currently being hovered over
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).hoverOver)
    			return true;
    	}
    	//check electrodes
    	for(Electrode e : electrodeList){
    		if(e.hoverOver)
    			return true;
    	}
    	return false;
    }
    
    public void selectNode(boolean multiSelect){
    	if(!multiSelect)
    		clearNodeSelection();
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).hoverOver&&totalSelected<2){
    			nodeList.get(i).setSelected(true);
    			updateNeighborList(nodeList.get(i));
    			totalSelected+=1;
    			nodeSelected=true;
    			break;
    		}
    	}
    	for(Electrode e : electrodeList){
    		if(!multiSelect)
    			if(e.hoverOver){
    				e.setSelected(true);
    				break;
    			}
    	}
    	
    	
    }
    public boolean checkSelectHover(Location worldPoint){
    	//input has to be in world coordinates
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).hoverOver && nodeList.get(i).selected){
    			dragNodeIndex=i;
    			nodeList.get(i).setDragOffset(worldPoint);
    			return true;
    		}
    	}
    	//check the electrodes
    	
    	return false; 
    }
    public void mouseOverEffects(Location worldPoint)
    { //input has to be in world coordinates
    	//check electrodes first so overlaying nodes have priority
    	int hoverIndex = -1;
    	double minDistance = nodeSize/2;
    	
    	//if panel has multiple layers visible, it hovers over whichever node
    	//has the closer center
    	
    	for(int i=0;i<nodeList.size();i++){
    		double distance = worldPoint.distanceTo(nodeList.get(i).getLoc());
    		if(distance<=minDistance && sm.isLayerVisible(nodeList.get(i).getLayer())){
    			minDistance = distance;
    			hoverIndex = i;
    		}
    		nodeList.get(i).setHoverOver(false);
    	}
    	if(hoverIndex>=0){
    		nodeList.get(hoverIndex).setHoverOver(true);
    	}
    }
    public void clearNodeSelection(){
		for(int i=0;i<nodeList.size();i++){
			nodeList.get(i).setSelected(false);
			nodeList.get(i).neighborToSelected=false;
		}
		activeGhost=true;
		nodeSelected=false;
		totalSelected=0;
    }
    public void dragNode(Location worldPoint)
    {
    	
    	Node n = nodeList.get(dragNodeIndex);
    	wPoint = worldPoint;
    	desiredLoc = new Location(n.newLocation(worldPoint));
    	if(validMove(desiredLoc,n.getLayer())){
    		n.moveNode(desiredLoc);
    		updateNeighborList(n);
    	}
    	
    }
    
    public boolean validMove(Location l, int layer){
    	if(layer!=sm.currentZ)
    		return false;
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).getLoc().distanceTo(l)<nodeSize && i!=dragNodeIndex && nodeList.get(i).getLayer()==layer){
    			nodeList.get(dragNodeIndex).setDragOffset(wPoint);
    			return false;
    		}
    	}
    	return true;
    }
    public void removeFromNeighborLists(Node targetNode){
    	for(int i=0;i<nodeList.size();i++){
    		for(int j=0;j<nodeList.get(i).getNeighborList().size();j++){
    			if(nodeList.get(i).getNeighborList().get(j).equals(targetNode)){
    				nodeList.get(i).getNeighborList().remove(j);
    				break;
    			}
    		}
    	}
    }
    public void updateNeighborList(Node targetNode){
    	//first part makes sure its previous neighbors are still neighbors
    	ArrayList<Node> targetNeighList = targetNode.getNeighborList();
    	
    	for(int i=targetNeighList.size()-1;i>-1;i--){
    		if(targetNode.getLoc().distanceTo(targetNeighList.get(i).getLoc())>nodeSize+2){
    			targetNeighList.remove(i);
    		}
    	}
    	//check to see if any new nodes are neighbors
    	for(int i=0;i<nodeList.size();i++){
    		if(sm.editor)
    			nodeList.get(i).neighborToGhost=false;
    		if(sm.selection)
    			nodeList.get(i).neighborToSelected=false;
    		if(targetNode.getLoc().distanceTo(nodeList.get(i).getLoc())<nodeSize+2 
    				&& !targetNode.equals(nodeList.get(i))&& sm.isAdjacentLayer(targetNode,nodeList.get(i))){
    			//check to not add duplicates
    			boolean duplicateExists=false;
    			for(int j =0;j<targetNeighList.size();j++){
    				if(targetNeighList.get(j).getLoc().equals(nodeList.get(i).getLoc())){
    					if(sm.editor)
    						nodeList.get(i).neighborToGhost=true;
    					if(sm.selection)
    						nodeList.get(i).neighborToSelected=true;
    					duplicateExists=true; 
    					break;
    				}
    			}
    			if(!duplicateExists){
    				if(sm.selection)
    					nodeList.get(i).neighborToSelected=true;
    				if(sm.editor)
    					nodeList.get(i).neighborToGhost=true;
    				targetNode.addNeighbor(nodeList.get(i));
    			}
    		}
    	}
    }
    public void addNode(int Z){
    	//inputs have to be in worldCoordinates
    	if(activeGhost){
	    	Node n = new Node(ghostNode.x,ghostNode.y,Z);
	    	addEdges(n);
	    	nodeList.add(n);
	    	activeGhost=false;
    	}
    }
    public void updateGhostLayer(int newLayer){
    	activeGhost=true;
    	ghostNode.setLayer(newLayer);
    }
    public void addEdges(Node n){
    	ArrayList<Node> gnList = ghostNode.getNeighborList();
    	for(int i=0;i<gnList.size();i++){
    		Edge newEdge = new Edge(n, gnList.get(i));
    		newEdge.addSelfToNodes();
    		edgeList.add(newEdge);
    	}
    }
    public void addEdgeToSelected(){
    	int index1=-1;
		int index2=-1;
		int selectCount=0;
    	if(totalSelected==1){
    		for(int i=0;i<nodeList.size();i++){
    			if(nodeList.get(i).selected){
    				index1=i;
    				break;
    			}
    		}
    		for(int i=0;i<nodeList.get(index1).getNeighborList().size();i++)
    		{
    			Edge newEdge = new Edge(nodeList.get(index1),nodeList.get(index1).getNeighborList().get(i));
    			if(noEdgeDuplicates(newEdge)){
    				newEdge.addSelfToNodes();
    				edgeList.add(newEdge);
    			}
    		}
    	}
    	else if(totalSelected==2){
    		for(int i=0;i<nodeList.size();i++){
    			if(nodeList.get(i).selected){
    				if(selectCount==0){
    					index1=i;
    					selectCount+=1;
    				}
    				else{
    					index2=i;
    					Edge newEdge = new Edge(nodeList.get(index1),nodeList.get(index2));
    		    		if(noEdgeDuplicates(newEdge)){
    		    			newEdge.addSelfToNodes();
    		    			edgeList.add(newEdge);
    		    		}
    		    		break;
    				}
    			}
    		}
    	}
    }
    public boolean noEdgeDuplicates(Edge e){
    	for(int i=0;i<edgeList.size();i++){
    		if(e.sameAs(edgeList.get(i)))
    			return false;
    	}
    	return true;
    }
    public void removeEdgesFromSelected(){
    	int index1=-1;
		int index2=-1;
		int selectCount=0;
		
    	if(totalSelected==1){
        	for(int i=0;i<nodeList.size();i++){
    			if(nodeList.get(i).selected){
    				removeAllEdgesFromNode(nodeList.get(i));
    				break;
    			}
    		}
    		
    	}
    	else if(totalSelected==2){
    		for(int i=0;i<nodeList.size();i++){
    			if(nodeList.get(i).selected){
    				if(selectCount==0){
    					index1=i;
    					selectCount+=1;
    				}
    				else{
    					index2=i;
    					for(int j=0;j<edgeList.size();j++){
    			    		if(edgeList.get(j).containsNode(nodeList.get(index1)) &&
    			    				edgeList.get(j).containsNode(nodeList.get(index2))){
    			    			nodeList.get(index1).removeEdge(edgeList.get(j));
    			    			nodeList.get(index2).removeEdge(edgeList.get(j));
    			    			edgeList.remove(j);
    			    			break;
    			    		}
    			    	}
    		    		break;
    				}
    			}
    		}
    	}
    }
    public void removeAllEdgesFromNode(Node n){
    	
    	System.out.println(n.getEdgeList().size());
		for(int i=0;i<n.getEdgeList().size();i++)
		{
			edgeList.remove(n.getEdgeList().get(i));
			
			for(int j=0;j<nodeList.size();j++){
				if(!nodeList.get(j).selected){
					System.out.println(j);
					nodeList.get(j).removeEdge(n.getEdgeList().get(i));
				}
	    	}
			
		}
		System.out.println(" check it");
		n.getEdgeList().clear();
    }
    public ArrayList<Edge> getEdges(){
    	return edgeList;
    }
    public int getSize(){
    	return nodeList.size();
    }
    public Node getNode(int index){
    	return nodeList.get(index);
    }
    public void deleteSelected(){
    	for(int i=nodeList.size()-1;i>-1;i--){
			if(nodeList.get(i).selected){
				removeFromNeighborLists(nodeList.get(i));
				removeAllEdgesFromNode(nodeList.get(i));
				nodeList.remove(i);
				}
		}
    	for(int i=0;i<nodeList.size();i++){
    		updateNeighborList(nodeList.get(i));
    	}
    	totalSelected=0;
		nodeSelected=false;
    }
    public Node getHoverNode(){
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).hoverOver)
    			return nodeList.get(i);
    	}
    	return null;
    }
    public Node getNodeAt(Location nodeLoc){
    	for(int i=0;i<nodeList.size();i++){
    		if(nodeList.get(i).getLoc().distanceTo(menuLocation)<nodeSize)
    			return nodeList.get(i);
    	}
    	return null;
    }
    public void setHoveredTo(String option){
    	Node n = getNodeAt(menuLocation);
    	if(n !=null){
	    	if(option.equals(GROUND)){
	    		n.setGrounded(true);
	    	}
	    	else if(option.equals(NONE)){
	    		n.setNormal();
	    	}
	    	else if(option.equals(SOURCE)){
	    		n.setSourced(true);
	    	}
    	}
    }
    public void setMenuLocation(Location menuLoc){
    	menuLocation = menuLoc;
    	System.out.println(menuLoc.x+", "+menuLoc.y);
    }
    public int getNeighborListSize(Node n){
    	return n.getNeighborList().size();
    }
    public void removeNodesAtLayer(int layer){
		for(int i=nodeList.size()-1;i>-1;i--){
			if(nodeList.get(i).getLayer()==layer)
				nodeList.remove(i);
		}
	}
    public void sourceLeft(){
    	double xMax = -Double.MAX_VALUE;
    	for(Node n : nodeList){
    		if(n.selected && n.x > xMax){
    			xMax=n.x;
    		}
    	}
    	
    	for(Node n :nodeList){
    		if(n.x+10<=xMax){
    			n.setSourced(true);
    		}
    	}
    }
    public void groundRight(){
    	double xMin = Double.MAX_VALUE;
    	for(Node n : nodeList){
    		if(n.selected && n.getLoc().x<xMin){
    			xMin=n.getLoc().x;
    		}
    	}
    	for(Node n :nodeList){
    		if(n.getLoc().x-10>=xMin)
    			n.setGrounded(true);
    	}
    }
    public void clearAllNodes(){
    	for(Node n:nodeList){
    		n.setGrounded(false);
    		n.setSourced(false);
    	}
    }
    public void sourceNode(){
    	for(Node n:nodeList){
    		if(n.selected)
    			n.setSourced(true);
    	}
    }
    public void groundNode(){
    	for(Node n:nodeList){
    		if(n.selected)
    			n.setGrounded(true);
    	} 	
    }
    public void clearNode(){
    	for(Node n:nodeList){
    		if(n.selected){
    			n.setGrounded(false);
    			n.setSourced(false);
    		}
    	}
    }

}