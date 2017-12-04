

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CsvFileWriter {
	
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	//CSV file header
	private static final String FILE_HEADER = "";

	public void writeCsvFile(String fileName,ArrayList<Edge> eList,ArrayList<Node> nList) {
	
		FileWriter fileWriter = null;
				
		try {
			fileWriter = new FileWriter(fileName);
			//Write the CSV file header
			//fileWriter.append(FILE_HEADER.toString());
			
			//Add a new line separator after the header
			//fileWriter.append(NEW_LINE_SEPARATOR);
			
			//have first rows dedicated to the sourcing nodes
			int gCount=0;
			int sCount=0;
			for(Node n : nList){
				if(n.sourced)
					sCount+=1;
				if(n.grounded)
					gCount+=1;
			}
			//first line tells the MATLAB program where to read the csv file
			int totalEdges = eList.size()+sCount+gCount;
			int totalNodes = nList.size()+1; //+1 for the "source node"
			fileWriter.append(Integer.toString(totalEdges));
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(Integer.toString(totalNodes));
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			for(int i=0;i<sCount;i++){
				fileWriter.append(Integer.toString(-1));
				fileWriter.append(COMMA_DELIMITER);
				boolean lineDone = false;
				for(Node n : nList){
					if(n.sourced && !n.sourceSaved && !lineDone){
						fileWriter.append(Integer.toString(1));
						fileWriter.append(COMMA_DELIMITER);
						n.savedSource();
						lineDone=true;
					}
					else{
						fileWriter.append(Integer.toString(0));
						fileWriter.append(COMMA_DELIMITER);
					}
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			//next rows are for adding the columns with the ground
			for(int i=0;i<gCount;i++){
				boolean lineDone = false;
				fileWriter.append(Integer.toString(0));
				fileWriter.append(COMMA_DELIMITER);
				for(Node n : nList){
					if(n.grounded && !n.groundSaved && !lineDone){
						fileWriter.append(Integer.toString(-1));
						fileWriter.append(COMMA_DELIMITER);
						n.savedGround();
						lineDone=true;
					}
					else{
						fileWriter.append(Integer.toString(0));
						fileWriter.append(COMMA_DELIMITER);
					}
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			//Write the topology matrix into the fileWriter
			int topo = -1;
			for(Edge e : eList){
				fileWriter.append(Integer.toString(0)); // 0 for the "source node"
				fileWriter.append(COMMA_DELIMITER);
				for(Node n : nList){
					if(e.containsNode(n)){
						fileWriter.append(Integer.toString(topo));
						fileWriter.append(COMMA_DELIMITER);
						topo=topo*(-1);
					}
					else{
						fileWriter.append(Integer.toString(0));
						fileWriter.append(COMMA_DELIMITER);
					}
					n.resetSaves();
				}
				if(topo==1)
					System.err.println("TOO MANY OR TOO FEW NODES MATCH THIS EDGE:"+eList.indexOf(e));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			//second part writes the x,y,z of each node for rendering purposes
			for(int i=0;i<3;i++){
				fileWriter.append(Integer.toString(0));
				fileWriter.append(COMMA_DELIMITER); //0 coordinates for the "source node"
				for(Node n:nList){
					if(i==0)
						fileWriter.append(Double.toString(n.loc.x));
					else if(i==1)
						fileWriter.append(Double.toString(n.loc.y));
					else
						fileWriter.append(Double.toString(n.getZ()));
					fileWriter.append(COMMA_DELIMITER);
				}
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			System.out.println("CSV file was created successfully !!!");
			
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
			}
			
		}
	}
}