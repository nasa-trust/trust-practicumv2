package cmu.sv.recommendationsystem.processor;

public class TwitterTrustModel {
	
	private double twitterTrustFactor;
	
	private KRetweet kRetweet; 
	
	private TwitterUser twitterUser;
	
	

	public TwitterUser getTwitterUser() {
		return twitterUser;
	}

	public void setTwitterUser(TwitterUser twitterUser) {
		this.twitterUser = twitterUser;
	}

	public double getTwitterTrustFactor() {
		return twitterTrustFactor;
	}

	public void setTwitterTrustFactor(double twitterTrustFactor) {
		this.twitterTrustFactor = twitterTrustFactor;
	}

	public KRetweet getkRetweet() {
		return kRetweet;
	}

	public void setkRetweet(KRetweet kRetweet) {
		this.kRetweet = kRetweet;
	}
		
}
