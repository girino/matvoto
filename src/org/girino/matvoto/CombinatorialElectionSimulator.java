package org.girino.matvoto;

import java.math.BigInteger;
import java.util.ArrayList;


public class CombinatorialElectionSimulator extends ElectionSimulator {


	public CombinatorialElectionSimulator(int voters, int numCandidates) {
		super(voters, numCandidates);
	}
	
	@Override
	protected void countVoters(ArrayList<Voter> elements, BigInteger[] stats) {
		int[] counts = new int[elements.size()];
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		recurseCount(candidates, elementsArray, counts, numVoters, 0, 0, stats);
	}
	
	private void recurseCount(int[] candidates, Voter[] voters, int[] current, int numVoters, int pos, int sum, BigInteger[] stats) {
		if (pos == (current.length-1)) {
			current[pos] = (numVoters-sum);
			BigInteger multiplier = getMultiplier(current, numVoters);
			updateStats(candidates, voters, stats, current, multiplier);
		} else {
			int begin = numVoters - sum;
			for (int i = begin; i >=0; i--) {
				current[pos] = i;
				recurseCount(candidates, voters, current, numVoters, pos+1, sum+i, stats);
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
			new CombinatorialElectionSimulator(i, candidates).run();
		}
	}
}
