import java.util.ArrayList;


public class Edge {
	public Node node1, node2;
	
	public Edge(Node n1, Node n2){
		node1=n1;
		node2=n2;
		
	}
	public void addSelfToNodes(){
		node1.addEdge(this);
		node2.addEdge(this);
	}
	public Node getFirstNode(){
		return node1;
	}
	public Node getSecondNode(){
		return node2;
	}
	public boolean containsNode(Node n){
		return (n.equals(node1) || n.equals(node2));
	}
	public Location getFirstLoc(){
		return node1.getLoc();
	}
	public Location getSecondLoc(){
		return node2.getLoc();
	}
	public int getLayer(int oneORtwo){
		if(oneORtwo == 1){
			return node1.getLayer();
		}
		else if(oneORtwo == 2)
			return node2.getLayer();
		return -1;
	}
	public boolean isVisible(ArrayList<Integer> visibleLayers){
		return (visibleLayers.contains(node1.getLayer()) && visibleLayers.contains(node2.getLayer()));
	}
	
	public boolean sameAs(Edge e){
		return ((node1.equals(e.node1) && node2.equals(e.node2))||(node1.equals(e.node2) && node2.equals(e.node1)));
	}
	public boolean containsSelectedNode(){
		return (node1.selected || node2.selected);
	}
}
