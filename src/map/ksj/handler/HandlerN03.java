package map.ksj.handler;
import java.util.ArrayList;
import java.util.List;

import map.ksj.Area;
import map.ksj.Data;

/**
 * 国土数値情報JPGIS2.1(GML)形式の行政区画(面)を読み込むための
 * DefaultHandlerの継承クラス
 * @author fujiwara
 */
public class HandlerN03 extends KsjHandler {

	public Area[] getAreas() {
		List<Area> list = new ArrayList<Area>();
		for (Data data : this.getDataMap().values()) {
			list.add((Area) data);
		}
		assert(!list.isEmpty());
		return list.toArray(new Area[list.size()]);
	}

	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof Area)) {
				System.out.println(this.getClass() +": "+ data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

}
