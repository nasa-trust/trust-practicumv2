package dataset;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dataset.twitterDataProcessing.*;
import dataset.utility.*;

/**
 * @author NASA-Trust-Team
 * 
 */
public class TwitterDataSource implements DatasetInterface {

	public HashMap<String, UserAccount> getDataset() {
		HashMap<String, UserAccount> twitter = new HashMap<String, UserAccount>();
		String databasename = "twitter";
		String username = "root";
		String password = "Cherry00";
		String url = "jdbc:mysql://10.0.9.111:3306/" + databasename;
		//String nodeTable = "twitter.`node-backup-2012-09-25`";
		//String edgeTable = "twitter.`edge-backup-20120925`";
		String nodeTable = "twitter.node_subset_17oct";
		String edgeTable = "twitter.edge_subset_17oct";
		//String nodeTable = "twitter.node_subset";
		//String edgeTable = "twitter.edge_subset";
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println("MySQL Driver: " + e);
		}
		
		String sqlStatement = "select * from "+nodeTable +" where status = 'listed' || tier = '2';";
//		String sqlStatement = "select * from "+nodeTable +";";
		List<HashMap<String,String>> sqlNodeTable = Utility.readMySQL(sqlStatement, con);

		for (int i = 0; i < sqlNodeTable.size(); i = i + 1) {
			UserAccount user = new UserAccount();
			HashMap<String,String> row = new HashMap<String,String>(sqlNodeTable.get(i));
			user.setId(row.get("id"));
			if(row.get("name")!=null)user.setName(row.get("name"));
			else user.setName("null");
			if(row.get("username")!=null)user.setUsername(row.get("username"));
			else user.setUsername("null");
			user.setIn_degree(0);
			//TODO discuss later
			//user.setIn_degree(Integer.parseInt(row.get("in_degree"))); .. Do not remove this!
			//user.setOut_degree(Integer.parseInt(row.get("out_degree"))); .. Do not remove this!
			user.setTier(Integer.parseInt(row.get("tier")));
//			sqlStatement = "SELECT nodeB_id FROM "+edgeTable+" E where nodeA_id = '"
//					+ row.get("id") + "' and E.nodeB_id in (select N.id from "
//					+nodeTable+" N where N.status = 'listed' or N.status = 'unlisted');";
			sqlStatement = "SELECT nodeB_id FROM "+edgeTable+" where nodeA_id = '"+row.get("id")+"';";
			List<HashMap<String,String>> sqlEdgeTable 
					= new LinkedList<HashMap<String,String>>(Utility.readMySQL(sqlStatement, con));
			List<String> follow = new LinkedList<String>();
			for (int j=0; j<sqlEdgeTable.size(); j++)
			{
				follow.add(sqlEdgeTable.get(j).get("nodeB_id"));
			}
			user.setOut_degree(follow.size());
			user.setFollow((LinkedList<String>) follow);
			twitter.put(row.get("id").toString(), user);
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("Connection Closed: " + e);
		}
		return twitter;

	}
}
