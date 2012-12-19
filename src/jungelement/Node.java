/**
 * 
 */
package jungelement;

import cmu.sv.recommendationsystem.processor.DBLPKnowledgeFactor;
import cmu.sv.recommendationsystem.processor.DBLPSocialFactor;
import cmu.sv.recommendationsystem.processor.DBLPTrustModel;
import cmu.sv.recommendationsystem.processor.KCoauthorship;
import cmu.sv.recommendationsystem.processor.KPaperPublished;
import cmu.sv.recommendationsystem.processor.TwitterTrustModel;

/**
 * @author NASA-Trust-Team
 * 
 */
public final class Node {
	private static int count;
	private int id;

	private DBLPTrustModel dblpTrustModel;
	private TwitterTrustModel twitterTrustModel;

	public TwitterTrustModel getTwitterTrustModel() {
		return twitterTrustModel;
	}

	public void setTwitterTrustModel(TwitterTrustModel twitterTrustModel) {
		this.twitterTrustModel = twitterTrustModel;
	}

	public DBLPTrustModel getDblpTrustModel() {
		return dblpTrustModel;
	}

	public void setDblpTrustModel(DBLPTrustModel dblpTrustModel) {
		this.dblpTrustModel = dblpTrustModel;
	}

	// private String twitterId;
	private String name;
	private long dblpUserId;
	private double popularity;

	public Node() {
		id = ++count;
	}

	public long getDBLPUserId() {
		return dblpUserId;
	}

	public void setDBLPUserId(long dblpUserId) {
		this.dblpUserId = dblpUserId;
	}

	// public String getTwitterId() {
	// return twitterId;
	// }
	//
	// public void setTwitterId(String twitterId) {
	// this.twitterId = twitterId;
	// }
	//
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = new String(name);
	}

	public double getPopularity() {
		return popularity;
	}

	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}

	public String toString() {
		DBLPKnowledgeFactor dblpKnowledgeFactor = dblpTrustModel
				.getDblpKnowledgeFactor();
		DBLPSocialFactor dblpSocialFactor = dblpTrustModel
				.getDblpSocialFactor();

		double twitterFactor = 0;
		if (twitterTrustModel != null) {
			twitterFactor = twitterTrustModel.getTwitterTrustFactor();
		}

		KPaperPublished kPaperPublished = dblpKnowledgeFactor
				.getkPaperPublished();
		KCoauthorship kCoauthorship = dblpSocialFactor.getkCoauthorship();
		// KCitePower kCitePower = kPaperPublished.getkCitePower();
		System.out.println("Trust Value for Charles: "
				+ dblpTrustModel.getTrustValue());

		System.out.println("Value of KPaperPublished: "
				+ kPaperPublished.getFinalKPaperPublished());
		// KCoauthorship kCoauthorship =
		// dblpTrustProcessor.calculateKCoauthorship(51);
		// System.out.println(kCoauthorship.getRecentCoauthorshipNCOX());
		// System.out.println(kCoauthorship.getIntermediateCoauthorshipNCOX());
		// System.out.println(kCoauthorship.getOldCoauthorshipNCOX());
		// System.out.println(kCoauthorship.getTimeScaledCoauthorship());
		System.out.println("Value of coauthorships: "
				+ kCoauthorship.getTimeScaledCoauthorship());
		System.out.println("Set of coauthorship edges: "
				+ kCoauthorship.getCoauthorIdToSocialFactorFromCoauthor()
						.values());
		
		return "<html>Name:" + getName() + "<br>Trust Value:" + getPopularity()
				+ "<br>Knowledge Factor:"
				+ kPaperPublished.getFinalKPaperPublished()
				+ "<br>Social Factor:"
				+ kCoauthorship.getTimeScaledCoauthorship();
		
	}
}