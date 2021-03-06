package simulation.shop.coffee;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Simulation is the main class used to run the simulation. You may add any
 * fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;

	public static Machine grillMac;
	public static Machine fryerMac;
	public static Machine coffeeMaker2000Mac;

	public static AtomicReference<Queue<Customer>> customersInQueue = new AtomicReference<>(new LinkedList<Customer>());
	public static BlockingQueue<Customer> customerOrderList = new LinkedBlockingQueue<Customer>();
	public static Map<Customer, Boolean> customerOrdersStatus = new ConcurrentHashMap<Customer, Boolean>(); 

	public static AtomicReference<Queue<Customer>> customersInShop;
	public static AtomicInteger noOfTables;

	/**
	 * Used by other classes in the simulation to log events
	 * 
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		SimulationEvent.LOGGER.info(event.toString());
	}

	/**
	 * Function responsible for performing the simulation. Returns a List of
	 * SimulationEvent objects, constructed any way you see fit. This List will
	 * be validated by a call to Validate.validateSimulation. This method is
	 * called from Simulation.main(). We should be able to test your code by
	 * only calling runSimulation.
	 * 
	 * Parameters:
	 * 
	 * @param numCustomers
	 *            the number of customers wanting to enter the coffee shop
	 * @param numCooks
	 *            the number of cooks in the simulation
	 * @param numTables
	 *            the number of tables in the coffe shop (i.e. coffee shop
	 *            capacity)
	 * @param machineCapacity
	 *            the capacity of all machines in the coffee shop
	 * @param randomOrders
	 *            a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(int numCustomers, int numCooks, int numTables,
			int machineCapacity, boolean randomOrders) {

		// This method's signature MUST NOT CHANGE.

		// We are providing this events list object for you.
		// It is the ONLY PLACE where a concurrent collection object is
		// allowed to be used.
		events = new CopyOnWriteArrayList<SimulationEvent>();
		
		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers, numCooks, numTables, machineCapacity));

		// Set things up you might need
		customersInShop = new AtomicReference<>(new LinkedList<Customer>());
		noOfTables = new AtomicInteger(Simulation.events.get(0).simParams[2]);
		CountDownLatch latch = new CountDownLatch(numCustomers);
		// Start up machines
		grillMac = new Machine("Grill", FoodType.burger, machineCapacity);
		logEvent(SimulationEvent.machineStarting(grillMac, FoodType.burger, machineCapacity));
		fryerMac = new Machine("Fryer", FoodType.fries, machineCapacity);
		logEvent(SimulationEvent.machineStarting(fryerMac, FoodType.fries, machineCapacity));
		coffeeMaker2000Mac = new Machine("CoffeeMaker2000", FoodType.coffee, machineCapacity);
		logEvent(SimulationEvent.machineStarting(coffeeMaker2000Mac, FoodType.coffee, machineCapacity));
		// Let cooks in
		Thread[] cooks = new Thread[numCooks];
		for (int i = 0; i < cooks.length; i++) {
			cooks[i] = new Thread(new Cook("cook" + i));
			cooks[i].start();
		}

		// Build the customers.
		Thread[] customers = new Thread[numCustomers];
		List<Food> order;
		if (!randomOrders) {
			order = new CopyOnWriteArrayList<Food>();
			order.add(FoodType.burger);
			order.add(FoodType.burger);
			order.add(FoodType.fries);
			order.add(FoodType.coffee);
			for (int i = 0; i < customers.length; i++) {
				Customer cp = new Customer("Customer " + (i + 1), order,latch);
				customers[i] = new Thread(cp);
				customersInQueue.get().offer(cp);
			}
		} else {
			for (int i = 0; i < customers.length; i++) {
				Random rnd = new Random(27);
				int burgerCount = rnd.nextInt(3);
				int friesCount = rnd.nextInt(3);
				int coffeeCount = rnd.nextInt(3);
				order = new CopyOnWriteArrayList<Food>();
				for (int b = 0; b < burgerCount; b++) {
					order.add(FoodType.burger);
				}
				for (int f = 0; f < friesCount; f++) {
					order.add(FoodType.fries);
				}
				for (int c = 0; c < coffeeCount; c++) {
					order.add(FoodType.coffee);
				}
				Customer c = new Customer("Customer " + (i + 1), order, latch);
				customers[i] = new Thread(c);
				customersInQueue.get().offer(c);
			}
			
		}

		// Now "let the customers know the shop is open" by
		// starting them running in their own thread.
		for (int i = 0; i < customers.length; i++) {
			customers[i].start();
			// NOTE: Starting the customer does NOT mean they get to go
			// right into the shop. There has to be a table for
			// them. The Customer class' run method has many jobs
			// to do - one of these is waiting for an available
			// table...
		}			 
		try {
			// Wait for customers to finish
			// -- you need to add some code here...

//			for (int i = 0; i < customers.length; i++) {
//					customers[i].join();
//			}
			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads. There are other approaches
			// though, so you can change this if you want to.

			latch.await();
			for (int i = 0; i < cooks.length; i++){
				cooks[i].interrupt();
			}
			
			for (int i = 0; i < cooks.length; i++)
				cooks[i].join();
		//	throw new InterruptedException();
		} catch (InterruptedException e) {
			SimulationEvent.LOGGER.error("Simulation thread interrupted.");
//			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines

		logEvent(SimulationEvent.machineEnding(grillMac));
		logEvent(SimulationEvent.machineEnding(fryerMac));
		logEvent(SimulationEvent.machineEnding(coffeeMaker2000Mac));

		// Done with simulation
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	/**
	 * Entry point for the simulation.
	 *
	 * @param args
	 *            the command-line arguments for the simulation. There should be
	 *            exactly four arguments: the first is the number of customers,
	 *            the second is the number of cooks, the third is the number of
	 *            tables in the coffee shop, and the fourth is the number of
	 *            items each cooking machine can make at the same time.
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		 * if (args.length != 4) { System.err.
		 * println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders"
		 * ); System.exit(1); } int numCustomers = new
		 * Integer(args[0]).intValue(); int numCooks = new
		 * Integer(args[1]).intValue(); int numTables = new
		 * Integer(args[2]).intValue(); int machineCapacity = new
		 * Integer(args[3]).intValue(); boolean randomOrders = new
		 * Boolean(args[4]);
		 */
		int numCustomers = 6;
		int numCooks = 2;
		int numTables = 5;
		int machineCapacity = 4;
		boolean randomOrders = true;

		// Run the simulation and then
		// feed the result into the method to validate simulation.
//		System.out.println("Did it work? " + Validate
//				.validateSimulation(runSimulation(numCustomers, numCooks, numTables, machineCapacity, randomOrders)));
//		noOfTables = new AtomicInteger(Simulation.events.get(0).simParams[2]);
		SimulationEvent.LOGGER.info("Did it work? " + Validate
				.validateSimulation(runSimulation(numCustomers, numCooks, numTables, machineCapacity, randomOrders)));
	}
Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
		
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			// TODO Auto-generated method stub
		//	Simulation.logEvent(SimulationEvent.cookEnding(t));
			System.out.println("Exception thrown by thread"+t.getName()+"and it is"+e);
		}
	};
}