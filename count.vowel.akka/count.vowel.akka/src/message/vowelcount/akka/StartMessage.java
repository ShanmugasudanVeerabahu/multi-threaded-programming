package message.vowelcount.akka;

import java.util.List;
import java.util.Map;

/**
 * POJO that initiates that trigger master to start processing
 * @author shanmugasudan
 *
 */
public class StartMessage {

	private Map<String,List<String>> fileMap;
	
	public StartMessage(Map<String,List<String>> inputFilesMap){
		this.fileMap = inputFilesMap;
	}
	
	public Map<String,List<String>> getFileMap(){
		return fileMap;
	}
}
