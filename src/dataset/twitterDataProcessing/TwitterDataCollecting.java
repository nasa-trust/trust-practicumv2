package dataset.twitterDataProcessing;

import java.io.IOException;
import java.util.*;
import java.util.Date;
import java.sql.*;

import dataset.utility.*;

/**
 * @author NASA-Trust-Team
 * 
 */
public class TwitterDataCollecting {
	static int outDegreeLimit = 150;

	static String nodeTable = "twitter.`node_subset`";
	static String edgeTable = "twitter.`edge_subset`";
	static String blacklistTable = "twitter.blacklist";
	static HashMap<String,HashMap<String,String>> nodes = new HashMap<String,HashMap<String,String>>();
	static HashMap<String,HashMap<String,String>> edges = new HashMap<String,HashMap<String,String>>();
	static HashMap<String,HashMap<String,String>> blacklist_keywords = 
			new HashMap<String,HashMap<String,String>>();
	
	static int duplicateNodes = 0;
	static int duplicateEdges = 0;
	static int addedNodes = 0;
	static int addedEdges = 0;

	public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException, SQLException {
		// TODO Create cmd input later

		// Database Connection
		String userName = "root";
		String password = "Cherry00";
		String databaseName = "twitter";
		String url = "jdbc:mysql://127.0.0.1:3306/" + databaseName;
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, userName, password);

		// Get Blacklist Conditions
		List<HashMap<String,String>> sqlTable = new LinkedList<HashMap<String,String>>(
				Utility.readMySQL("select * from "+blacklistTable+";", con));
		blacklist_keywords = returnBlacklist(sqlTable);
		
		// Get All Nodes from DB		
		sqlTable = new LinkedList<HashMap<String,String>>(
				Utility.readMySQL("select * from "+nodeTable+" order by  tier, out_degree asc;", con));
		nodes = convertToNodeTable(sqlTable);
		
		// Get All Edges from DB
		sqlTable = new LinkedList<HashMap<String,String>>(
				Utility.readMySQL("select * from "+edgeTable+";", con));
		edges = convertToEdgeTable(sqlTable);
		
		// Get Node_A for Query API
		List<HashMap<String,String>> sqlTable_nodeA = new LinkedList<HashMap<String,String>>(
				Utility.readMySQL("select * from "+nodeTable+" where tier='2' and status = 'unlisted';", con));
		HashMap<String,HashMap<String,String>> nodeA = new HashMap<String,HashMap<String,String>>();
		nodeA = convertToNodeTable(sqlTable_nodeA);
		
		// Send REST API to Query Twitter
		collectingData(nodeA, con);
		
		// Read Node Table Line-By-Line and Mark Blacklist
		markBlacklist(con);
		
		// End
		con.close();
		System.out.println("--------END--------");
		printEnd();
	}

//TODO delete mySQL database + put function to check before write database
	public static void markBlacklist (Connection con) {
		Iterator<String> iterator = nodes.keySet().iterator();
		while (iterator.hasNext()) {
			String id=iterator.next();
			HashMap<String,String> line = nodes.get(id);
			if (line.get("blacklist").equalsIgnoreCase("0"))
			{
				String name = line.get("name");
				Iterator<String> iteratorBlacklist = blacklist_keywords.keySet().iterator();
				while(iteratorBlacklist.hasNext()){
					String keyword = iteratorBlacklist.next();
					String matchingStatement = "(?i).*"+blacklist_keywords.get(keyword).get("keyword")+".*";
					if(blacklist_keywords.get(keyword).get("case_sensitive").equalsIgnoreCase("1"))
					{
						matchingStatement = "(?-i).*"+blacklist_keywords.get(keyword).get("keyword")+".*";
					}
					if(name.matches(matchingStatement))
					{
						if(Utility.modeDebug)System.out.println(blacklist_keywords.get(keyword).get("keyword")
								+" : "+name + " : " + line.get("description"));
						Utility.writeMySQL(sqlStatementUpdate(nodeTable, "blacklist", "1",
								"id", id), con);
						break;
					}
				}
			}
		}
	}
	
	public static void collectingData (HashMap<String,HashMap<String,String>> nodeA, Connection con) throws 
		NumberFormatException, ClassNotFoundException, IOException, InterruptedException, SQLException{
		List<String> idKey = new LinkedList<String>();
		Iterator<String> iterator = nodeA.keySet().iterator();
		while (iterator.hasNext()) {
			idKey.add(iterator.next());
		}
		for (int i=0; i<idKey.size(); i++)
		{
			String key = idKey.get(i);
			if(Utility.modeDebug)
			{
			System.out.println("-----------------------------------------");
			System.out.println("ID: " + key + " , " + (i + 1) + "/"
					+ idKey.size());
			}
			String status = nodeA.get(key).get("status");
			int out_degree = Integer.parseInt(nodeA.get(key).get("out_degree")); 
			if(status.equalsIgnoreCase("unlisted"))
			{
				if(out_degree<=outDegreeLimit)
				{
					if(Utility.modeDebug) System.out.println("List Follows from id: " + key);
					if(out_degree!=0)
						twitterListFollow(key, Integer.parseInt(nodeA.get(key).get("tier")), con);
					else Utility.writeMySQL(sqlStatementUpdate(nodeTable, "status", "zero follow",
							"id", key), con);
				}
				else Utility.writeMySQL(sqlStatementUpdate(nodeTable, "status", "many follow",
						"id", key), con);
			} else {
				if(Utility.modeDebug) System.out.println("Skip List Follow: id => " + key
						+ ", status => " + status);
			}
		}

	}
	
	private static String urlListFollow(String userID) {
		return "http://api.twitter.com/1/friends/ids.json?cursor=-1&user_id="
				+ userID;
	}

	private static String urlUserInfo(String field, String value) {
		return "https://api.twitter.com/1/users/show.json?" + field + "="
				+ value;
	}
	
	private static String sqlStatementInsert(String tableName, HashMap<String,String> field) {
		String columns = null;
		String values = null;
		Iterator<String> iterator = field.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if(columns==null) columns=key;
			else columns=columns+","+key;
			if(values==null) values="'"+field.get(key)+"'";
			else values=values+","+"'"+field.get(key)+"'";
		}
		String sql = "INSERT INTO " + tableName + " ("+ columns +") "
				+ "VALUES (" + values + ");";
		return sql;
	}
	
	private static String sqlStatementUpdate(String tableName, HashMap<String,String> field,
			HashMap<String,String> condition)
	{
		String set = null;
		String cond = null;
		Iterator<String> iterator = field.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if(set==null) set = key + "='" + field.get(key) + "'";
			else set = set + ", " + key + "='" + field.get(key) + "'";
		}
		iterator = condition.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if(cond==null) cond = key + "='" + condition.get(key) + "'";
			else cond = cond + ", " + key + "='" + condition.get(key) + "'";
		}
		String sql = "UPDATE " + tableName + " SET " + set + " WHERE " + cond + ";";
		return sql;
	}
	
	private static String sqlStatementUpdate(String tableName,
			String fieldName, String fieldValue, String conditionField,
			String conditionValue) {
		return "UPDATE " + tableName + " SET " + fieldName + " = '"
				+ fieldValue + "' WHERE " + conditionField + " = '"
				+ conditionValue + "';";
	}

	private static void twitterListFollow(String inputId, int tier,
			Connection con) throws IOException, InterruptedException,
			ClassNotFoundException, SQLException {
		String queryText = Utility.sendGetRequest(urlListFollow(inputId));
		while (Utility.hitLimit) {
			System.out.println("Sleep 15 mins @ twitterListFollow: "
					+ new Date());
			printEnd();
			Thread.sleep(15 * 60 * 1000);
			queryText = Utility.sendGetRequest(urlListFollow(inputId));
		}
		if (Utility.modeDebug) System.out.println("Response from GET: " + queryText);
		String jsonKey = "ids";
		List<String> queryObject = new LinkedList<String>();
		String status = null;
		if (queryText.equalsIgnoreCase("unauthorized"))status="unauthorized"; 
		else if(queryText.equalsIgnoreCase("403"))status = "forbidden";
		else queryObject = (List<String>) Utility.decodeJSON(queryText, jsonKey);;
		if (Utility.modeDebug) {
			System.out.println(jsonKey + ": " + queryObject);
			System.out.println("Size: " + queryObject.size());
		}
		if (queryObject.size() != 0 && !queryText.equalsIgnoreCase("unauthorized")
				&& !queryText.equalsIgnoreCase("403")) {
			for (int i = 0; i < queryObject.size(); i = i + 1) 
			{
				String followId = String.valueOf(queryObject.get(i));
				if(Utility.modeDebug) System.out.println("Follow ID: "
						+ String.valueOf(queryObject.get(i)));
				//Check if followId is exist in nodeTable
				if (!nodes.containsKey(followId)) {
					List<String> user = new LinkedList<String>();
					user.add(followId);
					//getUserInfo(List<String> user, String field, int tier, Connection con)
					//getUserInfo includes writing DB and update nodes
					getUserInfo(user,"blank",tier+1,con);
				} else {
					duplicateNodes++;
					if(Utility.modeDebug)System.out.println(
							"twitterListFollow : Node ID duplicated => "
							+ followId);
				}
				//Check if inputId_followId is exist in edgeTable
				if (!edges.containsKey(inputId+"_"+followId)) {
					HashMap<String,String> edge = new HashMap<String,String>();
					edge.put("nodeA_id",inputId);
					edge.put("nodeB_id",followId);
					edge.put("tier",String.valueOf(tier));
					Utility.writeMySQL(sqlStatementInsert(edgeTable,edge), con);
					edges.put(inputId+"_"+followId, edge);
					addedEdges++;
				} else {
					duplicateEdges++;
					if(Utility.modeDebug)System.out.println(
							"twitterListFollow: Edges are duplicated => "
							+ inputId + " , " + followId);
				}
			}
			status = "listed";
		}
		Utility.writeMySQL(sqlStatementUpdate(nodeTable, "status", status,
						"id", inputId), con);
	}
	
	private static void getUserInfo(List<String> user, String field, int tier, Connection con) 
			throws IOException, InterruptedException
	{
		String queryText = null;
		for (int i=0; i<user.size();i++)
		{
			queryText = Utility.sendGetRequest(urlUserInfo("id", user.get(i)));
			while (Utility.hitLimit) {
				System.out.println("Sleep 15 mins @ getUserInfo: "
						+ new Date());
				printEnd();
				Thread.sleep(15 * 60 * 1000);
				queryText = Utility.sendGetRequest(urlUserInfo("id", user.get(i)));
			}
			if (!queryText.equalsIgnoreCase("unauthorized")) {
				String id = String.valueOf(Utility.decodeJSON(queryText, "id"));
				String name = String.valueOf(Utility.decodeJSON(queryText, "name"));
				name=name.replaceAll("'", "");
				String screen_name = String.valueOf(Utility.decodeJSON(queryText, "screen_name"));
				screen_name=screen_name.replaceAll("'", "");
				String followers_count = String.valueOf(Utility.decodeJSON(queryText, "followers_count"));
				String friends_count = String.valueOf(Utility.decodeJSON(queryText, "friends_count"));
				String location = String.valueOf(Utility.decodeJSON(queryText, "location"));
				location=location.replaceAll("'", "");
				String description = String.valueOf(Utility.decodeJSON(queryText, "description"));
				description=description.replaceAll("'", "");
				HashMap<String,String> updateField = new HashMap<String,String>();
				HashMap<String,String> conditionalField = new HashMap<String,String>();
				if (!field.equalsIgnoreCase("id")) updateField.put("id", id);
				if (!field.equalsIgnoreCase("name")) updateField.put("name", name);
				if (!field.equalsIgnoreCase("screen_name")) updateField.put("username", screen_name);
				updateField.put("out_degree", friends_count);
				updateField.put("in_degree", followers_count);
				updateField.put("location", location);
				updateField.put("description", description);
				if(Integer.parseInt(friends_count)==0) updateField.put("status", "zero follow");
				else if(Integer.parseInt(friends_count)<=outDegreeLimit)updateField.put("status", "unlisted");
				else updateField.put("status", "many follow");
				updateField.put("tier", String.valueOf(tier));
				updateField.put("blacklist", String.valueOf("0"));
				if(field.equalsIgnoreCase("blank"))
				{
					Utility.writeMySQL(sqlStatementInsert(nodeTable,updateField), con);
					nodes.put(id, updateField);
					addedNodes++;
				}
				else 
				{
					conditionalField.put(field, user.get(i));
					Utility.writeMySQL(sqlStatementUpdate(nodeTable,updateField,conditionalField), con);
				}
			}
		}
	}
	
	private static HashMap<String,HashMap<String,String>> returnBlacklist(List<HashMap<String,String>> sqlTable)
	{
		HashMap<String,HashMap<String,String>> blacklist_keywords = 
				new HashMap<String,HashMap<String,String>>();
		for (int i=0; i<sqlTable.size(); i++)
		{
			blacklist_keywords.put(sqlTable.get(i).get("keyword"),sqlTable.get(i));
		}
		return blacklist_keywords;
	}
	
	private static HashMap<String,HashMap<String,String>> convertToNodeTable(List<HashMap<String,String>> sqlTable)
	{
		HashMap<String,HashMap<String,String>> nodeTable = new HashMap<String,HashMap<String,String>>();
		for (int i=0; i<sqlTable.size(); i++)
		{
			nodeTable.put(sqlTable.get(i).get("id"), sqlTable.get(i));
		}
		return nodeTable;
	}
	
	private static HashMap<String,HashMap<String,String>> convertToEdgeTable(List<HashMap<String,String>> sqlTable)
	{
		HashMap<String,HashMap<String,String>> edgeTable = new HashMap<String,HashMap<String,String>>();
		for (int i=0; i<sqlTable.size(); i++)
		{
			edgeTable.put(sqlTable.get(i).get("nodeA_id")+"_"+
					sqlTable.get(i).get("nodeB_id"), sqlTable.get(i));
		}
		return edgeTable;
	}
	
	private static void printEnd(){
		System.out.println("SQL Error: "+Utility.sqlError);
		System.out.println("Node Duplicates: "+duplicateNodes);
		System.out.println("Edge Duplicates: "+duplicateEdges);
		System.out.println("Node Add: "+addedNodes);
		System.out.println("Edge Add: "+addedEdges);
	}
}