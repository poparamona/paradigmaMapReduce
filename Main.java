import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Main {

	public static void main(String [] args) throws Exception {
		
		/* Get command line arguments */
		if(args.length != 3) { // wrong numer of arguments
			throw new Exception("Invalid number of arguments");
		}
		
		int NT; // number of worker threads
		try {
			NT = Integer.parseInt(args[0]);
		}catch(NumberFormatException nfe) {
			throw new Exception("Argument 1 must be an integer");
		}
		
		// input and output files
		String inputFileName = args[1], 
				outputFileName = args[2];
		
		BufferedReader inp;
		try { 
			// (attempt to) open input file
			inp = new BufferedReader(new FileReader(new File(inputFileName)));
		} catch (FileNotFoundException e) {
			throw new Exception("Unable to open " + inputFileName);
		}
		
		BufferedWriter out;
		try {
			// (attempt to) open output file
			out = new BufferedWriter(new FileWriter(new File(outputFileName)));
		} catch (IOException e) {
			inp.close();
			throw new Exception("Unable to open " + outputFileName);
		}
		
		/* Read input file */
		String fileToCompare;
		int D, ND;
		double X;
		List<String> fileNames;
		try {
			fileToCompare = inp.readLine(); // file to be checked
			D = Integer.parseInt(inp.readLine()); // number of bytes in mapper fragment
			X = Double.parseDouble(inp.readLine()); // similarity threshold
			ND = Integer.parseInt(inp.readLine()); // number of documents
			
			fileNames = new ArrayList<>(); // document names
			for(int i = 0; i < ND; i++) {
				fileNames.add(inp.readLine());
			}
			
			if(!fileNames.contains(fileToCompare)) // in case the file for which we want to compute similarity is not in the list
				fileNames.add(fileToCompare);
		} catch (IOException e1) {
			out.close();
			throw new Exception("Input file structure is not valid");
		} finally {
			inp.close(); // done reading input files
		}
		
		/* Begin map phase */
		ExecutorService mapExecutorService = Executors.newFixedThreadPool(NT); // mapper workpool
		List<Future<MapperOutput>> mapperFutures = new ArrayList<>(); // Future objects that will hold the mapper results
		
		for(String file : fileNames) {
			/* Read document content */
			byte[] fileContent;
			try {
				fileContent = Files.readAllBytes(Paths.get(file));
			} catch (IOException e) {
				out.close();
				throw new Exception("Unable to read " + file);
			}
			
			/* Create mapper tasks and add to workpool */
			int size = fileContent.length;
			for(int i = 0; i < size; i += D) {
				MapperInput mi = new MapperInput(file, fileContent, i, Math.min(i + D, size));
				Mapper mapper = new Mapper(mi);
				Future<MapperOutput> fmo = mapExecutorService.submit(mapper);
				mapperFutures.add(fmo);
			}	
		}
		mapExecutorService.shutdown(); // done mapping
		
		/* Shuffle mapping phase results => lists of partial vectors for each file */
		Map<String, List<MapperOutput>> mapperOutputs = new HashMap<>();
		for(String file : fileNames)
			mapperOutputs.put(file, new ArrayList<MapperOutput>());
		for(Future<MapperOutput> f : mapperFutures) {
			MapperOutput mo = f.get();
			mapperOutputs.get(mo.getFileName()).add(mo);
		}
		
		/* Begin reduce phase */
		ExecutorService reduceExecutorService = Executors.newFixedThreadPool(NT); // reducer workpool
		List<Future<ReducerOutput>> reducerFutures = new ArrayList<>(); // Future objects that will hold the reducer results
		for(String file : fileNames) {
			if(!file.equals(fileToCompare)) {
				ReducerInput ri = new ReducerInput(mapperOutputs.get(fileToCompare), mapperOutputs.get(file));
				Reducer reducer = new Reducer(ri);
				Future<ReducerOutput> fro = reduceExecutorService.submit(reducer);
				reducerFutures.add(fro);
			}
		}
		
		reduceExecutorService.shutdown(); // done reducing
		
		/* Write results to output file */
		out.write("Rezultate pentru: (" + fileToCompare + ")\n\n");
		for(Future<ReducerOutput> fro : reducerFutures) {
			// truncate to 3 decimals
			double sim = Math.floor(fro.get().getSimilarity() * 1000) / 1000;
			// compare to threshold and display
			if(sim > X)
				out.write(fro.get().getFileName() + " (" + sim + "%)\n");
		}
		out.close(); // done writing
	}
}
