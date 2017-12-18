package actor.vowelcount.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import message.vowelcount.akka.NextFileMessage;
import message.vowelcount.akka.PartialResultMessage;
import message.vowelcount.akka.PredictCountMessage;
import message.vowelcount.akka.UpdatePredictionMessage;

public class Estimator extends UntypedActor {

	private double feedBack =1;
	private final ActorRef listener;
	private ActorRef mySender;
	public Estimator(final ActorRef listener){
		this.listener = listener;
	}
	@Override
	public void onReceive(Object message) throws Throwable {
		// TODO Auto-generated method stub
		if(message instanceof PredictCountMessage){
			PredictCountMessage temp = (PredictCountMessage) message;
			int parCount = temp.getCount();
			double predCount = parCount *2 * feedBack;
			listener.tell(new PartialResultMessage(temp.getFileName(),predCount), null);
		}else if(message instanceof UpdatePredictionMessage){
			UpdatePredictionMessage temp = (UpdatePredictionMessage) message;
			mySender = getSender();
			double predictCount = temp.getCurrCount() *2;
			double ratio = predictCount/temp.getActCount();
			if(ratio < 1)
				feedBack = 0.8;
			else
				feedBack  = 1.2;
			listener.tell(new UpdatePredictionMessage(temp.getFileName(),temp.getCurrCount(),temp.getActCount(),feedBack), getSelf());
			
		}	else if(message instanceof NextFileMessage){
			//NextFileMessage test = (NextFileMessage) message;
			mySender.tell(message, null);
		}else{
			unhandled(message);
		}
	}

}
