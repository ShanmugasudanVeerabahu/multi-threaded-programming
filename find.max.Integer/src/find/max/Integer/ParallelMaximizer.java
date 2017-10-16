package find.max.Integer;

//import java.util.LinkedList;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelMaximizer {

	int numThreads;
	ArrayList<ParallelMaximizerWorker> workers; // = new
												// ArrayList<ParallelMaximizerWorker>(numThreads);
	static final Logger LOGGER = LoggerFactory.getLogger(ParallelMaximizer.class);

	public ParallelMaximizer(int numThreads) {
		this.numThreads = numThreads;
		this.workers = new ArrayList<ParallelMaximizerWorker>(numThreads);
	}

	public static void main(String[] args) {
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10000; // number of integers in the list

		ParallelMaximizer maximizer = new ParallelMaximizer(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();

		// populate the list
		// TODO: change this implementation to test accordingly
//		Random rand = new Random();
		for (int i = 0; i < numElements; i++) {
//			int next = rand.nextInt();
//			list.add(next);
			 list.add(i);
		}

		// System.out.println("Values are : "+list.toString());
		// run the maximizer
		for (int i = 0; i < 10; i++) {
			try {
				LinkedList<Integer> myInput = new LinkedList<Integer>(list);
				LOGGER.info("Iteration number: "+ i);
				LOGGER.info("Maximum value for given list is:" + maximizer.max(myInput));
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}

	/**
	 * Finds the maximum by using <code>numThreads</code> instances of
	 * <code>ParallelMaximizerWorker</code> to find partial maximums and then
	 * combining the results.
	 * 
	 * @param list
	 *            <code>LinkedList</code> containing <code>Integers</code>
	 * @return Maximum element in the <code>LinkedList</code>
	 * @throws InterruptedException
	 */
	public int max(LinkedList<Integer> list) throws InterruptedException {
		int max = Integer.MIN_VALUE; // initialize max as lowest value

		// System.out.println(workers.size() +" workers size");
		// run numThreads instances of ParallelMaximizerWorker
		for (int i = 0; i < numThreads; i++) {
			workers.add(i, new ParallelMaximizerWorker(list));
			workers.get(i).start();
		}
		// System.out.println(workers.size() +" workers size");
		// wait for threads to finish
		for (int i = 0; i < numThreads; i++) {
			workers.get(i).join();

		}
		// int temp = workers.get(i).partialMax;
		// max = max > temp ? max : temp;
		// take the highest of the partial maximums
		// TODO: IMPLEMENT CODE HERE
		int temp = 0;
		for (int i = 0; i < numThreads; i++) {
			temp = workers.get(i).partialMax;
			max = max > temp ? max : temp;
		}
		return max;
	}

}
