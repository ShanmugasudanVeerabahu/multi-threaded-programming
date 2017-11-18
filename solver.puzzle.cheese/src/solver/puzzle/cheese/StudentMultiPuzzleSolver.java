package solver.puzzle.cheese;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This file needs to hold your solver to be tested. 
 * You can alter the class to extend any class that extends PuzzleSolver.
 * It must have a constructor that takes in a Puzzle.
 * It must have a solve() method that returns the datatype List<Direction>
 *   which will either be a reference to a list of steps to take or will
 *   be null if the puzzle cannot be solved.
 */
public class StudentMultiPuzzleSolver extends SkippingPuzzleSolver
{
	public ExecutorService execPool;
	
    public StudentMultiPuzzleSolver(Puzzle puzzle)
    {
        super(puzzle);
    }

    public List<Direction> solve()
    {
        // TODO: Implement your code here
    	List<Direction> result = null;
    	execPool = Executors.newCachedThreadPool();
		List<Future<List<Direction>>> futures = new LinkedList<Future<List<Direction>>>();
		LinkedList<DFS> tasks = new LinkedList<DFS>();
		try{
			Choice start = firstChoice(puzzle.getStart());
			
			int size = start.choices.size();
			for(int index = 0; index < size; index++){
				Choice currChoice = follow(start.at, start.choices.peek());
				
				tasks.add(new DFS(currChoice, start.choices.pop()));
				
			}
		}catch (SolutionFound e){
			System.out.println("caught");
		}
		try {
			futures = execPool.invokeAll(tasks);
		} catch (InterruptedException interException) {
			// TODO Auto-generated catch block
			interException.printStackTrace();
		}
		execPool.shutdown();
		for(Future<List<Direction>> ans : futures){
			try {
				
				if(ans.get() != null){
					result = ans.get();
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}

	private class DFS implements Callable<List<Direction>>{
		Choice head;
		Direction choiceDir;
		public DFS(Choice head, Direction choiceDir){
			this.head = head;
			this.choiceDir = choiceDir;
			
		}

		@Override
		public List<Direction> call() {
			// TODO Auto-generated method stub
			LinkedList<Choice> choiceStack = new LinkedList<Choice>();
			Choice currChoice;

			try{
				choiceStack.push(this.head);
				
				while(!choiceStack.isEmpty()){
					currChoice = choiceStack.peek();

					if(currChoice.isDeadend()){
						//backtrack
						choiceStack.pop();
						if (!choiceStack.isEmpty()) choiceStack.peek().choices.pop();
						continue;
					}
					choiceStack.push(follow(currChoice.at, currChoice.choices.peek()));
				}
				return null;
			}catch (SolutionFound e){
				Iterator<Choice> iter = choiceStack.iterator();
	            LinkedList<Direction> solutionPath = new LinkedList<Direction>();
	        
	           
	            while (iter.hasNext())
	            {
	            	currChoice = iter.next();
	                solutionPath.push(currChoice.choices.peek());
	            }
	            solutionPath.push(choiceDir);
	            if (puzzle.display != null) puzzle.display.updateDisplay();
	            
	            return pathToFullPath(solutionPath);
			}

		}

	}
}
