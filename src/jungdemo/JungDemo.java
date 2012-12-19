package jungdemo;

import dataset.*;
import dataset.twitterDataProcessing.UserAccount;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import jungelement.Edge;
import jungelement.Node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Transformer;

/**
 * 
 * @author NASA-Trust-Team
 */
public class JungDemo {
	public static final boolean modeDebug = false;
	public static boolean toStringMode = false;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		// Fetches twitter dataset
		/*DatasetInterface twitterDataset = new TwitterDataSource();

		HashMap<String, UserAccount> twitterUserAccounts = twitterDataset
				.getDataset();

		Map<String, Node> twitterNodes = new HashMap<String, Node>(
				getTwitterNodes(twitterUserAccounts));

		Iterator<String> iterator = twitterNodes.keySet().iterator();

		if (modeDebug) {
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				System.out.println("-Node-");
				System.out.println(twitterNodes.get(key).getTwitterId());
				System.out.println(twitterNodes.get(key).getPopularity());
			}
			System.out.println("-End-");
		}
		Map<Integer, Edge> twitterEdges = new HashMap<Integer, Edge>(
				getTwitterEdges(twitterUserAccounts, twitterNodes));

		JungDemo demo = new JungDemo();
		Graph<Node, Edge> graph = demo.instantiateGraphCreation(twitterNodes,
				twitterEdges);
		demo.projectGraph(graph);*/
	}

	public Graph<Node, Edge> instantiateGraphCreation(Map<String, Node> nodes,
			Map<Integer, Edge> edges) {
		Graph<Node, Edge> graph = new SparseMultigraph<Node, Edge>();
		Set<String> nodesSet = nodes.keySet();
		Iterator<String> itNodes = nodesSet.iterator();
		while (itNodes.hasNext()) {
			String twitterId = itNodes.next();
			Node node = nodes.get(twitterId);
			graph.addVertex(node);
		}
		Set<Integer> edgesSet = edges.keySet();
		Iterator<Integer> itEdges = edgesSet.iterator();
		while (itEdges.hasNext()) {
			int edgeValue = itEdges.next();
			Edge edge = edges.get(edgeValue);
			if(edge.getEndNode()!=null)	graph.addEdge(edge, edge.getStartNode(), edge.getEndNode(),
					EdgeType.DIRECTED);
		}
		if (modeDebug) {
			System.out.println("Edges:" + edges);
			System.out.println("Graph:" + graph);
		}
		return graph;
	}

	public void projectGraph(Graph<Node, Edge> graph,final double maxPopularity) {
		Layout<Node, Edge> layout = new ISOMLayout<Node, Edge>(graph);

		// Sets the initial size of the applet
		layout.setSize(new Dimension(1000, 700));
		VisualizationViewer<Node, Edge> visualizationViewer = new VisualizationViewer<Node, Edge>(
				layout);
		visualizationViewer.setPreferredSize(new Dimension(1000, 700));
		Transformer<Node, Shape> vertexSize = new Transformer<Node, Shape>() {
			public Shape transform(Node node) {
				Double popularity = node.getPopularity();
				
				popularity = (400*popularity)/maxPopularity;
				
				Ellipse2D circle = new Ellipse2D.Double((-6-0.3*popularity),
				(-6-0.3*popularity), (12+0.6*popularity),
				(12+0.6*popularity));				
				return circle;
			}
		};
		visualizationViewer.getRenderContext().setVertexShapeTransformer(
				vertexSize);
		Transformer<Node, Paint> vertexColor = new Transformer<Node, Paint>() {
			public Paint transform(Node node) {
				if(node.getPopularity()==0){
					return Color.WHITE;					
				}else if(node.getPopularity()<5){
					return Color.getHSBColor(0.2f, 0.5f, 0.9f);
				}else if(node.getPopularity()<10){
					return Color.getHSBColor(0.15f, 0.6f, 0.85f);
				}else if(node.getPopularity()<50){
					return Color.getHSBColor(0.1f, 0.7f, 0.7f);
				}else if(node.getPopularity()<100){
					return Color.getHSBColor(0.05f, 0.8f, 0.55f);
					
				}else{
					return Color.getHSBColor(0, 0.9f, 0.4f);
				}
//				if(node.getPopularity()==0){
//					return Color.WHITE;					
//				}else {
//					float popularindex = (float) (node.getPopularity() / 5 );
//					return Color.getHSBColor((float)0.2+(popularindex/20),1f,1f);
//				}
				
			}
		};
		visualizationViewer.getRenderContext().setVertexFillPaintTransformer(vertexColor);
		visualizationViewer
				.setVertexToolTipTransformer(new ToStringLabeller<Node>() {
					
					public String transform(Graph<Node, Edge> graph) {
						return graph.getVertices().toString();
					}
				});
		
		
		//visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Node>());
		
		JFrame frame = new JFrame("Interactive Graph View 3");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setForeground(Color.ORANGE);
		frame.getContentPane().add(visualizationViewer);
		
		frame.pack();
		frame.setVisible(true);
	}

	/*public static Map<String, Node> getTwitterNodes(
			HashMap<String, UserAccount> twitterData) {
		Iterator<String> iterator = twitterData.keySet().iterator();

		Map<String, Node> twitterNodes = new HashMap<String, Node>();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			Node node = new Node();
			UserAccount twitterUserAccounts = new UserAccount(
					twitterData.get(key));
			node.setTwitterId(twitterUserAccounts.getId());
			node.setName(twitterUserAccounts.getName());
			node.setPopularity(twitterUserAccounts.getIn_degree(),
					twitterUserAccounts.getOut_degree());
			twitterNodes.put(node.getTwitterId(), node);
		}
		return twitterNodes;
	}*/

	/*public static Map<Integer, Edge> getTwitterEdges(
			HashMap<String, UserAccount> twitterData,
			Map<String, Node> twitterNodes) {
		Iterator<String> iterator = twitterData.keySet().iterator();
		Map<Integer, Edge> twitterEdges = new HashMap<Integer, Edge>();
		while (iterator.hasNext()) {
			String key = iterator.next().toString();
			UserAccount twitterUserAccounts = new UserAccount(
					twitterData.get(key));
			if (modeDebug) {
				System.out.println("CHECK ID: " + twitterUserAccounts.getId());
			}
			Node startNode = twitterNodes.get(twitterUserAccounts.getId());
			LinkedList<String> followers = twitterUserAccounts.getFollow();
			if (followers != null) {
				for (int i = 0; i < followers.size(); i++) {
					Edge edge = new Edge();
					edge.setStartNode(startNode);
					edge.setEndNode(twitterNodes.get(followers.get(i)));
					edge.setEdgeWeight(1, 'B');
					twitterEdges.put(edge.getId(), edge);
				}
			}
		}
		return twitterEdges;
	}*/
}