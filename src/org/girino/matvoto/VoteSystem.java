package org.girino.matvoto;


public interface VoteSystem {

	int getWinner(Voter[] voters, int[] distribution, int[] candidates);
}
