package simulation.shop.coffee;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Customers are simulation actors that have two fields: a name, and a list of
 * Food items that constitute the Customer's order. When running, an customer
 * attempts to enter the coffee shop (only successful if the coffee shop has a
 * free table), place its order, and then leave the coffee shop when the order
 * is complete.
 */
public class Customer implements Runnable {
	private final List<Food> order;
	// JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final int orderNum;
	private static int runningCounter = 0;
	private CountDownLatch latch;

	/**
	 * You can feel free modify this constructor. It must take at least the name
	 * and order but may take other parameters if you would find adding them
	 * useful.
	 */
	public Customer(final String name, final List<Food> order,final CountDownLatch latch) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
		this.latch = latch;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public List<Food> getOrder() {
		return this.order;
	}

	public CountDownLatch getLatch(){
		return this.latch;
	}
	public String toString() {
		return "Customer name: " + name;
	}

	/**
	 * This method defines what an Customer does: The customer attempts to enter
	 * the coffee shop (only successful when the coffee shop has a free table),
	 * place its order, and then leave the coffee shop when the order is
	 * complete.
	 */
	public void run() {
		// YOUR CODE GOES HERE...
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		Queue<Customer> oldVal, newVal;
		do {
			oldVal = Simulation.customersInQueue.get();
			if (oldVal.size() > Simulation.noOfTables.get()) {

				Simulation.customersInQueue.get().remove(this);
				Simulation.customersInShop.get().add(this);
				Simulation.noOfTables.getAndDecrement();
				Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));

				Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
				
						Simulation.customerOrderList.add(this);

				Simulation.customerOrdersStatus.put(this, false);
			}
				newVal = new LinkedList<Customer>(Simulation.customersInQueue.get());
			

		} while (!Simulation.customersInQueue.compareAndSet(oldVal, newVal));
	}
}