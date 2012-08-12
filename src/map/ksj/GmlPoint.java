package map.ksj;

import java.awt.Point;

import util.FixedPoint;


/**
 * 点型
 * @author fujiwara
 */
public class GmlPoint implements Data {
	
	private int x;
	private int y;
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void link(String tag, Object obj) {
		if (obj instanceof Point) {
			Point point = (Point) obj;
			this.x = point.x;
			this.y = point.y;
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof GmlPoint) {
			GmlPoint p = (GmlPoint) obj;
			ret = this.x == p.x && this.y == p.y;
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return String.format("pt: %f,%f", FixedPoint.parseDouble(this.x), FixedPoint.parseDouble(this.y));
	}

	@Override
	public int hashCode() {
		return this.x + this.y;
	}
}
