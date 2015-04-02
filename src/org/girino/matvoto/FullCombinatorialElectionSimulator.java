package org.girino.matvoto;

import java.math.BigInteger;
import java.util.ArrayList;


public class FullCombinatorialElectionSimulator extends ElectionSimulator {
	
	public FullCombinatorialElectionSimulator(int voters, int numCandidates, VoteSystem[] system) {
		super(voters, numCandidates, system);
	}
	
	protected void countVoters(ArrayList<Voter> elements, BigInteger[] stats) {
		int[] votes = new int[numVoters];
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		recurseCount(candidates, elementsArray, votes, 0, stats);
	}
	
	private void recurseCount(int[] candidates, Voter[] validVoters, int[] votes, int pos, BigInteger[] stats) {
		if (pos == (votes.length)) {
			int[] current = new int[validVoters.length];
			for (int i = 0; i < votes.length; i++) {
				current[votes[i]]++;
			}
			updateStats(candidates, validVoters, stats, current, BigInteger.ONE);
		} else {
			for (int i = 0; i < validVoters.length; i++) {
				votes[pos] = i;
				recurseCount(candidates, validVoters, votes, pos+1, stats);
			}
		}
	}



	public static void main(String[] args) {
		int voters = VOTERS;
		int candidates = CANDIDATES;
		if (args.length > 0) {
			voters = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			candidates = Integer.parseInt(args[1]);
		}
		for (int i = candidates; i <= voters; i++) {
			new FullCombinatorialElectionSimulator(i, candidates, new VoteSystem[] { new PluralityVote(), new TwoRoundVote() }).run();
		}
	}
}
