package find.sequence.Integer;

import java.util.ArrayList;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class runs <code>numThreads</code> instances of
 * <code>ParallelMaximizerWorker</code> in parallel to find the maximum
 * <code>Integer</code> in a <code>LinkedList</code>.
 */
public class ParallelInspector {

	private int numThreads;
	private ArrayList<ParallelInspectorWorker> workers; // = new
														// ArrayList<ParallelMaximizerWorker>(numThreads);
	static final Logger logger = LoggerFactory.getLogger(ParallelInspector.class);


	public ParallelInspector(int numThreads) {
		this.numThreads = numThreads;
		this.workers = new ArrayList<ParallelInspectorWorker>(numThreads);

	}

	public static void main(String[] args) {
		int numThreads = 4; // number of threads for the maximizer
		int numElements = 10000; // number of integers in the list

		ParallelInspector maximizer = new ParallelInspector(numThreads);
		LinkedList<Integer> list = new LinkedList<Integer>();

		// populate the list
		// TODO: change this implementation to test accordingly
		// Random rand = new Random();
		for (int i = 0; i < numElements; i++) {
			// int next =rand.nextInt(1000);
			list.add(i);// list.add(next); //
		}
		for (int i = 0; i < 10; i++) {
		//	System.out.println("Iteration number: "+i);
			logger.info("Iteration number: "+i); 
			try {
				LinkedList<Integer> myInput = new LinkedList<Integer>(list);
				maximizer.executeSequence(myInput);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(),e);
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
	public void executeSequence(LinkedList<Integer> myInput) throws InterruptedException {
		// int max = Integer.MIN_VALUE; // initialize max as lowest value

		// System.out.println(workers.size() +" workers size");
		// run numThreads instances of ParallelMaximizerWorker

		ParallelEvenInspectorWorker even = new ParallelEvenInspectorWorker(myInput, "EVEN");
		workers.add(even);

		ParallelOddInspectorWorker odd = new ParallelOddInspectorWorker(myInput, "ODD");
		workers.add(odd);

		ParallelOrderInspectorWorker order = new ParallelOrderInspectorWorker(myInput, "ORDER");
		workers.add(order);

		ParallelJackInspectorWorker jack = new ParallelJackInspectorWorker(myInput, "JACK");
		workers.add(jack);

		even.start();
		odd.start();
		order.start();
		jack.start();
		// System.out.println(workers.size() +" workers size"+"Number of threads: "+numThreads);
		logger.debug(workers.size() +" workers size"+"Number of threads: "+numThreads);
		// wait for threads to finish
		for (int i = 0; i < numThreads; i++)
			workers.get(i).join();
		

		// print the result set of collected integers
		// TODO: IMPLEMENT CODE HERE
//		System.out.println(even.getName()+" reporting elements: " + even.evenList);
//		System.out.println(odd.getName() + " reporting elements: " + odd.oddList);
//		System.out.println(order.getName()+" reporting elements: " + order.orderList);
		logger.info(even.getName()+" reporting elements: " + even.evenList);
		logger.info(odd.getName() + " reporting elements: " + odd.oddList);
		logger.info(order.getName()+" reporting elements: " + order.orderList);
		
		if (jack.jackList.isEmpty())
//			System.out.println(jack.getName() + " is Empty.. FAILURE");
		logger.info(jack.getName() + " is Empty.. FAILURE");
		else
//			System.out.println(jack.getName() + " reporting elements: " + jack.jackList);
		logger.info(jack.getName() + " reporting elements: " + jack.jackList);
		workers.clear();
	}

}
