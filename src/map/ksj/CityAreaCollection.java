package map.ksj;

import java.awt.Polygon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 都道府県の行政区画(面)
 * @author fujiwara
 */
public class CityAreaCollection implements Serializable {

	private int code;
	private Polygon[] polygons;
	private CityAreas[] areas;
	

	public CityAreaCollection(int code, Polygon[] polygons, CityAreas[] areas) {
		this.code = code;
		this.polygons = polygons;
		this.areas = areas;
	}
	
	public CityAreaCollection(int code, CityArea[] areas) {
		this.code = code;
		Map<CityInfo, List<Polygon>> map = new LinkedHashMap<CityInfo, List<Polygon>>();
		for (CityArea area : areas) {
			CityInfo info = area.getInfo();
			
			List<Polygon> areaList = map.get(info);
			if (areaList == null) {
				areaList = new ArrayList<Polygon>();
				map.put(info, areaList);
			}
			areaList.add(area.getPolygon());
		}
		List<CityAreas> areasList = new ArrayList<CityAreas>();
		for (Map.Entry<CityInfo, List<Polygon>> entry : map.entrySet()) {
			CityInfo info = entry.getKey();
			List<Polygon> polygons = entry.getValue();
			areasList.add(new CityAreas(info, polygons.toArray(new Polygon[polygons.size()])));
		}
		this.areas = areasList.toArray(new CityAreas[areasList.size()]);

		this.initPolygons();
	}
	
	private void initPolygons() {
		long t0 = System.currentTimeMillis();
		List<Polygon> polygons = new ArrayList<Polygon>();
		for (CityAreas a : this.areas) {
			polygons.addAll(Arrays.asList(a.getPolygons()));
		}
		List<Polygon> newPolygons = GmlPolygon.getOpt(polygons);
		this.polygons = newPolygons.toArray(new Polygon[newPolygons.size()]);
		System.out.printf("%d polygon: %dms\n", this.code, (System.currentTimeMillis() - t0));
	}
	
	public String getName() {
		return CityInfo.PREF_NAME[this.code];
	}
	
	public Polygon[] getPolygons() {
		return this.polygons;
	}

	public int getCode() {
		return this.code;
	}
	
	public CityAreas[] getCityAreas() {
		return this.areas;
	}
	
}
