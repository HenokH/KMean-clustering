package KMean;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class CountingResult{
	
	int error;
	Map<String,Integer> trueCount= new HashMap<String,Integer>();//actual true classification as provided by the trainer
	Map<String,Integer> predictedforPrecision=new HashMap<String,Integer>();
	Map<String,Map<String,Integer>> confusionMatrix= new HashMap<String,Map<String,Integer>>();//for a particular sample
	FeatureNameAndValues2 featuresAndValues2=new FeatureNameAndValues2() ;

	CountingResult(){
		
		error=0;
		//initialize temporary counting result storage(which is a hashmap of actual outcome vs predicted ones) MAP<Actualclassification,<predicted classification,count>>
		//all to 0#
		for(String outputvalues:featuresAndValues2.output){
			Map<String,Integer> predicted= new HashMap<String,Integer>();
			for(String predictedOutputvalues:featuresAndValues2.output){
				predicted.put(predictedOutputvalues, 0);
			}
			confusionMatrix.put(outputvalues, predicted);
			trueCount.put(outputvalues, 0);
			predictedforPrecision.put(outputvalues, 0);
		}// end of intialize loop
	}
}

/**
 * @author henok
 *Trains the native Bayes Classifier  
 *Testes the native Bayes Classifier
 */

public class Trainer{
	
	FeatureNameAndValues2 featuresAndValues;
	List<List<String>> cars;             // training data
	int trainingDataSize,testingDataSize,totalDataSize;
	int numberOfClusters;
	
	
	public Trainer(CarData cardata, int kValue){
			cars= cardata.getInstanceData();
			totalDataSize=cardata.getNumberOfRow();
			featuresAndValues=new FeatureNameAndValues2();
			numberOfClusters=kValue;
		}
	
	
	
	public Map<Integer,double[]>	generateRandomCenters(){
		
		Map<Integer,double[]> allRandomCenters=new HashMap<Integer,double[]>();
		Random random=new Random();

		for(int i=0;i<numberOfClusters;i++){
			double[] randomCenter=new double[6];
			for(int j=0;j<6;j++){
			
				int columnMaxValue=featuresAndValues.AttributeValues.get(featuresAndValues.columnname.get(j)).size();
				double randomMax=(double)columnMaxValue+0.1;
				int randomMin=0;
				double result;
				do{
					double randomValue = random.nextDouble();
					result = randomMin + (randomValue * (randomMax - randomMin));
				}while(result>=(double)columnMaxValue);
				
				randomCenter[j]=result;
			}
			
			allRandomCenters.put(i, randomCenter);
			}
		return allRandomCenters; 
	}
	
	
	public void groupInstances(){
		int kclusters=numberOfClusters;
		Map<Integer,double[]> centeroids=generateRandomCenters();
		Map<Integer,List<Integer>> oldClusterCollections;
		Map<Integer,List<Integer>> newClusterCollections=new HashMap<Integer,List<Integer>>();
		Map<Integer,String> clusterClassification=new HashMap<Integer,String>();
		
		do {
			
			oldClusterCollections=new HashMap<Integer,List<Integer>>(newClusterCollections);

			for(int intialiyecenterI=0;intialiyecenterI<kclusters;intialiyecenterI++){
				
				List<Integer> thisClusterCollection=new ArrayList<Integer>();
				newClusterCollections.put(intialiyecenterI, thisClusterCollection);
			}
			
			for(int dataRow=0;dataRow<cars.size();dataRow++ ){
				
				int[] instanceNumerical=nominalToNumerical(dataRow);
				double distanceTocenter=0;
				int closestCenter=0;
				
				for(int kCenters= 0;kCenters<numberOfClusters;kCenters++){
					if(kCenters==0){
						distanceTocenter=distanceCalculate(instanceNumerical,centeroids.get(kCenters));
						closestCenter=kCenters;
					}
					else
						if(distanceTocenter>distanceCalculate(instanceNumerical,centeroids.get(kCenters))){
							distanceTocenter=distanceCalculate(instanceNumerical,centeroids.get(kCenters));
							closestCenter=kCenters;
						}
				}
				
				newClusterCollections.get(closestCenter).add(dataRow);
			}
			// get new cluster centers
			centeroids=calculateNewCenters(newClusterCollections);
		}while(!oldClusterCollections.equals(newClusterCollections));
		

		CountingResult countingResult=new CountingResult();
		int errorcount=0;
		for(int cluster:newClusterCollections.keySet()){
			String classificationOutput=determineClusterClassification(newClusterCollections.get(cluster));
			
			clusterClassification.put(cluster, classificationOutput);
			for(int i=0;i<newClusterCollections.get(cluster).size();i++){
				if(!classificationOutput.equals(cars.get(newClusterCollections.get(cluster).get(i)).get(6))){
					errorcount+=1;
				}
				countingResult=addToconfusionMatrix(newClusterCollections.get(cluster).get(i), classificationOutput, countingResult);

			}
		}
		//summerize classification result 
		for(int cluster:newClusterCollections.keySet()){
			
		    DecimalFormat df = new DecimalFormat("0.000");
			System.out.println("Cluster " +(cluster+1));
			System.out.print("   Centroid Location : ");
			for(int i=0;i<6;i++){
				System.out.print( featuresAndValues.columnname.get(i)+"="+df.format(centeroids.get(cluster)[i])+" , ");
			}
			int actualUnacc=0;
			int actualacc=0; int actualgood=0; int actualVgood=0;;
			 
			
			for(int j=0;j<newClusterCollections.get(cluster).size();j++){
				if(cars.get(newClusterCollections.get(cluster).get(j)).get(6).equals("unacc")){actualUnacc+=1;}
				if(cars.get(newClusterCollections.get(cluster).get(j)).get(6).equals("acc")){actualacc+=1;}
				if(cars.get(newClusterCollections.get(cluster).get(j)).get(6).equals("good")){actualgood+=1;}
				if(cars.get(newClusterCollections.get(cluster).get(j)).get(6).equals("vgood")){actualVgood+=1;}

			}
			
			System.out.println();
			System.out.println( "   Total instances in this cluster : "+newClusterCollections.get(cluster).size());
			System.out.println( "      Of which : "+" Actual unacc: "+actualUnacc+" Actual acc: "+actualacc+" Actual good: "+actualgood+" Actual vgood: "+actualVgood);
			System.out.println("   Cluster classified as  :"+clusterClassification.get(cluster));
			System.out.println("-------------------------------------");
		}

		System.out.println("==============================================");
		System.out.println("Error Rate = "+(double)errorcount/cars.size());
		System.out.println("==============================================");

		drawConfusionMatrix(countingResult);

		return;	
	}
	
	
	
	 public Map<Integer,double[]>	calculateNewCenters(Map<Integer,List<Integer>> newClusterCollections){
		 
		Map<Integer,double[]> newCenters=new HashMap<Integer,double[]>();
		
		for(int i=0;i<newClusterCollections.size();i++){
			
			double[] newCenter=new double[6];
			for(int j=0;j<newClusterCollections.get(i).size();j++){
				
				int inclusterDataRow=newClusterCollections.get(i).get(j);
				int[] instanceNumerical=nominalToNumerical(inclusterDataRow);
				for(int k=0;k<6;k++){
					
					newCenter[k]=newCenter[k]+(double)instanceNumerical[k];
				}
			}
			for(int k=0;k<6;k++){
				newCenter[k]=(double)newCenter[k]/(double)newClusterCollections.get(i).size();

			}
			newCenters.put(i, newCenter);
		}
		return newCenters; 
	}
		
	 
	 public int[] nominalToNumerical(int dataRow){
		 
		 int[]  nominalValues= new int[6];
		 for(int j=0;j<6;j++){
			 List<String> possibleColumnValues=featuresAndValues.AttributeValues.get(featuresAndValues.columnname.get(j));
			 int indexAsNumValue=possibleColumnValues.indexOf(cars.get(dataRow).get(j));
			 nominalValues[j]= indexAsNumValue;
		}
		return nominalValues;
	}
	
	
	
	public double distanceCalculate(int[] instanceNumerical,double[] centerNumerical){
		
		double distance=0;
		
		for(int i=0;i<6;i++){
			
			double differnce=(double)instanceNumerical[i]-centerNumerical[i];
			double diffencesquare=Math.pow(differnce, 2);
			distance += diffencesquare;
		}
		distance=Math.pow(distance,0.5);
		
		return distance;
	}

	
	
	
	public String determineClusterClassification(List<Integer> clusterCollection){

		String highestOccuranceClassification=null;
		int heighestCount=0;
		Map<String,Integer> classifcCount=new HashMap<String,Integer>();
		for(String outputName:featuresAndValues.output){
			classifcCount.put(outputName,0);
		}
		
		for(int instanceRow:clusterCollection){
		
			for(String outputName:featuresAndValues.output){
				if(cars.get(instanceRow).get(6).equals(outputName)){

					int count=classifcCount.get(outputName)+1;
					if(count>heighestCount){
						heighestCount=count;
						highestOccuranceClassification=outputName;
					}
				}
			}	
		}
		
		return highestOccuranceClassification;
	}
	
	
	
	
	public CountingResult addToconfusionMatrix(int dataLine, String predictedClass,CountingResult countingResult){
		//store count of predicted class vs true class in confusion matrix
		String trueClassification=cars.get(dataLine).get(6);
		int predictedcount=countingResult.confusionMatrix.get(trueClassification).get(predictedClass);
		predictedcount=predictedcount+1;
		int totalforPrecision=countingResult.predictedforPrecision.get(predictedClass);
		totalforPrecision=totalforPrecision+1;
		countingResult.predictedforPrecision.put(predictedClass, totalforPrecision);
		int tempTrueCount=countingResult.trueCount.get(trueClassification)+1;
	
		countingResult.trueCount.put(trueClassification,tempTrueCount);
		countingResult.confusionMatrix.get(trueClassification).put(predictedClass,predictedcount);

		//check and count if predicted classification is wrong
		if(!predictedClass.equals(trueClassification)){
			countingResult.error=countingResult.error+1;
		}
		return countingResult;
	}
	

	public void drawConfusionMatrix(CountingResult countingResult){
		int tableFormater=0;
		List<String> order=Arrays.asList("unacc", "acc", "good","vgood");
		for(String name:order){
			if(tableFormater==0){
				
				System.out.println("****************************      Confusion Matrix     *******************************");
				System.out.format("%-20s%-15s%-15s%-15s%-15s\n"," ","Classified ","Classified","Classified","Classified");
				System.out.format("%-20s","");

				for(String predictedname:order){
					System.out.format("%-15s","As>> "+predictedname);
				}
				
				System.out.format("%-15s\n","Total");
				tableFormater++;
			}
			System.out.format("%-20s","Actual "+name);
			
			for(String predictedname:order){
				System.out.format("%-15s",countingResult.confusionMatrix.get(name).get(predictedname));
			}
			
			System.out.format("%-15s%.2f\n",countingResult.trueCount.get(name)+" && Recall >> ",(double)countingResult.confusionMatrix.get(name).get(name)/countingResult.trueCount.get(name));
		
		}
		System.out.format("%-20s","Precision ");

		for(String name:order){
			double precision;
			if(countingResult.predictedforPrecision.get(name)!=0){
			 precision=(double)countingResult.confusionMatrix.get(name).get(name)/countingResult.predictedforPrecision.get(name);
			System.out.format("%.2f%-11s",precision,"");

		}
		else {String precision2="undefined";
			System.out.format("%-15s",precision2,"");}

		}
		
		System.out.println("\n----------------------------------------------------------------------------------" );
	}
}




