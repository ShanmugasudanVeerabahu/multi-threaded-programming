package actor.vowelcount.akka;


import java.util.Arrays;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import akka.actor.UntypedActor;
import message.vowelcount.akka.CountMessage;
import message.vowelcount.akka.FileMessage;
/**
 * Worker class that counts the no.of vowels in given file
 * @author shanmugasudan
 *
 */
public class Worker extends UntypedActor {
	
	private int vowelCount =0;

	@Override
	public void onReceive(Object message) throws Throwable {
		// TODO Auto-generated method stub
		if(message instanceof FileMessage){
			FileMessage temp = (FileMessage) message;
			String fileName = temp.getFileName();
			countVowels(temp.getFileData());
			getSender().tell(new CountMessage(fileName,vowelCount), null);
		}
	}
	
	public void countVowels(List<String> fileData){
		for(String l: fileData){		
			for(Character c:l.toLowerCase().toCharArray()){
				switch(c){
				case 'a':
				case 'e':
				case 'i':
				case 'o':
				case 'u':
				case 'y':
					vowelCount++;
					break;
				}
			}
		}									
	}
}
