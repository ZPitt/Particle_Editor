import java.awt.Dimension;
import java.util.ArrayList;


public class Node {
	double x,y,z;
	public int layerZ;
	Location loc,dragOffset,desiredLoc;
	public boolean hoverOver, neighborToSelected,neighborToGhost,selected,grounded,sourced,sourceSaved,groundSaved;
	public int dragX,dragY;
	public Dimension size;
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	public ArrayList<Node> neighborList = new ArrayList<Node>();
	
	public Node(double xLoc, double yLoc, int layer)
	{
		loc = new Location(xLoc,yLoc);
		dragOffset = new Location(0,0);
		desiredLoc = new Location(0,0);
		x=xLoc;
		y=yLoc;
		layerZ=layer;
	}
	public void setSize(Dimension newSize){
		size = newSize;
	}
	public Dimension getSize(){
		return size;
	}
	public void addEdge(Edge newEdge){
		edges.add(newEdge);
	}
	public void removeEdge(Edge targetEdge){
	}
	public int getLayer(){
		return layerZ;
	}
	public void savedSource(){
		sourceSaved=true;
	}
	public void savedGround(){
		groundSaved=true;
	}
	public void resetSaves(){
		groundSaved=false;
		sourceSaved=false;
	}
	public Location getLoc()
	{
		return new Location(x,y);
	}
	public void setLoc(Location newLoc){
		loc = newLoc;
		x = loc.x;
		y = loc.y;
	}
	public void setHoverOver(boolean hover)
	{
		hoverOver=hover;
	}
	
	public boolean isHovered()
	{
		return hoverOver;
	}
	public void setSelected(boolean select){
		selected=select;
	}
	public Location newLocation(Location newCurrent)
	{
		desiredLoc.x = newCurrent.x-dragOffset.x;
		desiredLoc.y = newCurrent.y-dragOffset.y;
		return desiredLoc;
	}
	public void setDragOffset(Location clickPoint){
		dragOffset.x=clickPoint.x-loc.x;
		dragOffset.y=clickPoint.y-loc.y;
	}
	public void moveNode(Location finalLoc){
		loc=finalLoc;
		x=loc.x;
		y=loc.y;
	}
	public void setGrounded(boolean tf){
		grounded=tf;
	}
	public void setSourced(boolean tf){
		sourced=tf;
	}
	public void setNormal(){
		sourced=false;
		grounded=false;
	}
	public ArrayList<Node> getNeighborList(){
		return neighborList;
	}
	public void addNeighbor(Node newNeighbor){
		neighborList.add(newNeighbor);
	}
	public void setLayer(int newLayer){
		layerZ=newLayer;
	}
	public ArrayList<Edge> getEdgeList(){
		return edges;
	}

}
