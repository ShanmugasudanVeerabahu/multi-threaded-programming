package simulation.shop.coffee;

import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the coffee shop (only successful if the
 * coffee shop has a free table), place its order, and then leave the 
 * coffee shop when the order is complete.
 */
public class Customer implements Runnable {
	private final List<Food> order;
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final int orderNum;    
	private final int cusPriority;
	private static int runningCounter = 0;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(final String name,final List<Food> order,final int cusPriority) {
		this.name = name;
		this.order = order;
		this.orderNum = ++runningCounter;
		this.cusPriority = cusPriority;
	}
	public int getOrderNum() {
        return orderNum;
    }
	
	public List<Food> getOrder(){
		return this.order;
	}
	public int getCusPriority(){
		return this.cusPriority;
	}

	public String toString() {
		return "Customer name: "+name +" with priority: "+cusPriority;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the coffee shop (only successful when the coffee shop has a
	 * free table), place its order, and then leave the coffee shop
	 * when the order is complete.
	 */
	public void run() {
		//YOUR CODE GOES HERE...
		  Simulation.logEvent(SimulationEvent.customerStarting(this));


	        synchronized (Simulation.customersInQueue) {

	            int numberOfTables = Simulation.events.get(0).simParams[2];

	            while (Simulation.customersInQueue.size() >= numberOfTables) {
	                try {
	                    Simulation.customersInQueue.wait();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	            Simulation.customersInQueue.add(this);
	            Simulation.logEvent(SimulationEvent.customerEnteredCoffeeShop(this));
	            Simulation.customersInQueue.notifyAll();
	        }

	        synchronized (Simulation.customerOrderList) {
	            Simulation.customerOrderList.add(this);
	            Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, this.order, this.orderNum));
	            Simulation.customerOrderList.notifyAll();
	        }

	        synchronized (Simulation.customerOrdersStatus) {
	            Simulation.customerOrdersStatus.put(this, false);
	        }

	        synchronized (Simulation.customerOrdersStatus) {
	            while (!Simulation.customerOrdersStatus.get(this)) {
	                try {
	                    Simulation.customerOrdersStatus.wait();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	            Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, this.order, this.orderNum));
	            Simulation.customerOrdersStatus.notifyAll();
	        }

	        synchronized (Simulation.customersInQueue) {
	            Simulation.customersInQueue.remove(this);
	            Simulation.logEvent(SimulationEvent.customerLeavingCoffeeShop(this));
	            Simulation.customersInQueue.notifyAll();
	        }
	}
}