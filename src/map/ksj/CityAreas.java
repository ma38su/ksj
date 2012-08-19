package map.ksj;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Arrays;

import map.Label;

/**
 * 行政区画(面)
 * 
 * @author fujiwara
 *
 */
public class CityAreas implements Label {

	private Polygon[] polygons;
	private CityInfo info;
	
	private transient int x;
	private transient int y;
	private transient Rectangle bounds;

	public CityAreas(CityInfo info, Polygon[] polygons) {
		this.info = info;
		this.polygons = polygons;
		initBounds();
	}
	
	public String getName() {
		return this.info.getCn2();
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}

	private void initBounds() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		long maxX = 0, maxY = 0;
		int maxS = 0;
		for (Polygon polygon : this.polygons) {
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
		this.bounds = new Rectangle(minX, minY, (int) (maxX - minX), (int) (maxY - minY));
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
	
	public Polygon[] getPolygons() {
		return this.polygons;
	}

	public void draw(Graphics2D g) {
		for (Polygon p : this.polygons) {
			g.drawPolygon(p);
		}
	}
	
	public void fill(Graphics2D g) {
		for (Polygon p : this.polygons) {
			g.fillPolygon(p);
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
}
