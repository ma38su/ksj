import handler.HandlerN02;
import handler.HandlerN07;
import handler.HandlerP11;
import io.KsjDataManager;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class Main {
	public static void main(String[] args) {
		
		KsjDataManager mgr = new KsjDataManager(".data");

		System.out.println("<Get Ksj Data from Server>");
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
		
		System.out.println("<Parse Ksj Data>");
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {

			{	// N02
				SAXParser parser = factory.newSAXParser();
				long t0 = System.currentTimeMillis();
				File file = new File(".data/N02-11.xml");
				HandlerN02 handlerN02 = new HandlerN02();
				parser.parse(file, handlerN02);
				System.out.printf("%d: %dms\n", 2, (System.currentTimeMillis() - t0));
			}

			for (int code = 47; code > 0; --code) {
				{	// P11
					SAXParser parser = factory.newSAXParser();
					long t0 = System.currentTimeMillis();
					File file = new File(".data" +File.separatorChar+ String.format("%02d" +File.separatorChar+ "P11-10_%02d-jgd-g.xml", code, code));
					HandlerP11 handlerP11 = new HandlerP11();
					parser.parse(file, handlerP11);
					System.out.printf("%d: %dms\n", code, (System.currentTimeMillis() - t0));
				}
				
				{	// N07
					SAXParser parser = factory.newSAXParser();
					long t0 = System.currentTimeMillis();
					File file = new File(".data" +File.separatorChar+ String.format("%02d" +File.separatorChar+ "N07-11_%02d.xml", code, code));
					HandlerN07 handlerN07 = new HandlerN07();
					parser.parse(file, handlerN07);
					System.out.printf("%d: %dms\n", code, (System.currentTimeMillis() - t0));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
