
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
 
 
public class KMeans {
	private ArrayList<Long> data = null;
	private int k ;
	private ArrayList<Integer>[] graph;
	
	
	public KMeans(ArrayList<Long> data, int k){
		this.data = new ArrayList<Long>(data);
		this.k = k;
		this.graph = cluster(this.data, this.k);
		
	}
	
	public ArrayList<Integer>[] getGraph(){
		return graph;
	}
	

// calculate distance between two Long type value.
	private Long cal1DDistance(Long Long1, Long Long2) {
		return Math.abs(Long1 -Long2);
	}

	private ArrayList<Integer>[] cluster(ArrayList<Long> data, int k){
		//System.out.println("data length = "+data.size());
		//System.out.println("k = "+k);
		ArrayList<Integer> center = new ArrayList<Integer>();
		//initial first center point of each group
		//Note : the value n of elements of center stand for the nth element of data, same as group.
		for(int i = 0; i < k; i++){
			Integer temp = -1;
			Boolean isDuplicate = true;
			// prevent duplicate random value
			while(isDuplicate){
				Random ran = new Random();
				temp = ran.nextInt(data.size());
				isDuplicate = false;
				for(int z = 0; z < center.size(); z++){
					if(center.get(z) == temp){
						isDuplicate = true;
					}
				}
			}
			center.add(temp);
		}
		//System.out.println("first round select : "+center);

	//find nearest center point for every point, return which is nth element in center
		//note : group[i] is beLong to the ith element in center
		ArrayList<Integer> groups[] = new ArrayList[k];
		ArrayList<Integer> centerOld = new ArrayList<Integer>();
		//System.out.println(centerOld);
		//System.out.println(center);
		while(isCenterDiff(centerOld, center)){
			
			//clear group after re-find center
			for(int i = 0; i < groups.length; i++){
				if(groups[i] != null)groups[i].clear();
				else groups[i] = new ArrayList<Integer>();
			}
			
			// clear old center after comparing to the new center
			centerOld.clear();
			
			for(int i = 0; i < data.size(); i++){
				if(!center.contains(i)){
					int whichCenter = decideCenter(i, center, data);
					if(groups[whichCenter] == null)groups[whichCenter] = new ArrayList<Integer>();
					groups[whichCenter].add(i);
				}
			}
		
			//before re-find center point, add the center point to each group.
			for(int i = 0; i < groups.length; i++){
				groups[i].add(center.get(i));
			}
			centerOld = new ArrayList<Integer>(center);
	
			//clear center
			center.clear();
		
			//re-find center from group
			for(int i = 0; i < groups.length; i++){
				center.add(groups[i].get(findCenterofGroup(groups[i], data)));
				
			}
			
		}
       
		return groups;
	}

//return which is the nearest center point from the "j"th point of ArrayList "data" 
	private int decideCenter(int j, ArrayList<Integer> center, ArrayList<Long> data) {
		Long distance = Long.valueOf(-1);
		int temp = -1;
		for(int i = 0; i < center.size(); i++){
			if(distance == Long.valueOf(-1) 
					|| distance > cal1DDistance(data.get(j), data.get(center.get(i)))){
				distance = cal1DDistance(data.get(j), data.get(center.get(i)));
				temp = i;
			}
		}
		return temp;
	}
	
	// find the nearest point to the center of the group and return it's order in singleGroup
	private int findCenterofGroup(ArrayList<Integer> singleGroup, ArrayList<Long> data){
		Long sum = Long.valueOf(0);
		for(int i = 0; i < singleGroup.size(); i++){
			sum += data.get(singleGroup.get(i));
		}
		Long avg = sum/singleGroup.size();
		
		int temp = -1;
		Long distance = Long.valueOf(-1);;
		for(int i = 0; i < singleGroup.size(); i++){
			if(distance == Long.valueOf(-1) 
					|| distance > cal1DDistance(avg, data.get(singleGroup.get(i)))){
				distance = cal1DDistance(avg, data.get(singleGroup.get(i)));
				temp = i;
			}
		}
		return temp;
	}
	
	private Boolean isCenterDiff (ArrayList<Integer> oldCenter, ArrayList<Integer> center){
		
		ArrayList<Integer> aSorted = new ArrayList();
		ArrayList<Integer> bSorted = new ArrayList();
		
		if(oldCenter.size() != center.size())return true;
		for(int temp: oldCenter){
			
			if(aSorted.size() == 0){
				aSorted.add(temp);
				
			}
			else{
				for(int i = 0; i < aSorted.size(); i++){
					if(aSorted.get(i) > temp){
						aSorted.add(i,temp);
						break;
					}
					else if(i == (aSorted.size()-1)){
						aSorted.add(temp);
						break;
					}
				}
			}
		}
		
		for(int temp: center){
			if(bSorted.size() == 0){
				bSorted.add(temp);
			}
			else{
				for(int i = 0; i < bSorted.size(); i++){
					if(bSorted.get(i) > temp){
						bSorted.add(i,temp);
						break;
					}
					else if(i == (bSorted.size()-1)){
						bSorted.add(temp);
						break;
					}
				}
			}
		}
		
		for(int i = 0; i < aSorted.size(); i++){
			int distance = Math.abs(aSorted.get(i) - bSorted.get(i)); 
			if(distance > 3)return true;
		}
		return false;
	}
}