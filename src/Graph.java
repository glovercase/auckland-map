import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

/**
 * This represents the data structure storing all the roads, nodes, and
 * segments, as well as some information on which nodes and segments should be
 * highlighted.
 * 
 * @author Casey Glover
 */
public class Graph {
	// map node IDs to Nodes.
	Map<Integer, Node> nodes = new HashMap<Integer, Node>();
	// map road IDs to Roads.
	Map<Integer, Road> roads;
	// just some collection of Segments.
	Collection<Segment> segments;
	//restrictions data
	Map<Integer, Restriction> restrictions;
	Node goalNode;
	Node oldGoalNode;
	Node highlightedNode;
	Collection<Road> highlightedRoads = new HashSet<Road>();
	Collection<Node> highlightedNodes = new HashSet<Node>();
	Stack<Quartet> highlightedSegments = new Stack<Quartet>();
	

	public Graph(File nodes, File roads, File segments, File polygons, File restrictions) {
		this.nodes = Parser.parseNodes(nodes, this);
		this.roads = Parser.parseRoads(roads, this);
		this.segments = Parser.parseSegments(segments, this);
		this.restrictions = Parser.parseRestrictions(restrictions, this);
	}

	public void draw(Graphics g, Dimension screen, Location origin, double scale) {
		// a compatibility wart on swing is that it has to give out Graphics
		// objects, but Graphics2D objects are nicer to work with. Luckily
		// they're a subclass, and swing always gives them out anyway, so we can
		// just do this.
		Graphics2D g2 = (Graphics2D) g;
		 
		// draw all the segments.
		g2.setColor(Mapper.SEGMENT_COLOUR);
		for (Segment s : segments)
			s.draw(g2, origin, scale);

		// draw the segments of all highlighted roads.
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		for (Road road : highlightedRoads) {
			for (Segment seg : road.components) {
				seg.draw(g2, origin, scale);
			}
		}
		

		// draw all the nodes.
		g2.setColor(Mapper.NODE_COLOUR);
		for (Node n : nodes.values())
			n.draw(g2, screen, origin, scale);

		// draw the highlighted node, if it exists.
		if (highlightedNode != null) {
			g2.setColor(Mapper.HIGHLIGHT_COLOUR);
			highlightedNode.draw(g2, screen, origin, scale);
		
		}
		if(goalNode != null){
			g2.setColor(Mapper.GOAL_COLOR);
			goalNode.draw(g2, screen, origin, scale);
			
		}
		
		if(oldGoalNode != null){
			g2.setColor(Mapper.NODE_COLOUR);
			oldGoalNode.draw(g2, screen, origin, scale);
		}
		
		//draw the nodes that are articulation points
		g2.setColor(Mapper.GOAL_COLOR);
				
		for(Node node : highlightedNodes){
			node.draw(g2, screen, origin, scale);
		}
		
		g2.setColor(Mapper.HIGHLIGHT_COLOUR);
		g2.setStroke(new BasicStroke(3));
		
		while(!highlightedSegments.isEmpty()){
			Quartet q = highlightedSegments.pop();
			
			q.getPathto().draw(g2, origin, scale);
		}
		
		
		
		
	}
	
	public void setGoalNode(Node node){
		this.goalNode = node;
	}
	
	public void unsetGoalNode(Node node){
		this.oldGoalNode = node;
	}

	public void setHighlight(Node node) {
		this.highlightedNode = node;
	}
	
	public void setHighLitedNodes(Collection<Node> nodes){
		this.highlightedNodes = nodes;
	}
	
	public void setHighlightedSegments(Stack<Quartet> quart){
		quart.pop();
		this.highlightedSegments = quart;
	}

	public void setHighlight(Collection<Road> roads) {
		this.highlightedRoads = roads;
	}
}
