package map.ksj;

import java.awt.Graphics2D;
import java.io.Serializable;

/**
 * バスルートのクラス
 * @author fujiwara
 */
public class BusRoute implements Data, Serializable {

	public BusRoute() {
		this.info = new BusRouteInfo();
	}
	
	public BusRoute(GmlCurve curve, BusRouteInfo info) {
		this.curve = curve;
		this.info = info;
	}
	
	/**
	 * 路線
	 */
	private GmlCurve curve;

	/**
	 * バス路線情報のクラス
	 */
	private BusRouteInfo info;
	
	/**
	 * 平日運行頻度
	 */
	private double ratePerDay;

	/**
	 * 土曜日運行頻度
	 */
	private double ratePerSaturday;
	
	/**
	 * 日祝日運行頻度
	 */
	private double ratePerHoliday;
	
	/**
	 * バス路線の系統番号・系統名
	 * 系統が未整備であれば路線名・事業者名と連番
	 */
	private String line;
	
	/**
	 * @return バス路線情報のクラス
	 */
	public BusRouteInfo getInfo() {
		return this.info;
	}
	
	public void setInfo(BusRouteInfo info) {
		this.info = info;
	}

	/**
	 * @return 路線
	 */
	public GmlCurve getCurve() {
		return this.curve;
	}
	
	public void setCurve(GmlCurve curve) {
		this.curve = curve;
	}

	/**
	 * @return バス系統
	 */
	public String getLine() {
		return this.line;
	}
	
	public void draw(Graphics2D g) {
		this.curve.draw(g);
	}

	/**
	 * @return 平日運行頻度
	 */
	public double getRateParDay() {
		return this.ratePerDay;
	}

	/**
	 * @return 日祝日運行頻度
	 */
	public double getRatePerHoliday() {
		return this.ratePerHoliday;
	}

	/**
	 * @return 土曜日運行頻度
	 */
	public double getRatePerSaturday() {
		return this.ratePerSaturday;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlCurve) {
			if ("ksj:brt".equals(tag)) {
				this.curve = (GmlCurve) obj;
			}
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:bsc".equals(tag)) {
				int type = Integer.parseInt(string);
				this.info.setType(type);
			} else if ("ksj:boc".equals(tag)) {
				this.info.setOperationCommunity(string);
			} else if ("ksj:bln".equals(tag)) {
				this.info.setLine(string);
			} else if ("ksj:rpd".equals(tag)) {
				this.ratePerDay = Double.parseDouble(string);
			} else if ("ksj:rps".equals(tag)) {
				this.ratePerSaturday = Double.parseDouble(string);
			} else if ("ksj:rph".equals(tag)) {
				this.ratePerHoliday = Double.parseDouble(string);
			} else if ("ksj:rmk".equals(tag)) {
				if (!"".equals(string)) {
					System.out.println("remark: "+ string);
				}
			}
		}
	}
	
}
