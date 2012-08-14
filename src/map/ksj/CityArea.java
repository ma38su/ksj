package map.ksj;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * 行政区画(面)
 * 
 * @author fujiwara
 *
 */
public class CityArea implements Data, Serializable {

	private GmlPolygon polygon;
	private CityInfo info;

	public CityArea() {
		this.info = new CityInfo();
	}
	
	public CityArea(CityInfo info, GmlPolygon polygon) {
		this.info = info;
		this.polygon = polygon;
	}
	
	public CityInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(CityInfo info) {
		this.info = info;
	}
	
	public GmlPolygon getPolygon() {
		return this.polygon;
	}

	public void draw(Graphics2D g) {
		g.drawPolygon(this.polygon);
	}
	
	public void fill(Graphics2D g) {
		g.fillPolygon(this.polygon);
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlPolygon) {
			assert("ksj:are".equals(tag));
			this.polygon = (GmlPolygon) obj;
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:prn".equals(tag)) {
				this.info.setPrn(string);
			} else if ("ksj:sun".equals(tag)) {
				this.info.setSun(string);
			} else if ("ksj:con".equals(tag)) {
				this.info.setCon(string);
			} else if ("ksj:cn2".equals(tag)) {
				this.info.setCn2(string);
			} else if ("ksj:aac".equals(tag)) {
				int aac = "".equals(string) ? -1 : Integer.parseInt(string);
				this.info.setAac(aac);
			}
		}
	}

	@Override
	public int hashCode() {
		return this.polygon.hashCode() + this.info.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof CityArea) {
			CityArea area = (CityArea) obj;
			ret = this.polygon.equals(area.polygon) && this.info.equals(area.info);
		}
		return ret;
	}
	
}
