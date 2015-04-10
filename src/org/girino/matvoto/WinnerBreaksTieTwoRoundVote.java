package org.girino.matvoto;



public class WinnerBreaksTieTwoRoundVote implements VoteSystem {

	//@Override
	public int getWinner(Voter[] voters, int[] counts, int[] candidates) {
		
		int[] votes1 = new int[candidates.length];
		int winner1 = 0;
		for (int i = 0; i < counts.length; i++) {
			int count = counts[i];
			Voter v = voters[i];
			int prefferedCandidate = v.getPreferredCandidate(candidates);
			for (int j = 0; j < candidates.length; j++) {
				if (candidates[j] == prefferedCandidate) {
					votes1[j] += count;
					if (votes1[j] > votes1[winner1]) winner1 = j;
					else if (votes1[j] == votes1[winner1] && j < winner1) winner1 = j;
				}
			}
		}
		// finds second
		int second = (winner1==0)?1:0;
		for (int i = 0; i < votes1.length; i++) {
			if (i != winner1 && votes1[i] > votes1[second]) second = i;
		}
		
		// second round
		int[] secondRound = new int[] {candidates[winner1], candidates[second]};
		int[] votes2 = new int[secondRound.length];
		for (int i = 0; i < counts.length; i++) {
			int count = counts[i];
			Voter v = voters[i];
			int prefferedCandidate = v.getPreferredCandidate(secondRound);
			for (int j = 0; j < secondRound.length; j++) {
				if (secondRound[j] == prefferedCandidate) {
					votes2[j] += count;
				}
			}
		}
		return votes2[1] > votes2[0]?secondRound[1]:secondRound[0];
	}

}
