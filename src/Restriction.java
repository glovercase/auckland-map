
public class Restriction {
	
	public final int roadfrom;
	public final int roadto;
	public final int from;
	public final int on;
	public final int to;
	public final Graph graph;
	
	public Restriction(Graph g, int n1, int r1, int n2, int r2, int n3){
		this.from = n1;
		this.roadfrom = r1;
		this.on = n2;
		this.roadto = r2;
		this.to = n3;
		this.graph = g;
	}
}
