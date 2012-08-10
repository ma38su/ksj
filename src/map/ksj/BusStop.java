package map.ksj;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * バス停留所のクラス
 * @author fujiwara
 */
public class BusStop implements Data {

	/**
	 * バス路線情報
	 */
	private	final List<BusRouteInfomation> infos = new ArrayList<BusRouteInfomation>();

	/**
	 * 地点
	 */
	private Point point;

	/**
	 * バス停名
	 */
	private String name;

	/**
	 * @return　バス停名
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return 地点
	 */
	public Point getPoint() {
		return this.point;
	}
	
	/**
	 * @return バス路線情報
	 */
	public List<BusRouteInfomation> getBusRouteInfos() {
		return this.infos;
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlPoint) {
			this.point = ((GmlPoint) obj).getPoint();
		} else if (obj instanceof BusRouteInfomation) {
			infos.add((BusRouteInfomation) obj);
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:busStopName".equals(tag)) {
				this.name = string;
			}
		}
	}
}
