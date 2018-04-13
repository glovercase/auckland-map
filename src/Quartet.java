


public class Quartet implements Comparable<Quartet>{
	
	private Node currentNode;
	private Quartet from;
	private Segment pathto;
	
	private double costTohere;
	private double totalCostToGoal;
	
	public Quartet(Node node, Quartet from, Segment seg, double costTohere, double totalCostToGoal){
		this.setCurrentNode(node);
		this.setFrom(from);
		this.setCostTohere(costTohere);
		this.setPathto(seg);
		this.setTotalCost(totalCostToGoal);
	}
	
	public double getTotalCost(){
		return totalCostToGoal;
	}
	
	public void setTotalCost(double cost){
		this.totalCostToGoal = cost;
	}
	
	public Node getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(Node currentNode) {
		this.currentNode = currentNode;
	}

	public Quartet getFrom() {
		return from;
	}

	public void setFrom(Quartet from) {
		this.from = from;
	}

	public double getCostTohere() {
		return costTohere;
	}

	public void setCostTohere(double costTohere) {
		this.costTohere = costTohere;
	}

	public Segment getPathto() {
		return pathto;
	}

	public void setPathto(Segment pathto) {
		this.pathto = pathto;
	}

	@Override
	public int compareTo(Quartet n){
		return (int)totalCostToGoal - (int)n.totalCostToGoal;
	}
	
	public String toString(){
		
		return "Road: " +pathto.road.name + ". Distance: " + pathto.length + " KM";
	}

}

