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

	// this implementation assumes that it is either a full election or a two candidate election.
	// please uncomment the commented part if you feel that you'll have other flavours. 
	public int getPreferredCandidate(int[] candidates) {
		if (candidates.length == preferences.length) return preferences[0];
		for (int candidate : preferences) {
			if (candidates[0] == candidate || candidates[1] == candidate) return candidate;
//			for (int i = 2; i < candidates.length; i++) {
//				if (candidates[i] == candidate) return candidate;
//			}
		}
		throw new RuntimeException("Candidates not among preferences.");
	}

	public int badPreferredCandidate(int[] candidates) {
		for (int candidate : preferences) {
			if (Arrays.binarySearch(candidates, candidate) >= 0) return candidate;
		}
		throw new RuntimeException("Candidates not among preferences.");
	}

}
