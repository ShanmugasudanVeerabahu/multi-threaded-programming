package find.sequence.Integer;


import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Random;

import org.junit.Test;

public class PublicTest {

	private int	threadCount = 10; // number of threads to run
	private ParallelInspector maximizer = new ParallelInspector(threadCount);
	
	@Test
	public void compareMax() {
		int size = 10000; // size of list
		LinkedList<Integer> list = new LinkedList<Integer>();
		Random rand = new Random();
		int serialMax = Integer.MIN_VALUE;
		int parallelMax = 0;
		// populate list with random elements
		Long t = System.nanoTime();
		for (int i=0; i<size; i++) {
			int next = rand.nextInt(1000);
			list.add(next);
			serialMax = Math.max(serialMax, next); // compute serialMax
		}
		Long s = System.nanoTime();
		System.out.println("Serial Time: "+(s-t)/1000000);
		System.out.println("Serial max is:"+ serialMax);
		// try to find parallelMax
		try {
			Long t1 = System.nanoTime();
			maximizer.executeSequence(list);
			Long t2 = System.nanoTime();
			System.out.println("Parallel time: "+(t2-t1)/1000000);
			System.out.println("parallel max: "+ parallelMax);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("The test failed because the max procedure was interrupted unexpectedly.");
		} catch (Exception e) {
			e.printStackTrace();
			fail("The test failed because the max procedure encountered a runtime error: " + e.getMessage());
		}
		
		assertEquals("The serial max doesn't match the parallel max", serialMax, parallelMax);
	}
}


