package org.girino.tse.apportionment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.girino.tse.DatabaseAccess;

public class Proportional extends AbstractCountPartyRule implements ElectionRule {
	
	private ApportionmentMethod method;
	private boolean allowsCoalition;
	
	public Proportional(ApportionmentMethod method) {
		this(method, false);
	}
	public Proportional(ApportionmentMethod method, boolean allowsCoalition) {
		this.method = method;
		this.allowsCoalition = allowsCoalition;
	}

	@Override
	public List<Map<String, Object>> listElectedOfficials(DatabaseAccess dbAccess, String state, int year, String position) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (position == null) position = "DEPUTADO FEDERAL";
		List<Map<String, Object>> ret = null;
		String groupingKey;
		Method listBestN;
		if (this.allowsCoalition) {
			ret = dbAccess.listAllVotesByCoalition(state, year, position);
			groupingKey = "COMPOSICAO_LEGENDA";
			listBestN = dbAccess.getClass().getDeclaredMethod("listBestNForCoalition", String.class, Integer.TYPE, String.class, Integer.TYPE, String.class);
		} else {
			ret = dbAccess.listAllVotesByParty(state, year, position);
			groupingKey = "SIGLA_PARTIDO";
			listBestN = dbAccess.getClass().getMethod("listBestNForParty", String.class, Integer.TYPE, String.class, Integer.TYPE, String.class);
		}
		Integer chairs = dbAccess.getChairsForElection(state, year, position);
		this.method.calculateChairs(ret, "VOTOS", "CHAIRS", chairs);
		
		List<Map<String, Object>> elected = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> m : ret) {
			chairs = (Integer)m.get("CHAIRS");
			
			String coalition = (String)m.get(groupingKey);
			elected.addAll((List<Map<String, Object>>) listBestN.invoke(dbAccess, state, year, coalition, chairs, position));
		}
		
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
