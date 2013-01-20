package heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import problem.Element;
import problem.Evaluator;
import problem.Knapsack;

/**
 * Local search heuristic.
 * 
 * @author omegak
 */
public class LocalSearch extends Heuristic {
	
	@Override
	public Knapsack executeOnce(Knapsack knapsack) {		
		int neighbours = generateRandomNumberOfNeighbours();
		Knapsack best = null;
		
		for (int i=0; i<neighbours; i++) {
			Knapsack candidate = generateNeighbour(knapsack);
			
			if (best == null) {
				best = candidate;
			} else if (candidate.evaluate() > best.evaluate()) {
				best = candidate;
			}
		}
		
		return best;
	}
}
