package map.ksj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 都道府県の行政区画(面)
 * @author fujiwara
 */
public class CityAreaCollection implements Serializable {

	private int code;
	private CityAreas[] areas;
	

	public CityAreaCollection(int code, CityAreas[] areas) {
		this.code = code;
		this.areas = areas;
	}
	
	public CityAreaCollection(int code, CityArea[] areas) {
		this.code = code;
		Map<CityInfo, List<GmlPolygon>> map = new LinkedHashMap<CityInfo, List<GmlPolygon>>();
		for (CityArea area : areas) {
			CityInfo info = area.getInfo();
			
			List<GmlPolygon> areaList = map.get(info);
			if (areaList == null) {
				areaList = new ArrayList<GmlPolygon>();
				map.put(info, areaList);
			}
			areaList.add(area.getPolygon());
		}
		List<CityAreas> areasList = new ArrayList<CityAreas>();
		for (Map.Entry<CityInfo, List<GmlPolygon>> entry : map.entrySet()) {
			CityInfo info = entry.getKey();
			List<GmlPolygon> polygons = entry.getValue();
			areasList.add(new CityAreas(info, polygons.toArray(new GmlPolygon[polygons.size()])));
		}
		this.areas = areasList.toArray(new CityAreas[areasList.size()]);
	}

	public int getCode() {
		return this.code;
	}
	
	public CityAreas[] getCityAreas() {
		return this.areas;
	}
	
}
