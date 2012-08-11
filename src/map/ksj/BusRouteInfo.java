package map.ksj;

import java.io.Serializable;

/**
 * バス路線情報のクラス
 * @author fujiwara
 */
public class BusRouteInfo implements Data, Serializable {

	private static int count = 0;
	
	private int id;

	/**
	 * バス区分
	 */
	private int type;

	/**
	 * 事業者名
	 */
	private String operationCommunity;

	/**
	 * バス系統
	 */
	private String line;
	
	public BusRouteInfo() {
		this.id = count++;
	}
	
	public BusRouteInfo(int type, String line, String operationCommunity) {
		this.type = type;
		this.line = line;
		this.operationCommunity = operationCommunity;
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	 * @return バス区分
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
	 * @return バス系統
	 */
	public String getLine() {
		return this.line;
	}
	
	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:busType".equals(tag)) {
				this.type = Integer.parseInt(string);
			} else if ("ksj:busOperationCompany".equals(tag)) {
				this.operationCommunity = string;
			} else if ("ksj:busLineName".equals(tag)) {
				this.line = string;
			}
		}
	}
	
	
	@Override
	public String toString() {
		return String.format("[%02d] %s (%s)", this.type, this.line, this.operationCommunity);
	}

	@Override
	public int hashCode() {
		return this.type + this.line.hashCode() + this.operationCommunity.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean ret = false;
		if (obj instanceof BusRouteInfo) {
			BusRouteInfo info = (BusRouteInfo) obj;
			ret = this.type == info.type && 
					this.line.equals(info.line) &&
					this.operationCommunity.equals(info.operationCommunity);
		}
		return ret;
	}

}
