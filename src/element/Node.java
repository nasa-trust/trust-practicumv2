/**
 * 
 */
package element;

import jungdemo.JungDemo;

/**
 * @author NASA-Trust-Team
 * 
 */
public final class Node {
	private double publicationsWeight = 1.00;
	private double followersWeight = 1.00;
	private double followingsWeight = 1.00;
	private static int count;
	private int id;
	private String twitterId;
	private String name;
	private double popularity;

	public Node() {
		id = ++count;
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}

	public double getPublicationsWeight() {
		return publicationsWeight;
	}

	public void setPublicationsWeight(double publicationsWeight) {
		this.publicationsWeight = publicationsWeight;
	}

	public double getFollowersWeight() {
		return followersWeight;
	}

	public void setFollowersWeight(double followersWeight) {
		this.followersWeight = followersWeight;
	}

	public double getFollowingsWeight() {
		return followingsWeight;
	}

	public void setFollowingsWeight(double followingsWeight) {
		this.followingsWeight = followingsWeight;
	}

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

	public void setPopularity(int numberOfPublications) {
		popularity = publicationsWeight * numberOfPublications;
	}

	public void setPopularity(int numberOfFollowers, int numberFollowing) {
		popularity = (followersWeight * numberOfFollowers)
				+ (followingsWeight * numberFollowing);
	}

	public void setPopularity(double popularity) {
		this.popularity = popularity;
	}

	public String toString() {
		return "<html>Name:" + getName() + "<br>Popularity Index:"
					+ getPopularity() + "</html>";
		

	}
}