package cmu.sv.recommendationsystem.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TwitterTrustProcessor {
	
	

	
	
	
	// public HashMap<String, TwitterTrustModel> expertTwitterTrustModel(
	// ArrayList<String> expertNames) {
	// String context = "Cloud Computing";
	//
	// ArrayList<String> twitterContextFilteredUser =
	// twitterUsersForAContext(context);
	//
	// for (String twitterUser : twitterContextFilteredUser) {
	// ArrayList<Tweet> tweets = SOLRQueries
	// .parseTweetInfoForAUser(twitterUser);
	// System.out.println(tweets.size());
	// }
	//
	// return twitterUserToTrustModel;
	// }

	
	public HashMap<String, TwitterTrustModel> createTrustModelForFilteredUserFromList() {

		HashMap<String, TwitterTrustModel> mapOfUserNameToTwitterUser = new HashMap<String, TwitterTrustModel>();

		File file = new File("mappedcommondatasets.txt");
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String fileString;
			while ((fileString = bufferedReader.readLine()) != null) {
				
				String[] terms = fileString.split(";");
				KRetweet kRetweet = new KRetweet();
				kRetweet.setNumofRetweetRecent(Integer.parseInt(terms[1]));
			
				TwitterUser twitterUser = new TwitterUser();
				TwitterTrustModel twitterTrustModel = new TwitterTrustModel();
				twitterTrustModel.setkRetweet(kRetweet);
				twitterUser.setTwitterUserName(terms[0]);
				twitterUser.setActualName(terms[2]);
				twitterTrustModel.setTwitterUser(twitterUser);
				
				twitterTrustModel.setTwitterTrustFactor(kRetweet.getSigmaRetweet()*kRetweet.getNumofRetweetRecent());
				mapOfUserNameToTwitterUser.put(terms[2], twitterTrustModel);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return mapOfUserNameToTwitterUser;

	}

	public ArrayList<String> twitterUsersForAContext(String context) {
		// / Obtain the twitter User using a context: for instance Cloud
		// Computing ///
		// String context = "Cloud Computing";

		ArrayList<String> twitterContextFilteredUser = SOLRQueries
				.parseTwitterUsersFromContext(context);

		return twitterContextFilteredUser;
	}

	public static void main(String[] args) {
		String context = "*";
		TwitterTrustProcessor processor = new TwitterTrustProcessor();
//		ArrayList<String> twitterContextFilteredUser = processor
//				.twitterUsersForAContext(context);
//
//		for (String twitterUser : twitterContextFilteredUser) {
//			ArrayList<Tweet> tweets = SOLRQueries
//					.parseTweetInfoForAUser(twitterUser);
//			System.out.println(tweets.size());
//		}
		HashMap<String, TwitterTrustModel> mapOfUserNameToTwitterUser = processor.createTrustModelForFilteredUserFromList();
		Set<String> userNames = mapOfUserNameToTwitterUser.keySet();
		for(String userName: userNames){
			System.out.println(mapOfUserNameToTwitterUser.get(userName).getTwitterTrustFactor());
			
		}
	}

}
