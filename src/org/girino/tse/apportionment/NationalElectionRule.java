package org.girino.tse.apportionment;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.girino.tse.DatabaseAccess;

public class NationalElectionRule extends AbstractCountPartyRule {

	ElectionRule rule;
	
	public NationalElectionRule(ElectionRule rule) {
		this.rule = rule;
	}

	@Override
	public List<Map<String, Object>> listElectedOfficials(
			DatabaseAccess dbAccess, String dummy, int year, String position)
			throws SQLException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		List<Map<String, Object>> ret = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> states = dbAccess.listAllStates(year, position);
		for (Map<String, Object> map : states) {
			String state = (String)map.get("SIGLA_UF");
			List<Map<String, Object>> stateOfficials = rule.listElectedOfficials(dbAccess, state, year, position);
			ret.addAll(stateOfficials);
		}
		return ret;
	}

	@Override
	protected List<Map<String, Object>> getVotesByParty(
			DatabaseAccess dbAccess, String state, int year, String position,
			String party) throws SQLException {
		return dbAccess.getVotesByPartyNational(year, party, position);
	}
	
}
