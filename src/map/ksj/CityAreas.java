package map.ksj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 行政区画(面)
 * 
 * @author fujiwara
 *
 */
public class CityAreas implements Serializable {

	private GmlPolygon[] polygons;
	private CityInfo info;
	
	private transient int x;
	private transient int y;
	private transient Rectangle bounds;

	public CityAreas(CityInfo info, GmlPolygon[] polygons) {
		this.info = info;
		this.polygons = polygons;
		initBounds();
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

	private void initBounds() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = 0, maxY = 0;
		int maxS = 0;
		for (GmlPolygon polygon : polygons) {
			Rectangle r = polygon.getBounds();
			if (minX > r.x) minX = r.x;
			if (minY > r.y) minY = r.y;
			if (maxX < r.x + r.width) maxX = r.x + r.width;
			if (maxY < r.y + r.height) maxY = r.y + r.height;
			if (maxS < r.width * r.height) {
				maxS = r.width * r.height;
				this.x = r.x + r.width / 2;
				this.y = r.y + r.height / 2;
			}
		}
		this.bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	public CityInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(CityInfo info) {
		this.info = info;
	}
	
	public GmlPolygon[] getPolygons() {
		return this.polygons;
	}

	public void draw(Graphics2D g) {
		for (GmlPolygon p : this.polygons) {
			p.draw(g);
		}
	}
	
	public void fill(Graphics2D g) {
		for (GmlPolygon p : this.polygons) {
			p.fill(g);
		}
	}
	
	@Override
	public int hashCode() {
		return this.info.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof CityAreas) {
			CityAreas area = (CityAreas) obj;
			ret = this.info.equals(area.info) && Arrays.equals(this.polygons, area.polygons);
		}
		return ret;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		initBounds();
	}
}
