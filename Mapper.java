import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;


public class Mapper implements Callable<MapperOutput> {
	
	private MapperInput input;
	public Mapper(MapperInput input) {
		this.input = input;
	}
	
	@Override
	public MapperOutput call() throws Exception {
		MapperOutput mo = new MapperOutput(input.getFileName());
		int s = input.getStart();
		int f = input.getFinish();
		byte [] fileBytes = input.getFileContent();
		
		/* Adjust start and finish positions if necessary */
		
		// find the actual starting position for this fragment (do not start in the middle of a word)
		if(s!= 0 && fileBytes[s - 1] != ' ') 
			while(s < f && fileBytes[s - 1] != ' ') 
				s++;
		
		// find the actual end of the fragment (the end of the last word) 
		while(f < fileBytes.length - 1 && fileBytes[f] != ' ')
			f++;

		/* Split fragment into words and count them */
		
		String fileContents = new String(Arrays.copyOfRange(fileBytes, s, f), "US-ASCII");
		// remove all characters except lower-case letters and spaces. replace with spaces
		fileContents = fileContents.toLowerCase().replaceAll("[^a-z ]", " ");
		// use StringTokenizer to split fragment
		StringTokenizer st = new StringTokenizer(fileContents, " ");
		while(st.hasMoreTokens()) {
			mo.addWord(st.nextToken());
		}
		
		return mo;
	}

}
