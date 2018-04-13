import java.text.DecimalFormat;


public class Printer {
		String name = "";
		Double dist;
		
		public Printer(String name, double dist){
			this.name = name;
			this.dist = dist;
		}
		
		public String toString(){
			 DecimalFormat df = new DecimalFormat("#.###");
			return "Road: " +name+ ". Distance: " +df.format(dist) +"KM";
		}
		
		public String toStingTime(){
			DecimalFormat df = new DecimalFormat("#.##");
			return "Road: "+name+". Time: "+df.format(dist)+ " Min";
		}
}
