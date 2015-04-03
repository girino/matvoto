package org.girino.matvoto;

import java.util.Arrays;


public class PluralityVote implements VoteSystem {

	//@Override
	public int getWinner(Voter[] voters, int[] counts, int[] candidates) {
		Arrays.sort(candidates);
		int[] votes = new int[candidates.length];
		int winner = 0;
		for (int i = 0; i < counts.length; i++) {
			int count = counts[i];
			Voter v = voters[i];
			int prefferedCandidate = v.getPreferredCandidate(candidates);
			for (int j = 0; j < candidates.length; j++) {
				if (candidates[j] == prefferedCandidate) {
					votes[j] += count;
					if (votes[j] > votes[winner]) winner = j;
					else if (votes[j] == votes[winner] && j < winner) winner = j;
				}
			}
		}
		return candidates[winner];
	}

}
