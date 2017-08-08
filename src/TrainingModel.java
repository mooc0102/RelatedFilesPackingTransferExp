import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class TrainingModel {
	private String dataPath;
	private ArrayList<Integer>[] graph;
	private int screen_on_times;
	private int trainingLine;
	private int startTrainingLine;
	private ArrayList<Long> timeData;
	private ArrayList<String> fileList;
	private HashMap<Long,String> time2File;
	private HashMap<String, Long> fileName2Size;
	private HashMap<String, Integer> fileName2Num;
	private KMeans k;
	private int[][] file2TimeIntervalTable;
	private double[][] distanceSet;
	private ArrayList<String>[] relatedFileTable;
	private int[] avgRelatedFileNumOfFile;
	private Long limitSize;
	
	private int days;
    
	

	public TrainingModel(String dataPath, Long limitSize, int days, boolean isSim){
		this.limitSize = limitSize;
		this.dataPath = dataPath;
		this.days = days;
		if(isSim){
			setTrainingLine();
		}
		else{
			setTrainingLine2();
		}
		consMetadata();
		//System.out.println("k-means start");
		this.k = new KMeans(timeData, screen_on_times);
	    this.graph = k.getGraph();
	    
		
		//System.out.println("k-means over");
	    consFile2TimeIntervalTable();
	    consDistanceSet();
	    consAvgRelatedFileNumOfFile();
	    consRelatedFileTable();
	   
	}
	
	
	private void setTrainingLine(){
		int Line = 0;
		try{
            FileReader fr = new FileReader(dataPath);
            BufferedReader br = new BufferedReader(fr);
            while(br.readLine() != null){
            	Line++;
            }
		}catch(IOException e){
			e.printStackTrace();
		}
		Double rate = (Double.valueOf(days) - Double.valueOf(7))/Double.valueOf(days);
		trainingLine = new Double(Double.valueOf(Line) * rate).intValue();
		startTrainingLine = 0;
	}
	
	private void setTrainingLine2(){
		int Line = 0;
		try{
            FileReader fr = new FileReader(dataPath);
            BufferedReader br = new BufferedReader(fr);
            while(br.readLine() != null){
            	Line++;
            }
		}catch(IOException e){
			e.printStackTrace();
		}
		Double rate = (Double.valueOf(days) - Double.valueOf(14))/Double.valueOf(days);
		
		startTrainingLine = new Double(Double.valueOf(Line) * rate).intValue();
		rate = Double.valueOf(7)/Double.valueOf(days);
		
		trainingLine = new Double(Double.valueOf(Line) * rate).intValue();
	}
	
	public int getTrainingLine(){
		
		return trainingLine;
	}
	
	
	
	// Count all distance of each file point, a set access time, to each file point and save them to a 2d array, distanceSet.
	private void consDistanceSet(){
		distanceSet = new double[fileList.size()][fileList.size()];
		for(int i = 0; i < fileList.size(); i++){
			for(int j = 0; j < fileList.size(); j++)
				distanceSet[i][j] = calDistance(file2TimeIntervalTable[i],file2TimeIntervalTable[j]);
		}
		//System.out.println("consDistanceSet over");
	}
	
	private void consRelatedFileTable(){
		relatedFileTable = new ArrayList [fileList.size()];
		//System.out.println("            Progress             ");
		//System.out.println("0 % |---------------------| 100 %");
		//System.out.print("    |");
		Double tempD = Double.valueOf(fileList.size() * 0.05);
		int round = Integer.valueOf(tempD.intValue());
		for(int i = 0; i < fileList.size(); i++){
			
			if( i % round == 0 ){
				//System.out.print("-");
			}
			relatedFileTable[i] = new ArrayList();
			for(int j = 0; j < fileList.size(); j++){
				
				// Get the index of relatedFileTable[i] should be insert 
				int result = compare(i, relatedFileTable[i], distanceSet[i][j]);	
					relatedFileTable[i].add(result, fileList.get(j));	
					
					// Limit the size of arraylist, relatedFileTable[i], 
					// to the number of avgRelatedFileNumOfFile[i]
						for(int z = relatedFileTable[i].size() - 1; 
								relatedFileTable[i].size() > avgRelatedFileNumOfFile[i]; z--)
							relatedFileTable[i].remove(z);
			}
		}
		//System.out.println("|");
		//System.out.println("consRelatedFileTable over");
	}
	
	
	// Return where should be insert in the related file sort
	
	// The parameter, fileNum, is the specified file 
	// which be count distance to all file 
	// and is the index of arraylist, fileList.
	
	// The arraylist, relatedFileList, is a file list in string arraylist 
	// which sorted by distance to all file.
	// The closer distance would be sorted less index.
	private int compare(int fileNum, ArrayList<String> relatedFileList, double distance) {
		
		//if no file in the list
		if(relatedFileList.size() == 0)
			return 0;
		
		//if smaller than or equal to the least one in list 
		if(distanceSet[fileNum][fileName2Num.get(relatedFileList.get(0))] 
				>= distance)
			return 0;
		//if larger than or equal to the largest one in list
		if(distanceSet[fileNum][fileName2Num.get(relatedFileList.get(relatedFileList.size()-1))] 
				<= distance)
			return relatedFileList.size();
		
		//if no above special condition, we could start to count where to insert. 
		for(int i = 0; i < relatedFileList.size() - 1; i++){
			int currentFile = fileName2Num.get(relatedFileList.get(i));
			int nextFile = fileName2Num.get(relatedFileList.get(i+1));	
			if(distanceSet[fileNum][currentFile] <= distance
					&& distanceSet[fileNum][nextFile] >= distance)
				return i+1;
		}
		
		// Something we did not consider happen 
		System.out.println("somthing wrong happen!!");
		return -1;
	}
	
	//Just implement euclidean distance between two Long type array 
	private double calDistance(int[] a, int[] b){
		int sum = 0;
		for(int i = 0; i < a.length; i++){
			sum += Math.pow( a[i] - b[i], 2 );
		}
		
		return Math.pow(sum, 0.5);
	}
	
	// Count average related files number of each file
	private void consAvgRelatedFileNumOfFile(){
		avgRelatedFileNumOfFile = new int[fileList.size()];
		for(int i = 0; i < file2TimeIntervalTable.length; i++){
			int count = 0;
			int relatedFileSum = 0;
			
			//search every time interval of specified file
			for(int j = 0; j < file2TimeIntervalTable[i].length; j++){
				
				//when the time interval of specified file is not zero 
				if(file2TimeIntervalTable[i][j] > 0){
					count++;
					
					// search the related file of specified file in that time interval
					for(int z = 0; z < file2TimeIntervalTable.length; z++){
						if(file2TimeIntervalTable[z][j] > 0){
							relatedFileSum++;
						}
					}
				}
			}
			
			//count the average related file number of specified file
			avgRelatedFileNumOfFile[i] = relatedFileSum / count;
		}
		//System.out.println("consAvgRelatedFileNumOfFile over");
	}
	
	//
	private void consFile2TimeIntervalTable(){
		file2TimeIntervalTable = new int [fileList.size()][graph.length];
        //initial array to 0
        for(int i = 0; i < fileList.size(); i++ ){
        	for(int j = 0; j < graph.length; j++ ){
        		file2TimeIntervalTable[i][j] = 0;
        	}
        }
        
        //write file - time interval table
        //note that the element of graph is just the index of timeData
        for(int i = 0; i < graph.length; i++ ){
        	for(int j = 0; j < graph[i].size(); j++ ){
        		int timeIndex = graph[i].get(j);
        		Long time = timeData.get(timeIndex);
        		String whichFile = time2File.get(time);
        		int fileIndex = fileList.indexOf(whichFile);
        		file2TimeIntervalTable[fileIndex][i]++;
        	}
        }
        //System.out.println("consFile2TimeIntervalTable over");
	}
	
	private void consMetadata(){
		try{
            FileReader fr = new FileReader(dataPath);
            BufferedReader br = new BufferedReader(fr);
            String oneLine;
            screen_on_times = 0;
            
            // Record every access time in access_log.txt 
            timeData = new ArrayList<Long>();
            
            // Record the non-duplicate file name in access_log.txt
            //and provide a searchable file name list
            fileList = new ArrayList<String>();
            
            // HashMap (Key, Value) = time2File (Access time, File name) 
            time2File = new HashMap();
            
            fileName2Size = new HashMap();
            
            fileName2Num = new HashMap();
            
            int curLineNum = 0;
            boolean isRepeatScreen = false; 
            while(!(oneLine = br.readLine()).equals(null) 
            		&& curLineNum < startTrainingLine){
            	curLineNum++;
            }
            while(!(oneLine = br.readLine()).equals(null) 
            		&& curLineNum < startTrainingLine + trainingLine){
            	curLineNum++;
            	
            	// if screen event
            	if(oneLine.contains("ScreenOn") && !isRepeatScreen){
            		isRepeatScreen = true;
            		screen_on_times++;
            		}
            	
            	// if file access event
            	else if(!oneLine.contains("ScreenOn") && !oneLine.contains("ScreenOff")){
            		
            		//Get file Size
            		String sizeStr = oneLine.substring(oneLine.lastIndexOf("_")+1, oneLine.length());
            		Long size = Long.valueOf(sizeStr);
            		if(limitSize != -1 && size > limitSize){
            			continue;
            			
            		}
            		
            		
            		isRepeatScreen = false;
            		
            		
            	
            		
            		//Get time data
            		String timeStr = oneLine.substring(0, oneLine.indexOf("_"));
            		Long time = Long.valueOf(timeStr);
            		timeData.add(time);
            		
            		
            		
            		// Get file name
            		String fileName = oneLine.replace(timeStr+"_", "");
            		fileName = fileName.replace("_"+sizeStr, "");
            		if(!fileList.contains(fileName)){
            			fileList.add(fileName);
            			fileName2Num.put(fileName, fileList.size() - 1);
            		}
            		
            		// Set hashmap, time2File
            		time2File.put(time,fileName);
            		
            		// Set hashmap, fileName2Size
            		fileName2Size.put(fileName, size);
            	}
            	
            };
            br.close();
            fr.close();
			}catch(IOException e){
				e.printStackTrace();
			}
	}
	public ArrayList[] getRelatedFileTable(){
		
		return this.relatedFileTable;
	}
	
	public ArrayList<String> getFileList(){
		
		return this.fileList;
	}
	public HashMap file2Size(){
	
	return this.fileName2Size;
}
}
