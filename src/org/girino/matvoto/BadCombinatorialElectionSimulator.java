package org.girino.matvoto;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class BadCombinatorialElectionSimulator {

	public static final int VOTERS = 3;
	public static final int CANDIDATES = 3;

	int numVoters;
	int[] candidates;
	VoteSystem[] system;
	
	public BadCombinatorialElectionSimulator(int voters, int numCandidates, VoteSystem[] system) {
		candidates = makeCandidates(numCandidates);
		numVoters = voters;
		this.system = system;
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
		long[] stats = new long[2];
		countVoters(l, stats);
		double percent = 100.0*(((double)stats[1])/((double)stats[0]));
		System.out.println(numVoters + "," + candidates.length + "," + stats[0] + "," + stats[1] + "," + percent);
	}
	
	private void countVoters(ArrayList<Voter> elements, long[] stats) {
		int[] counts = new int[elements.size()];
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		recurseCount(candidates, elementsArray, counts, numVoters, 0, 0, stats);
	}
	
	private long getMultiplier(int[] current, int numVoters) {
		long b = numVoters;
		long a = current[0];
		long ret = CombinatorialHelper.combination(a, b);
		for (int i = 1; i < current.length; i++) {
			b -= a;
			a = current[i];
			ret *= CombinatorialHelper.combination(a, b);
		}
		return ret;
	}
	
	private void recurseCount(int[] candidates, Voter[] voters, int[] current, int numVoters, int pos, int sum, long[] stats) {
		if (pos == (current.length-1)) {
			current[pos] = (numVoters-sum);
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
			long multiplier = getMultiplier(current, numVoters);
			System.out.println(Arrays.toString(current) + " -> " + multiplier);
			stats[0] += multiplier;
			if (differs) {
				stats[1] += multiplier;
			}
		} else {
			int begin = numVoters - sum;
			for (int i = begin; i >=0; i--) {
				current[pos] = i;
				recurseCount(candidates, voters, current, numVoters, pos+1, sum+i, stats);
			}
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
		if (args.length > 0) {
			voters = Integer.parseInt(args[0]);
		}
		if (args.length > 1) {
			candidates = Integer.parseInt(args[1]);
		}
		for (int i = candidates; i <= voters; i++) {
			new BadCombinatorialElectionSimulator(i, candidates, new VoteSystem[] { new PluralityVote(), new TwoRoundVote() }).run();
		}
	}
}