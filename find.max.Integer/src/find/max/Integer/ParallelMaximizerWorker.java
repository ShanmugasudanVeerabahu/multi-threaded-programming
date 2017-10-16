package find.max.Integer;


import java.util.LinkedList;

/**
 * Given a <code>LinkedList</code>, this class will find the maximum over a
 * subset of its <code>Integers</code>.
 */
public class ParallelMaximizerWorker extends Thread {

	protected LinkedList<Integer> list;
	protected int partialMax ; // initialize to lowest value
	
	public ParallelMaximizerWorker(LinkedList<Integer> list) {
		this.list = list;
		this.partialMax = Integer.MIN_VALUE;
	}
	
	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		int number= Integer.MIN_VALUE;
		while (true) {
			
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized(list) {
				if (list.isEmpty())
					return; // list is empty
				
				number =list.remove();
			}
			
			// update partialMax according to new value
			// TODO: IMPLEMENT CODE HERE
			partialMax = partialMax > number ? partialMax : number;
		}
	}
	
	public int getPartialMax() {
		return partialMax;
	}

}

