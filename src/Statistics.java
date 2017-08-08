import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Statistics {
	
	private String dataPath;
	private ArrayList<Long> timeData, screenTime;
	private ArrayList<String> fileList;
	private HashMap<Long, String> time2File;
	private HashMap<String, Long> file2Size;
	
	private int fileNumber; 
	private double sizeMedian;
	private double avgSize;
	private double sizeStdDev;
	private double fileStdDev;
	private long avgAccessPeriod;
	private double accessProb;
	private long avgScreenPeriod; 
	private double screenProb;
	private int largest;
	private int fileNumberOfDays[];
	public Statistics(String dataPath){
		this.dataPath = dataPath;
		this.largest = 0;
		consMetadata();
		fileNumber = fileList.size();
		countAvgStdSize();
		countStdFile();
		countAvgAccessPeriodProb();
		countAvgScreenPeriodProb();
	}
	
	public Statistics(String dataPath, int largest){
		this.dataPath = dataPath;
		this.largest = largest;
		consMetadata();
		fileNumber = fileList.size();
		countAvgStdSize();
		countMedianSize();
		countStdFile();
		countAvgAccessPeriodProb();
		countAvgScreenPeriodProb();
		countFileNumberOfDays();
	}
	
	private void countFileNumberOfDays (){
		ArrayList<String> note4fileList = new ArrayList<String>(); 
		fileNumberOfDays = new int[largest];
		for(int i = 0; i < timeData.size(); i++){
			Double d = Double.valueOf(i)*Double.valueOf(largest)/
						Double.valueOf(timeData.size());
			int index = d.intValue();
			Long time = timeData.get(i);
			String file = time2File.get(time);
			if(!note4fileList.contains(file)){
				fileNumberOfDays[index]++;
				note4fileList.add(file);
			}
		}
	}
	public double getAvgSize (){
		return avgSize;
	}
	public int[] getFileNumberOfDays (){
		return fileNumberOfDays;
	}
	public double getSizeStdDev(){
		return sizeStdDev;
	}
	public double getFileStdDev(){
		return fileStdDev;
	}
	
	public long getAvgAccessPeriod(){
		return avgAccessPeriod;
	}
	public double getAccessProb(){
		return accessProb;
	}
	public long getAvgScreenPeriod(){
		return avgScreenPeriod;
	}
	public double getScreenProb(){
		return screenProb;
	}
	
	
	private void countAvgScreenPeriodProb(){
		//int largest = 19;
		Long timeStart = screenTime.get(0);
		int size = screenTime.size();
		avgScreenPeriod = (screenTime.get(size - 1) - screenTime.get(0));
		//System.out.println(avgAccessPeriod);
		ArrayList<Long> sortLargest = new ArrayList();
		for(int i = 1; i < size; i++){
			long temp = screenTime.get(i) - screenTime.get(i-1);
			if(sortLargest.size() == 0){
				sortLargest.add(temp);
			}
			else {
				for(int j = 0; j < sortLargest.size(); j++){
					if(temp > sortLargest.get(j)){
						sortLargest.add(j,temp);
						break;
					}
					else if(j == sortLargest.size()-1){
						sortLargest.add(temp);
						break;
					}
				}
			}
			if(sortLargest.size() > largest)sortLargest.remove(largest);
		}
		for(Long t : sortLargest){
			//System.out.println(t);
			avgScreenPeriod -= t;
		}
		avgScreenPeriod /= ((size -1)-largest);
		//System.out.println(avgAccessPeriod);
				
		int temp = 0;
		int index = 0;
		for(long start = (long)0+timeStart, end = (long)avgScreenPeriod+timeStart; 
				end < screenTime.get(size -1);
				start += avgScreenPeriod, end += avgScreenPeriod){
			for(int i = index; i < size; i++){
				long t = screenTime.get(i);
				if(start <=  t && t < end){
					temp++;
					index = i;
					break;
				}	
			}	
		}
		screenProb = (double)temp / (double)(size-1);
		
	}
	
	
	
	private void countAvgAccessPeriodProb(){
		//int largest = 19;
		Long timeStart = timeData.get(0);
		int size = timeData.size();
		avgAccessPeriod = (timeData.get(size - 1) - timeData.get(0));
		//System.out.println(avgAccessPeriod);
		ArrayList<Long> sortLargest = new ArrayList();
		for(int i = 1; i < size; i++){
			long temp = timeData.get(i) - timeData.get(i-1);
			if(sortLargest.size() == 0){
				sortLargest.add(temp);
			}
			else {
				for(int j = 0; j < sortLargest.size(); j++){
					if(temp > sortLargest.get(j)){
						sortLargest.add(j,temp);
						break;
					}
				}
			}
			if(sortLargest.size() > largest)sortLargest.remove(largest);
		}
		for(Long t : sortLargest){
			//System.out.println(t);
			avgAccessPeriod -= t;
		}
		avgAccessPeriod /= ((size -1)-largest);
		//System.out.println(avgAccessPeriod);
		
		int temp = 0;
		int index = 0;
		for(long start = (long)0+timeStart, end = (long)avgAccessPeriod+timeStart; 
				end < timeData.get(size -1);
				start += avgAccessPeriod, end += avgAccessPeriod){
			for(int i = index; i < size; i++){
				long t = timeData.get(i);
				if(start <=  t && t < end){
					temp++;
					index = i;
					
					break;
				}	
			}	
		}
		accessProb = (double)temp / (double)(size-1);
		
	}
	
	
	private void countStdFile(){
		int fileAccessTimes[] = new int[fileList.size()];
		ArrayList<Integer> fileSortAccessTimes = new ArrayList<Integer>();
		for(int i = 0; i < fileAccessTimes.length; i++){
			fileAccessTimes[i] = 0;
		}
		
		for(long time: timeData){
			String file = time2File.get(time);
			//System.out.println(file);
			int index = fileList.indexOf(file);
			//System.out.println(index);
			fileAccessTimes[index]++;
		}
		
		for(int unsortTimes: fileAccessTimes){
			if(fileSortAccessTimes.size() == 0){
				fileSortAccessTimes.add(unsortTimes);
				continue;
			}
			for(int i = 0; i < fileSortAccessTimes.size(); i++){
				int compared = fileSortAccessTimes.get(i);
				
				if(unsortTimes < compared){	
					fileSortAccessTimes.add(i, unsortTimes);
					break;
				}
				else if(i == fileSortAccessTimes.size() -1){
					fileSortAccessTimes.add(unsortTimes);
					break;
				}
			}
		}
		
		
		
		
		int sum = 0;
		for(int i = fileSortAccessTimes.size() - 1; i >= 0; i--){
			sum += fileSortAccessTimes.get(i);
			
			if(sum > (((double)timeData.size())*0.95449974)){
				fileStdDev = (double)(fileSortAccessTimes.size() - 1 - i) / (double)4;
				break;
			}
		}
	
	}
	
	private void countAvgStdSize(){
		avgSize = 0;
		sizeStdDev = 0;
		for(String temp: fileList){
			long tempS = file2Size.get(temp);
			avgSize += tempS;
		}
		avgSize /= fileList.size();
		
		double var = 0;
		for(String temp: fileList){
			long tempS = file2Size.get(temp);
	         var += (Math.pow((double)(tempS-(long)avgSize),2)/(double)fileList.size());
		 }
		 sizeStdDev = Math.pow(var,0.5);
	}
	
	private void countMedianSize(){
		ArrayList<Long> sortedSize = new ArrayList<Long>();  
		for(String temp: fileList){
			long tempS = file2Size.get(temp);
			if(sortedSize.size() == 0){
				sortedSize.add(tempS);
			}
			else{
				for(int i = 0; i < sortedSize.size(); i++){
					if(tempS <= sortedSize.get(i)){
						sortedSize.add(i, tempS);
						break;
					}
					else if(i == sortedSize.size() -1){
						sortedSize.add(i, tempS);
						break;
					}
				}
			}
		}
		sizeMedian = sortedSize.get(fileList.size()/2);
		//System.out.println("median = "+sizeMedian);
	}
	
	
	private void consMetadata(){
		try{
            FileReader fr = new FileReader(dataPath);
            BufferedReader br = new BufferedReader(fr);
            String oneLine;
            
            // Record every access time in access_log.txt 
            timeData = new ArrayList<Long>();
            
            screenTime = new ArrayList<Long>();
            
   
            time2File = new HashMap();
            file2Size = new HashMap();
            
            // Record the non-duplicate file name in access_log.txt
            //and provide a searchable file name list
            fileList = new ArrayList<String>();
            
           
            
            while((oneLine = br.readLine()) != null ){
            	
            	// if screen event
            	if(oneLine.contains("ScreenOn")){
            
            		String timeStr = oneLine.substring(0, oneLine.indexOf("_"));
            		Long time = Long.valueOf(timeStr);
            		screenTime.add(time);
            		}
            	
            	// if file access event
            	else if(!oneLine.contains("ScreenOn") && !oneLine.contains("ScreenOff")){
            		
            		//Get file Size
            		String sizeStr = oneLine.substring(oneLine.lastIndexOf("_")+1, oneLine.length());
            		Long size = Long.valueOf(sizeStr);
            		
            		
            		//Get time data
            		String timeStr = oneLine.substring(0, oneLine.indexOf("_"));
            		Long time = Long.valueOf(timeStr);
            		timeData.add(time);
            		
            		
            		
            		// Get file name
            		String fileName = oneLine.replace(timeStr+"_", "");
            		fileName = fileName.replace("_"+sizeStr, "");
            		
            		if(!fileList.contains(fileName)){
            			fileList.add(fileName);
            			file2Size.put(fileName,size);
            		}
            		
       
            		time2File.put(time, fileName);
            	}
            	
            };
            br.close();
            fr.close();
			}catch(IOException e){
				e.printStackTrace();
			}
	
	}
	
	public int getFileNumber(){
		
		return fileNumber;
	}
	public double getSizeMedian(){
		
		return sizeMedian;
	}
	
}
