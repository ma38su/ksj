package map.ksj;

import java.awt.Point;

public class AdministrativeArea implements Data {

	private Point[] points;
	private String prn;
	private String sun;
	private String con;
	private String cn2;
	private String aac;
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlPolygon) {
			assert("ksj:are".equals(tag));
			this.points = ((GmlPolygon) obj).getPoints();
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:prn".equals(tag)) {
				this.prn = string;
			} else if ("ksj:sun".equals(tag)) {
				this.sun = string;
			} else if ("ksj:con".equals(tag)) {
				this.con = string;
			} else if ("ksj:cn2".equals(tag)) {
				this.cn2 = string;
			} else if ("ksj:aac".equals(tag)) {
				this.aac = string;
//				"ksj:aac codeSpace="AdministrativeAreaCode.xml">01367</ksj:aac>
			}
		}
	}
}
