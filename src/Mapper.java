import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * This is the main class for the mapping program. It extends the GUI abstract
 * class and implements all the methods necessary, as well as having a main
 * function.
 * 
 */
public class Mapper extends GUI {
	public static final Color NODE_COLOUR = new Color(77, 113, 255);
	public static final Color SEGMENT_COLOUR = new Color(130, 130, 130);
	public static final Color HIGHLIGHT_COLOUR = new Color(255, 219, 77);
	public static final Color GOAL_COLOR = new Color(10, 250, 10);

	// these two constants define the size of the node squares at different zoom
	// levels; the equation used is node size = NODE_INTERCEPT + NODE_GRADIENT *
	// log(scale)
	public static final int NODE_INTERCEPT = 1;
	public static final double NODE_GRADIENT = 0.8;

	// defines how much you move per button press, and is dependent on scale.
	public static final double MOVE_AMOUNT = 100;
	// defines how much you zoom in/out per button press, and the maximum and
	// minimum zoom levels.
	public static final double ZOOM_FACTOR = 1.3;
	public static final double MIN_ZOOM = 1, MAX_ZOOM = 200;

	// how far away from a node you can click before it isn't counted.
	public static final double MAX_CLICKED_DISTANCE = 0.15;

	// these two define the 'view' of the program, ie. where you're looking and
	// how zoomed in you are.
	private Location origin;
	private double scale;

	// our data structures.
	private Graph graph;
	private Trie trie;
	
	
	//boolean buttons
	private boolean AStarButton = false;
	private boolean ArticButton = false;
	private boolean distButton = false;
	private boolean timeButton = false;
	
	// for A* start and goal
	private boolean startNode = true;
	private Node startingNode;
	private Node goalNode;
	//data structures 
	HashSet<Node> articulationPoints = new HashSet<Node>();
	HashSet<Node> articPoints = new HashSet<Node>();
	Stack<Quartet> path = new Stack<Quartet>();
	Stack<Quartet> tempstack = new Stack<Quartet>();
	Stack<Quartet> printPath = new Stack<Quartet>();
	Stack<Elem> articulationPts = new Stack<Elem>();
	Map<Integer, Restriction> restrictionMap = new HashMap<Integer,Restriction>();
	
	@Override
	protected void redraw(Graphics g) {
		if (graph != null)
			graph.draw(g, getDrawingAreaDimension(), origin, scale);
	}

	@Override
	protected void onClick(MouseEvent e) {
		Location clicked = Location.newFromPoint(e.getPoint(), origin, scale);
		// find the closest node.
		double bestDist = Double.MAX_VALUE;
		Node closest = null;
		
		for (Node node : graph.nodes.values()) {
			double distance = clicked.distance(node.location);
			if (distance < bestDist) {
				bestDist = distance;
				closest = node;
			}
		}

		// if it's close enough, highlight it and show some information.
		if (clicked.distance(closest.location) < MAX_CLICKED_DISTANCE) {
			if(AStarButton){
				if(startNode){
					startingNode = closest;
					graph.setHighlight(closest);
					startNode = false;
					graph.unsetGoalNode(goalNode);
				}else{
					graph.setGoalNode(closest);
					startNode = true;
					goalNode = closest;
					aStar(startingNode, goalNode);
					
					graph.setHighlightedSegments(path);
					
					getTextOutputArea().setText(outPut());
				}
			}
			if(ArticButton){
				articulationPoints = articPoints(closest);	
				graph.setHighLitedNodes(articulationPoints);
				
			}
			
		}
	}
	
	public String outPut(){
		while(!tempstack.isEmpty()){
			printPath.push(tempstack.pop());
			
			}
			
			Queue<Printer> pathwayList = new ArrayDeque<Printer>();
			Quartet peek = printPath.peek();
			Printer pe = new Printer(peek.getPathto().road.name, peek.getPathto().length);
			pathwayList.add(pe);
			while(printPath.size() > 1){
				Quartet q = printPath.pop();
				if(q.getPathto().road.name.equals(pe.name)){
					pe.dist += q.getPathto().length;
				}else{
					pe = new Printer(q.getPathto().road.name, q.getPathto().length);
					pathwayList.add(pe);
				}

			}
			double total = 0;
			String pathway ="";
			printPath.pop();
			for(Printer p: pathwayList){
				if(timeButton){
					total += p.dist;
					pathway += p.toStingTime()+"\n";
				}
				if(distButton){
					total += p.dist;
					pathway += p.toString()+ "\n";
				}
			}
			DecimalFormat df = new DecimalFormat("#.##");
			if(timeButton){
				
				 pathway +="\n Total: "+ df.format(total) +" MIN";
			}
			if(distButton){
				 pathway +="\n Total: "+df.format(total)+" KM";
			}
			return pathway;
	}
	
	public void aStar(Node start, Node goal){
		restrictionMap = graph.restrictions;
		PriorityQueue<Quartet> fringe = new PriorityQueue<Quartet>();
			for(Node n: graph.nodes.values()){
				n.visited = false;
				n.pathFrom = null;
			}
			Double estToGoal = 0.0;
			if(timeButton){
				estToGoal = 50/start.location.distance(goal.location);
			}else{
				estToGoal = start.location.distance(goal.location);
			}
			Quartet first = new Quartet(start, null, null, 0, estToGoal);
			fringe.add(first);
			
			Boolean Found = false;
			
			while(!fringe.isEmpty() && (!Found) ){
				Quartet step = fringe.poll();
				if(!step.getCurrentNode().visited){
					step.getCurrentNode().visited = true;
					step.getCurrentNode().pathFrom = step.getFrom();
				}
				if(step.getCurrentNode().equals(goal)){
					Quartet temp = step;
					while(temp != null){
						path.push(temp);
						tempstack.push(temp);
						temp = temp.getFrom();
					}
					step.getFrom();
					
					Found = true;
				}
				Restriction restriction = restrictionMap.get(step.getCurrentNode());
				
				for(Segment s :step.getCurrentNode().segments){
					if(restriction != null){
					if(step.getCurrentNode().nodeID == restriction.on && 
							step.getFrom().getCurrentNode().nodeID == restriction.from && 
							step.getFrom().getPathto().road.roadID == restriction.roadfrom){
						if(s.road.roadID == restriction.roadto && s.end.nodeID == restriction.to){
							System.out.println("cars");
							continue;
						}
					}
					}
					double costToNeigh = 0.0;
					double estTotal = 0.0;
					if(timeButton){
						int speed = speed(s.road.speed);
						costToNeigh = step.getCostTohere() + speed/s.length;
						estTotal = costToNeigh + speed/s.end.location.distance(goal.location);
					}else{
						costToNeigh = step.getCostTohere() + s.length;
						estTotal = costToNeigh + s.end.location.distance(goal.location);
					}
					Quartet neighbor = new Quartet(s.end, step, s, costToNeigh, estTotal);
					if(!s.end.visited){	
						fringe.add(neighbor);
					}	
				}	
			}
		
	}
	
	/**
	 * takes a speed case and returns the correct speed
	 * @param speedlevel
	 * @return speed
	 */
	public int speed(int speedlevel){
			switch(speedlevel){
				case 0: return 5;
				case 1: return 20;
				case 2: return 40;
				case 3: return 60;
				case 4: return 80;
				case 5: return 100;
				case 6: return 110;
				case 7: return 300;
				default: return 50;
			}
	}
	
	public HashSet<Node> articPoints(Node start){
		for(Node n: graph.nodes.values()){
			n.depth = Double.POSITIVE_INFINITY;
		}
		
		start.depth = 0;
		int numSubTrees = 0;
		
		for(Segment s: start.segments){
			if(s.end.depth == Double.POSITIVE_INFINITY){
				recArtPts(s.end, 1, start);
				numSubTrees ++;
			}
			if (numSubTrees > 1){
				articulationPoints.add(start);
			}
			
		}
		return articulationPoints;
	}
	
	public double recArtPts(Node node, int depth, Node fromNode){
		node.depth = depth;
		double reachBack = depth;
		for(Segment s: node.segments){
			if(s.end.equals(fromNode)){
				continue;
			}
			if(s.end.depth < Double.POSITIVE_INFINITY){
				reachBack = Math.min(reachBack, s.end.depth);
			}else{
				double childReach = recArtPts(s.end, depth+1, node);
				if(childReach >= depth){
					articulationPoints.add(node);
				}
				reachBack = Math.min(reachBack, childReach);
			}
		}
		return reachBack;
	}
	
	
	
	@Override
	protected void onSet(Action a) {
		// set a boolean true or false depending on the button pressed
		if(a == GUI.Action.ARTIC){
			
			ArticButton = true;
			AStarButton = false;
		}else if(a == GUI.Action.ASTAR){
			
			AStarButton = true;
			ArticButton = false;
		}else if(a == GUI.Action.DIST){
			distButton = true;
			timeButton = false;
				
		}else if(a == GUI.Action.TIME){
			distButton = false;
			timeButton = true;
		}
		
		
	}
	
	
	@Override
	protected void onWheel(MouseWheelEvent e){
		int notches = e.getWheelRotation();//e.MOUSE_WHEEL
			if(notches < 0){
				if (scale > MIN_ZOOM) {
					scaleOrigin(false);
					scale /= ZOOM_FACTOR;
				}
			} else {	
				if (scale < MAX_ZOOM) {
					// yes, this does allow you to go slightly over/under the
					// max/min scale, but it means that we always zoom exactly to
					// the centre.
					scaleOrigin(true);
					scale *= ZOOM_FACTOR;
				}
				
			}
	}

	@Override
	protected void onSearch() {
		if (trie == null)
			return;

		// get the search query and run it through the trie.
		String query = getSearchBox().getText();
		Collection<Road> selected = trie.get(query);

		// figure out if any of our selected roads exactly matches the search
		// query. if so, as per the specification, we should only highlight
		// exact matches. there may be (and are) many exact matches, however, so
		// we have to do this carefully.
		boolean exactMatch = false;
		for (Road road : selected)
			if (road.name.equals(query))
				exactMatch = true;

		// make a set of all the roads that match exactly, and make this our new
		// selected set.
		if (exactMatch) {
			Collection<Road> exactMatches = new HashSet<Road>();
			for (Road road : selected)
				if (road.name.equals(query))
					exactMatches.add(road);
			selected = exactMatches;
		}

		// set the highlighted roads.
		graph.setHighlight(selected);

		// now build the string for display. we filter out duplicates by putting
		// it through a set first, and then combine it.
		Collection<String> names = new HashSet<String>();
		for (Road road : selected)
			names.add(road.name);
		String str = "";
		for (String name : names)
			str += name + "; ";

		if (str.length() != 0)
			str = str.substring(0, str.length() - 2);
		getTextOutputArea().setText(str);
	}

	@Override
	protected void onMove(Move m) {
		if (m == GUI.Move.NORTH) {
			origin = origin.moveBy(0, -MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.SOUTH) {
			origin = origin.moveBy(0, MOVE_AMOUNT / scale);
		} else if (m == GUI.Move.EAST) {
			origin = origin.moveBy(-MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.WEST) {
			origin = origin.moveBy(MOVE_AMOUNT / scale, 0);
		} else if (m == GUI.Move.IN) {
			if (scale < MAX_ZOOM) {
				// yes, this does allow you to go slightly over/under the
				// max/min scale, but it means that we always zoom exactly to
				// the centre.
				scaleOrigin(true);
				scale *= ZOOM_FACTOR;
			}
		} else if (m == GUI.Move.OUT) {
			if (scale > MIN_ZOOM) {
				scaleOrigin(false);
				scale /= ZOOM_FACTOR;
			}
			
		}
	}

	
	@Override
	protected void onLoad(File nodes, File roads, File segments, File polygons, File restrictions) {
		graph = new Graph(nodes, roads, segments, polygons, restrictions);
		trie = new Trie(graph.roads.values());
		origin = new Location(-250, 250); // close enough
		scale = 1;
	}
	
	@Override
	protected void onLoadR(File restrictions) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * This method does the nasty logic of making sure we always zoom into/out
	 * of the centre of the screen. It assumes that scale has just been updated
	 * to be either scale * ZOOM_FACTOR (zooming in) or scale / ZOOM_FACTOR
	 * (zooming out). The passed boolean should correspond to this, ie. be true
	 * if the scale was just increased.
	 */
	private void scaleOrigin(boolean zoomIn) {
		Dimension area = getDrawingAreaDimension();
		double zoom = zoomIn ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;

		int dx = (int) ((area.width - (area.width * zoom)) / 2);
		int dy = (int) ((area.height - (area.height * zoom)) / 2);

		origin = Location.newFromPoint(new Point(dx, dy), origin, scale);
	}

	public static void main(String[] args) {
		new Mapper();
	}

	


	
}
