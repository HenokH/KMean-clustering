package KMean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author henok
 * Class definning the differnt columns and values in the data file
 * @columnname: defines which attribute is represented by each column
 * @attributevalue : defines possible values of each attribute 
 * @output defines possible values of classification
 *
 */
public class FeatureNameAndValues2 {
	public   final List<String> output= Arrays.asList("unacc", "acc", "good","vgood");
	public   final Map<Integer, String> columnname= new HashMap<Integer, String>();
	   {	
		   columnname.put(0,"buying");
		   columnname.put(1,"maint");
		   columnname.put(2,"doors");
		   columnname.put(3,"persons");
		   columnname.put(4,"lug_boot");
		   columnname.put(5,"safety");
	   }
	public   final Map<String, List<String>> AttributeValues = new HashMap<String, List<String>>();	    
	    {
	    	AttributeValues.put("buying", Arrays.asList("vhigh","high","med", "low"));
	    	AttributeValues.put("maint", Arrays.asList("vhigh","high","med", "low"));
	    	AttributeValues.put("doors", Arrays.asList("2","3","4","5more"));
	    	AttributeValues.put("persons", Arrays.asList("2","4","more"));
	    	AttributeValues.put("lug_boot", Arrays.asList("small","med","big"));
	    	AttributeValues.put("safety", Arrays.asList("low","med","high"));
	    }
}
////