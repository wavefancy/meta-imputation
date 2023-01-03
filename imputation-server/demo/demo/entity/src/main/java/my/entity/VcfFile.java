package my.entity;

import java.util.Set;

public class VcfFile {
	
	private Set<Integer> chunks;

	private Set<String> chromosomes;

	private Set<String> rawChromosomes;
	
	private String vcfFilename;

	private String indexFilename;

	private int noSamples = -1;

	private int noSnps = -1;

	private boolean phased = true;
	
	private boolean chrX = false;
	
	private boolean phasedAutodetect = true;

	private int chunkSize;
	
	private boolean chrPrefix;
	
	public VcfFile() {

	}

	public int getNoSnps() {
		return noSnps;
	}

	public void setNoSnps(int noSnps) {
		this.noSnps = noSnps;
	}

	public Set<String> getChromosomes() {
		return chromosomes;
	}

	public String getChromosome() {
		return chromosomes.iterator().next();
	}

	public String getRawChromosome() {
		return rawChromosomes.iterator().next();
	}
	
	public String getVcfFilename() {
		return vcfFilename;
	}

	public String getIndexFilename() {
		return indexFilename;
	}

	public int getNoSamples() {
		return noSamples;
	}

	public Set<Integer> getChunks() {
		return chunks;
	}

	public void setChunks(Set<Integer> chunks) {
		this.chunks = chunks;
	}

	public void setVcfFilename(String vcfFilename) {
		this.vcfFilename = vcfFilename;
	}

	public void setIndexFilename(String indexFilename) {
		this.indexFilename = indexFilename;
	}

	public void setNoSamples(int noSamples) {
		this.noSamples = noSamples;
	}

	public void setChromosomes(Set<String> chromosomes) {
		this.chromosomes = chromosomes;
	}
	
	public void setRawChromosomes(Set<String> rawChromosomes) {
		this.rawChromosomes = rawChromosomes;
	}
	
	public void setChrPrefix(boolean chrPrefix){
		this.chrPrefix = chrPrefix;
	}
	
	public boolean hasChrPrefix(){
		return this.chrPrefix;
	}

	public void setPhased(boolean phased) {
		this.phased = phased;
	}

	public boolean isPhased() {
		return phased;
	}

	public String toString() {
		return "Chromosome: " + getChromosome() + "\n Samples: "
				+ getNoSamples() + "\n Snps: " + getNoSnps() + "\n Chunks: "
				+ getChunks().size();
	}

	public String[] getFilenames() {
		return new String[] { getVcfFilename(), getIndexFilename() };
	}

	public String getType() {

		if (phased) {

			return "VCF-PHASED";

		} else {

			return "VCF-UNPHASED";

		}
	}

	public boolean isPhasedAutodetect() {
		return phasedAutodetect;
	}

	public void setPhasedAutodetect(boolean phasedAutodetect) {
		this.phasedAutodetect = phasedAutodetect;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}

	public boolean isChrX() {
		return chrX;
	}

	public void setChrX(boolean chrX) {
		this.chrX = chrX;
	}

}
