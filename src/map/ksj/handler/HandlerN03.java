package map.ksj.handler;
import java.util.ArrayList;
import java.util.List;

import map.ksj.AdministrativeArea;
import map.ksj.Data;

/**
 * 国土数値情報JPGIS2.1(GML)形式の行政界(面)を読み込むための
 * DefaultHandlerの継承クラス
 * @author fujiwara
 */
public class HandlerN03 extends KsjHandler {

	public AdministrativeArea[] getAdministrativeAreas() {
		List<AdministrativeArea> list = new ArrayList<AdministrativeArea>();
		for (Data data : this.getDataMap().values()) {
			list.add((AdministrativeArea) data);
		}
		assert(!list.isEmpty());
		return list.toArray(new AdministrativeArea[]{});
	}

	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof AdministrativeArea)) {
				System.out.println(this.getClass() +": "+ data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

}
