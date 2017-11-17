

import java.io.FileWriter;
import java.io.IOException;

public class CsvFileWriter {
	
	//Delimiter used in CSV file
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	//CSV file header
	private static final String FILE_HEADER = "";

	public void writeCsvFile(String fileName) {
	
		FileWriter fileWriter = null;
				
		try {
			fileWriter = new FileWriter(fileName);
			//Write the CSV file header
			fileWriter.append(FILE_HEADER.toString());
			
			//Add a new line separator after the header
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			//Write the matrix into the fileWriter
			for(int i=0;i<10;i++){
				fileWriter.append(Integer.toString(0));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(Integer.toString(0));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(Integer.toString(0));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(Integer.toString(0));
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(Integer.toString(0));
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