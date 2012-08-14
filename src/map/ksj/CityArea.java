package map.ksj;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

/**
 * 行政区画(面)
 * 
 * @author fujiwara
 *
 */
public class CityArea implements Data {

	private List<Polygon> polygons;
	private CityInfo info;

	public CityArea() {
		this.info = new CityInfo();
		this.polygons = new ArrayList<Polygon>();
	}

	public CityInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(CityInfo info) {
		this.info = info;
	}
	
	public List<Polygon> getPolygons() {
		return this.polygons;
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlPolygons) {
			assert("ksj:are".equals(tag));
			GmlPolygons gml = (GmlPolygons) obj;
			this.polygons.addAll(gml.getPolygons());
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
		return this.info.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof CityArea) {
			CityArea area = (CityArea) obj;
			ret = this.info.equals(area.info) && this.polygons.equals(area.polygons);
		}
		return ret;
	}
	
}
