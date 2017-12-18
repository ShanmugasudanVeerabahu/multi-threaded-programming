package message.vowelcount.akka;

/**
 * POJO that updates feedback score based on actual total vowel count present in current file
 * @author shanmugasudan
 *
 */
public class UpdatePredictionMessage {

	private double currCount;
	private double actCount;
	private double feedbackScore;
	private String fileName;
	
	public UpdatePredictionMessage(final String fileName, final double currCount, final double actCount,final double feedbackScore){
		this.actCount = actCount;
		this.currCount = currCount;
		this.fileName = fileName;
		this.feedbackScore = feedbackScore;
	}

	public double getCurrCount() {
		return currCount;
	}

	public double getActCount() {
		return actCount;
	}

	public String getFileName() {
		return fileName;
	}
	
	public double getFeedBackScore(){
		return feedbackScore;
	}
	
}
