import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Simulation {
	private double avgSize;
	private double sizeStdDev, fileStdDev;
	private Long accessTimes;
	private int fileNumber;
	private ArrayList<String> fileList ;
	private HashMap<String, Long> file2Size;
	private Long avgAccessPeriod, avgScreenPeriod, simMsTime;
	private double accessProb, screenProb;
	private ArrayList<Long> timeData, screenTime;
	private ArrayList<String> fileData;
	private double sizeMedian;
	private String outputFile;
	
	public Simulation(
			Long simMsTime, 
			int fileNumber, 
			double avgSize, double sizeStdDev, 
			double fileStdDev,
			Long avgAccessPeriod, double accessProb,
			Long avgScreenPeriod, double screenProb,
			String outputFile){
		
		this.fileNumber = fileNumber;
		this.simMsTime = simMsTime;
		this.accessTimes = simMsTime/avgAccessPeriod;
		this.avgSize = avgSize;
		this.fileStdDev = fileStdDev;
		this.sizeStdDev = sizeStdDev;
		this.avgAccessPeriod = avgAccessPeriod;
		this.accessProb = accessProb;
		this.avgScreenPeriod = avgScreenPeriod;
		this.screenProb = screenProb;
		this.outputFile = outputFile;
		
		genFile();
		genAccessTiming();
		genScreenTiming();
		writeRecordFile();
		
	}
	
	public Simulation(
			Long simMsTime, 
			int fileNumber, 
			double sizeMedian,
			double fileStdDev,
			Long avgAccessPeriod, double accessProb,
			Long avgScreenPeriod, double screenProb,
			String outputFile){
		
		this.fileNumber = fileNumber;
		this.simMsTime = simMsTime;
		this.accessTimes = simMsTime/avgAccessPeriod;
		this.sizeMedian = sizeMedian;
		this.fileStdDev = fileStdDev;
		
		this.avgAccessPeriod = avgAccessPeriod;
		this.accessProb = accessProb;
		this.avgScreenPeriod = avgScreenPeriod;
		this.screenProb = screenProb;
		this.outputFile = outputFile;
		
		genMFile();
		
		genAccessTiming();
		
		genScreenTiming();
		
		writeRecordFile();
		
		
	}
	
	private void writeRecordFile(){
		try {
			
			FileWriter fw = new FileWriter(outputFile,false);
			BufferedWriter bw = new BufferedWriter(fw);
			int index = 0;
			//System.out.println(timeData.size());
			for(Long tempTime : screenTime){
				for(int i = index; i < timeData.size(); i++){
					if(tempTime >= timeData.get(i)){
						String oneLine = String.valueOf(timeData.get(i))+"_"+
								fileData.get(i)+"_"+
								String.valueOf(file2Size.get(fileData.get(i)));
						bw.write(oneLine+"\n");
					}
					else {
						index = i;
						break;
					}
				}
				String oneLine = String.valueOf(tempTime)+"_ScreenOn";
				bw.write(oneLine+"\n");
			}
			for(int i = index; i < timeData.size(); i++){
				String oneLine = String.valueOf(timeData.get(i))+"_"+
						fileData.get(i)+"_"+
						String.valueOf(file2Size.get(fileData.get(i)));
				bw.write(oneLine+"\n");
			}
			bw.close();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	private void genFile(){
		Random rd = new Random();
		fileList = new ArrayList<String>();
		file2Size = new HashMap<String,Long>();
		for(int i = 0; i < fileNumber; i++){
			String fileName = String.valueOf(i)+".txt";
			fileList.add(fileName);
			double ranSize = -1;
			while(ranSize < 0){
				ranSize = rd.nextGaussian() * sizeStdDev + avgSize;
			}
					
			file2Size.put(fileName, (long)ranSize);
		}
	}
	
	private void genMFile(){
		
		fileList = new ArrayList<String>();
		file2Size = new HashMap<String,Long>();
		for(int i = 0; i < fileNumber; i++){
			String fileName = String.valueOf(i)+".txt";
			fileList.add(fileName);
			double size = sizeMedian;		
			file2Size.put(fileName, (long)size);
		}
	}
	
	private void genScreenTiming(){
		Random rd = new Random();
		screenTime = new ArrayList<Long>();
		for(Long nowTime = Long.valueOf(0); nowTime < simMsTime; nowTime +=  Long.valueOf(avgScreenPeriod)){
			int tempTimes = getPoisson(screenProb);
			for(int j = 0; j < tempTimes; j++){
				Long addTime =  nowTime + Long.valueOf(j);
				screenTime.add(addTime);
			}
		}
	}
	private void genAccessTiming(){
		timeData = new ArrayList<Long>();
		fileData = new ArrayList<String>();
		Random rd = new Random();
		
		for(Long nowTime = Long.valueOf(0); nowTime < simMsTime; nowTime +=  Long.valueOf(avgAccessPeriod)){
			int tempTimes = getPoisson(accessProb);
			for(int j = 0; j < tempTimes; j++){
					Long addTime =  nowTime+ Long.valueOf(j);
					int tempFileIndex = -1;
					while(tempFileIndex < 0 || tempFileIndex >=fileList.size()){
						tempFileIndex = (int)(rd.nextGaussian() * fileStdDev + fileNumber/2);
					}
					//int tempFileIndex = rd.nextInt(fileList.size());
					String tempFile = fileList.get(tempFileIndex);
					
					timeData.add(addTime);
					fileData.add(tempFile);
				}
			
		}
		
	}
	
	
	
	
	
	public int getPoisson(double lambda) {
		  double L = Math.exp(-lambda);
		  double p = 1.0;
		  int k = 0;

		  do {
		    k++;
		    p *= Math.random();
		  } while (p > L);

		  return k - 1;
		}
}
