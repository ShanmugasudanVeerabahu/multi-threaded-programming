package simulation.shop.coffee;


import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Machine is used to make a particular Food.  Each Machine makes
 * just one kind of Food.  Each machine has a capacity: it can make
 * that many food items in parallel; if the machine is asked to
 * produce a food item beyond its capacity, the requester blocks.
 * Each food item takes at least item.cookTimeMS milliseconds to
 * produce.
 */
public class Machine {
	public final String machineName;
	public final Food machineFoodType;

	//YOUR CODE GOES HERE...

    public int capacity;
    public Queue<Food> orderItem;

	/**
	 * The constructor takes at least the name of the machine,
	 * the Food item it makes, and its capacity.  You may extend
	 * it with other arguments, if you wish.  Notice that the
	 * constructor currently does nothing with the capacity; you
	 * must add code to make use of this field (and do whatever
	 * initialization etc. you need).
	 */
	public Machine(String nameIn, Food foodIn, int capacityIn) {
		this.machineName = nameIn;
		this.machineFoodType = foodIn;
		
		//YOUR CODE GOES HERE...

        this.capacity = capacityIn;
        this.orderItem = new ConcurrentLinkedQueue<Food>();

	}
	

	

	/**
	 * This method is called by a Cook in order to make the Machine's
	 * food item.  You can extend this method however you like, e.g.,
	 * you can have it take extra parameters or return something other
	 * than Object.  It should block if the machine is currently at full
	 * capacity.  If not, the method should return, so the Cook making
	 * the call can proceed.  You will need to implement some means to
	 * notify the calling Cook when the food item is finished.
	 */
	public void makeFood(Cook cook, int orderNumber) throws InterruptedException {
		//YOUR CODE GOES HERE...
		 orderItem.offer(machineFoodType);
	        Thread makeFoodThread = new Thread(new CookAnItem(cook, orderNumber));
	        makeFoodThread.start();
	}

	//THIS MIGHT BE A USEFUL METHOD TO HAVE AND USE BUT IS JUST ONE IDEA
	private class CookAnItem implements Runnable {
		Cook cook;
        int orderNumber;
		public CookAnItem(Cook cook, int orderNumber) {
            this.cook = cook;
            this.orderNumber = orderNumber;
        }
		public void run() {
			try {
				//YOUR CODE GOES HERE...
				  Simulation.logEvent(SimulationEvent.machineCookingFood(Machine.this, machineFoodType));
	                Thread.sleep(machineFoodType.cookTimeMS);
	                Simulation.logEvent(SimulationEvent.machineDoneFood(Machine.this, machineFoodType));
	                Simulation.logEvent(SimulationEvent.cookFinishedFood(cook, machineFoodType, orderNumber));
	                    cook.orderCompleteList.add(orderItem.remove());
			} catch(InterruptedException e) { }
		}
	}
 
	@Override
	public String toString() {
		return this.machineName;
	}
}