package test;

import java.io.BufferedWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import test.data.Data;
import test.data.MarketData;

public class Utils {

	/**
	 * use Json to print out data
	 */
	public static void prettilyPrint(Object o, BufferedWriter bw) {
		ObjectMapper mapper = new ObjectMapper();
	    mapper.enable(SerializationFeature.INDENT_OUTPUT);
	    try {
			String jsonString = mapper.writeValueAsString(o);
	    	System.out.println(jsonString);
	    	if (bw != null) {
	    		bw.write(jsonString);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	} // prettilyPrint()
	
	/**
	 * use Json to print out data as one line per market data
	 */
	public static void utilityPrint(Data data, BufferedWriter bw) {
		ObjectMapper mapper = new ObjectMapper();
	    try {
	    	for (MarketData market : data.getMarketData().values()) {
				String jsonString = mapper.writeValueAsString(market);
//		    	System.out.println(jsonString);
		    	if (bw != null) {
		    		bw.write(jsonString + "\r\n");
		    	}
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	} // utilityPrint()

} // class Utils
