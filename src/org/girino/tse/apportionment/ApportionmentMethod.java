package org.girino.tse.apportionment;

import java.util.List;
import java.util.Map;

public interface ApportionmentMethod {

	List<Integer> calculateChairs(List<Map<String, Object>> dbResults,
			String key, String outKey, int totalChairs);
}
