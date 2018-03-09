package test.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.Test;
import test.Utils;
import test.rawData.Mc;
import test.rawData.RawData;
import test.rawData.Rc;
import test.rawData.Runners;

/**
 * class for all the markets
 *  - array of MarketData, one record for each market
 *  - MarketData contains a map of market records
 *  - nonMarketData is an index of ids for markets for which we are not interested in
 */
public class Data {

	private Map<String, MarketData> marketData;
	private Map<String, String> nonMarketData;

	//Constructor
	public Data() {
		marketData = new HashMap<String, MarketData>();
		nonMarketData = new HashMap<String, String>();
	} // Constructor

	//Public methods
	/**
	 * add a new record to MarketData array from market change message
	 *  - or update an existing record if market already in
	 *  - some mcs have market definition, some market definitions have runners
	 *    - if market definition is in blacklist, return
	 *  - some mcs have rcs
	 *   - if mc has rc only then no way to know if it is match_odds so add a new market data record anyway
	 *    - if mc has market definition and it is not match_odds then remove it again and add id to blacklist
	 * @param rawData - market change
	 */
	public void putMarketData(RawData rawData) {
		
		//raw data can have multiple market change messages (must have at least one)
		if (rawData.getMc().size() < 1) {
			System.out.println("ERROR: no mc in " + rawData.getOp() + 
					", pt = " + rawData.getPt() + 
					", clk = " + rawData.getClk());
			Runtime.getRuntime().halt(0);
		}

		//not all have marketDefinition but all have id, check it against blacklist
		for (Mc mc : rawData.getMc()) {
			if (!nonMarketData.containsKey(mc.getId())) {
				
				//copy non-null fields to attributes
				MarketData newMarketData = copyFrom(marketData.get(mc.getId()), mc, rawData.getPt());

				if (mc.getId().contains(Test.FORENSIC)) {
					Utils.prettilyPrint(newMarketData, null);
				}

				if (newMarketData != null) {
					marketData.put(mc.getId(), newMarketData);
				} else {
					marketData.remove(mc.getId());
					nonMarketData.put(mc.getId(), mc.getMarketDefinition().getMarketType());
				}
			}
		}
	} // putMarketData()
	
	//Private methods
	/**
	 * copy non-null fields only from mc to market data
	 *  - if this mc is not for our type of market then return null
	 */
	private MarketData copyFrom(MarketData marketData, Mc mc, long timeStamp) {

		//convert from rawdata to data format
		MarketData mcData = new MarketData(mc, timeStamp);

		if (mc.getId().contains(Test.FORENSIC)) {
//			Utils.prettilyPrint(mcData, null);
		}

		if (marketData == null) {
			marketData = new MarketData();
		}

		//if there is market definition, copy market definition
		if (mc.getMarketDefinition() != null &&
				mc.getMarketDefinition().getMarketType() != null) {
			
			//one track mind and singles
			if (!mc.getMarketDefinition().getMarketType().equals("MATCH_ODDS") ||
					mc.getMarketDefinition().getRunners().get(0).getName().contains("/")) {
				return null;
			} else {
				marketData.setId(mc.getId());
				if (mcData.getTime() != null) {
					marketData.setTime(mcData.getTime());
				}
				if (mcData.getName() != null) {
					marketData.setName(mcData.getName());
				}
				if (mcData.getMarketType() != null) {
					marketData.setMarketType(mcData.getMarketType());
				}
				if (mcData.getEventName() != null) {
					marketData.setEventName(mcData.getEventName());
				}
				if (mcData.getTime() != null) {
					marketData.setTime(mcData.getTime());
				}
				
				//if there is runners, update or create players
				if (mc.getMarketDefinition().getRunners() != null &&
						mc.getMarketDefinition().getRunners().size() > 0) {
					for (Runners runner : mc.getMarketDefinition().getRunners()) {
						Player existingPlayer = marketData.getPlayers().get(runner.getId());
						
						//update existing player
						if (existingPlayer != null) {
							if (runner.getName() != null) {
								existingPlayer.setName(runner.getName());
							}
							if (runner.getStatus() != null) {
								existingPlayer.setStatus(runner.getStatus());
							}
						}
						
						//create new player
						else {
							existingPlayer = marketData.createNewPlayer(runner);
						}
						
						//overwrite/create
						marketData.getPlayers().put(runner.getId(), existingPlayer);
					} 
				}
			}
		}

		//if there is runner change (can occur with or without market definition) update players
		if (mc.getRc() != null &&
				mc.getRc().size() > 0) {
			for (Rc rc : mc.getRc()) {
				Player existingPlayer = marketData.getPlayers().get(rc.getId());
				if (existingPlayer != null) {
					existingPlayer.addNewTransaction(rc, timeStamp);
				}
				
				//create new player
				else {
					marketData.addNewPlayer(rc, timeStamp);
				}
			}
		}

		//TODO:
		// - probably should store and check timestamps to only overwrite older updates.
		// - don't just add runner change, check if a previous update with same timestamp is already there and overwrite it
			
		return marketData;
		
	} // copyFrom()

	//Accessors and Mutators
	public Map<String, MarketData> getMarketData() {return marketData;}
	public void setMarketData(Map<String, MarketData> marketData) {this.marketData = marketData;}

	
} // class Data
