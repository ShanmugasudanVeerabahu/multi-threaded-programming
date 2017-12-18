package main.vowelcount.akka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import actor.vowelcount.akka.Estimator;
import actor.vowelcount.akka.Listener;
import actor.vowelcount.akka.Master;
import actor.vowelcount.akka.Worker;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import message.vowelcount.akka.StartMessage;

/**
 * Main class for your estimation actor system.
 *
 * @author shanmugasudan
 *
 */
public class User {
	
	private static final String filePath = ".\\input\\Akka_Text";
	private static final Map<String,List<String>> fileMap = new LinkedHashMap<>();
	private static String dirName ="Akka_Text" ;

	public static void main(String[] args) throws Exception {

		/*
		 * Create the Estimator Actor and send it the StartProcessingFolder
		 * message. Once you get back the response, use it to print the result.
		 * Remember, there is only one actor directly under the ActorSystem.
		 * Also, do not forget to shutdown the actorsystem
		 */

		List<Path> files = Files.list(Paths.get(User.filePath)).collect(Collectors.toList());
		readFile(files);
		final ActorSystem system = ActorSystem.create("EstimationSystem");
		final int workerCount = 2;
		final ActorRef listener = system.actorOf(Props.create(Listener.class),"listener");
		final ActorRef estimator = system.actorOf(Props.create(Estimator.class,listener),"estimator");
		final ActorRef master = system.actorOf(Props.create(Master.class,workerCount,listener,dirName,estimator),"master");
		
		master.tell(new StartMessage(fileMap),null);
	}
	
	public static void readFile(List<Path> filePath) throws IOException{
		for(Path path: filePath){
		String fileName = path.getFileName().toString();
		List<String> fileContent = Files.readAllLines(path);
		
		fileMap.put(fileName,fileContent); 
		}							 
	}
		
	
}
