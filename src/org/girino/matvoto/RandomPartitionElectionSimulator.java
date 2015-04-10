package org.girino.matvoto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;


public class RandomPartitionElectionSimulator extends ElectionSimulator {

	int rounds = 0;
	
	public RandomPartitionElectionSimulator(int voters, int numCandidates, int rounds) {
		super(voters, numCandidates);
		this.rounds = rounds;
	}
	
	protected void countVoters(ArrayList<Voter> elements, BigInteger[] stats) {
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		for (int i = 0; i < this.rounds; i++) {
			randomCount(candidates, elementsArray, numVoters, stats);
		}
	}
	
	private void randomCount(int[] candidates, Voter[] voters, int numVoters, BigInteger[] stats) {

		int[] partitions = new int[voters.length+1];
		partitions[0] = 0; partitions[voters.length] = numVoters;
		for (int i = 1; i < partitions.length-1; i++) {
			partitions[i] = rnd.nextInt(numVoters+1);
		}
		Arrays.sort(partitions);
		int[] current = new int[voters.length];
		for (int i = 0; i < current.length; i++) {
			current[i] = partitions[i+1] - partitions[i];
		}
		int sum = 0;
		for (int i = 0; i < current.length; i++) {
			sum += current[i];
		}
		if (sum != numVoters) throw new RuntimeException();
		BigInteger multiplier = /*BigInteger.ONE;*/getMultiplier(current, numVoters)/*.divide(BigInteger.valueOf(2))*/;
		updateStats(candidates, voters, stats, current, multiplier);
	}


	public static void main(String[] args) {
		int voters = VOTERS;
		int candidates = CANDIDATES;
		int repetitions = REPETITIONS;
		if (args.length > 0) {
			voters = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			candidates = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			repetitions = Integer.parseInt(args[2]);
		}
		for (int i = candidates; i <= voters; i++) {
			new RandomPartitionElectionSimulator(i, candidates, repetitions).run();
		}
	}
}
