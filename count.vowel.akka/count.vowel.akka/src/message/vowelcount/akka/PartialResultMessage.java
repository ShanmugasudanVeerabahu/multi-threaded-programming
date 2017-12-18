package message.vowelcount.akka;
/**
 * POJO that describes partially processed file vowel count sent by one worker
 * @author shanmugasudan
 *
 */
public class PartialResultMessage {

	private String fileName;
	private double count;
	
	public PartialResultMessage(final String fileName, final double count){
		this.fileName = fileName;
		this.count = count;
	}
	
	public String getFileName(){
		return fileName;
	}
	public double getCount(){
		return count;
	}
}
