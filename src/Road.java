import java.util.Collection;
import java.util.HashSet;

/**
 * Road represents ... a road ... in our graph, which is some metadata and a
 * collection of Segments. We have lots of information about Roads, but don't
 * use much of it.
 * 
 * @author Casey Glover
 */
public class Road {
	public final int roadID;
	public final String name, city;
	public final Collection<Segment> components;
	public final boolean oneway;
	public final int speed;
	public final boolean notforcar;
	public final boolean notforpede;
	public final boolean notforbicy;

	public Road(int roadID, int type, String label, String city, String oneway,
			int speed, int roadclass, String notforcar, String notforpede,
			String notforbicy) {
		this.roadID = roadID;
		this.city = city;
		this.name = label;
		this.speed = speed;
		this.notforcar = Boolean.parseBoolean(notforcar);
		this.notforpede = Boolean.parseBoolean(notforpede);
		this.notforbicy = Boolean.parseBoolean(notforbicy);
		this.oneway = Boolean.parseBoolean(oneway);
		this.components = new HashSet<Segment>();
	}

	public void addSegment(Segment seg) {
		
		components.add(seg);
	}
	
	
}
