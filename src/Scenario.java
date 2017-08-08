import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Scenario {
	private String dataPath;
	private int lineIndex, screen_on_times, avgFileSize;
	private ArrayList<String> trainingFileList, testFileList;
	private ArrayList<String>[] relatedFileTable;
	private ArrayList<Long> timeData;
	private HashMap<String, Long> fileName2Size;
	private HashMap<Long,String> time2File;
	private Long request_time;
	private Double response_time;
	private Double cost_energy;
	private Long tradrequest_time;
	private Double tradcost_time;
	private Double tradcost_energy;
	private Double tradresponse_time;
	private Double cost_time;
	private ArrayList<ArrayList<Long>> time_sequence;
	
	
	private Double settingUpTime, transTime, settingUpEnergy, transEnergy;
	private HashMap<String,Long> trainingFile2Size;
	
	public Scenario(String path, ArrayList[] relatedFileTable, ArrayList<String> trainingFileList, int lineIndex, HashMap trainingFile2Size){
		this.dataPath = path;
		this.lineIndex = lineIndex;
		this.trainingFileList = trainingFileList;
		this.relatedFileTable = relatedFileTable;
		this.trainingFile2Size = trainingFile2Size;
		consMetadata();
		
		
		
		this.settingUpTime = 0.064;
		this.transTime = 0.00011;

		this.settingUpEnergy = 0.00933;
		this.transEnergy = 0.0000455379;
		
		
		
		
		realRun();
		realTradRun();
		}

	
	private void realRun(){
	
		int packFile = 0;
		ArrayList<String> fileOfDevices = new ArrayList<String>();
		Long response_timeMs = Long.valueOf(0);
		response_time = Double.valueOf(0);
		Long requestSize = Long.valueOf(0);
		Long requestTimes = Long.valueOf(0);
		Long packageSize = Long.valueOf(0);
		Long packageTimes = Long.valueOf(0);
		
		Long nowTime = Long.valueOf(0);
		
		for(int i = 0; i < timeData.size(); i++){
			if(i == 0){
				nowTime = timeData.get(i);
			}

			Long time = timeData.get(i);
			String file = time2File.get(time);
		
			if(fileOfDevices.contains(file)){
				//just open the file
				
				
			}
			else{
				
				if(nowTime > timeData.get(i)){
					Long busyMs = nowTime - timeData.get(i);
					
					response_timeMs += busyMs;
					response_time += Double.valueOf(busyMs/Long.valueOf(1000));
					//System.out.println(busyMs);
					
				}
				else{
					nowTime = timeData.get(i);
					response_time += 0;
				}
				
				if(trainingFileList.contains(file)){
					fileOfDevices.add(file);
					Long size = trainingFile2Size.get(file);
					
					requestTimes += 1;
					requestSize += size;
					
					Double temp_time = settingUpTime+((Double.valueOf(size) / Double.valueOf(1024)) * transTime);
					
					
					nowTime += new Double(temp_time * Double.valueOf(1000)).longValue();
					response_timeMs += new Double(temp_time * Double.valueOf(1000)).longValue();
					response_time+= temp_time;
					
					int fileIndex = trainingFileList.indexOf(file);
					ArrayList<String> temp = relatedFileTable[fileIndex];
					Long total_size = Long.valueOf(0);
					nowTime += new Double(settingUpTime * Double.valueOf(1000)).longValue();
					
					//response_time+= settingUpTime;
					boolean check = true;
					for(String fileName : temp){
						if(!fileOfDevices.contains(fileName) && !fileName.equals(file) ){
							
							if(check){
								packageTimes += 1;
								check = false;
							}
							fileOfDevices.add(fileName);
							size = trainingFile2Size.get(fileName);
							packFile++;
							total_size += size;
							
						}
					}
					packageSize += total_size;
					
					temp_time = Double.valueOf(total_size / Long.valueOf(1024)) * transTime;
					nowTime += new Double(temp_time * Double.valueOf(1000)).longValue();
					}
				else{

					
					Long size = fileName2Size.get(file);
					
					requestTimes += 1;
					requestSize += size;
					
					Double temp_time = Double.valueOf(size / Long.valueOf(1024)) * transTime + settingUpTime;
					 
					nowTime += new Double(temp_time * Double.valueOf(1000)).longValue();
					response_timeMs += new Double(temp_time * Double.valueOf(1000)).longValue();
					response_time += temp_time;
					
					fileOfDevices.add(file);
				}
			}
			
		}
		
		
		//System.out.println("請求檔案次數: "+requestTimes);
		//System.out.println("請求檔案大小: "+requestSize);
		//System.out.println("打包次數: "+packageTimes);
		//System.out.println("打包大小: "+packageSize);
		response_time = Double.valueOf(response_timeMs/1000);
		//System.out.println(response_time);
		request_time = requestTimes;
		cost_time = requestTimes.doubleValue() * settingUpTime 
				+ (requestSize.doubleValue() / Double.valueOf(1024)) * transTime
				+ packageTimes.doubleValue() * settingUpTime 
				+ (requestSize.doubleValue() / Double.valueOf(1024)) * transTime;
		cost_energy = requestTimes.doubleValue() * settingUpEnergy 
				+ (requestSize.doubleValue() / Double.valueOf(1024)) * transEnergy
				+ packageTimes.doubleValue() * settingUpEnergy 
				+ (requestSize.doubleValue() / Double.valueOf(1024)) * transEnergy;
		
	}
	
	public double getRequest_time(){
		return request_time;
	}
	
	public double getCost_time(){
		return cost_time;
	}
	
	public double getResponse_time(){
		return response_time;
	}
	
	public double getCost_energy(){
		return cost_energy;
	}
	
	
	private void realTradRun(){
		Long requestSize = Long.valueOf(0);
		Long requestTimes = Long.valueOf(0);

		ArrayList<String> fileOfDevices = new ArrayList<String>();
		
		tradresponse_time = Double.valueOf(0);
		
		Long nowTime = Long.valueOf(0);
		for(int i = 0; i < timeData.size(); i++){
			if(i == 0)nowTime = timeData.get(0);
			Long time = timeData.get(i);
			String file = time2File.get(time);
			Long size = fileName2Size.get(file);
			
			if(fileOfDevices.contains(file)){
				//just open the file
			}
			else{			
				if(nowTime > timeData.get(i)){
					Long busyMs = nowTime - timeData.get(i);
					tradresponse_time += Double.valueOf(busyMs/Long.valueOf(1000));
					//System.out.println("trad : "+busyMs);
					
				}
				else {
					nowTime = timeData.get(i);
					tradresponse_time += 0;
				}
				

				requestTimes += 1;
				requestSize += size;
				Double temp_time = Double.valueOf(size / Long.valueOf(1024)) * transTime + settingUpTime;
				nowTime += new Double(temp_time * Double.valueOf(1000)).longValue();
				tradresponse_time += temp_time;
				fileOfDevices.add(file);
			}
			
		}
		//System.out.println(tradresponse_time);
		//System.out.println("傳統請求檔案次數: "+requestTimes);
		//System.out.println("傳統請求檔案大小: "+requestSize);
		tradrequest_time = requestTimes;
		tradcost_time = requestTimes.doubleValue() * settingUpTime 
				+ (requestSize.doubleValue() / Double.valueOf(1024)) * transTime;
		tradcost_energy = requestTimes.doubleValue() * settingUpEnergy 
				+ (requestSize.doubleValue() / Double.valueOf(1024)) * transEnergy;
	}
	
	public double getTradrequest_time(){
		return tradrequest_time;
	}
	
	public double getTradcost_time(){
		return tradcost_time;
	}
	
	public double getTradresponse_time(){
		return tradresponse_time;
	}
	
	public double getTradcost_energy(){
		return tradcost_energy;
	}
	
	private void consMetadata(){
		try{
			avgFileSize = 0;
            FileReader fr = new FileReader(dataPath);
            BufferedReader br = new BufferedReader(fr);
            String oneLine;
            screen_on_times = 0;
            
            // Record every access time in access_log.txt 
            timeData = new ArrayList<Long>();
            
            // Record the non-duplicate file name in access_log.txt
            //and provide a searchable file name list
            testFileList = new ArrayList<String>();
            
            // HashMap (Key, Value) = time2File (Access time, File name) 
            time2File = new HashMap<Long, String>();
            
            
            fileName2Size = new HashMap();
            
            int curLineNum = 0;
            boolean isRepeatScreen = false; 
            
            while((oneLine = br.readLine()) != null && curLineNum < lineIndex){
            	curLineNum++;
            }
            
            if(oneLine.equals(null)){
            	System.out.println("have no enough data to test");
            	 br.close();
                 fr.close();
            	return;
            };
            while((oneLine = br.readLine()) != null){
            	// if screen event
            	if(oneLine.contains("ScreenOn") && !isRepeatScreen){
            		isRepeatScreen = true;
            		screen_on_times++;
            		}
            	// if file access event
            	else if(!oneLine.contains("ScreenOn") && !oneLine.contains("ScreenOff") ){
            		
            		isRepeatScreen = false;
            		//Get time data
            		String timeStr = oneLine.substring(0, oneLine.indexOf("_"));
            		Long time = Long.valueOf(timeStr);
            		timeData.add(time);
      
            		
            		
            		
            		//Get file Size
            		String sizeStr = oneLine.substring(oneLine.lastIndexOf("_")+1, oneLine.length());
            		Long size = Long.valueOf(sizeStr);
            		avgFileSize += size;
            		
            		// Get file name
            		String fileName = oneLine.replace(timeStr+"_", "");
            		fileName = fileName.replace("_"+sizeStr, "");
            		if(!testFileList.contains(fileName)){
            			testFileList.add(fileName);
            		}
            		
            		
            		
            		// Set hashmap, time2File
            		time2File.put(time,fileName);
            		
            		// Set hashmap, fileName2Size
            		fileName2Size.put(fileName, size);
            		
            	}
            	curLineNum++;
            };
            br.close();
            fr.close();
            avgFileSize /= timeData.size();
			}catch(IOException e){
				e.printStackTrace();
			}
	}
	
	
	
}
