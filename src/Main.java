import handler.HandlerN02;
import handler.HandlerN07;
import io.KsjDataManager;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Curve;


public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(".data");

		for (int type : new int[]{2}) {
			for (File file : mgr.getKsjFile(type)) {
				System.out.printf("%02d   : %s\n", type, file.getPath());
			}
		}
		for (int code = 47; code > 0; --code) {
			for (int type : new int[]{3, 7, 11}) {
				for (File file : mgr.getKsjFile(type, code)) {
					System.out.printf("%02d-%02d: %s\n", type, code, file.getPath());
				}
			}
		}
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {

			{
				SAXParser parser = factory.newSAXParser();
				long t0 = System.currentTimeMillis();
				File file = new File(".data/N02-11.xml");
				HandlerN02 handlerN02 = new HandlerN02();
				parser.parse(file, handlerN02);
				System.out.printf("%d: %dms\n", 2, (System.currentTimeMillis() - t0));
			}

			for (int code = 47; code > 0; --code) {
				SAXParser parser = factory.newSAXParser();
				long t0 = System.currentTimeMillis();
				File file = new File(".data" +File.separatorChar+ String.format("%02d" +File.separatorChar+ "N07-11_%02d.xml", code, code));
				HandlerN07 handlerN07 = new HandlerN07();
				parser.parse(file, handlerN07);
				System.out.printf("%d: %dms\n", code, (System.currentTimeMillis() - t0));
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
