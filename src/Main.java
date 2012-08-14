

import java.io.File;

import database.KsjDataManager;

public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(
				".data"+File.separatorChar+"org",
				".data"+File.separatorChar+"csv",
				".data"+File.separatorChar+"serialize"
		);

		mgr.getAreaCollections();
		mgr.getBusCollections();

		/*
		for (int code = 1; code <= 47; ++code) {
			mgr.getBusCollection(code);
		}
		*/

		mgr.getRailwayCollection();

	}
}
