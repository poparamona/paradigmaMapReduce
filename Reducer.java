import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;


public class Reducer implements Callable<ReducerOutput> {

	private ReducerInput input;
	
	public Reducer(ReducerInput input) {
		this.input = input;
	}
	
	private Map<String, Integer> aggregateFileData(List<MapperOutput> fileData) {
		
		/* Get the set of words in the file (reunion of sets from each fragment) */
		Set<String> words = new HashSet<>();
		for(MapperOutput mo : fileData) {
			words.addAll(mo.getWords());
		}
		
		/* Get the total count for each word in the entire file */
		Map<String, Integer> wordCount = new HashMap<String, Integer>();
		for(String word : words) {
			int count = 0;
			for(MapperOutput mo : fileData)
				count += mo.getCount(word);
			wordCount.put(word, count);
		}
		
		return wordCount;
	}
	
	@Override
	public ReducerOutput call() throws Exception {
		
		/* Compute total number of apparitions of each word / file */
		Map<String, Integer> file1 = aggregateFileData(input.getFile1Data());
		Map<String, Integer> file2 = aggregateFileData(input.getFile2Data());
		
		/* Compute total number of words / file */
		int total1 = 0, total2 = 0;
		for(Map.Entry<String, Integer> e : file1.entrySet())
			total1 += e.getValue();
		for(Map.Entry<String, Integer> e : file2.entrySet())
			total2 += e.getValue();
		
		/* Compute similarity as sum(f(t, file1) * f(t, file2)) */
		double similarity = 0;
		for(Map.Entry<String, Integer> e : file1.entrySet()) {
			Integer f2 = file2.get(e.getKey());
			if(f2 != null) // if the word is not in the file then f(word, file) = 0
				similarity += ((double)e.getValue() / total1) * ((double)f2 / total2);
		}
		return new ReducerOutput(input.getFileName(), similarity * 100);
	}

}
