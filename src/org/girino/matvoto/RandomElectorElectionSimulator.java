package org.girino.matvoto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public class RandomElectorElectionSimulator extends ElectionSimulator {

	int rounds = 0;
	
	public RandomElectorElectionSimulator(int voters, int numCandidates, int rounds) {
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

		int[] current = new int[voters.length];
		for (int i = 0; i < numVoters; i++) {
			current[rnd.nextInt(current.length)]++;
		}
		int sum = 0;
		for (int i = 0; i < current.length; i++) {
			sum += current[i];
		}
		if (sum != numVoters) throw new RuntimeException();
		
		updateStats(candidates, voters, stats, current, BigInteger.ONE);
	}

	public static void main(String[] args) {
		int voters = VOTERS;
		int candidates = 8;
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
		ElectionSimulator.system = new VoteSystem[] {
				new PluralityVote(),
				new TwoRoundVote(),
//				new WinnerBreaksTieTwoRoundVote(),
//				new LoserBreaksTieTwoRoundVote(),
//				new OnlyTiesTwoRoundVote(),
		};
		for (int i = 1; i <= 1000000; i += Math.ceil(i/100.0)) {
			new RandomElectorElectionSimulator(i, candidates, repetitions).run();
		}
	}
}
