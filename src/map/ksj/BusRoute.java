package map.ksj;

import java.io.Serializable;

/**
 * バスルートのクラス
 * @author fujiwara
 */
public class BusRoute implements Data, Serializable {

	/**
	 * 路線
	 */
	private GmlCurve curve;

	/**
	 * バス区分
	 */
	private int type;
	
	/**
	 * 事業者名
	 */
	private String operationCommunity;
	
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
	 * @return バス区分コード
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * @return 事業者名
	 */
	public String getOperationCommunity() {
		return this.operationCommunity;
	}
	
	/**
	 * @return 路線
	 */
	public GmlCurve getCurve() {
		return this.curve;
	}

	/**
	 * @return バス系統
	 */
	public String getLine() {
		return this.line;
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
				this.type = Integer.parseInt(string);
			} else if ("ksj:boc".equals(tag)) {
				this.operationCommunity = string;
			} else if ("ksj:bln".equals(tag)) {
				this.line = string;
			} else if ("ksj:rpd".equals(tag)) {
				this.ratePerDay = Double.parseDouble(string);
			} else if ("ksj:rps".equals(tag)) {
				this.ratePerSaturday = Double.parseDouble(string);
			} else if ("ksj:rph".equals(tag)) {
				this.ratePerHoliday = Double.parseDouble(string);
			} else if ("ksj:rmk".equals(tag)) {
				System.out.println("remark: "+ string);
			}
		}
	}
	
}
