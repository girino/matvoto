package org.girino.matvoto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public abstract class ElectionSimulator {

	public static final int REPETITIONS = 10000;
	public static final int VOTERS = 1000;
	public static final int CANDIDATES = 3;

	protected int numVoters;
	protected int[] candidates;
	protected VoteSystem[] system;
	protected Random rnd = new Random();

	public ElectionSimulator(int voters, int numCandidates, VoteSystem[] system) {
		candidates = makeCandidates(numCandidates);
		numVoters = voters;
		this.system = system;
	}
	
	// template methods
	protected abstract void countVoters(ArrayList<Voter> elements, BigInteger[] stats);

	protected static BigInteger getMultiplier(int[] current, int numVoters) {
			long a = numVoters;
			long b = current[0];
			BigInteger ret = CombinatorialHelper.combinationBig(a, b);
	//		System.out.println("0: " + a + ", " + b + " -> " + ret);
			for (int i = 1; i < current.length; i++) {
				a -= b;
				b = current[i];
				ret = ret.multiply(CombinatorialHelper.combinationBig(a, b));
	//			System.out.println(i + ": " + a + ", " + b + " -> " + ret);
			}
			return ret;
		}

	protected int[] makeCandidates(int numCandidates) {
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
		BigInteger[] stats = new BigInteger[] { new BigInteger("0"), new BigInteger("0") };
		countVoters(l, stats);
		double percent = 100.0*((stats[1].doubleValue())/(stats[0].doubleValue()));
//		System.out.println(numVoters + "," + candidates.length + "," + stats[0] + "," + stats[1] + "," + percent);
		System.out.println(numVoters + "," + candidates.length + "," + percent);
	}

	public ElectionSimulator() {
		super();
	}

	private List<int[]> makeCombinations(int[] elements) {
		Stack<int[]> ret = new Stack<int[]>();
		perm(ret, elements, 0);
		return ret;
	}

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

	protected void swap(int[] v, int i, int j) {
		int tmp = v[i];
		v[i] = v[j];
		v[j] = tmp;
	}

	protected void updateStats(int[] candidates, Voter[] voters, BigInteger[] stats,
			int[] current, BigInteger multiplier) {
		if (multiplier == null) multiplier = BigInteger.ONE;
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
				stats[0] = stats[0].add(multiplier);
				if (differs) {
					stats[1] = stats[1].add(multiplier);
				}
			}

	protected void shuffle(int[] v) {
		for (int i = 0; i < v.length; i++) {
			swap(v, i, rnd.nextInt(v.length));
		}
	}

}