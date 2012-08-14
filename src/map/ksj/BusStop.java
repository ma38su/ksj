package map.ksj;

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
	private	BusRouteInfo[] infos;

	/**
	 * 地点(X座標)
	 */
	private int x;

	/**
	 * 地点(Y座標)
	 */
	private int y;

	/**
	 * バス停名
	 */
	private String name;

	public BusStop() {
		this.infos = new BusRouteInfo[0];
	}

	public BusStop(String name, int x, int y, BusRouteInfo[] infos) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.infos = infos;
	}
	
	public void addRouteInfo(BusRouteInfo info) {
		List<BusRouteInfo> tmp = new ArrayList<BusRouteInfo>(infos.length + 1);
		tmp.add(info);
		infos = tmp.toArray(new BusRouteInfo[tmp.size()]);
	}
	
	/**
	 * @return　バス停名
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return 地点(X座標)
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * @return 地点(X座標)
	 */
	public int getY() {
		return this.y;
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
			GmlPoint p = (GmlPoint) obj;
			this.x = p.getX();
			this.y = p.getY();
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
