package org.girino.matvoto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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

	public ElectionSimulator(int voters, int numCandidates) {
		candidates = makeCandidates(numCandidates);
		numVoters = voters;
//		this.system = new VoteSystem[] { new PluralityVote(), new TwoRoundVote(), new BadTwoRoundVote() };
		this.system = new VoteSystem[] { new PluralityVote(), new TwoRoundVote() };
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
		BigInteger[] stats = new BigInteger[system.length];
		Arrays.fill(stats, BigInteger.ZERO);
		countVoters(l, stats);
		double[] percent = new double[stats.length-1];
		for (int i = 0; i < percent.length; i++) {
			percent[i] = 100.0*((stats[i+1].doubleValue())/(stats[0].doubleValue()));
		}
//		System.out.println(numVoters + "," + candidates.length + "," + stats[0] + "," + stats[1] + "," + percent);
		System.out.print("[" + numVoters);
		for (double p : percent) System.out.print("," + p);
		System.out.println("],");
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
				boolean differs[] = new boolean[winners.length-1];
				for (int i = 1; i < system.length; i++) {
					if (winners[i] != winners[0]) {
						differs[i-1] = true;
					}
				}
				stats[0] = stats[0].add(multiplier);
				for (int i = 0; i < differs.length; i++) {
					if (differs[i]) {
						stats[i+1] = stats[i+1].add(multiplier);
					}
				}
			}

	protected void shuffle(int[] v) {
		for (int i = 0; i < v.length; i++) {
			swap(v, i, rnd.nextInt(v.length));
		}
	}

}