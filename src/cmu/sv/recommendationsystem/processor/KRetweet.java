package cmu.sv.recommendationsystem.processor;

public class KRetweet {
	
	
	private double sigmaRetweet=TrustModelWeights.sigmaRetweet;
	
	private double numofRetweetRecent=0;
	
	private double numofRetweetIntermediate=0;
	
	private double numofRetweetOld=0;
	
	

	public double getSigmaRetweet() {
		return sigmaRetweet;
	}

	public void setSigmaRetweet(double sigmaRetweet) {
		this.sigmaRetweet = sigmaRetweet;
	}

	public double getNumofRetweetRecent() {
		return numofRetweetRecent;
	}

	public void setNumofRetweetRecent(double numofRetweetRecent) {
		this.numofRetweetRecent = numofRetweetRecent;
	}

	public double getNumofRetweetIntermediate() {
		return numofRetweetIntermediate;
	}

	public void setNumofRetweetIntermediate(double numofRetweetIntermediate) {
		this.numofRetweetIntermediate = numofRetweetIntermediate;
	}

	public double getNumofRetweetOld() {
		return numofRetweetOld;
	}

	public void setNumofRetweetOld(double numofRetweetOld) {
		this.numofRetweetOld = numofRetweetOld;
	}
	
}
