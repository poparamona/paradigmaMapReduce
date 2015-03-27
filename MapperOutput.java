import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MapperOutput {

	private String fileName; // file that the fragment belongs to
	private Map<String, Integer> partialCount; // (word, count) pairs - result of indexing
	
	public MapperOutput(String fileName) {
		this.fileName = fileName;
		this.partialCount = new HashMap<String, Integer>();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/* Used to get the fragment word count */
	public int getCount(String word) {
		Integer count = partialCount.get(word);
		if(count == null) // not found in this fragment
			return 0;
		return count; // found
	}
	
	public Set<String> getWords() {
		return partialCount.keySet();
	}

	/* Used during word counting in the map phase */
	public void addWord(String word) {
		if(partialCount.containsKey(word)) {
			// word is already in the "dictionary". increment count
			partialCount.put(word, partialCount.get(word) + 1);
		} else {
			// add word to "dictionary"
			partialCount.put(word, 1);
		}
	}
}
