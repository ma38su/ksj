package map.ksj;

import java.awt.Rectangle;

public class PrefectureCollection {
	
	private int code;
	private CityAreas[] areas;
	private BusCollection bus;
	
	private Rectangle bounds;

	public PrefectureCollection(int code, CityAreas[] areas, BusCollection bus) {
		this.code = code;
		this.areas = areas;
		this.bus = bus;

		this.initBounds();
	}
	
	public int getCode() {
		return this.code;
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	public CityAreas[] getAreas() {
		return this.areas;
	}
	
	public BusCollection getBusCollection() {
		return this.bus;
	}
	
	private void initBounds() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = 0, maxY = 0;
		for (CityAreas area : this.areas) {
			Rectangle r = area.getBounds();
			if (minX > r.x) minX = r.x;
			if (minY > r.y) minY = r.y;
			if (maxX < r.x + r.width) maxX = r.x + r.width;
			if (maxY < r.y + r.height) maxY = r.y + r.height;
		}
		this.bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}
}
