package map.ksj.handler;
import java.util.ArrayList;
import java.util.List;

import map.ksj.Data;
import map.ksj.RailroadSectionData;
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
				assert(data instanceof RailroadSectionData);
			}
		}
		return ret.toArray(new Station[ret.size()]);
	}
	
	public RailroadSectionData[] getRailroadSections() {
		List<RailroadSectionData> ret = new ArrayList<RailroadSectionData>();
		for (Data data : this.getDataMap().values()) {
			if (data instanceof RailroadSectionData) {
				ret.add((RailroadSectionData) data);
			} else {
				assert(data instanceof Station);
			}
		}
		return ret.toArray(new RailroadSectionData[ret.size()]);
	}
	
	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof RailroadSectionData) && !(data instanceof Station)) {
				System.out.println(this.getClass() +": "+ data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

}
