package org.girino.tse.apportionment;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.girino.tse.DatabaseAccess;

public class Distritao extends AbstractCountPartyRule {
	
	public List<Map<String, Object>> listElectedOfficials(DatabaseAccess dbAccess, String state, int year, String position) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (position == null) position = "DEPUTADO FEDERAL";
		Integer chairs = dbAccess.getChairsForElection(state, year, position);
		List<Map<String, Object>> elected = dbAccess.listBestN(state, year, chairs, position);
		
		elected.sort(new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				long a = ((Number) o1.get("VOTOS")).longValue();
				long b = ((Number) o2.get("VOTOS")).longValue();
				if (a == b) return 0;
				return (a > b)?1:-1;
			}
		});
		return elected;
	}
	
}
