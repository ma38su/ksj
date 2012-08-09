import handler.HandlerN02;
import io.KsjDataManager;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Curve;


public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(".data");
		for (int code = 47; code > 0; --code) {
			for (int type : new int[]{3, 7, 11}) {
				for (File file : mgr.getKsjFile(type, code)) {
					System.out.println("file: "+ file.getPath());
				}
			}
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			long t0 = System.currentTimeMillis();

			SAXParser parser = factory.newSAXParser();
			File file = new File("/.data/N02-11.xml");
			HandlerN02 handlerN01 = new HandlerN02();
			parser.parse(file, handlerN01);
			
			Curve[] curves = handlerN01.getCurves();
			
			System.out.println("time: "+ (System.currentTimeMillis() - t0)+ "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
