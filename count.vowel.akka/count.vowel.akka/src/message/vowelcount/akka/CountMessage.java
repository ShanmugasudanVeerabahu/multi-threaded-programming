package message.vowelcount.akka;

/**
 * POJO that contains the current count of vowels processed
 * by a worker actor
 * @author shanmugasudan
 *
 */
public class CountMessage {

	private final int currCount;
	private final String fileName;
	
	public CountMessage(final String fileName, final int count){
		this.currCount = count;
		this.fileName = fileName;
	}

	public int getCurrCount() {
		return currCount;
	}
	public String getFileName(){
		return fileName;
	}
	
}
