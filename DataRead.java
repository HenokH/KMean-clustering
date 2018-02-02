package KMean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author henok
 *CarData class used to store the data read from file line by line 
 *Structured using a list of a list 
 *Each inner List contains the attribute of each cars
 *Outer List contains all the cars
 */

 class CarData {
	private List<List<String>> instanceData;
	
	private int numberOfRow;
	private int numberOfColumns;

	
	/**
	 * @param data read from file
	 */
	public CarData(List<List<String>> data) {
		
		instanceData = data;
		numberOfRow = instanceData.size();
		numberOfColumns = 7;
	}

	public List<List<String>> getInstanceData() {
		return instanceData;
	}

	public int getNumberOfRow() {
		return numberOfRow;
	}

	public int getNumberOfColumns() {
		return numberOfColumns;
	}
	

}



/**
 * @author henok
 *Class to read data from car data file line by line and store it in an list of a list of strings.
 *Inner array list contains car instances where each entry into the array denotes
 *attributes values and the last entry denotes the output  
 */
public class DataRead {

	
	/**
	 * @return car data stored in CarData object
	 */
	public  CarData dataRead()  {
			List<List<String>> instanceList = new ArrayList<List<String>>();
			
			try {
				File file = new File("resources"+File.separator+"car.data");
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					List<String> instancetemp = Arrays.asList(line.split(","));
					List<String> instance=new ArrayList<String>();
					for(String str: instancetemp){
						instance.add(str);
					}
					instanceList.add(instance);
				}
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			CarData cardata =new CarData(instanceList);
			return cardata;

	}

}
