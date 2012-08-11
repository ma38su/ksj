package map.ksj;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * バス停留所のクラス
 * @author fujiwara
 */
public class BusStop implements Data, Serializable {

	public BusStop() {
		this.infos = new BusRouteInfo[0];
	}

	public BusStop(String name, Point point, BusRouteInfo[] infos) {
		this.name = name;
		this.point = point;
		this.infos = infos;
	}
	
	public void addRouteInfo(BusRouteInfo info) {
		List<BusRouteInfo> tmp = new ArrayList<BusRouteInfo>(infos.length + 1);
		tmp.add(info);
		infos = tmp.toArray(new BusRouteInfo[tmp.size()]);
	}

	/**
	 * バス路線情報
	 */
	private	BusRouteInfo[] infos;

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
	public BusRouteInfo[] getBusRouteInfos() {
		return this.infos;
	}

	@Override
	public void link(String tag, Object obj) {
		if (obj instanceof GmlPoint) {
			this.point = ((GmlPoint) obj).getPoint();
		} else if (obj instanceof BusRouteInfo) {
			this.addRouteInfo((BusRouteInfo) obj);
		} else if (obj instanceof String) {
			String string = (String) obj;
			if ("ksj:busStopName".equals(tag)) {
				this.name = string;
			}
		}
	}
}
