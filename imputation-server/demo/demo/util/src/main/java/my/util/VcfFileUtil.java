//package my.util;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.Vector;
//import java.util.zip.GZIPOutputStream;
//
////import org.apache.hadoop.conf.Configuration;
////import org.apache.hadoop.fs.FSDataInputStream;
////import org.apache.hadoop.fs.FileStatus;
////import org.apache.hadoop.fs.FileSystem;
////import org.apache.hadoop.fs.Path;
//
//import genepi.command.Command;
//import genepi.io.FileUtil;
//import genepi.io.text.LineReader;
//import htsjdk.variant.vcf.VCFFileReader;
//import my.entity.VcfFile;
//
//public class VcfFileUtil {
//
//	public static String TABIX_PATH = "bin/";
//
//	public static void setTabixBinary(String binaries) {
//		TABIX_PATH = binaries;
//	}
//	
//	public static String getBinary(){
//		return TABIX_PATH;
//	}
//	
//	private static VcfFile createVcfFileStructure (
//			String vcfFilename,
//			Set<Integer> chunks,
//			Set<String> chromosomes, 
//			Set<String> rawChromosomes, 
//			int noSnps, int noSamples,
//			boolean phased, 
//			boolean phasedAutodetect, 
//			boolean firstLine, 
//			HashSet<String> samples,
//			int chunksize) {
//		VcfFile vcfFile = new VcfFile();
//		vcfFile.setVcfFilename(vcfFilename);
//		vcfFile.setIndexFilename(vcfFilename + ".tbi");
//		vcfFile.setNoSnps(noSnps);
//		vcfFile.setNoSamples(noSamples);
//		vcfFile.setChunks(chunks);
//		vcfFile.setChromosomes(chromosomes);
//		
//		boolean hasChrPrefix = false;
//		for (String chromosome: rawChromosomes){
//			if (chromosome.startsWith("chr")){
//				hasChrPrefix = true;
//			}
//		}
//		vcfFile.setRawChromosomes(rawChromosomes);
//		vcfFile.setChrPrefix(hasChrPrefix);
//		vcfFile.setPhased(phased);
//		vcfFile.setPhasedAutodetect(phasedAutodetect);
//		vcfFile.setChunkSize(chunksize);
//		return vcfFile;
//	}
//	
//	
//	private static int getnoSamples(String vcfFilename) {
//		VCFFileReader reader = new VCFFileReader(new File(vcfFilename), false);
//		int noSamples = reader.getFileHeader().getGenotypeSamples().size();
//		reader.close();
//		return noSamples;
//	}
//	
//	private static VcfFile getInfoFromVcf(String vcfFilename, LineReader lineReader, int chunksize) throws Exception{	
//		Set<Integer> chunks = new HashSet<Integer>();
//		Set<String> chromosomes = new HashSet<String>();
//		Set<String> rawChromosomes = new HashSet<String>();
//		int noSnps = 0;
//		int noSamples = getnoSamples(vcfFilename);
//		
//		boolean phased = true;
//		boolean phasedAutodetect = true;
//		boolean firstLine = true;
//		HashSet<String> samples = new HashSet<>();
//		
//		while (lineReader.next()) {
//			String line = lineReader.get();
//			if(line.startsWith("#")) {	
//				if (!line.startsWith("#CHROM"))
//					continue;		
//				String[] tiles = line.split("\t");
//				// check sample names, stop when not unique
//				for (int i = 0; i < tiles.length; i++) {
//					String sample = tiles[i];
//					if (samples.contains(sample)) {
//						throw new IOException("Two individuals or more have the following ID: " + sample);
//					}
//					samples.add(sample);
//				}
//			}
//			
//			String tiles[] = line.split("\t", 10);
//			if (tiles.length < 3) {
//				throw new IOException("The provided VCF file is not tab-delimited");
//			}
//			
//			String chromosome = tiles[0];
//			rawChromosomes.add(chromosome);
//			chromosome = chromosome.replaceAll("chr", "");
//			int position = Integer.parseInt(tiles[1]);
//
//			if (phased) {
//				boolean containsSymbol = tiles[9].contains("/");
//				if (containsSymbol) {
//					phased = false;
//				}
//			}
//
//			if (firstLine) {
//				boolean containsSymbol = tiles[9].contains("/") || tiles[9].contains(".");
//				if (!containsSymbol) {
//					phasedAutodetect = true;
//				} else {
//					phasedAutodetect = false;
//				}
//				firstLine = false;
//			}
//
//			// TODO: check that all are phased
//			// context.getGenotypes().get(0).isPhased();
//			chromosomes.add(chromosome);
//			if (chromosomes.size() > 1) {
//				throw new IOException(
//						"The provided VCF file contains more than one chromosome. Please split your input VCF file by chromosome");
//			}
//
//			String ref = tiles[3];
//			String alt = tiles[4];
//
//			if (ref.equals(alt)) {
//				throw new IOException("The provided VCF file is malformed at variation " + tiles[2]
//						+ ": reference allele (" + ref + ") and alternate allele  (" + alt + ") are the same.");
//			}
//
//			int chunk = position / chunksize;
//			if (position % chunksize == 0) {
//				chunk = chunk - 1;
//			}
//			chunks.add(chunk);
//			noSnps++;
//		}
//		
//		VcfFile vcfFile = createVcfFileStructure (
//				vcfFilename, chunks, chromosomes, rawChromosomes, 
//				noSnps, noSamples, phased, phasedAutodetect, 
//				firstLine, samples, chunksize);
//		return vcfFile;
//	}
//	
//	public static VcfFile load(String vcfFilename, int chunksize, boolean createIndex) throws IOException {
//		try {
//			LineReader lineReader = new LineReader(vcfFilename);
//			VcfFile vcfFile = getInfoFromVcf(vcfFilename, lineReader, chunksize);
//			lineReader.close();
//			
//			// create index
//			if (createIndex && !new File(vcfFilename + ".tbi").exists()) {
//
//				File command = new File(TABIX_PATH);
//				if (!command.canExecute()){
//					command.setExecutable(true, false);
//				}
//				
//				Command tabix = new Command(TABIX_PATH);
//				tabix.setParams("-f", "-p", "vcf", vcfFilename);
//				tabix.saveStdErr("tabix.output");
//				int returnCode = tabix.execute();
//				if (returnCode != 0) {
//					throw new IOException(
//							"The provided VCF file is malformed. Error during index creation: "
//									+ FileUtil.readFileAsString("tabix.output"));
//				}
//			}
//			return vcfFile;
//		} catch (Exception e) {
//			throw new IOException(e.getMessage());
//		}
//	}
//
//	public static Set<String> validChromosomes = new HashSet<String>();
//
//	static {
//
//		validChromosomes.add("1");
//		validChromosomes.add("2");
//		validChromosomes.add("3");
//		validChromosomes.add("4");
//		validChromosomes.add("5");
//		validChromosomes.add("6");
//		validChromosomes.add("7");
//		validChromosomes.add("8");
//		validChromosomes.add("9");
//		validChromosomes.add("10");
//		validChromosomes.add("11");
//		validChromosomes.add("12");
//		validChromosomes.add("13");
//		validChromosomes.add("14");
//		validChromosomes.add("15");
//		validChromosomes.add("16");
//		validChromosomes.add("17");
//		validChromosomes.add("18");
//		validChromosomes.add("19");
//		validChromosomes.add("20");
//		validChromosomes.add("21");
//		validChromosomes.add("22");
//		validChromosomes.add("23");
//		validChromosomes.add("X");
//		validChromosomes.add("MT");
//
//		validChromosomes.add("chr1");
//		validChromosomes.add("chr2");
//		validChromosomes.add("chr3");
//		validChromosomes.add("chr4");
//		validChromosomes.add("chr5");
//		validChromosomes.add("chr6");
//		validChromosomes.add("chr7");
//		validChromosomes.add("chr8");
//		validChromosomes.add("chr9");
//		validChromosomes.add("chr10");
//		validChromosomes.add("chr11");
//		validChromosomes.add("chr12");
//		validChromosomes.add("chr13");
//		validChromosomes.add("chr14");
//		validChromosomes.add("chr15");
//		validChromosomes.add("chr16");
//		validChromosomes.add("chr17");
//		validChromosomes.add("chr18");
//		validChromosomes.add("chr19");
//		validChromosomes.add("chr20");
//		validChromosomes.add("chr21");
//		validChromosomes.add("chr22");
//		validChromosomes.add("chr23");
//		validChromosomes.add("chrX");
//		validChromosomes.add("chrMT");
//
//	
//	}
//
//	public static boolean isValidChromosome(String chromosome) {
//		return validChromosomes.contains(chromosome);
//	}
//
//	public static boolean isChrX(String chromosome) {
//		return chromosome.equals("X") || chromosome.equals("23") || chromosome.equals("chrX") || chromosome.equals("chr23");
//	}
//	
//	public static boolean isChrMT(String chromosome) {
//		return chromosome.equals("MT") || chromosome.equals("chrMT");
//	}
//
//
//	public static void mergeGz(String local, String hdfs, String ext) throws FileNotFoundException, IOException {
//		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(local));
//		merge(out, hdfs, ext);
//	}
//
//
//	public static void merge(OutputStream out, String hdfs, String ext) throws IOException {
//	/*
//		Configuration conf = HdfsUtil.getConfiguration();
//
//		FileSystem fileSystem = FileSystem.get(conf);
//		Path pathFolder = new Path(hdfs);
//		FileStatus[] files = fileSystem.listStatus(pathFolder);
//
//		List<String> filenames = new Vector<String>();
//
//		if (files != null) {
//
//			// filters by extension and sorts by filename
//			for (FileStatus file : files) {
//				if (!file.isDir() && !file.getPath().getName().startsWith("_")
//						&& (ext == null || file.getPath().getName().endsWith(ext))) {
//					filenames.add(file.getPath().toString());
//				}
//			}
//			Collections.sort(filenames);
//
//			boolean firstFile = true;
//			boolean firstLine = true;
//
//			for (String filename : filenames) {
//
//				Path path = new Path(filename);
//
//				FSDataInputStream in = fileSystem.open(path);
//
//				LineReader reader = new LineReader(in);
//
//				while (reader.next()) {
//
//					String line = reader.get();
//
//					if (line.startsWith("#")) {
//
//						if (firstFile) {
//							if (!firstLine) {
//								out.write('\n');
//							}
//							out.write(line.getBytes());
//							firstLine = false;
//						}
//
//					} else {
//
//						if (!firstLine) {
//							out.write('\n');
//						}
//						out.write(line.getBytes());
//						firstLine = false;
//					}
//
//				}
//
//				in.close();
//				firstFile = false;
//
//			}
//
//			out.close();
//		}
//*/
//	}
//	
//	public static void createIndex(String vcfFilename, boolean force) throws IOException{
//		if (force){
//			if (new File(vcfFilename + ".tbi").exists()){
//				new File(vcfFilename + ".tbi").delete();
//			}
//		}
//		if (!new File(vcfFilename + ".tbi").exists()) {
//			createIndex(vcfFilename);
//		}
//	}
//
//
//	public static void createIndex(String vcfFilename) throws IOException{
//		Command tabix = new Command(TABIX_PATH);
//		tabix.setParams("-f", "-p", "vcf", vcfFilename);
//		tabix.saveStdErr("tabix.output");
//		int returnCode = tabix.execute();
//
//		if (returnCode != 0) {
//			throw new IOException(
//					"The provided VCF file is malformed. Error during index creation: "
//							+ FileUtil.readFileAsString("tabix.output"));
//		}
//		
//	}
//}