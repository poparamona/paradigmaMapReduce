
public class MapperInput {

	private String fileName; // file name for future reference
	private byte [] fileContent; // file content for processing
	private int start, finish; // (theoretical) start and finish positions
	
	public MapperInput(String fileName, byte [] fileContent, int start, int finish) {
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.start = start;
		this.finish = finish;
	}

	public String getFileName() {
		return fileName;
	}

	public int getStart() {
		return start;
	}

	public int getFinish() {
		return finish;
	}

	public byte [] getFileContent() {
		return fileContent;
	}
}
