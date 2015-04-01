package org.girino.matvoto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;


public class RandomPartitionElectionSimulator {

	private static final int REPETITIONS = 100000;
	public static final int VOTERS = 100;
	public static final int CANDIDATES = 3;

	int numVoters;
	int[] candidates;
	VoteSystem[] system;
	int rounds = 0;
	Random rnd = new Random();
	
	public RandomPartitionElectionSimulator(int voters, int numCandidates, int rounds, VoteSystem[] system) {
		candidates = makeCandidates(numCandidates);
		numVoters = voters;
		this.system = system;
		this.rounds = rounds;
	}
	
	private int[] makeCandidates(int numCandidates) {
		int[] candidates = new int[numCandidates];
		for (int i = 0; i < numCandidates; i++) {
			candidates[i] = i;
		}
		return candidates;
	}
	
	public void run() {
		List<int[]> combinations = makeCombinations(candidates);
		ArrayList<Voter> l = new ArrayList<Voter>();
		for (int[] preference : combinations) {
			Voter v = new Voter(preference);
			l.add(v);
			//System.out.println(Arrays.toString(preference));
		}
		int[] stats = new int[2];
		countVoters(l, stats);
		double percent = 100.0*(((double)stats[1])/((double)stats[0]));
		System.out.println(numVoters + "," + candidates.length + "," + stats[0] + "," + stats[1] + "," + percent);
	}
	
	private void countVoters(ArrayList<Voter> elements, int[] stats) {
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		for (int i = 0; i < this.rounds; i++) {
			randomCount(candidates, elementsArray, numVoters, stats);
		}
	}
	
	private void shuffle(int[] v) {
		for (int i = 0; i < v.length; i++) {
			swap(v, i, rnd.nextInt(v.length));
		}
	}
	
	private void randomCount(int[] candidates, Voter[] voters, int numVoters, int[] stats) {

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
		
		// results
		int[] winners = new int[system.length];
		for (int i = 0; i < system.length; i++) {
			winners[i] = system[i].getWinner(voters, current, candidates);
		}
		boolean differs = false;
		for (int i = 1; i < system.length; i++) {
			if (winners[i] != winners[i-1]) {
				differs = true;
				break;
			}
		}
		stats[0]++;
		if (differs) {
			stats[1]++;
		}
	}

	private List<int[]> makeCombinations(int[] elements) {
		Stack<int[]> ret = new Stack<int[]>();
		perm(ret, elements, 0);
		return ret;
	}

	/* recursive function to generate permutations */
	private void perm(Stack<int[]> s, int[] v, int i) {
		if (i == v.length) {
			// add to permutation stack
			s.push(v.clone());
		} else
			/* recursively explore the permutations starting
			 * at index i going through index n-1
			 */
			for (int j = i; j < v.length; j++) {
				swap(v, i, j);
				perm(s, v, i+1);
				// swap back
				swap(v, i, j);
			}
	}
	
	private void swap(int[] v, int i, int j) {
		int tmp = v[i];
		v[i] = v[j];
		v[j] = tmp;
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
			new RandomPartitionElectionSimulator(i, candidates, repetitions, new VoteSystem[] { new PluralityVote(), new TwoRoundVote() }).run();
		}
	}
}
