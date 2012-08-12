package map.ksj.handler;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.ksj.Data;
import map.ksj.GmlCurve;
import map.ksj.RailroadSection;
import map.ksj.Station;

/**
 * 国土数値情報JPGIS2.1(GML)形式の鉄道(線)を読み込むための
 * DefaultHandlerの継承クラス
 * @author fujiwara
 */
public class HandlerN02 extends KsjHandler {
	
	public Station[] getStations() {
		List<Station> ret = new ArrayList<Station>();
		for (Data data : this.getDataMap().values()) {
			if (data instanceof Station) {
				ret.add((Station) data);
			} else {
				assert(data instanceof RailroadSection);
			}
		}
		return ret.toArray(new Station[ret.size()]);
	}
	
	public RailroadSection[] getRailroadSections() {
		List<RailroadSection> ret = new ArrayList<RailroadSection>();
		for (Data data : this.getDataMap().values()) {
			if (data instanceof RailroadSection) {
				ret.add((RailroadSection) data);
			} else {
				assert(data instanceof Station);
			}
		}
		return ret.toArray(new RailroadSection[ret.size()]);
	}
	
	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof RailroadSection) && !(data instanceof Station)) {
				System.out.println(this.getClass() +": "+ data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

	@Override
	protected void postProcessing() {
		Map<GmlCurve, Integer> idxMap = new HashMap<GmlCurve, Integer>();
		Map<Point2D, List<GmlCurve>> pointMap = new HashMap<Point2D, List<GmlCurve>>();

		for (Data data : this.getDataMap().values()) {
			GmlCurve curve = null;
			if (data instanceof Station) {
				curve = ((Station) data).getCurve();
			} else if (data instanceof RailroadSection) {
				curve = ((RailroadSection) data).getCurve();
			} else {
				throw new IllegalStateException();
			}
				
			if (!idxMap.containsKey(curve)) {
				idxMap.put(curve, idxMap.size());
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
