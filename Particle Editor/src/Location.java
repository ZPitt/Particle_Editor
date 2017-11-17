import java.awt.Point;

public class Location {
	public double x = 0;
	public double y = 0;
	public double z = 0;
	
	public Location(double xLoc, double yLoc)
	{
		x = xLoc;
		y = yLoc;
	}
	public Location(double xLoc, double yLoc, double zLoc){
		x = xLoc;
		y = yLoc;
		z = zLoc;
	}
	public void setZ(double newZ){
		z = newZ;
	}
	public Location(Location location){
		x = location.x;
		y = location.y;
	}
	public double distanceTo(Location point){
		return Math.pow(Math.pow(x-point.x,2)+Math.pow(y-point.y,2),0.5);
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public Point toPoint(){
		int newX = (int) Math.round(x);
		int newY = (int) Math.round(y);
		return new Point(newX,newY);
	}
	public boolean equals(Location point){
		if(x-point.x==0 && y-point.y==0){
			return true;
		}
		return false;
	}

}
