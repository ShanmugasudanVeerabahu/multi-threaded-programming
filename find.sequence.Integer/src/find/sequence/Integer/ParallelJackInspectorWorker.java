package find.sequence.Integer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines <code>Jack list</code> to store numbers that sum up to 21
 * computed by <code>ParallelMaximizerWorker</code> thread for a given
 * <code>LinkedList</code>.
 */
public class ParallelJackInspectorWorker extends ParallelInspectorWorker {

	protected LinkedList<Integer> list;
	List<Integer> jackList;

	public ParallelJackInspectorWorker(LinkedList<Integer> list, String name) {
		super(list, name);
		this.list = list;
		this.jackList = new LinkedList<>();
	}

	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		int number = Integer.MIN_VALUE;
		int sum = 21;
		while (true) {
			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized (list) {
				if (list.isEmpty()) {
					return;
				}
				// list is empty

				number = list.remove();
			}
			if (sum > 0) {
				if (number == 21) {
					jackList.add(number);
					return;
				} else if (sum -number  >= 0) {
					jackList.add(number);
				}
				sum -= number;
			}
		}
	}

}
