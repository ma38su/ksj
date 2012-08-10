package map.ksj;

import java.awt.Point;

public class GmlPoint implements Data {
	
	private Point point;
	
	public Point getPoint() {
		return this.point;
	}
	
	public void link(String tag, Object obj) {
		if (obj instanceof Point) {
			this.point = (Point) obj;
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlPoint) {
			GmlPoint p = (GmlPoint) obj;
			ret = this.point.equals(p.point);
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return "point: "+ this.point;
	}

	@Override
	public int hashCode() {
		return this.point.hashCode();
	}
}
