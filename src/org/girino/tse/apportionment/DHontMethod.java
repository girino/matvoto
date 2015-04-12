package org.girino.tse.apportionment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DHontMethod implements ApportionmentMethod {

	public List<Integer> calculateChairs(List<Map<String, Object>> dbResults,
			String key, String outKey, int totalChairs) {
		List<Integer> votes = new ArrayList<Integer>();
		List<Integer> chairs = new ArrayList<Integer>();
		for (Map<String, Object> m : dbResults) {
			Integer v = ((Number)m.get(key)).intValue();
			votes.add(v);
			chairs.add(0);
		}
		
		for (int chair = 0; chair < totalChairs; chair++) {
			double max = -1;
			int maxpos = -1;
			for (int i = 0; i < votes.size(); i++) {
				int v = votes.get(i);
				int c = chairs.get(i);
				double mean = ((double)v)/((double)c+1);
				if (mean > max) {
					max = mean;
					maxpos = i;
				}
			}
			chairs.set(maxpos, chairs.get(maxpos)+1);
		}
		for (int i = 0; i < votes.size(); i++) {
			dbResults.get(i).put(outKey, chairs.get(i));
		}
		
		return chairs;
	}
	

}
