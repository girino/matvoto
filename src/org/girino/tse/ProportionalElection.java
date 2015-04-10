package org.girino.tse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class ProportionalElection {
	
	private static List<Integer> calculateChairs(List<Map<String, Object>> dbResults,
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
	
	public static void main(String[] args) throws SQLException {
		
		String state = "SP";
		int year = 2014;
		
		DatabaseAccess a = new DatabaseAccess();
		List<Map<String, Object>> ret = a.listAllVotesByCoalition(state, year);
		calculateChairs(ret, "VOTOS", "CHAIRS", 70);
		List<Map<String, Object>> elected = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m : ret) {
			Integer chairs = (Integer)m.get("CHAIRS");
			String coalition = (String)m.get("COMPOSICAO_LEGENDA");
			elected.addAll(a.listBestNForCoalition(state, year, coalition, chairs));
		}
		elected.sort(new Comparator<Map<String, Object>>() {

			public int compare(Map<String, Object> m1, Map<String, Object> m2) {
				return ((Comparable)m1.get("VOTOS")).compareTo(m2.get("VOTOS"));
			}

		});
		for (Map<String, Object> map : elected) {
			System.out.println(map);
		}
	}



}
