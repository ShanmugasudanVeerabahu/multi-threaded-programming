package message.vowelcount.akka;

/**
 * POJO that displays the final result count of vowels in a file
 * 
 * @author shanmugasudan
 *
 */
public class FinalResultMessage {

	private String fileName;
	private double vowelCount;

	public FinalResultMessage(final String fileName, final double vowelCount) {
		this.fileName = fileName;
		this.vowelCount = vowelCount;
	}

	public String getFileName() {
		return fileName;
	}

	public double getVowelCount() {
		return vowelCount;
	}

}
