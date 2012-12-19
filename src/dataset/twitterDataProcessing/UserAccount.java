package dataset.twitterDataProcessing;

import java.util.*;

/**
 * @author NASA-Trust-Team
 * 
 */
public class UserAccount {
	private String name;
	private String id;
	private String username;
	private int tier;
	private int in_degree;
	private int out_degree;
	private LinkedList<String> follow;

	public UserAccount() {
		super();
	}

	public UserAccount(UserAccount userAccount) {
		super();
		if (userAccount.getName() != null)
			this.name = new String(userAccount.getName());
		this.id = new String(userAccount.getId());
		if (userAccount.getUsername() != null)
			this.username = new String(userAccount.getUsername());
		this.tier = userAccount.getTier();
		this.in_degree = userAccount.getIn_degree();
		this.out_degree = userAccount.getOut_degree();
		this.follow = new LinkedList<String>(userAccount.getFollow());
	}

	public UserAccount(String lastname, String firstname, String id,
			String username, int tier, int in_degree, int out_degree) {
		this.name = lastname;
		this.id = id;
		this.username = username;
		this.tier = tier;
		this.in_degree = in_degree;
		this.out_degree = out_degree;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}

	public int getIn_degree() {
		return in_degree;
	}

	public void setIn_degree(int in_degree) {
		this.in_degree = in_degree;
	}

	public int getOut_degree() {
		return out_degree;
	}

	public void setOut_degree(int out_degree) {
		this.out_degree = out_degree;
	}

	public LinkedList<String> getFollow() {
		return follow;
	}

	public void setFollow(LinkedList<String> follow) {
		this.follow = follow;
	}

}
