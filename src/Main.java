

import java.awt.Point;
import java.io.File;

import util.FixedPoint;

import map.KsjDataManager;
import map.ksj.GmlCurve;
import map.ksj.RailroadSection;
import map.ksj.RailwayDataset;
import map.ksj.Station;


public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(
				".data"+File.separatorChar+"org",
				".data"+File.separatorChar+"csv"
		);
		
		RailwayDataset railway = mgr.getRailwayDataset();
		Station[] stations = railway.getStations();
		railway.getOtherLines();

		Station myodani = null;
		for (Station st : stations) {
			if ("名谷".equals(st.getName())) {
				myodani = st;
				GmlCurve curve = st.getCurve();
				int npts = curve.getArrayLength();
				int[] xpts = curve.getArrayX();
				int[] ypts = curve.getArrayY();
				System.out.println("-- 名谷 --");
				for (int i = 0; i < npts; i++) {
					System.out.printf("%f, %f\n", FixedPoint.parseDouble(ypts[i]), FixedPoint.parseDouble(xpts[i]));
				}
			}
		}
		
		if (myodani != null) {
			for (RailroadSection section : railway.sections) {
				GmlCurve curve = section.getCurve();
				
				Point p = myodani.getCurve().getLastPoint();
				if (p.equals(curve.getFirstPoint()) || p.equals(curve.getLastPoint())) {
					int npts = curve.getArrayLength();
					int[] xpts = curve.getArrayX();
					int[] ypts = curve.getArrayY();
					System.out.println("---");
					for (int i = 0; i < npts; i++) {
						System.out.printf("%f, %f\n", FixedPoint.parseDouble(ypts[i]), FixedPoint.parseDouble(xpts[i]));
					}
				}
			}
		}
		

		/*
		mgr.getBusCollections();
		mgr.getAreaCollections();
		mgr.getRailwayCollection();

		for (int code = 1; code <= 47; ++code) {
			mgr.getBusCollection(code);
		}
		*/

	}
}
