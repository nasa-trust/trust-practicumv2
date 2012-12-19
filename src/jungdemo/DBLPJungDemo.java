//package jungdemo;
//
//import dataset.DBLPDataSource;
//import dataset.DatasetInterface;
//import dataset.DBLPDataProcessing.DBLPUser;
//import edu.uci.ics.jung.graph.Graph;
//import element.*;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//public class DBLPJungDemo {
//
//	private static boolean debugMode = false;
//	
//	public static void main(String[] args) 
//	{
//		DatasetInterface dblpDataset = new DBLPDataSource();
//		HashMap<String,DBLPUser> dblp = dblpDataset.getDataset();
//		System.out.println("Create Node Objects");
//		Map<String, Node> dblpNodes = new HashMap<String, Node>(
//				getDBLPNodes(dblp));
//		System.out.println("Create Edge Objects");
//		Map<Integer, Edge> dblpEdges = new HashMap<Integer, Edge>(
//				getDBLPEdges(dblp, dblpNodes));
//		System.out.println("Start Processing Plotting");
//		dblp = null;
//		JungDemo demo = new JungDemo();
//		Graph<Node, Edge> graph = demo.instantiateGraphCreation(dblpNodes,
//				dblpEdges);
//		demo.projectGraph(graph);
//	}
//	
//	public static Map<String,Node> getDBLPNodes(HashMap<String,DBLPUser> dblp)
//	{
//		Map<String,Node> dblpNodes = new HashMap<String,Node>();
//		Iterator<String> iterator = dblp.keySet().iterator();
//		while(iterator.hasNext())
//		{
//			String name = iterator.next();
//			Node node = new Node();
//			node.setName(name);
//			DBLPUser author = dblp.get(name);
//			int popularity = author.countArticle()+author.countBook()
//					+author.countIncollection()+author.countInproceedings()+author.countMastersthesis()
//					+author.countPhdthesis()+author.countProceedings()+author.countWww();
//			if(debugMode) 
//			{
//				System.out.println(name);
//				System.out.println(author.countArticle());
//				System.out.println(author.countBook());
//				System.out.println(author.countIncollection());
//				System.out.println(author.countInproceedings());
//				System.out.println(author.countMastersthesis());
//				System.out.println(author.countPhdthesis());
//				System.out.println(author.countProceedings());
//				System.out.println(author.countWww());
//				System.out.println("="+popularity);
//			}
//			node.setPopularity(popularity);
//			dblpNodes.put(node.getName(), node);
//		}
//		return dblpNodes;
//	}
//	
//	public static Map<Integer,Edge> getDBLPEdges(Map<String,DBLPUser> dblp,
//			Map<String, Node> dblpNodes)
//	{
//		//Fix later bcoz restructure
//		Iterator<String> iterator = dblp.keySet().iterator();
//		Map<Integer, Edge> dblpEdges = new HashMap<Integer, Edge>();
//		while (iterator.hasNext()) {
//			String key = iterator.next().toString();
//			DBLPUser author = dblp.get(key);
//			author.setCoauthors();
//			Node startNode = dblpNodes.get(key);
//			Map<String,Integer> coauthors = author.getCoauthors();
//			if (coauthors != null) {
//				Iterator<String> coauthors_iterator = coauthors.keySet().iterator();
//				while (coauthors_iterator.hasNext())
//				{
//					String coauthor_name = coauthors_iterator.next();
//					if(!coauthor_name.equalsIgnoreCase(key))
//					{
//					int coauthor_count = coauthors.get(coauthor_name);
//					Edge edge = new Edge();
//					edge.setStartNode(startNode);
//					edge.setEndNode(dblpNodes.get(coauthor_name));
//					edge.setEdgeWeight(coauthor_count, 'A');
//					dblpEdges.put(edge.getId(), edge);	
//					}
//				}
//			}
//		}
//		return dblpEdges;	
//	}
//
//}
