package simulation.shop.coffee;

import java.util.LinkedList;
import java.util.List;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;

	private Customer currentCustomerOrder;
    public List<Food> orderCompleteList = new LinkedList<Food>();
	/**
	 * You can feel free modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while (true) {
                //YOUR CODE GOES HERE...
                synchronized (Simulation.customerOrderList) {

                    while (Simulation.customerOrderList.isEmpty()) {
                        Simulation.customerOrderList.wait();
                    }
                    currentCustomerOrder = Simulation.customerOrderList.remove();
                    Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, currentCustomerOrder.getOrder(), currentCustomerOrder.getOrderNum()));
                    Simulation.customerOrderList.notifyAll();
                }

                for (int i = 0; i < currentCustomerOrder.getOrder().size(); i++) {
                    Food currentFoodItem = currentCustomerOrder.getOrder().get(i);

                    //Burger
                    if (currentFoodItem.equals(FoodType.burger)) {
                        synchronized (Simulation.grillMac.orderItemsList) {
                            while (Simulation.grillMac.orderItemsList.size() >= Simulation.grillMac.capacity) {
                                Simulation.grillMac.orderItemsList.wait();
                            }
                            Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.burger, currentCustomerOrder.getOrderNum()));
                            Simulation.grillMac.makeFood(this, currentCustomerOrder.getOrderNum());
                            Simulation.grillMac.orderItemsList.notifyAll();
                        }
                    } else if (currentFoodItem.equals(FoodType.fries)) {
                        synchronized (Simulation.fryerMac.orderItemsList) {
                            while (Simulation.fryerMac.orderItemsList.size() >= Simulation.fryerMac.capacity) {
                                Simulation.fryerMac.orderItemsList.wait();
                            }
                            Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.fries, currentCustomerOrder.getOrderNum()));
                            Simulation.fryerMac.makeFood(this, currentCustomerOrder.getOrderNum());
                            Simulation.fryerMac.orderItemsList.notifyAll();
                        }
                    } else {
                        synchronized (Simulation.coffeeMaker2000Mac.orderItemsList) {
                            while (!(Simulation.coffeeMaker2000Mac.orderItemsList.size() < Simulation.coffeeMaker2000Mac.capacity)) {
                                Simulation.coffeeMaker2000Mac.orderItemsList.wait();
                            }
                            Simulation.logEvent(SimulationEvent.cookStartedFood(this, FoodType.coffee, currentCustomerOrder.getOrderNum()));
                            Simulation.coffeeMaker2000Mac.makeFood(this, currentCustomerOrder.getOrderNum());
                            Simulation.coffeeMaker2000Mac.orderItemsList.notifyAll();
                        }
                    }

                }
                synchronized (orderCompleteList) {
                    while (orderCompleteList.size() != currentCustomerOrder.getOrder().size()) {
                        orderCompleteList.wait();
                        orderCompleteList.notifyAll();
                    }
                }

                Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, currentCustomerOrder.getOrderNum()));

                synchronized (Simulation.customerOrdersStatus) {
                    Simulation.customerOrdersStatus.put(currentCustomerOrder, true);
                    Simulation.customerOrdersStatus.notifyAll();
                }
                orderCompleteList = new LinkedList<Food>();
            }
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
		}
	}
}