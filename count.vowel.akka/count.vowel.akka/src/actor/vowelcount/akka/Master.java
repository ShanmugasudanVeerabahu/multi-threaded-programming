package actor.vowelcount.akka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinPool;
import message.vowelcount.akka.StartMessage;
import message.vowelcount.akka.UpdatePredictionMessage;
import message.vowelcount.akka.CountMessage;
import message.vowelcount.akka.EndMessage;
import message.vowelcount.akka.FileMessage;
import message.vowelcount.akka.FinalResultMessage;
import message.vowelcount.akka.FirstFileMessage;
import message.vowelcount.akka.GreetMessage;
import message.vowelcount.akka.NextFileMessage;
import message.vowelcount.akka.PredictCountMessage;

/**
 * Master class that coordinates the workers within the system
 * 
 * @author shanmugasudan
 *
 */
public class Master extends UntypedActor {

	private final ActorRef listener;
	private final ActorRef workRouter;
	private final ActorRef estimator;
	private final int workerCount;
	private Map<String, List<List<String>>> workerFilesMap;
	private String currFileName;
	private List<String> currentFile;
	private String directoryName;
	private Set<Entry<String, List<String>>> mainMapEntry;
	private Iterator<String> fileNamesSet;
	private Map<String, Integer> vowelsInFileMap;

	public Master(final int workerCount, ActorRef listener, final String directoryName,final ActorRef estimator) {

		this.workRouter = getContext().actorOf(new RoundRobinPool(workerCount).props(Props.create(Worker.class)),
				"workRouter");
		this.listener = listener;
		this.workerCount = workerCount;
		this.directoryName = directoryName;
		this.estimator = estimator;
		this.vowelsInFileMap = new HashMap<>();
	}

	@Override
	public void onReceive(Object message) throws Throwable {
		// TODO Auto-generated method stub
		if (message instanceof StartMessage) {
			StartMessage temp = (StartMessage) message;
			mainMapEntry = temp.getFileMap().entrySet();
			processMap();
			listener.tell(new GreetMessage(), getSelf());

		} else if (message instanceof FirstFileMessage) {
			currFileName = fileNamesSet.next();
			List<List<String>> files = workerFilesMap.get(currFileName);
			for (int i = 0; i < workerCount; i++) {
				workRouter.tell(new FileMessage(directoryName, currFileName, files.get(i)), getSelf());
			}

		} else if (message instanceof CountMessage) {
			CountMessage temp = (CountMessage) message;
			String fileName = temp.getFileName();
			int vowelCount = temp.getCurrCount();
			if (vowelsInFileMap.containsKey(fileName)) {
				int currCount = vowelCount;
				vowelCount += vowelsInFileMap.get(fileName);
				vowelsInFileMap.put(fileName, vowelCount);
				estimator.tell(new UpdatePredictionMessage(fileName, currCount,vowelCount,0.0), getSelf());
			} else{
				vowelsInFileMap.put(fileName, vowelCount);
				estimator.tell(new PredictCountMessage(fileName, vowelCount), null);
			}
		} else if (message instanceof NextFileMessage) {
			if (fileNamesSet.hasNext()) {
				currFileName = fileNamesSet.next();
				List<List<String>> files = workerFilesMap.get(currFileName);
				for (int i = 0; i < workerCount; i++) {
					workRouter.tell(new FileMessage(directoryName, currFileName, files.get(i)), getSelf());
				}
			} else
				getSelf().tell(new EndMessage(), null);
		} else if (message instanceof EndMessage) {
			listener.tell(new EndMessage(), null);
			getContext().system().terminate();
		} else {
			unhandled(message);
		}

	}

	@SuppressWarnings("serial")
	public void processMap() {
		workerFilesMap = new LinkedHashMap<>();
		for (Entry<String, List<String>> test : mainMapEntry) {
			currFileName = test.getKey();
			currentFile = test.getValue();
			int fileSize = currentFile.size();

			List<String> file1 = new ArrayList<>(currentFile.subList(0, fileSize / workerCount));
			List<String> file2 = new ArrayList<>(currentFile.subList(fileSize / workerCount + 1, fileSize));
			List<List<String>> valFiles = new ArrayList<List<String>>() {
				{
					add(file1);
					add(file2);
				}
			};
			workerFilesMap.put(currFileName, valFiles);
		}
		fileNamesSet = workerFilesMap.keySet().iterator();
	}

}
