package cmu.sv.recommendationsystem.processor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import dataset.DBLPDataProcessing.DBLPUser;
import dataset.DBLPDataProcessing.Publication;

public class SOLRQueries {

	public static String solrServerBaseURL = "http://localhost:8983/solr/";
	public static String dblpCitationTitleQuery;
	private static int numberOfUsers = Integer.MAX_VALUE;

	public static ArrayList<DBLPUser> DBLPSourceContextMiner(String context) {
		ArrayList<Long> contextMinedDBLPUsersIdList = SOLRQueries
				.parsePublicationAndCitationTitlesToGetTOPNUsers(numberOfUsers,
						context);
		ArrayList<DBLPUser> contextMinedDBLPUserList = SOLRQueries
				.parseDBLPUserInfoFromId(contextMinedDBLPUsersIdList);
		return contextMinedDBLPUserList;
	}

	public static ArrayList<DBLPUser> parseDBLPUserInfoFromId(
			ArrayList<Long> contextMinedDBLPUsersIdList) {
		ArrayList<DBLPUser> contextMinedDBLPUserList = new ArrayList<DBLPUser>();

		int counter = 0;
		int end = 0;
		while (counter < contextMinedDBLPUsersIdList.size()) {

			end = contextMinedDBLPUsersIdList.size() - counter >= 30 ? counter + 30
					: contextMinedDBLPUsersIdList.size();
			List<Long> subList = (List<Long>) contextMinedDBLPUsersIdList
					.subList(counter, end);

			String userIdQuery = "";
			userIdQuery = subList.toString().replaceAll(", ", "%20OR%20id:")
					.replaceAll("\\[", "").replaceAll("\\]", "");

			String dblpTitleQuery = solrServerBaseURL
					+ "dblpuser/select/?q=id:" + userIdQuery
					+ "&indent=on&wt=json&rows=" + numberOfUsers;

			System.out.println(dblpTitleQuery);
			String titleQueryResponse = executeSOLRQuery(dblpTitleQuery);

			JSONParser jSONParser = new JSONParser();
			JSONObject responseJSONObject;

			/*
			 * Parse the json obtained from the DBLP query
			 */
			try {
				responseJSONObject = (JSONObject) jSONParser
						.parse(titleQueryResponse);
				JSONArray userJSONArray = (JSONArray) ((JSONObject) responseJSONObject
						.get("response")).get("docs");
				for (Object userObject : userJSONArray) {
					JSONObject userJSONObject = (JSONObject) userObject;
					DBLPUser dblpUser = new DBLPUser();
					dblpUser.setId(Integer.parseInt(userJSONObject.get("id")
							.toString()));
					dblpUser.setName(userJSONObject.get("name").toString());
					JSONArray coauthorshipJSONArray = (JSONArray) userJSONObject
							.get("coauthorshipid");

					// ArrayList<Integer> coauthorshipList = new
					// ArrayList<Integer>();
					// for(Object coauthorshipValue: coauthorshipJSONArray){
					// coauthorshipList.add((Integer)coauthorshipValue);
					// }

					List<Long> coauthorshipList = new ArrayList<Long>();
					if (coauthorshipJSONArray != null) {
						for (Object coauthorship : coauthorshipJSONArray) {
							coauthorshipList.add((Long) coauthorship);
						}
					}

					dblpUser.setCoauthorship(coauthorshipList);

					JSONArray publicationIdJSONArray = (JSONArray) userJSONObject
							.get("publicationid");
					List<Long> publicationList = new ArrayList<Long>();
					for (Object publication : publicationIdJSONArray) {
						publicationList.add((Long) publication);
					}

					ArrayList<Publication> publicationObjectList = retrieveDBLPPublicationObjectFromId(publicationList);
					dblpUser.setPublication(publicationObjectList);
					contextMinedDBLPUserList.add(dblpUser);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter = counter + 31;
		}
		return contextMinedDBLPUserList;
	}

	private static ArrayList<Publication> retrieveDBLPPublicationObjectFromId(
			List<Long> publicationIdJSONArray) {
		ArrayList<Publication> dblpPublicationList = new ArrayList<Publication>();

		int counter = 0;
		int end = 0;
		while (counter < publicationIdJSONArray.size()) {

			end = publicationIdJSONArray.size() - counter >= 30 ? counter + 30
					: publicationIdJSONArray.size();
			List<Long> subList = (List<Long>) publicationIdJSONArray.subList(
					counter, end);
			// for (Long pubid : publicationIdJSONArray) {
			String publicationIdQuery = subList.toString()
					.replaceAll(", ", "%20OR%20id:").replaceAll("\\[", "")
					.replaceAll("\\]", "");

			String dblpTitleQuery = solrServerBaseURL
					+ "publication/select/?q=id:" + publicationIdQuery
					+ "&indent=on&wt=json&rows=" + numberOfUsers;
			System.out.println("publication sub query: " + dblpTitleQuery);

			String titleQueryResponse = executeSOLRQuery(dblpTitleQuery);

			JSONParser jSONParser = new JSONParser();
			JSONObject responseJSONObject;

			/*
			 * Parse the json obtained from the DBLP query
			 */
			try {
				responseJSONObject = (JSONObject) jSONParser
						.parse(titleQueryResponse);
				JSONArray publicationJSONArray = (JSONArray) ((JSONObject) responseJSONObject
						.get("response")).get("docs");
				for (Object publicationObject : publicationJSONArray) {
					Publication publication = new Publication();
					JSONObject publicationJSONObject = (JSONObject) publicationObject;
					if (publicationJSONObject.get("id") != null) {
						String id = publicationJSONObject.get("id").toString();
						publication.setId(Integer.parseInt(id));
					}
					if (publicationJSONObject.get("type") != null) {
						String type = publicationJSONObject.get("type")
								.toString();
						publication.setType(type);
					}
					if (publicationJSONObject.get("author") != null) {
						List<Integer> authorList = new ArrayList<Integer>();
						for (Object author : (JSONArray) publicationJSONObject
								.get("author")) {
							authorList.add((Integer) author);
						}
						publication.setAuthor(authorList);
					}
					if (publicationJSONObject.get("editor") != null) {
						List<Integer> editorList = new ArrayList<Integer>();
						for (Object editor : (JSONArray) publicationJSONObject
								.get("editor")) {
							editorList.add((Integer) editor);
						}
						publication.setEditor(editorList);
					}
					if (publicationJSONObject.get("title") != null) {
						String title = publicationJSONObject.get("title")
								.toString();
						publication.setTitle(title);
					}
					if (publicationJSONObject.get("booktitle") != null) {
						String booktitle = publicationJSONObject.get(
								"booktitle").toString();
						publication.setBooktitle((booktitle));
					}
					if (publicationJSONObject.get("pages") != null) {
						String pages = publicationJSONObject.get("pages")
								.toString();
						publication.setPages(pages);
					}
					if (publicationJSONObject.get("year") != null) {
						String year = publicationJSONObject.get("year")
								.toString();
						publication.setYear(year);
					}
					if (publicationJSONObject.get("address") != null) {
						String address = publicationJSONObject.get("address")
								.toString();
						publication.setAddress(address);
					}
					if (publicationJSONObject.get("journal") != null) {
						String journal = publicationJSONObject.get("journal")
								.toString();
						publication.setJournal(journal);
					}
					if (publicationJSONObject.get("citetitle") != null) {
						List<String> citeList = new ArrayList<String>();
						for (Object crossref : (JSONArray) publicationJSONObject
								.get("citetitle")) {
							citeList.add((String) crossref);
						}
						publication.setCite(citeList);
					}
					if (publicationJSONObject.get("crossref") != null) {
						List<String> crossrefList = new ArrayList<String>();
						for (Object crossref : (JSONArray) publicationJSONObject
								.get("crossref")) {
							crossrefList.add((String) crossref);
						}
						publication.setCrossref(crossrefList);
					}
					if (publicationJSONObject.get("mdate") != null) {
						String mdate = publicationJSONObject.get("mdate")
								.toString();
						publication.setMdate(mdate);
					}
					dblpPublicationList.add(publication);

				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter = counter + 31;
		}
		return dblpPublicationList;
	}

	public static String executeSOLRQuery(String query) {
		String respContent = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet http = new HttpGet(query);
			HttpResponse response = httpclient.execute(http);
			HttpEntity entity = response.getEntity();

			respContent = EntityUtils.toString(entity);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return respContent;
	}

	public static ArrayList<Long> parsePublicationAndCitationTitlesToGetTOPNUsers(
			int numberOfUsers, String context) {

		String dblpTitleQuery = solrServerBaseURL
				+ "publication/select/?q=title:" + context
				+ "%20OR%20citetitle:" + context + "&indent=on&wt=json&rows="
				+ numberOfUsers + "&fl=authorid";
		System.out.println(dblpTitleQuery);

		ArrayList<Long> dblpContextFilteredUserIdList = new ArrayList<Long>();
		String titleQueryResponse = executeSOLRQuery(dblpTitleQuery);

		JSONParser jSONParser = new JSONParser();
		JSONObject responseJSONObject;

		/*
		 * Parse the json obtained from the DBLP query
		 */
		try {
			responseJSONObject = (JSONObject) jSONParser
					.parse(titleQueryResponse);
			JSONArray userJSONArray = (JSONArray) ((JSONObject) responseJSONObject
					.get("response")).get("docs");
			for (Object userObject : userJSONArray) {
				JSONObject userJSONObject = (JSONObject) userObject;
				JSONArray authorIdArray = (JSONArray) userJSONObject
						.get("authorid");
				if (authorIdArray != null) {
					for (Object authorIdObject : authorIdArray) {
						Long authorId = (Long) authorIdObject;
						dblpContextFilteredUserIdList.add(authorId);
					}
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(dblpContextFilteredUserIdList);
		return dblpContextFilteredUserIdList;
	}

	public static List<CoauthorshipEdge> parseCoauthorsFromAuthorId(int authorId) {
		List<CoauthorshipEdge> coauthorshipEdgeList = new ArrayList<CoauthorshipEdge>();
		String dblpTitleQuery = solrServerBaseURL
				+ "coauthorship/select?q=userid:" + authorId
				+ "&indent=on&wt=json&rows=" + numberOfUsers;

		String titleQueryResponse = executeSOLRQuery(dblpTitleQuery);

		JSONParser jSONParser = new JSONParser();
		JSONObject responseJSONObject;

		/*
		 * Parse the json obtained from the DBLP query
		 */
		try {
			responseJSONObject = (JSONObject) jSONParser
					.parse(titleQueryResponse);
			JSONArray coAuthorJSONArray = (JSONArray) ((JSONObject) responseJSONObject
					.get("response")).get("docs");
			for (Object coauthor : coAuthorJSONArray) {
				CoauthorshipEdge coauthorshipEdge = new CoauthorshipEdge();
				JSONObject coauthorJSONObject = (JSONObject) coauthor;
				JSONArray coauthorDateArray = (JSONArray) coauthorJSONObject
						.get("date");
				List<Integer> coauthorDateList = new ArrayList<Integer>();
				if (coauthorDateArray != null) {
					for (Object coauthorDate : coauthorDateArray) {
						String coauthorDateSplit = ((String) coauthorDate)
								.split(" ")[0];
						coauthorDateList.add(Integer
								.parseInt(coauthorDateSplit));
					}
					coauthorshipEdge.setCoauthorshipDates(coauthorDateList);

					coauthorshipEdge.setCoauthorId(Integer
							.parseInt(coauthorJSONObject.get("coauthorid")
									.toString()));
					coauthorshipEdge.setMappingId(Integer
							.parseInt(coauthorJSONObject.get("id").toString()));
					coauthorshipEdgeList.add(coauthorshipEdge);
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return coauthorshipEdgeList;
	}

	public static void main(String args[]) {

		// List<String> expertNames = new ArrayList<String>();
		// expertNames.add("Raymond Wong");
		// // expertNames.add("\"Michiaki Tatsubori\"");
		// // expertNames.add("\"Karthik Gomadam\"");
		// // expertNames.add("\"Manfred Reichert\"");
		// // expertNames.add("\"Tony Shan\"");
		// // expertNames.add("\"Robert van Engelen\"");
		//
		// ArrayList<DBLPUser> arrayList =
		// parseDBLPUserInfoFromName(expertNames);
		// System.out.println(arrayList.size());
		// for (DBLPUser user : arrayList) {
		// System.out.println(user.getName());
		// }
		// System.out.println("filtering users done");
		ArrayList<Tweet> tweets = parseTweetInfoForAUser("kimwood");
		for (Tweet tweet : tweets) {
			System.out.println(tweet.getTitle());
			System.out.println(tweet.getTimestamp());
			System.out.println("----------------------");
		}

	}

	public static ArrayList<DBLPUser> parseDBLPUserInfoFromName(
			List<String> expertNames) {
		ArrayList<DBLPUser> contextMinedDBLPUserList = new ArrayList<DBLPUser>();

		int counter = 0;
		int end = 0;
		while (counter < expertNames.size()) {

			end = expertNames.size() - counter >= 30 ? counter + 30
					: expertNames.size();
			List<String> subList = (List<String>) expertNames.subList(counter,
					end);

			String userIdQuery = "";
			userIdQuery = subList.toString().replaceAll(", ", "\" OR name:\"")
					.replaceAll("\\[", "\"").replaceAll("\\]", "\"");

			try {
				userIdQuery = URLEncoder.encode(userIdQuery, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String dblpTitleQuery = solrServerBaseURL
					+ "dblpuser/select/?q=name:" + userIdQuery
					+ "&indent=on&wt=json&rows=" + numberOfUsers;

			System.out.println(dblpTitleQuery);
			String titleQueryResponse = executeSOLRQuery(dblpTitleQuery);

			JSONParser jSONParser = new JSONParser();
			JSONObject responseJSONObject;

			/*
			 * Parse the json obtained from the DBLP query
			 */
			try {
				responseJSONObject = (JSONObject) jSONParser
						.parse(titleQueryResponse);
				JSONArray userJSONArray = (JSONArray) ((JSONObject) responseJSONObject
						.get("response")).get("docs");
				for (Object userObject : userJSONArray) {
					JSONObject userJSONObject = (JSONObject) userObject;
					DBLPUser dblpUser = new DBLPUser();
					dblpUser.setId(Integer.parseInt(userJSONObject.get("id")
							.toString()));
					dblpUser.setName(userJSONObject.get("name").toString());
					JSONArray coauthorshipJSONArray = (JSONArray) userJSONObject
							.get("coauthorshipid");

					// ArrayList<Integer> coauthorshipList = new
					// ArrayList<Integer>();
					// for(Object coauthorshipValue: coauthorshipJSONArray){
					// coauthorshipList.add((Integer)coauthorshipValue);
					// }

					List<Long> coauthorshipList = new ArrayList<Long>();
					if (coauthorshipJSONArray != null) {
						for (Object coauthorship : coauthorshipJSONArray) {
							coauthorshipList.add((Long) coauthorship);
						}
					}

					dblpUser.setCoauthorship(coauthorshipList);

					JSONArray publicationIdJSONArray = (JSONArray) userJSONObject
							.get("publicationid");
					List<Long> publicationList = new ArrayList<Long>();
					for (Object publication : publicationIdJSONArray) {
						publicationList.add((Long) publication);
					}

					ArrayList<Publication> publicationObjectList = retrieveDBLPPublicationObjectFromId(publicationList);
					dblpUser.setPublication(publicationObjectList);
					contextMinedDBLPUserList.add(dblpUser);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter = counter + 31;
		}
		return contextMinedDBLPUserList;
	}

	public static ArrayList<String> parseTwitterUsersFromContext(String context) {

		String contextWords[] = context.split(" ");

		ArrayList<String> twitterUsernames = new ArrayList<String>();

		String contextQuery = "";

		for (int i = 0; i < contextWords.length; i++) {
			if (i < contextWords.length - 1) {
				contextQuery = contextQuery + contextWords[i]
						+ "%20OR%20title:";
			} else {
				contextQuery = contextQuery + contextWords[i];
			}

		}

		String twitterSOLRQuery = solrServerBaseURL
				+ "tweet-1/select/?q=title:" + contextQuery
				+ "&indent=on&wt=json&rows=" + numberOfUsers;

		System.out.println(twitterSOLRQuery);

		String titleQueryResponse = executeSOLRQuery(twitterSOLRQuery);

		JSONParser jSONParser = new JSONParser();
		JSONObject responseJSONObject;

		/*
		 * Parse the json obtained from the DBLP query
		 */
		try {
			responseJSONObject = (JSONObject) jSONParser
					.parse(titleQueryResponse);
			JSONArray userJSONArray = (JSONArray) ((JSONObject) responseJSONObject
					.get("response")).get("docs");
			for (Object userObject : userJSONArray) {
				JSONObject userJSONObject = (JSONObject) userObject;
				String twitterUserNameURL = userJSONObject.get("user")
						.toString();
				String twitterUserName = twitterUserNameURL
						.substring(twitterUserNameURL.lastIndexOf("/") + 1);
				twitterUsernames.add(twitterUserName);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return twitterUsernames;
	}

	public static ArrayList<Tweet> parseTweetInfoForAUser(String expertName) {
		
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		
		String twitterSOLRQuery = solrServerBaseURL
				+ "tweet-1/select/?q=user:*" + expertName
				+ "&indent=on&wt=json&rows=" + numberOfUsers;
		
		System.out.println(twitterSOLRQuery);
		
		String titleQueryResponse = executeSOLRQuery(twitterSOLRQuery);

		JSONParser jSONParser = new JSONParser();
		JSONObject responseJSONObject;

		/*
		 * Parse the json obtained from the DBLP query
		 */
		try {
			responseJSONObject = (JSONObject) jSONParser
					.parse(titleQueryResponse);
			JSONArray userJSONArray = (JSONArray) ((JSONObject) responseJSONObject
					.get("response")).get("docs");
			for (Object userObject : userJSONArray) {
				JSONObject userJSONObject = (JSONObject) userObject;
				String twitterUserNameURL = userJSONObject.get("user").toString();
				String twitterUserName = twitterUserNameURL.substring(twitterUserNameURL.lastIndexOf("/")+1);
				Tweet tweet = new Tweet();
				tweet.setId(userJSONObject.get("id").toString());
				tweet.setTimestamp(userJSONObject.get("timestamp").toString());
				tweet.setTitle(userJSONObject.get("title").toString());
				tweet.setUser(twitterUserName);
				tweets.add(tweet);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tweets;
	}
	
	
}
