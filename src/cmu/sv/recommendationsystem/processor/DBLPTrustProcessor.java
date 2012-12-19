package cmu.sv.recommendationsystem.processor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dataset.DBLPDataProcessing.DBLPUser;
import dataset.DBLPDataProcessing.Publication;

public class DBLPTrustProcessor {
	
	TrustModelWeights trustModelWeights;
	public DBLPTrustProcessor(){
		trustModelWeights = new TrustModelWeights();
	}

	/*
	 * This method would return take as input all the developers names D =
	 * {D1,D2,D3....DN} along with the context and return a Map of developer
	 * names to DBLP Trust Values
	 */

	public HashMap<String, DBLPTrustModel> calculateDeveloperTrustMatrix(
			List<String> developerList, String context) {

		HashMap<String, DBLPTrustModel> developerNameMappedToTrustModelValue = new HashMap<String, DBLPTrustModel>();

		ArrayList<DBLPUser> contextFilteredDBLPUserList = SOLRQueries
				.DBLPSourceContextMiner(context);

		// /////////////////////// Make nameTOObject Map for filtered DBLP Users
		// ////////////////////////

		HashMap<String, DBLPUser> contextFilteredDBLPUserNameToObject = new HashMap<String, DBLPUser>();
		for (DBLPUser contextFilteredDBLPUser : contextFilteredDBLPUserList) {
			contextFilteredDBLPUserNameToObject.put(
					contextFilteredDBLPUser.getName(), contextFilteredDBLPUser);
		}

		// //////////////////////////////////////////////////////////////////////////////////////////////

		/*
		 * Take each Software then get the set of developers on that
		 */

		for (String developerName : developerList) {
			DBLPUser matchedDeveloper = contextFilteredDBLPUserNameToObject
					.get(developerName);
			DBLPTrustModel dblpTrustModel = null;
			if (matchedDeveloper != null) {
				dblpTrustModel = calculateDBLPTrustFactor(matchedDeveloper);
				dblpTrustModel.setDblpUser(matchedDeveloper);
			}
			developerNameMappedToTrustModelValue.put(developerName,
					dblpTrustModel);
		}
		return developerNameMappedToTrustModelValue;

	}
	
	public ArrayList<DBLPTrustModel> expertTrustMatrix(List<String> expertNames) {

		ArrayList<DBLPTrustModel> expertTrustModelList = new ArrayList<DBLPTrustModel>();

		ArrayList<DBLPUser> expertDBLPObjects = SOLRQueries
				.parseDBLPUserInfoFromName(expertNames);
		DBLPTrustModel dblpTrustModel = null;
		for (DBLPUser expertUser : expertDBLPObjects) {
			dblpTrustModel = calculateDBLPTrustFactor(expertUser);
			dblpTrustModel.setDblpUser(expertUser);			
			expertTrustModelList.add(dblpTrustModel);
		}
		return expertTrustModelList;

	}
	
	
	public ArrayList<DBLPTrustModel> trustModelForAuthorIds(ArrayList<Long> authorIdList) {

		ArrayList<DBLPTrustModel> expertTrustModelList = new ArrayList<DBLPTrustModel>();

		ArrayList<DBLPUser> expertDBLPObjects = SOLRQueries.parseDBLPUserInfoFromId(authorIdList);
		DBLPTrustModel dblpTrustModel = null;
		for (DBLPUser expertUser : expertDBLPObjects) {
			dblpTrustModel = calculateDBLPTrustFactor(expertUser);
			dblpTrustModel.setDblpUser(expertUser);			
			expertTrustModelList.add(dblpTrustModel);
		}
		return expertTrustModelList;

	}
	

	public DBLPTrustModel calculateDBLPTrustFactor(DBLPUser dblpUser) {
		DBLPTrustModel dblpTrustModel = new DBLPTrustModel();

		// ///// Knowledge Factor ///////////
		KPaperPublished kPaperPublished = calculateKPaperPublished(dblpUser);
		DBLPKnowledgeFactor dblpKnowledgeFactor = new DBLPKnowledgeFactor();
		dblpKnowledgeFactor.setkPaperPublished(kPaperPublished);

		// ///////////////////////////////////
		dblpTrustModel.setDblpKnowledgeFactor(dblpKnowledgeFactor);
		// //// Social Factor ///////////
		DBLPSocialFactor dblpSocialFactor = new DBLPSocialFactor();
		KCoauthorship kCoauthorship = calculateKCoauthorship(dblpUser.getId());

		
		dblpSocialFactor.setkCoauthorship(kCoauthorship);
		dblpTrustModel.setDblpSocialFactor(dblpSocialFactor);
		// //////////////////////////////////

		dblpTrustModel.setTrustValue(dblpSocialFactor.getkCoauthorship()
				.getTimeScaledCoauthorship()
				+ dblpKnowledgeFactor.getkPaperPublished()
						.getFinalKPaperPublished());

		return dblpTrustModel;
	}

	// private KCoauthorship calculateKCoauthorship(int authorId) {
	// double coauthorshipCount = 0;
	// TimeScale timeScale = new TimeScale();
	// KCoauthorship kCoauthorship = new KCoauthorship();
	//
	// List<CoauthorshipEdge> coauthorshipDetailsList = SOLRQueries
	// .parseCoauthorsFromAuthorId(authorId);
	// int currentYear = Calendar.getInstance().get(Calendar.YEAR);
	//
	// for (CoauthorshipEdge coauthorshipEdge : coauthorshipDetailsList) {
	// List<Integer> coauthorshipDatesList = coauthorshipEdge
	// .getCoauthorshipDates();
	// int numberOfRecentCoauthorships = 0;
	// int numberOfIntermediateCoauthorships = 0;
	// int numberOfOldCoauthorships = 0;
	// for (int coauthorshipYear : coauthorshipDatesList) {
	// if (coauthorshipYear >= (currentYear - timeScale
	// .getRecentYears())) {
	// numberOfRecentCoauthorships++;
	// }else if(coauthorshipYear >= (currentYear -
	// timeScale.getIntermediateYears())
	// && (coauthorshipYear < currentYear - timeScale.getRecentYears())){
	// numberOfIntermediateCoauthorships++;
	// }else{
	// numberOfOldCoauthorships++;
	// }
	// }
	// kCoauthorship.setRecentCoauthorshipNCOX(kCoauthorship.getRecentCoauthorshipNCOX()+numberOfRecentCoauthorships);
	// kCoauthorship.setIntermediateCoauthorshipNCOX((kCoauthorship.getIntermediateCoauthorshipNCOX()+numberOfIntermediateCoauthorships));
	// kCoauthorship.setOldCoauthorshipNCOX((kCoauthorship.getOldCoauthorshipNCOX()+numberOfOldCoauthorships));
	// }
	// coauthorshipCount =
	// timeScale.gettRecent()*kCoauthorship.getRecentCoauthorshipNCOX()
	// +
	// timeScale.gettIntermediate()*kCoauthorship.getIntermediateCoauthorshipNCOX()
	// + timeScale.gettOld()*kCoauthorship.getOldCoauthorshipNCOX();
	// kCoauthorship.setTimeScaledCoauthorship(coauthorshipCount);
	// return kCoauthorship;
	// }

	private KCoauthorship calculateKCoauthorship(int authorId) {
		double coauthorshipCount = 0;
		
		TimeScale timeScale = new TimeScale();
		KCoauthorship kCoauthorship = new KCoauthorship();
		double socialCoathorshipFactorForaCoauthor = 0;

		List<CoauthorshipEdge> coauthorshipDetailsList = SOLRQueries
				.parseCoauthorsFromAuthorId(authorId);
		
		
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		////
		
		double maxCoauth = 0;
		
		/////
		for (CoauthorshipEdge coauthorshipEdge : coauthorshipDetailsList) {
			List<Integer> coauthorshipDatesList = coauthorshipEdge
					.getCoauthorshipDates();
			int numberOfRecentCoauthorships = 0;
			int numberOfIntermediateCoauthorships = 0;
			int numberOfOldCoauthorships = 0;
			for (int coauthorshipYear : coauthorshipDatesList) {
				if (coauthorshipYear >= (currentYear - timeScale
						.getRecentYears())) {
					numberOfRecentCoauthorships++;
				} else if (coauthorshipYear >= (currentYear - timeScale
						.getIntermediateYears())
						&& (coauthorshipYear < currentYear
								- timeScale.getRecentYears())) {
					numberOfIntermediateCoauthorships++;
				} else {
					numberOfOldCoauthorships++;
				}
			}
			socialCoathorshipFactorForaCoauthor = timeScale.gettRecent()
					* numberOfRecentCoauthorships
					+ timeScale.gettIntermediate()
					* numberOfIntermediateCoauthorships + timeScale.gettOld()
					* numberOfOldCoauthorships;
			HashMap<Integer, Double> coauthorIdToSocialFactorFromCoauthor = kCoauthorship
					.getCoauthorIdToSocialFactorFromCoauthor();
			coauthorIdToSocialFactorFromCoauthor.put(
					coauthorshipEdge.getCoauthorId(),
					socialCoathorshipFactorForaCoauthor);
			kCoauthorship
					.setCoauthorIdToSocialFactorFromCoauthor(coauthorIdToSocialFactorFromCoauthor);
		}
		HashMap<Integer, Double> coauthorIdToSocialFactorFromCoauthor = kCoauthorship
				.getCoauthorIdToSocialFactorFromCoauthor();
		Set<Integer> coauthoridSet = coauthorIdToSocialFactorFromCoauthor
				.keySet();
		for (Integer coauthorid : coauthoridSet) {
			coauthorshipCount = coauthorshipCount
					+ coauthorIdToSocialFactorFromCoauthor.get(coauthorid);
		}
		kCoauthorship.setTimeScaledCoauthorship(Math.ceil(coauthorshipCount));
		
		return kCoauthorship;
	}

	private KPaperPublished calculateKPaperPublished(DBLPUser dblpUser) {
		double kPaperPublished = 0;

		KPaperPublished publishingConstants = new KPaperPublished();
		int publicationYear;
		int year = Calendar.getInstance().get(Calendar.YEAR);
		TimeScale timeScale = new TimeScale();
		List<Publication> publicationList = dblpUser.getPublication();
		KCitePower kCitePower = new KCitePower();

		for (Publication publication : publicationList) {
			
			publicationYear = publication.getYear() == null? year :Integer.parseInt(publication.getYear());
			int numberOfCitations = publication.getCitationcount();
			if (publication.getType().equalsIgnoreCase("article")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfArticleRecentNOX((publishingConstants
									.getNumOfArticleRecentNOX() + 1));
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfArticleIntermediateNXY(publishingConstants
									.getNumOfArticleIntermediateNXY() + 1);
				} else {
					publishingConstants
							.setNumOfArticleOldNYZ(publishingConstants
									.getNumOfArticleOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsA(kCitePower
						.getNumberOfCitationsA() + numberOfCitations);
			}
			if (publication.getType().equalsIgnoreCase("book")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfBooksRecentNOX((publishingConstants
									.getNumOfBooksRecentNOX() + 1));
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfBooksIntermediateNXY(publishingConstants
									.getNumOfBooksIntermediateNXY() + 1);
				} else {
					publishingConstants.setNumOfBooksOldNYZ(publishingConstants
							.getNumOfBooksOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsB(kCitePower
						.getNumberOfCitationsB() + numberOfCitations);

			}
			if (publication.getType().equalsIgnoreCase("incollection")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfIncollectionRecentNOX(publishingConstants
									.getNumOfIncollectionRecentNOX() + 1);
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfIncollectionIntermediateNXY(publishingConstants
									.getNumOfIncollectionIntermediateNXY() + 1);
				} else {
					publishingConstants
							.setNumOfIncollectionOldNYZ(publishingConstants
									.getNumOfIncollectionOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsIC(kCitePower
						.getNumberOfCitationsIC() + numberOfCitations);
			}
			if (publication.getType().equalsIgnoreCase("inproceedings")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfInProceedingRecentNOX(publishingConstants
									.getNumOfInProceedingRecentNOX() + 1);
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfInProceedingIntermediateNXY(publishingConstants
									.getNumOfInProceedingIntermediateNXY() + 1);
				} else {
					publishingConstants
							.setNumOfInProceedingOldNYZ(publishingConstants
									.getNumOfInProceedingOldNYZ());
				}
				kCitePower.setNumberOfCitationsIP(kCitePower
						.getNumberOfCitationsIP() + numberOfCitations);

			}
			if (publication.getType().equalsIgnoreCase("masterthesis")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfMasterThesisRecentNOX(publishingConstants
									.getNumOfMasterThesisRecentNOX());
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfMasterThesisIntermediateNXY(publishingConstants
									.getNumOfMasterThesisIntermediateNXY() + 1);
				} else {
					publishingConstants
							.setNumOfMasterThesisOldNYZ(publishingConstants
									.getNumOfMasterThesisOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsM(kCitePower
						.getNumberOfCitationsM() + numberOfCitations);
			}
			if (publication.getType().equalsIgnoreCase("phdthesis")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfPhdThesisRecentNOX(publishingConstants
									.getNumOfPhdThesisRecentNOX() + 1);
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfPhdThesisIntermediateNXY(publishingConstants
									.getNumOfPhdThesisIntermediateNXY() + 1);
				} else {
					publishingConstants
							.setNumOfPhdThesisOldNYZ(publishingConstants
									.getNumOfPhdThesisOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsP(kCitePower
						.getNumberOfCitationsP() + numberOfCitations);
			}
			if (publication.getType().equalsIgnoreCase("proceeding")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfProceedingRecentNOX(publishingConstants
									.getNumOfProceedingRecentNOX() + 1);
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfProceedingIntermediateNXY(publishingConstants
									.getNumOfProceedingIntermediateNXY() + 1);
				} else {
					publishingConstants
							.setNumOfProceedingOldNYZ(publishingConstants
									.getNumOfProceedingOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsPH(kCitePower
						.getNumberOfCitationsPH() + numberOfCitations);
			}
			if (publication.getType().equalsIgnoreCase("www")) {

				if (publicationYear >= year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfWWWRecentNOX(publishingConstants
									.getNumOfWWWRecentNOX() + 1);
				} else if (publicationYear >= year
						- timeScale.getIntermediateYears()
						&& publicationYear < year - timeScale.getRecentYears()) {
					publishingConstants
							.setNumOfWWWIntermediateNXY(publishingConstants
									.getNumOfWWWIntermediateNXY() + 1);
				} else {
					publishingConstants.setNumOfWWWOldNYZ(publishingConstants
							.getNumOfWWWOldNYZ() + 1);
				}
				kCitePower.setNumberOfCitationsW(kCitePower
						.getNumberOfCitationsW() + numberOfCitations);
			}
		}
		
		kPaperPublished = publishingConstants.getAlphaArticle()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfArticleRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants.getNumOfArticleIntermediateNXY() + timeScale
						.gettOld()
						* publishingConstants.getNumOfArticleOldNYZ())
				+ publishingConstants.getAlphaBook()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfBooksRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants.getNumOfBooksIntermediateNXY() + timeScale
						.gettOld() * publishingConstants.getNumOfBooksOldNYZ())
				+ publishingConstants.getAlphaInCollection()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfIncollectionRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants
								.getNumOfIncollectionIntermediateNXY() + timeScale
						.gettOld()
						* publishingConstants.getNumOfIncollectionOldNYZ())
				+ publishingConstants.getAlphaInProceeding()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfInProceedingRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants
								.getNumOfInProceedingIntermediateNXY() + timeScale
						.gettOld()
						* publishingConstants.getNumOfInProceedingOldNYZ())
				+ publishingConstants.getAlphaMasterThesis()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfMasterThesisRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants
								.getNumOfMasterThesisIntermediateNXY() + timeScale
						.gettOld()
						* publishingConstants.getNumOfMasterThesisOldNYZ())
				+ publishingConstants.getAlphaPhdThesis()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfPhdThesisRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants
								.getNumOfPhdThesisIntermediateNXY() + timeScale
						.gettOld()
						* publishingConstants.getNumOfPhdThesisOldNYZ())
				+ publishingConstants.getAlphaProceeding()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfProceedingRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants
								.getNumOfProceedingIntermediateNXY() + timeScale
						.gettOld()
						* publishingConstants.getNumOfProceedingOldNYZ())
				+ publishingConstants.getAlphaWWW()
				* (timeScale.gettRecent()
						* publishingConstants.getNumOfWWWRecentNOX()
						+ timeScale.gettIntermediate()
						* publishingConstants.getNumOfWWWIntermediateNXY() + timeScale
						.gettOld() * publishingConstants.getNumOfWWWOldNYZ())
				+ publishingConstants.getAlphaArticle()
				* kCitePower.getNumberOfCitationsA()
				+ publishingConstants.getAlphaBook()
				* kCitePower.getNumberOfCitationsB()
				+ publishingConstants.getAlphaInCollection()
				* kCitePower.getNumberOfCitationsIC()
				+ publishingConstants.getAlphaInProceeding()
				* kCitePower.getNumberOfCitationsIP()
				+ publishingConstants.getAlphaMasterThesis()
				* kCitePower.getNumberOfCitationsM()
				+ publishingConstants.getAlphaPhdThesis()
				* kCitePower.getNumberOfCitationsPH()
				+ publishingConstants.getAlphaProceeding()
				* kCitePower.getNumberOfCitationsP()
				+ publishingConstants.getAlphaWWW()
				* kCitePower.getNumberOfCitationsW();

		publishingConstants.setFinalKPaperPublished(Math.ceil(kPaperPublished));
		return publishingConstants;
	}

	public static void main(String args[]) {
		DBLPTrustProcessor dblpTrustProcessor = new DBLPTrustProcessor();
		List<String> expertNames = new ArrayList<String>();
		expertNames.add("Chun Li");
		DBLPTrustModel dblpTrustModel = dblpTrustProcessor
				.expertTrustMatrix(expertNames).get(0);
		DBLPKnowledgeFactor dblpKnowledgeFactor = dblpTrustModel
				.getDblpKnowledgeFactor();
		DBLPSocialFactor dblpSocialFactor = dblpTrustModel.getDblpSocialFactor();
		
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

	}
}
