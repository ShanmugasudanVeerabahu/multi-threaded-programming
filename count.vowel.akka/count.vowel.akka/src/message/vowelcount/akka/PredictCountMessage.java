package message.vowelcount.akka;
/**
 * POJO that is used by estimator to predict the total vowel count in entire file
 * @author shanmugasudan
 *
 */
public class PredictCountMessage {

	private String fileName;
	private int count;
	
	public PredictCountMessage(final String fileName, final int count){
		this.fileName = fileName;
		this.count = count;
	}
	
	public String getFileName(){
		return fileName;
	}
	public int getCount(){
		return count;
	}
}
