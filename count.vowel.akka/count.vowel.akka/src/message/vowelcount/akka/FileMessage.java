package message.vowelcount.akka;

import java.util.List;

/**
 * POJO that provides details about file and its directory
 * @author shanmugasudan
 *
 */
public class FileMessage {

	private final String fileName;
	private final String dirName;
	private final List<String> fileData;
	
	public FileMessage(final String dirName, final String fileName,final List<String> fileData){
		this.fileName = fileName;
		this.dirName = dirName;
		this.fileData = fileData;
	}

	public String getFileName() {
		return fileName;
	}

	public String getDirName() {
		return dirName;
	}
	public List<String> getFileData(){
		return fileData;
	}
	
}
