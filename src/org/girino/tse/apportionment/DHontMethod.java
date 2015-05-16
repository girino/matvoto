package org.girino.tse.apportionment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DHontMethod implements ApportionmentMethod {

	public List<Integer> calculateChairs(List<Map<String, Object>> dbResults,
			String key, String outKey, int totalChairs) {
		List<Long> votes = new ArrayList<Long>();
		List<Integer> chairs = new ArrayList<Integer>();
		for (Map<String, Object> m : dbResults) {
			Long v = ((Number)m.get(key)).longValue();
			votes.add(v);
//			chairs.add(0);
		}
		
		// calcula coeficiente partidario
		long totalvotes = 0;
		for (Long v : votes) {
			totalvotes += v;
		}
		long coef = Math.round((double)totalvotes / (double)totalChairs);
		int chair = 0;
		for (Long v : votes) {
			int quocientePartidario = (int)Math.floorDiv(v, coef);
			chairs.add(quocientePartidario);
			chair += quocientePartidario;
		}
		// avoid situations where no party has the minimum coefficient
		int min = 0;
		if (chair == 0) min = -1;

		for (; chair < totalChairs; chair++) {
			double max = -1;
			int maxpos = -1;
			for (int i = 0; i < votes.size(); i++) {
				long v = votes.get(i);
				long c = chairs.get(i);
				double mean = ((double)v)/((double)c+1);
				if (c > min && mean > max) {
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
