package dataset.utility;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import org.json.simple.parser.*;

import com.mysql.jdbc.ResultSetMetaData;

/**
 * @author NASA-Trust-Team
 * 
 */
public class Utility {
	public static boolean modeDebug = true;
	public static boolean modeDebugUtility = false;
	public static int noHTTPRequest = 0;
	public static boolean hitLimit = false;
	public static int sqlError = 0;

	public Utility() {
		super();
	}

	public static String sendGetRequest(String destinationURL)
			throws IOException
	// TODO optimize this code by checking X-FeatureRateLimit status
	{
		int code = 0;
		noHTTPRequest++;
		try {
			URL url = new URL(destinationURL);
			URLConnection urlConnection = url.openConnection();
			code = ((HttpURLConnection) urlConnection).getResponseCode();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
				sb.append(line);
			rd.close();
			Utility.hitLimit = false;
			return sb.toString();
		} catch (IOException e) {
			System.out.println("Error from sendGetRequest: " + e.getMessage());
			if (code == 400) {
				Utility.hitLimit = true;
				System.out.println("Total HTTP request: " + noHTTPRequest);
				return "400";
			} else if (code == 401) {
				Utility.hitLimit = false;
				return "unauthorized";
			} else if (code == 403) {
				Utility.hitLimit = false;
				return "403";
			} else if (code == 200) {
				return "200";
			} else {
				Utility.hitLimit = false;
				System.out.println("Undefinde Error");
				return null;
			}
		}
	}

	public static Object decodeJSON(String inputJSON, String field) {
		if (modeDebugUtility)
			System.out.println("Input JSON: " + inputJSON);
		JSONParser parser = new JSONParser();
		ContainerFactory containerFactory = new ContainerFactory() {
			public List<String> creatArrayContainer() {
				return new LinkedList<String>();
			}

			public Map<String,String> createObjectContainer() {
				return new LinkedHashMap<String,String>();
			}
		};
		Map<String,String> json = new HashMap<String,String>();
		try {
			json = (Map<String,String>) parser.parse(inputJSON, containerFactory);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Iterator iter = json.entrySet().iterator();
		Object returnObject = null;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (entry.getKey().toString().compareTo(field) == 0) {
				returnObject = entry.getValue();
				if (modeDebugUtility)
					System.out.println("Get ID:" + entry.getValue());
			}
		}
		return returnObject;
	}

//	public static String decodeJSON(String receiveJSON, String receiveField,
//			int dummy) throws ParseException {
//		JSONParser parser = new JSONParser();
//		UtilityKeyFinder finder = new UtilityKeyFinder();
//		finder.setMatchKey(receiveField);
//		while (!finder.isEnd()) {
//			parser.parse(receiveJSON, finder, true);
//			if (finder.isFound()) {
//				finder.setFound(false);
//				if (modeDebugUtility)
//					System.out.println(finder.getValue());
//				return String.valueOf(finder.getValue());
//			}
//		}
//		return "not found";
//	}

	public static LinkedList<String> readCSV(String inputFileName)
			throws IOException {
		String fileName = inputFileName;
		LineNumberReader lnr = new LineNumberReader(new FileReader(new File(
				fileName)));
		lnr.skip(Long.MAX_VALUE);
		System.out.println("Total number of nodeA: " + lnr.getLineNumber());
		LinkedList<String> nameList = new LinkedList<String>();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(
				fileName)));
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			if (modeDebugUtility)
				System.out.println(line);
			nameList.add(nameList.size(), line);
		}
		bufferedReader.close();
		return nameList;
	}

	public static void writeCSV(LinkedList<String> nodeA,
			LinkedList<String> nodeB, String pathName) throws IOException {
		FileWriter fileWriter = new FileWriter(pathName);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		for (int i = 0; i < nodeB.size(); i = i + 1) {
			printWriter.print(nodeA.get(i));
			printWriter.print(",");
			printWriter.println(nodeB.get(i));
		}
		printWriter.flush();
		printWriter.close();
		fileWriter.close();
	}

	public static void writeMySQL(String query, Connection connection) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			System.out.println(query);
			statement.close();
		} catch (Exception e) {
			sqlError++;
			System.out.println("Error from writeMySQL: " + e.getMessage());
			System.out.println("Error from statement: " + query);
		}
	}

	public static List<HashMap<String,String>> readMySQL(String query, Connection con) {
		ResultSet resultSet = null;
		List<HashMap<String,String>> sqlTable = new LinkedList<HashMap<String,String>>();
		List<String> columnName = new LinkedList<String>();
		try {
			Statement statement = con.createStatement();
			resultSet = statement.executeQuery(query);
			ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();		
			for (int i=1; i<=columnCount; i++)
			{
				columnName.add(metaData.getColumnName(i));
			}
			while (resultSet.next())
			{
				HashMap<String,String> row = new HashMap<String,String>();
				for (int j=1; j<=columnName.size(); j++)
				{
					row.put(columnName.get(j-1), resultSet.getString(columnName.get(j-1)));
					if(Utility.modeDebugUtility) System.out.println(
							columnName.get(j-1)+" : "+resultSet.getString(columnName.get(j-1)));
				}
				sqlTable.add(row);
			}
			resultSet.close();
			statement.close();
		} catch (Exception e) {
			System.out.println("Error from readMySQL: " + e.getMessage());
			System.out.println("Error from statement: " + query);
			e.printStackTrace();
		}
		if(Utility.modeDebugUtility) System.out.println("Sql Table Rows => "+sqlTable.size());
		return sqlTable;
	}
	
}