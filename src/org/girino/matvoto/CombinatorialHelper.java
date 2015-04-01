package org.girino.matvoto;

import java.math.BigInteger;
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
		if (b == 0 || a == b) return 1; 
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

	static Map<Long, Map<Long, BigInteger>> combinationMapBig = new HashMap<Long, Map<Long, BigInteger>>();
	
	public static BigInteger combinationBig(long a, long b) {
		if (b == 0 || a == b) return BigInteger.ONE; 
		Map<Long, BigInteger> secondLevel = combinationMapBig.get(a);
		if (secondLevel == null) {
			secondLevel = new HashMap<Long, BigInteger>();
			combinationMapBig.put(a, secondLevel);
		}
		if (secondLevel.containsKey(b)) {
			return secondLevel.get(b);
		}
		BigInteger ret = combinationBig(a-1, b-1).add(combinationBig(a-1, b));
		secondLevel.put(b, ret);
		return ret;
	}
	
}
