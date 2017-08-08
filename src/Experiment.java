import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Experiment {
	
	public static void main(String[] args) {
		int limitSize[] = { 1,2,4,8,16,32,64,128,256,512,1024,2048,4096, 8192,16384,32768}; //KB
		String dataPath[] = {
				"/Users/YuCheng/Documents/access_log/fish_19.txt",
				//"/Users/YuCheng/Documents/access_log/500_14.txt",
				//"/Users/YuCheng/Documents/access_log/boyu_16.txt",
			};
			
			//int day[] = {19, 14, 16};
			int day[] = {19};
		for(int Size : limitSize){	
			Double costtime = Double.valueOf(0), tradcosttime = Double.valueOf(0),
					costenergy = Double.valueOf(0),tradcostenergy = Double.valueOf(0),
					requesttime = Double.valueOf(0),tradrequesttime = Double.valueOf(0),
					responsetime = Double.valueOf(0),tradresponsetime = Double.valueOf(0);
			for(int i = 0; i < dataPath.length; i++){
				TrainingModel trainingModel = 
						new TrainingModel(dataPath[i], Long.valueOf(-1), day[i], false);
		
				Scenario scn = new Scenario(
						dataPath[i], 
						trainingModel.getRelatedFileTable(), 
						trainingModel.getFileList(), 
						trainingModel.getTrainingLine(),
						trainingModel.file2Size()) ;
				
				costtime +=scn.getCost_time();
				tradcosttime += scn.getTradcost_time();
				costenergy += scn.getCost_energy();
				tradcostenergy += scn.getTradcost_energy();
				requesttime += scn.getRequest_time();
				tradrequesttime += scn.getTradrequest_time();
				responsetime += scn.getResponse_time();
				tradresponsetime += scn.getTradresponse_time();
			}
			System.out.println(costtime+"  "+tradcosttime);
			System.out.println(costenergy+"  "+tradcostenergy);
			System.out.println(requesttime+"  "+tradrequesttime);
			System.out.println(responsetime+"  "+tradresponsetime);
			System.out.println("===========================");
			System.out.println(costtime/tradcosttime);
			System.out.println(costenergy/tradcostenergy);
			System.out.println(requesttime/tradrequesttime);
			System.out.println(responsetime/tradresponsetime);
			System.out.println("===========================");
		}
	}
	
	
	
	/*
	public static void main(String[] args) {

		
		String dataPath[] = {
			"/Users/YuCheng/Documents/access_log/fish_19.txt",
			"/Users/YuCheng/Documents/access_log/500_14.txt",
			"/Users/YuCheng/Documents/access_log/boyu_16.txt",
		};
		
		int day[] = {19, 14, 16};
		Statistics sta;
		int fileNumber = 0;;
		double avgSize = 0, sizeStdDev = 0, fileStdDev = 0, 
				accessProb = 0, screenProb = 0, sizeMedian = 0;
		Long avgAccessPeriod = Long.valueOf(0), avgScreenPeriod = Long.valueOf(0);
		int fileNumberOfDays[][] = new int[ dataPath.length][];
		
		
		for(int i = 0; i < dataPath.length; i++){
			sta = new Statistics(dataPath[i], day[i]);
			
			fileNumber += sta.getFileNumber();
			//System.out.println("file number : "+sta.getFileNumber());
			
			avgSize += sta.getAvgSize(); 
			//System.out.println("file avg size : "+sta.getAvgSize());
			
			sizeStdDev += sta.getSizeStdDev(); 
			//System.out.println(sta.getSizeStdDev());
			
			sizeMedian += sta.getSizeMedian(); 
			//System.out.println(sta.getSizeMedian());
			
			fileStdDev += sta.getFileStdDev();
			//System.out.println("file std dev : "+sta.getFileStdDev());
			
			avgAccessPeriod += sta.getAvgAccessPeriod();
			//System.out.println("avg access period : "+sta.getAvgAccessPeriod());
			
			accessProb += sta.getAccessProb();
			//System.out.println("access prob : "+sta.getAccessProb());
			
			avgScreenPeriod += sta.getAvgScreenPeriod(); 
			//System.out.println(sta.getAvgScreenPeriod());
			
			screenProb += sta.getScreenProb();
			//System.out.println(sta.getScreenProb());
			
			
			fileNumberOfDays[i] = sta.getFileNumberOfDays();

		}
		SimpleRegression regression = new SimpleRegression();
		
		int total = 0;
		for(int[] FND : fileNumberOfDays){
			total = 0;
			for(int i = 0; i < FND.length; i++){
				total += FND[i];
				regression.addData(i,total);
			}
		}
		
		
		fileNumber /= dataPath.length;
		//System.out.println("file number : "+fileNumber);
		avgSize /= (double)dataPath.length;
		sizeStdDev /= (double)dataPath.length;
		sizeMedian /= (double)dataPath.length;
		//System.out.println("sizeMedian : "+sizeMedian);
		fileStdDev /= (double)dataPath.length;
		accessProb /= (double)dataPath.length;
		//System.out.println("access prob : "+accessProb);
		screenProb /= (double)dataPath.length;
		//System.out.println("screen prob : "+screenProb);
		avgAccessPeriod /= Long.valueOf(dataPath.length);
		//System.out.println("avg access period : "+avgAccessPeriod);
		avgScreenPeriod /= Long.valueOf(dataPath.length);
		//System.out.println("avg screen period : "+avgScreenPeriod);
		//System.out.println("--------------------");
		
		//int simDays[] = {14,21,28,35,42,49,56,63,70,77};
		int simDays[] = {8,9,10,11,12,13,14,15,16,17};
		for(int simDay : simDays){
			
			String outputFile = "/Users/YuCheng/Documents/output/op.txt";
			File file = new File(outputFile);
			if(file.exists()){
				file.delete();
				}
			
			Long simMs = Long.valueOf(simDay) * Long.valueOf(24) * Long.valueOf(3600) * Long.valueOf(1000);
			Double predict = regression.predict(simDay);
			//System.out.println("predict file number : "+predict.intValue());
			
			Simulation sim = new Simulation(
					//simMsTime, //61 days
					simMs,
					//fileNumber,
					predict.intValue(),
					//8000,
					sizeMedian,
					//avgSize, sizeStdDev,
					fileStdDev,
					avgAccessPeriod, accessProb,
					avgScreenPeriod, screenProb,
					outputFile);
					
			//String outputFile = "/Users/YuCheng/Documents/output/op.txt";
			//String outputFile = "/Users/YuCheng/Documents/access_log/fish_19.txt";
			
			TrainingModel trainingModel = new TrainingModel(outputFile, Long.valueOf(-1), simDay, true);
			
			Scenario scn = new Scenario(
					outputFile, 
					trainingModel.getRelatedFileTable(), 
					trainingModel.getFileList(), 
					trainingModel.getTrainingLine(),
					trainingModel.file2Size()) ;
			
			System.out.println("---------  "+simDay+"  ---------");
			System.out.println(scn.getCost_time());
			System.out.println(scn.getTradcost_time());
			System.out.println(scn.getCost_energy());
			System.out.println(scn.getTradcost_energy());
			System.out.println(scn.getRequest_time());
			System.out.println(scn.getTradrequest_time());
			System.out.println(scn.getResponse_time());
			System.out.println(scn.getTradresponse_time());
			
			
			
			
			
			
		}
		
		
	}*/
}
