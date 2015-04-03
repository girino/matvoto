package org.girino.matvoto;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public class RandomResultsElectionSimulator extends ElectionSimulator {

	int rounds = 0;
	
	public RandomResultsElectionSimulator(int voters, int numCandidates, int rounds) {
		super(voters, numCandidates);
		this.rounds = rounds;
	}
	
	@Override
	protected void countVoters(ArrayList<Voter> elements, BigInteger[] stats) {
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		for (int i = 0; i < this.rounds; i++) {
			randomCount(candidates, elementsArray, numVoters, stats);
		}
	}
	
	private void randomCount(int[] candidates, Voter[] voters, int numVoters, BigInteger[] stats) {

		int[] current = new int[voters.length];
		current[0] = rnd.nextInt(numVoters+1);
		int sum = current[0];
		for (int i = 1; i < current.length-1; i++) {
			current[i] = rnd.nextInt(numVoters-sum+1);
			sum += current[i];
		}
		current[current.length-1] = numVoters-sum;
		// shuffle
		//System.out.println(Arrays.toString(current));
		shuffle(current);
		//System.out.println(Arrays.toString(current));
		// check sum
		sum = 0;
		for (int i = 0; i < current.length; i++) {
			sum += current[i];
		}
		if (sum != numVoters) throw new RuntimeException();
		
		BigInteger multiplier = /*BigInteger.ONE;//*/ getMultiplier(current, numVoters).divide(BigInteger.valueOf(2));
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
			new RandomResultsElectionSimulator(i, candidates, repetitions).run();
		}
	}
}
