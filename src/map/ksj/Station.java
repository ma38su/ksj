package map.ksj;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import map.Label;

public class Station implements Label, Data, RailroadSection {

	/**
	 * 駅名
	 */
	private String name;

	/**
	 * 路線情報
	 */
	private RailroadInfo info;
	
	private GmlCurve curve;

	public Station() {
		this.info = new RailroadInfo();
	}
	
	public Station(String name, RailroadInfo info, GmlCurve curve) {
		this.name = name;
		this.info = info;
		this.curve = curve;
	}

	/**
	 * @return　駅名
	 */
	public String getName() {
		return this.name;
	}
	
	public int getX() {
		return this.curve.getFirstPoint().x;
	}
	
	public int getY() {
		return this.curve.getFirstPoint().y;
	}
	
	@Override
	public RailroadInfo getInfo() {
		return this.info;
	}
	
	@Override
	public void setInfo(RailroadInfo info) {
		this.info = info;
	}
	
	@Override
	public GmlCurve getCurve() {
		return this.curve;
	}
	
	@Override
	public void setCurve(GmlCurve curve) {
		this.curve = curve;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:lin".equals(tag)) {
				this.info.setLine(string);
			} else if ("ksj:opc".equals(tag)) {
				this.info.setCompany(string);
			} else if ("ksj:stn".equals(tag)) {
				this.name = string;
			} else if ("ksj:int".equals(tag)) {
				int instituteType = Integer.parseInt(string);
				this.info.setInstituteType(instituteType);
			} else if ("ksj:rac".equals(tag)) {
				int railwayType = "".equals(string) ? -1 : Integer.parseInt(string);
				this.info.setRailwayType(railwayType);
			}
		} else if (obj instanceof RailroadSectionData) {
			assert(false); // ここはもう通らない
			RailroadSectionData section = (RailroadSectionData) obj;
			if (this.curve == null) {
				this.curve = section.getCurve();
			} else {
				assert(this.curve.equals(section.getCurve()));
				this.info = section.getInfo();
			}
		} else if (obj instanceof GmlCurve) {
			GmlCurve curve = (GmlCurve) obj;
			if (this.curve == null) {
				this.curve = curve;
			} else {
				assert(this.curve.equals(curve));
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
