package actor.vowelcount.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import message.vowelcount.akka.EndMessage;
import message.vowelcount.akka.FinalResultMessage;
import message.vowelcount.akka.FirstFileMessage;
import message.vowelcount.akka.GreetMessage;
import message.vowelcount.akka.NextFileMessage;
import message.vowelcount.akka.PartialResultMessage;
import message.vowelcount.akka.UpdatePredictionMessage;

/**
 * Listener actor that prints output to the console
 * 
 * @author shanmugasudan
 *
 */
public class Listener extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Throwable {
		// TODO Auto-generated method stub
		if (message instanceof GreetMessage) {
			System.out.println("Starting the operation: file Processing");
			getSender().tell(new FirstFileMessage(), null);
		} else if (message instanceof PartialResultMessage) {
			PartialResultMessage parResult = (PartialResultMessage) message;
			System.out.println(
					"FileName: " + parResult.getFileName() + " Partial vowel count: " + parResult.getCount());
		} else if (message instanceof FinalResultMessage) {
			FinalResultMessage result = (FinalResultMessage) message;
			System.out.println(
					"FileName: " + result.getFileName() + " The Final vowel count: " + result.getVowelCount());
			

		} else if (message instanceof UpdatePredictionMessage) {
			UpdatePredictionMessage updResult = (UpdatePredictionMessage) message;
			ActorRef test = getSender();
			System.out.println(
					"FileName: " + updResult.getFileName() + " Updated Feedback: " + updResult.getFeedBackScore());
			getSelf().tell(new FinalResultMessage(updResult.getFileName(),updResult.getActCount()), null);
			test.tell(new NextFileMessage(), null);
		}if (message instanceof EndMessage) {
			System.out.println("Stopping the operation: file Processing");
		} else {
			unhandled(message);
		}
	}

}
