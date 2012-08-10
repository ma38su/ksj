package model;

/**
 * バス路線情報のクラス
 * @author fujiwara
 */
public class BusRouteInfomation implements Data {

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
}
