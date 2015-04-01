package org.girino.matvoto;

import java.util.HashMap;
import java.util.Map;

public class CombinatorialHelper {
	
	static Map<Long, Long> factorialMap = new HashMap<Long, Long>();
	
	public static long factorial(long i) {
		if (i <= 1) return 1;
		if (factorialMap.containsKey(i)) {
			return factorialMap.get(i);
		}
		long ret = i * factorial(i-1);
		factorialMap.put(i, ret);
		return ret;
	}

	static Map<Long, Map<Long, Long>> combinationMap = new HashMap<Long, Map<Long, Long>>();
	
	public static long combination(long a, long b) {
		if (a == 0 || a == b) return 1; 
		Map<Long, Long> secondLevel = combinationMap.get(a);
		if (secondLevel == null) {
			secondLevel = new HashMap<Long, Long>();
			combinationMap.put(a, secondLevel);
		}
		if (secondLevel.containsKey(b)) {
			return secondLevel.get(b);
		}
		long ret = combination(a-1, b-1) + combination(a-1, b);
		secondLevel.put(b, ret);
		return ret;
	}
	
}
