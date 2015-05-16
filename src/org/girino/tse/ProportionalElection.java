package org.girino.tse;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.girino.tse.apportionment.Distritao;
import org.girino.tse.apportionment.ElectionRule;
import org.girino.tse.apportionment.NationalElectionRule;
import org.girino.tse.apportionment.ProportionalDHont;

public class ProportionalElection {

	public static void main(String[] args) throws SQLException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		String state = "BR";
		int year = 2014;
		
		NumberFormat f3 = new DecimalFormat("%3d"); 
		
		NumberFormat f12 = new DecimalFormat("%12d");

		DatabaseAccess a = new DatabaseAccess();
		ElectionRule[] rules = new ElectionRule[] {
				new ProportionalDHont(true), 
				new ProportionalDHont(false),
				new Distritao() 
				};
		for (ElectionRule innerRule : rules) {
			ElectionRule rule = new NationalElectionRule(innerRule);
//			ElectionRule rule =  (innerRule);
			List<Map<String, Object>> parties = rule.listElectedParties(a,
					state, year, "DEPUTADO FEDERAL");
			for (Map<String, Object> map : parties) {
//				System.out.println(map);
				int ichairs = (Integer)map.get("CHAIRS");
				String chairs = String.format("%3d", ichairs);
				long ivotes = ((Number)map.get("VOTOS")).longValue();
				String votes = String.format("%12d", ivotes);
				String party = (String)map.get("SIGLA_PARTIDO");
				party = String.format("%1$8s", party);
				
				System.out.println("Partido: " + party + "    Votos: " + votes + "    Cadeiras: " + chairs);
			}
//			System.out.println("---------------------");
//			List<Map<String, Object>> officials = rule.listElectedOfficials(a,
//			state, year, "DEPUTADO FEDERAL");
//			for (Map<String, Object> map : officials) {
//				System.out.println(map);
//			}
			System.out.println("\n=====================\n");
		}
	}

}
