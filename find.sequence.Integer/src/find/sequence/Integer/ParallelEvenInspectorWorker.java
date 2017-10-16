package find.sequence.Integer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class defines <code>even list</code> to store even numbers identified by
 * <code>ParallelMaximizerWorker</code> thread for a given
 * <code>LinkedList</code>.
 */
public class ParallelEvenInspectorWorker extends ParallelInspectorWorker {

	protected LinkedList<Integer> list;
	List<Integer> evenList;

	public ParallelEvenInspectorWorker(LinkedList<Integer> list, String name) {
		super(list, name);
		this.list = list;
		this.evenList = new LinkedList<>();
	}

	/**
	 * Update <code>partialMax</code> until the list is exhausted.
	 */
	public void run() {
		int number = Integer.MIN_VALUE;
		while (true) {

			// check if list is not empty and removes the head
			// synchronization needed to avoid atomicity violation
			synchronized (list) {
				if (list.isEmpty())
					return; // list is empty

				number = list.remove();
			}
			
				boolean res = number % 2 == 0 ? evenList.add(number) : false;
			
		}
	}

}
