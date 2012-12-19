package cmu.sv.recommendationsystem.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class TrustModelIntegration {
	
	private double knowledgeFactorWeight=0.75;
	
	private double socialFactorWeight = 0.25;
	
	

	public double getKnowledgeFactorWeight() {
		return knowledgeFactorWeight;
	}



	public void setKnowledgeFactorWeight(double knowledgeFactorWeight) {
		this.knowledgeFactorWeight = knowledgeFactorWeight;
	}



	public double getSocialFactorWeight() {
		return socialFactorWeight;
	}



	public void setSocialFactorWeight(double socialFactorWeight) {
		this.socialFactorWeight = socialFactorWeight;
	}



	public static void main() {
		
		ArrayList<String> userNames = new ArrayList<String>();
		File file = new File("mappedcommondatasets.txt");
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String fileString;
			while ((fileString = bufferedReader.readLine()) != null) {
				String[] terms = fileString.split(";");
				userNames.add(terms[0]);
			}
		} catch (Exception ex) {

		}
		DBLPTrustProcessor dblpTrustProcessor = new DBLPTrustProcessor();
		ArrayList<DBLPTrustModel> dblpTrustModels = dblpTrustProcessor.expertTrustMatrix(userNames);
		
		TwitterTrustProcessor twitterTrustProcessor = new TwitterTrustProcessor();
		HashMap<String, TwitterTrustModel> hashMap = twitterTrustProcessor.createTrustModelForFilteredUserFromList();
		
		
		
	}
}
