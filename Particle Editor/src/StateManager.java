import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JCheckBox;


public class StateManager {
	public boolean editor, selection,helpPage;
	public final String EDIT= "editor";
	public final String SELECT="selection";
	public final String HELP ="help";
	public String previousState,packingType;
	public int currentZ = 1;
	public ArrayList<Integer> viewableLayers = new ArrayList<Integer>();
	public ArrayList<JCheckBox> cbList;
	public NodeManager nm;
	
	public StateManager(NodeManager nodeManager){
		editor=true;
		selection=false;
		nm = nodeManager;
	}
	public void setState(String newState){
		editor=false;
		selection=false;
		helpPage=false;
		if(newState.compareTo(EDIT)==0){
			editor=true;
		}
		if(newState.compareTo(SELECT)==0){
			selection=true;
		}
		if(newState.compareTo(HELP)==0){
			if(newState.compareTo(SELECT)==0)
				previousState=SELECT;
			else if(newState.compareTo(EDIT)==0)
				previousState=EDIT;
			helpPage=true;
		}
	}
	public void setPackingType(String type){
		packingType = type;
	}
	public String getPackingType(){
		return packingType;
	}
	public void toggleHelpPage(){
		if(helpPage){
			if(previousState.compareTo(EDIT)==0){
				editor=true;
			}
			else if(previousState.compareTo(SELECT)==0){
				selection=true;
			}
			helpPage=false;
		}
		else{
			if(selection)
				previousState=SELECT;
			else if(editor)
				previousState=EDIT;
			helpPage=true;
			editor=false;
			selection=false;
		}
		
	}
	public void toggleSelection(){
		if(selection || editor){
			if(selection)
				editor=true;
			else
				editor=false;
			selection=!selection;
		}
	}
	public void setCurrentZ(int newZ){
		currentZ = newZ;
		for(int i = 0;i<cbList.size();i++){
			cbList.get(i).setForeground(null);
		}
		cbList.get(currentZ-1).setForeground(Color.RED);
		nm.updateGhostLayer(currentZ);
		nm.clearNodeSelection();
	}
	public void updateVisibleLayers(ArrayList<JCheckBox> checkBoxList){
		cbList = checkBoxList;
		viewableLayers.clear();
		for(int i = 0;i<cbList.size();i++){
			if(checkBoxList.get(i).isSelected()){
				String checkBoxLabel = cbList.get(i).getText();
				String layerNumber = checkBoxLabel.substring(6,checkBoxLabel.length());
				viewableLayers.add(Integer.parseInt(layerNumber));
			}
		}
	}
	public int inCurrentLayers(int targetLayer){
		//method returns 2 if the targetLayer does not exist in the viewable layers. Otherwise, returns 0 if targetLayer 
		//is the same as the current view. Returns 1 if above current Layer, returns -1 if below;
		for(int i=0;i<viewableLayers.size();i++){
			if(targetLayer == viewableLayers.get(i)){
				if(targetLayer==currentZ) 
					return 0;
				else if(targetLayer>currentZ)
					return 1;
				else if(targetLayer<currentZ)
					return -1;
			}
		}
		return 2;
	}
	public boolean isCurrentLayerVisible(){
		return viewableLayers.contains(currentZ);
	}
	public boolean isLayerVisible(int targetLayer){
		return viewableLayers.contains(targetLayer);
	}
	public ArrayList<Integer> getVisibleLayers(){
		return viewableLayers;
	}
	public boolean isAdjacentLayer(Node a, Node b){
		return Math.abs(a.getLayer()-b.getLayer())<=1;	
	}
	public void setVisible(int layer){
		cbList.get(layer-1).setSelected(true);
	}
	public void viewUpperLevels(boolean shiftUp){
		
	}
	public void viewLowerLevels(boolean shiftUp){
		
	}

}
