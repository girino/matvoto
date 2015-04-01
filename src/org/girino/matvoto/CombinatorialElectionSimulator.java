package org.girino.matvoto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class CombinatorialElectionSimulator {

	public static final int VOTERS = 4;
	public static final int CANDIDATES = 3;

	int numVoters;
	int[] candidates;
	VoteSystem[] system;
	
	public CombinatorialElectionSimulator(int voters, int numCandidates, VoteSystem[] system) {
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
		int[] votes = new int[numVoters];
		Voter[] elementsArray = elements.toArray(new Voter[0]);
		recurseCount(candidates, elementsArray, votes, 0, stats);
	}
	
	static Map<Long, Long> m = new HashMap<Long, Long>();
	private void recurseCount(int[] candidates, Voter[] validVoters, int[] votes, int pos, long[] stats) {
		if (pos == (votes.length)) {
			int[] current = new int[validVoters.length];
			for (int i = 0; i < votes.length; i++) {
				current[votes[i]]++;
			}
//			int sum = 0;
//			for (int i = 0; i < current.length; i++) {
//				sum += current[i];
//			}
//			if (sum != numVoters) throw new RuntimeException();
//			System.out.println(Arrays.toString(current));
//			System.out.println(Arrays.toString(votes));
			
			int[] winners = new int[system.length];
			for (int i = 0; i < system.length; i++) {
				winners[i] = system[i].getWinner(validVoters, current, candidates);
			}
			boolean differs = false;
			for (int i = 1; i < system.length; i++) {
				if (winners[i] != winners[i-1]) {
					differs = true;
					break;
				}
			}
			long l = 0;
			long mult = 1;
			for (int i = 0; i < current.length; i++) {
				l += current[i] * mult;
				mult *= 10;
			}
			Long old = m.get(l);
			if (old == null) old = 0L;
			m.put(l, old+1);
//			System.out.println(Arrays.toString(current) + " -> " + (old+1));
			stats[0]++;
			if (differs) {
				stats[1]++;
			}
		} else {
			for (int i = 0; i < validVoters.length; i++) {
				votes[pos] = i;
				recurseCount(candidates, validVoters, votes, pos+1, stats);
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
	
	private static long getMultiplier(long[] current, int numVoters) {
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
			m = new HashMap<Long, Long>();
			new CombinatorialElectionSimulator(i, candidates, new VoteSystem[] { new PluralityVote(), new TwoRoundVote() }).run();
		}
		
		CombinatorialElectionSimulator c = new CombinatorialElectionSimulator(voters, candidates, new VoteSystem[] { new PluralityVote(), new TwoRoundVote() });
		int s = c.makeCombinations(c.makeCandidates(candidates)).size();
		for (Long l: m.keySet()) {
			Long x = m.get(l);
			long[] ret = new long[s];
			long mult = 1;
			for (int j = 0; j < ret.length; j++) {
				ret[j] = (l/mult) % 10;
				mult *= 10;
			}
			System.out.println(Arrays.toString(ret) + " -> " + x + " = " + getMultiplier(ret, voters));
		}
	}
}