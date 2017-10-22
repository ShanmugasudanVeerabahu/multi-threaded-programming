package simulation.shop.coffee;

import java.util.List;


/**
 * Validates a simulation
 */
public class Validate {
	private static class InvalidSimulationException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3492789137369034386L;

		public InvalidSimulationException() { }
	};

	// Helper method for validating the simulation
	private static void check(boolean check,
			String message) throws InvalidSimulationException {
		if (!check) {
			System.err.println("SIMULATION INVALID : "+message);
			throw new Validate.InvalidSimulationException();
		}
	}

	/** 
	 * Validates the given list of events is a valid simulation.
	 * Returns true if the simulation is valid, false otherwise.
	 *
	 * @param events - a list of events generated by the simulation
	 *   in the order they were generated.
	 *
	 * @returns res - whether the simulation was valid or not
	 */
	public static boolean validateSimulation(List<SimulationEvent> events) {
		try {
			check(events.get(0).event == SimulationEvent.EventType.SimulationStarting,
					"Simulation didn't start with initiation event");
			check(events.get(events.size()-1).event == 
					SimulationEvent.EventType.SimulationEnded,
					"Simulation didn't end with termination event");

			/* In hw3 you will write validation code for things such as:
				Should not have more eaters than specified
				Should not have more cooks than specified
				The coffee shop capacity should not be exceeded
				The capacity of each machine should not be exceeded
				Eater should not receive order until cook completes it
				Eater should not leave coffee shop until order is received
				Eater should not place more than one order
				Cook should not work on order before it is placed
			 */

			return true;
		} catch (InvalidSimulationException e) {
			return false;
		}
	}
}
