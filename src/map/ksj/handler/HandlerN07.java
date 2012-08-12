package map.ksj.handler;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.ksj.BusRoute;
import map.ksj.Data;
import map.ksj.GmlCurve;

/**
 * 国土数値情報JPGIS2.1(GML)形式のバスルート(線)を読み込むための
 * DefaultHandlerの継承クラス
 * @author fujiwara
 */
public class HandlerN07 extends KsjHandler {

	public BusRoute[] getBusRoutes() {
		List<BusRoute> ret = new ArrayList<BusRoute>();
		for (Data data : this.getDataMap().values()) {
			assert(data instanceof BusRoute);
			ret.add((BusRoute) data);
		}
		return ret.toArray(new BusRoute[ret.size()]);
	}
	
	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof BusRoute)) {
				System.out.println(this.getClass() +": "+ data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}
	
	@Override
	protected void postProcessing() {
		List<GmlCurve> curveList = new ArrayList<GmlCurve>();

		Map<GmlCurve, Integer> idxMap = new HashMap<GmlCurve, Integer>();
		Map<Point2D, List<GmlCurve>> pointMap = new HashMap<Point2D, List<GmlCurve>>();

		for (Data data : this.getDataMap().values()) {
			if (data instanceof GmlCurve) {
				GmlCurve curve = (GmlCurve) data;
				
				if (!idxMap.containsKey(curve)) {
					idxMap.put(curve, idxMap.size());
					curveList.add(curve);
				}

				Point p1 = curve.getFirstPoint();
				List<GmlCurve> curves1 = pointMap.get(p1);
				if (curves1 == null) {
					curves1 = new ArrayList<GmlCurve>();
					pointMap.put(p1, curves1);
				}
				curves1.add(curve);
				
				Point p2 = curve.getLastPoint();
				List<GmlCurve> curves2 = pointMap.get(p2);
				if (curves2 == null) {
					curves2 = new ArrayList<GmlCurve>();
					pointMap.put(p2, curves2);
				}
				curves2.add(curve);
			}
		}

		for (List<GmlCurve> curves : pointMap.values()) {
			if (curves.size() >= 2) {
				for (int i = 0; i < curves.size(); i++) {
					GmlCurve c1 = curves.get(i);
					int idx1 = idxMap.get(c1);
					for (int j = i + 1; j < curves.size(); j++) {
						GmlCurve c2 = curves.get(j);
						int idx2 = idxMap.get(c2);

						c1.addLink(idx2);
						c2.addLink(idx1);
					}
				}
			}
		}
	}
	
}
