package map.ksj;

import java.awt.Polygon;
import java.awt.Rectangle;

public class PrefectureCollection {
	
	private int code;
	private Polygon[] polygons;
	private CityAreas[] areas;
	private BusCollection bus;
	
	private transient Rectangle bounds;

	public PrefectureCollection(int code, Polygon[] polygons) {
		this.code = code;
		this.polygons = polygons;
		
		this.initPreBounds();
	}
	
	public PrefectureCollection(int code, Polygon[] polygons, CityAreas[] areas, BusCollection bus) {
		this.code = code;
		this.polygons = polygons;
		this.areas = areas;
		this.bus = bus;

		this.initBounds();
	}
	
	public Polygon[] getPolygons() {
		return this.polygons;
	}
	
	public String getName() {
		return CityInfo.PREF_NAME[this.code];
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
	
	public void setAreas(CityAreas[] areas) {
		this.areas = areas;
		this.initBounds();
	}
	
	public BusCollection getBusCollection() {
		return this.bus;
	}
	
	public void setBusCollection(BusCollection bus) {
		this.bus = bus;
	}
	
	private void initPreBounds() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		int maxX = 0, maxY = 0;
		for (Polygon polygon : this.polygons) {
			Rectangle r = polygon.getBounds();
			if (minX > r.x) minX = r.x;
			if (minY > r.y) minY = r.y;
			if (maxX < r.x + r.width) maxX = r.x + r.width;
			if (maxY < r.y + r.height) maxY = r.y + r.height;
		}
		this.bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
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
