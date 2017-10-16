package find.sequence.Integer;

import java.util.LinkedList;

/**
 * This Abstract class defines <code>numThreads</code> hierarchy of 
 * <code>ParallelMaximizerWorker</code> that exhibits different behaviors
 * for a given <code>LinkedList</code>.
 */
public abstract class ParallelInspectorWorker extends Thread{
	
	protected LinkedList<Integer> list;
	
	public ParallelInspectorWorker(LinkedList<Integer> list,String name){
		super(name);
		this.list = list;
	}
}
