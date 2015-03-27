
public class ReducerOutput {

	private String fileName;
	private double similarity;
	
	public ReducerOutput(String fileName, double similarity) {
		this.fileName = fileName;
		this.similarity = similarity;
	}
	
	public String getFileName() {
		return fileName;
	}

	public double getSimilarity() {
		return similarity;
	}
}
