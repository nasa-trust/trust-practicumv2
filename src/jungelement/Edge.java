/**
 * 
 */
package jungelement;

/**
 * @author NASA-Trust-Team
 * 
 */
public final class Edge
{
	private int id;
	private static int count;
	private Node startNode;
	private Node endNode;
	private double edgeWeight;
	public Edge()
	{
		id = ++count;
	}
	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public Node getStartNode()
	{
		return startNode;
	}

	public void setStartNode( Node startNode )
	{
		this.startNode = startNode;
	}

	public void setEndNode( Node endNode )
	{
		this.endNode = endNode;
	}

	public double getEdgeWeight()
	{
		return edgeWeight;
	}

	public void setEdgeWeight( double edgeWeight )
	{
		this.edgeWeight = edgeWeight;
	}

	public Node getEndNode()
	{
		return endNode;
	}

	public String toString()
	{
		return "E " + this.id;
	}

}