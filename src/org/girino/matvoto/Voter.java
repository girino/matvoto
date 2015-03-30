package org.girino.matvoto;

import java.util.Arrays;
import java.util.List;

public class Voter {
	
	public Voter(int[] preferences) {
		setPreferences(preferences);
	}
	
	public Voter(List<Integer> preferences) {
		setPreferences(preferences);
	}
	
	int[] preferences;

	public int[] getPreferences() {
		return preferences;
	}

	public void setPreferences(int[] preferences) {
		this.preferences = preferences;
	}

	public void setPreferences(List<Integer> preferences) {
		int[] pref = new int[preferences.size()];
		for (int i = 0; i < pref.length; i++) {
			pref[i] = preferences.get(i);
		}
		this.preferences = pref;
	}

	public int getPreferredCandidate(int[] candidates) {
		for (int candidate : preferences) {
			if (Arrays.binarySearch(candidates, candidate) >= 0) return candidate;
		}
		throw new RuntimeException("Candidates not among preferences.");
	}

}
