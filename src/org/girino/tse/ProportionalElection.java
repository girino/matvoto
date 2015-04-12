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

import org.girino.tse.apportionment.DHontMethod;

public class ProportionalElection {
	
	private static List<Integer> calculateChairs(List<Map<String, Object>> dbResults,
			String key, String outKey, int totalChairs) {
		return new DHontMethod().calculateChairs(dbResults, key, outKey, totalChairs);
	}
	
	public static void main(String[] args) throws SQLException {
		
		String state = "SP";
		int year = 2014;
		
		DatabaseAccess a = new DatabaseAccess();
//		List<Map<String, Object>> ret = a.listAllVotesByCoalition(state, year);
		List<Map<String, Object>> ret = a.listAllVotesByParty(state, year);
		calculateChairs(ret, "VOTOS", "CHAIRS", 70);
		List<Map<String, Object>> elected = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m : ret) {
			Integer chairs = (Integer)m.get("CHAIRS");
			String coalition = (String)m.get("SIGLA_PARTIDO");
			elected.addAll(a.listBestNForParty(state, year, coalition, chairs));
			System.out.println(a.listBestNForParty(state, year, coalition, chairs));
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
