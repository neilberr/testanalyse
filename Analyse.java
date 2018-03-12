package test;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.Rengine;

import test.data.Data;
import test.data.MarketData;
import test.data.Player;
import test.data.Transaction;

public class Analyse {
	
	public static void analyse(Data data) {

		// Create an R vector in the form of a string.
	    String javaVector = "c(1,2,3,4,5)";
	    
	    // Start Rengine.
	    Rengine engine = new Rengine(new String[] { "--no-save" }, false, null);

	    for (MarketData market : data.getMarketData().values()) {
	    	for (Player player : market.getPlayers().values()) {
	    	    String preMatchTransactionVector = "c(";
	    	    String inPlayTransactionVector = "c(";
	    	    String appendComma = "";

	    	    List<List<Transaction>> allTransactions = getMatchTransactions(player.getTransactions(), market.getTime()); 
	    	    List<Transaction> preMatchTransactions = allTransactions.get(0);
	    	    List<Transaction> inPlayTransactions = allTransactions.get(1);

	    	    for (Transaction transaction : preMatchTransactions) {
	    	    	System.out.print(transaction.getPrice() + "|");
	    			preMatchTransactionVector += appendComma + transaction.getPrice();
	    			appendComma = ",";
	    	    }
	    	    preMatchTransactionVector += ")";
	    	    appendComma = "";
	    	    System.out.println("");

	    	    for (Transaction transaction : inPlayTransactions) {
	    	    	System.out.print(transaction.getPrice() + "|");
	    			inPlayTransactionVector += appendComma + transaction.getPrice();
	    			appendComma = ",";
	    	    }
	    	    inPlayTransactionVector += ")";

	    	    System.out.println("ipvector = " + inPlayTransactionVector);
	    	    System.out.println("pmvector = " + preMatchTransactionVector);

	    	    // The vector that was created in JAVA context is stored in 'rVector' which is a variable in R context.
	    	    engine.eval("rVector=" + preMatchTransactionVector);
	    	    engine.eval("tVector=" + inPlayTransactionVector);
	    	    
	    	    //Calculate MEAN of vector using R syntax.
	    	    engine.eval("meanValr=mean(rVector)");
	    	    engine.eval("sdValr=sd(rVector)");
	    	    engine.eval("meanValt=mean(tVector)");
	    	    engine.eval("sdValt(tVector)");
	    	    
	    	    //Retrieve MEAN value
	    	    double preMatchMean = engine.eval("meanValr").asDouble();
	    	    double preMatchStdDev = engine.eval("sdValr").asDouble();
	    	    double inPlayMean = engine.eval("meanValt").asDouble();
	    	    double inPlayStdDev = engine.eval("sdValt").asDouble();
	    	    
	    	    String pad1 = "";
	    	    for (int i = 0; i < (25 - market.getEventName().length()); i++) {
	    	    	pad1 += " ";
	    	    }

	    	    String pad2 = "";
	    	    for (int i = 0; i < (50 - market.getEventName().length() - player.getName().length() - pad1.length()); i++) {
	    	    	pad2 += " ";
	    	    }

	    	    //Print output values
	    	    System.out.println(market.getEventName() + ":" + pad1 + 
	    	    		player.getName() + ":" + 
	    	    		"(" + player.getStatus() + ")" + pad2 + 
	    	    		"pm(" + Utils.dp3(preMatchMean) + ", " + Utils.dp3(preMatchStdDev) + ")" +
	    	    		" ip(" + Utils.dp3(inPlayMean) + ", " + Utils.dp3(inPlayStdDev) + ")");

//	    	    System.out.println("Mean of given vector is=" + Utils.dp3(mean));	    	
//	    	    System.out.println("Standard deviation of given vector is=" + Utils.dp3(stdDev));	    	
	    	}
	    	System.out.println("");
	    }
	} // analyse()

	/**
	 * return all the transactions prior to start time
	 *  - return them sorted by time
	 */
	private static List<List<Transaction>> getMatchTransactions(List<Transaction> transactions, String startTime) {
//		long startTimeAsMillis = Utils.dateToMillis(startTime);
		List<Transaction> preMatchTransactions = new ArrayList<Transaction>();
		List<Transaction> inPlayTransactions = new ArrayList<Transaction>();
		List<List<Transaction>> transactionsToReturn = new ArrayList<List<Transaction>>(); 

		double count = 0.0;
		for (Transaction transaction : transactions) {
//			if Utils.before(startTime, transaction.getTime()...
			if (count < transactions.size()/2.0) {
				preMatchTransactions.add(transaction);
			} else {
				inPlayTransactions.add(transaction);
			}
			count += 1.0;
		}
		transactionsToReturn.add(preMatchTransactions); 
		transactionsToReturn.add(inPlayTransactions); 
		return transactionsToReturn;
	} // get PreMatchTransactions()

} // class Analyse
