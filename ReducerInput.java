import java.util.List;


public class ReducerInput {
	
	private String fileName; // name of compared file
	private List<MapperOutput> file1Data, file2Data; // partial vectors for each file
	
	public ReducerInput(List<MapperOutput> file1Data, List<MapperOutput> file2Data) {
		this.file1Data = file1Data;
		this.file2Data = file2Data;
		this.fileName = file2Data.get(0).getFileName(); 
	}
	
	public String getFileName() {
		return fileName;
	}

	public List<MapperOutput> getFile1Data() {
		return file1Data;
	}

	public List<MapperOutput> getFile2Data() {
		return file2Data;
	}	
}
