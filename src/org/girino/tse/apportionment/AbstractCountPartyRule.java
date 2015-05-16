package org.girino.tse.apportionment;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.girino.tse.DatabaseAccess;

public abstract class AbstractCountPartyRule implements ElectionRule {

	public abstract List<Map<String, Object>> listElectedOfficials(DatabaseAccess dbAccess, String state,
			int year, String position) throws SQLException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException;

	public List<Map<String, Object>> listElectedParties(DatabaseAccess dbAccess, String state, int year,
			String position) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, SQLException {
				if (position == null) position = "DEPUTADO FEDERAL";
				List<Map<String, Object>> elected = this.listElectedOfficials(dbAccess, state, year, position);
				// count by party
				Map<String, Map<String, Object>> partyMap = new HashMap<String, Map<String, Object>>();
				for (Map<String, Object> candidate : elected) {
					String party = (String)candidate.get("SIGLA_PARTIDO");
					if (!partyMap.containsKey(party)) {
						List<Map<String, Object>> votes = getVotesByParty(dbAccess, state, year, position, party);
						Map<String, Object> newParty = new HashMap<String, Object>();
						newParty.put("SIGLA_PARTIDO", party);
						newParty.put("SIGLA_UF", state);
						newParty.put("CHAIRS", 0);
						newParty.put("VOTOS", votes.get(0).get("VOTOS"));
						partyMap.put(party, newParty);
					}
					Map<String, Object> newParty = partyMap.get(party);
					newParty.put("CHAIRS", ((Integer)newParty.get("CHAIRS")) + 1);
				}
				List<Map<String, Object>> ret = new ArrayList<Map<String,Object>>();
				for (Entry<String, Map<String, Object>> entry : partyMap.entrySet()) {
					ret.add(entry.getValue());
				}
				ret.sort(new Comparator<Map<String, Object>>() {
					public int compare(Map<String, Object> o1, Map<String, Object> o2) {
						long ca = ((Number) o1.get("CHAIRS")).longValue();
						long cb = ((Number) o2.get("CHAIRS")).longValue();
						if (ca == cb) {
							long a = ((Number) o1.get("VOTOS")).longValue();
							long b = ((Number) o2.get("VOTOS")).longValue();
							if (a == b) return 0;
							return (a > b)?1:-1;
						} else {
							return (ca > cb)?1:-1;
						}
			
					}
				});
				return ret;
			}

	protected List<Map<String, Object>> getVotesByParty(
			DatabaseAccess dbAccess, String state, int year, String position,
			String party) throws SQLException {
		return dbAccess.getVotesByParty(state, year, party, position);
	}

}