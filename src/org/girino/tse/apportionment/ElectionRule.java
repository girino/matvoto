package org.girino.tse.apportionment;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.girino.tse.DatabaseAccess;

public interface ElectionRule {

	public List<Map<String, Object>> listElectedOfficials(DatabaseAccess dbAccess, String state, int year, String position) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	public List<Map<String, Object>> listElectedParties(DatabaseAccess dbAccess, String state, int year, String position) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;;
	
}
