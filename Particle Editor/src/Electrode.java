
public class Electrode extends Node {
	public String type;
	
	public Electrode(double xLoc, double yLoc, int layer) {
		super(xLoc, yLoc, layer);
	}
	public void  setElectrode(String electrodeType){
		type = electrodeType;
	}
	

}
