package map.ksj;

import java.io.Serializable;

public class Area implements Data, Serializable {

	private GmlPolygon polygon;
	private AreaInfo info;

	public Area() {
		this.info = new AreaInfo();
	}
	
	public Area(AreaInfo info, GmlPolygon polygon) {
		this.info = info;
		this.polygon = polygon;
	}
	
	public AreaInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(AreaInfo info) {
		this.info = info;
	}
	
	public GmlPolygon getPolygon() {
		return this.polygon;
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
		if (obj instanceof Area) {
			Area area = (Area) obj;
			ret = this.polygon.equals(area.polygon) && this.info.equals(area.info);
		}
		return ret;
	}
}
