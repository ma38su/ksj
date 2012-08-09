import handler.HandlerN02;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import model.Curve;


public class Main {
	public static void main(String[] args) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			long t0 = System.currentTimeMillis();

			SAXParser parser = factory.newSAXParser();
			File file = new File("/Users/ma38su/Documents/ksj/N02-11_GML/N02-11.xml");
			HandlerN02 handlerN01 = new HandlerN02();
			parser.parse(file, handlerN01);
			
			Curve[] curves = handlerN01.getCurves();
			
			System.out.println("time: "+ (System.currentTimeMillis() - t0)+ "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
