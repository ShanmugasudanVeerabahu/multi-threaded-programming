package find.sequence.Integer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines <code>order list</code> to store ordered numbers
 * identified by <code>ParallelMaximizerWorker</code> thread for a given
 * <code>LinkedList</code>.
 */
public class ParallelOrderInspectorWorker extends ParallelInspectorWorker {

	protected LinkedList<Integer> list;
	List<Integer> orderList;

	public ParallelOrderInspectorWorker(LinkedList<Integer> list, String name) {
		super(list, name);
		this.list = list;
		this.orderList = new LinkedList<>();
	}

	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		int prevNumber = Integer.MIN_VALUE;
		int number = Integer.MIN_VALUE;
		while (true) {
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized (list) {
				if (list.isEmpty())
					return; // list is empty

				prevNumber = list.remove();
			}
			synchronized (orderList) {
				if (prevNumber > number) {
					orderList.add(prevNumber);
					number = prevNumber;
				}
			}
		}
	}

}
