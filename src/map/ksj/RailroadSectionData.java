package map.ksj;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class RailroadSectionData implements Data, RailroadSection {

	/**
	 * 路線情報
	 */
	private RailroadInfo info;

	private GmlCurve curve;
	
	public transient List<Integer> links = new ArrayList<Integer>();
	
	public int[] getLinks() {
		int[] ret = new int[this.links.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = this.links.get(i);
		}
		return ret;
	}
	
	public void addLink(int idx) {
		this.links.add(idx);
	}

	public RailroadSectionData() {
		this.info = new RailroadInfo();
	}
	
	public RailroadSectionData(RailroadInfo info, GmlCurve curve) {
		this.info = info;
		this.curve = curve;
	}
	
	public RailroadInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(RailroadInfo info) {
		this.info = info;
	}
	
	public GmlCurve getCurve() {
		return this.curve;
	}

	public void setCurve(GmlCurve curve) {
		this.curve = curve;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			assert("ksj:loc".equals(tag));
			this.curve = (GmlCurve) obj;
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:lin".equals(tag)) {
				this.info.setLine(string);
			} else if ("ksj:opc".equals(tag)) {
				this.info.setCompany(string);
			} else if ("ksj:int".equals(tag)) {
				int instituteType = Integer.parseInt(string);
				this.info.setInstituteType(instituteType);
			} else if ("ksj:rac".equals(tag)) {
				int railwayType = "".equals(string) ? -1 : Integer.parseInt(string);
				this.info.setRailwayType(railwayType);
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		this.curve.draw(g);
	}

	@Override
	public Rectangle getBounds() {
		return this.curve.getBounds();
	}

	@Override
	public boolean join(RailroadSection section) {
		GmlCurve curve = section.getCurve();
		boolean ret = this.curve.isConnected(curve);
		if (ret) {
			this.curve = this.curve.join(curve);
		}
		return ret;
	}

}
