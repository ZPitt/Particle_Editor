import java.awt.Dimension;
import java.awt.Point;

public class ViewPort
{
	public float z =1.0f;
	public Dimension vpSize,relSize;
	public double wid,hei,focalX,focalY,w, h;
	public int originX,originY;
	public Point screenCenter,origin;
	public Location focus,dragState;
	public double zoom =1;
	public double dragX=0;
	public double dragY=0;
	public ViewPort(Dimension dimension)
	{
		focalX = 0;//where the focus is in world coordinates
		focalY = 0;
		vpSize=dimension;//size of viewport in pixels
		relSize=dimension;//size in terms of world coordinates
		w = dimension.getWidth();//dimensions of viewport in world Coordinates
		h = dimension.getHeight();
		wid = w;
		hei = h;
		originX = (int)w/2;//initiating origin in viewport coordinates
		originY = (int)h/2;
		focus = new Location(0,0);//where center of screen is in world Coordinates
		origin = new Point(0,0);
	}
	public Point getCenter()
	{	//Returns center of screen in VP pixels
		return screenCenter;
	}
	public void setSize(Dimension viewportSize)
	{
		vpSize = viewportSize;
		relSize=viewportSize;
		updateBounds();
	}
	public void updateOriginToCurrent()
	{
		screenCenter = new Point(vpSize.width/2,vpSize.height/2);
	}
	public void updateFocus(Point current)
	{
		Location newCurrent = convertToWorld2(current);
		dragX = newCurrent.x-dragState.x;
		dragY = newCurrent.y-dragState.y;
		focus.x-=dragX;
		focus.y-=dragY;
		dragState.x = (int) (newCurrent.x-dragX);
		dragState.y = (int) (newCurrent.y-dragY);
	}
	public void setDownWorldCoord(Point baseDown)
	{
		dragState=convertToWorld2(baseDown);
	}
	public void incrementZoom(double zoomAmount)
	{
		zoom+=zoomAmount;
		if(zoom>3.0)
			zoom=3.0;
		else if(zoom<0.1)
			zoom=0.1;
		
		updateBounds();
	}
	public Dimension getVPSize(){
		return vpSize;
	}
	public Dimension getBounds(){
		return relSize;
	}
	public Location getWorldLoc(){
		//returns the world coordinate of the upper left corner of viewport
		return new Location(focus.x-relSize.width/2,focus.y-relSize.height/2);
	}
	public Point getVPOrigin(){
		//returns Viewport location of the origin
		return convertToViewport2(new Location(0,0));
	}
	public void updateBounds(){
		w = vpSize.getWidth();//dimensions of viewport in world Coordinates
		h = vpSize.getHeight();
		wid= w*zoom;
		hei =h*zoom;
		relSize = new Dimension((int)Math.round(wid),(int)Math.round(hei));
	}
	public Location getFocus()
	{
		// returns focus in world coordinates
		return focus;
	}
	public Point getVPFocus()
	{
		return convertToViewport2(focus);
		
	}
	public double getZoom()
	{
		return zoom;
	}
	
	public Point convertToViewport2(Location loc){
		return new Point((int)(Math.round((double)(loc.x-focus.x+relSize.width/2)/(double)relSize.width*vpSize.width)),
				(int)(Math.round((double)(loc.y-focus.y+relSize.height/2)/(double)relSize.height*vpSize.height)));
  
    }
	public Location convertToWorld2(Point p){
		//returns location in (double,double) format
		double xRatio = (double)p.x/vpSize.getWidth();
    	double yRatio = (double)p.y/vpSize.getHeight();
    	double worldX = (relSize.width*xRatio)+getWorldLoc().x;
    	double worldY = (relSize.height*yRatio)+getWorldLoc().y;
    	return new Location(worldX,worldY);
		
	}
	public Point convertSize(Location loc){
		return new Point((int)(Math.round(loc.x/zoom)),(int)(Math.round(loc.y/zoom)));
	}
	public int convertSize(int targetSize){
		return (int)(Math.round(targetSize/zoom));
	}
}

