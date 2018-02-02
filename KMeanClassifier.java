package KMean;



import java.util.Scanner;

public class KMeanClassifier {

	public static void main(String[] args)   {
		
		DataRead datafile=new DataRead();
		CarData cardata=datafile.dataRead();
		
		//code to introduce noise on the the dataset
        System.out.println("Enter k value  or to close type '0' to exit ");
        System.out.println("Enter k value  :  ");

          Scanner scanner = new Scanner( System.in );
          int kvalue=scanner.nextInt();
          while(kvalue!=0) {
        	    // operate

          Trainer trainer=new Trainer(cardata,kvalue);
          trainer.groupInstances();
          System.out.println("Enter k value  or type '0' when finished :  ");
          kvalue=scanner.nextInt();

          }
          scanner.close();
				
	}

}

