package test.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import test.Test;
import test.Utils;
import test.rawData.Mc;
import test.rawData.Rc;
import test.rawData.Runners;

/**
 * class for one market
 *  - id, name, type, time
 *  - a Map of players (as they are unique)
 *  - a catalogue of records which have happened
 */
public class MarketData {
	
	private String id;
	private String time;
	private String name;
	private String marketType;
	private String eventName;
	private Map<Integer, Player> players;
	//TODO an array of events especially transactions

	//Constructor
	public MarketData() {
		id = "id";
		name = "name";
		marketType = "type";
		time = "time";
		eventName = "eventName";
		players = new HashMap<Integer, Player>();
	} // Constructor

	//Constructor from RawData
	public MarketData(Mc mc, long timeStamp) {

		//id is mandatory
		id = mc.getId();

		if (mc.getId().contains(Test.FORENSIC)) {
			Utils.prettilyPrint(mc, null);
		}

		//check if there is a market definition
		if (mc.getMarketDefinition() != null) {
			time = mc.getMarketDefinition().getMarketTime();
			name = mc.getMarketDefinition().getName();
			marketType = mc.getMarketDefinition().getMarketType();
			eventName = mc.getMarketDefinition().getEventName();
			players = new HashMap<Integer, Player>();

			//check if there are runners
			if (mc.getMarketDefinition().getRunners() != null) {
			
				for (Runners runner : mc.getMarketDefinition().getRunners()) {
//					System.out.println("runner: " + i.getName());
					players.put(runner.getId(), new Player(runner));
				}
			}

			//check if there are runner changes
			if (mc.getRc() != null) {

				//runner changes get added to player (create if not exist)
				for (Rc rc : mc.getRc()) {

//					System.out.println("rc[" + rc.getId() + "]: " + rc.getLtp());
					
					if (!players.containsKey(rc.getId())) {
						players.put(rc.getId(), new Player(rc, timeStamp));
					} else {
						Player existingPlayer = players.get(rc.getId());
						existingPlayer.addNewTransaction(rc, timeStamp);
						players.put(rc.getId(), existingPlayer);
					}
				}
			}

		}
	} // Constructor(RawData)

	//public methods
	/**
	 * player is inner
	 */
	public Player createNewPlayer(Runners runner) {
		return new Player(runner);
	} // createNewPlayer()

	/**
	 * player is inner
	 */
	public void addNewPlayer(Rc rc, long timeStamp) {
		this.getPlayers().put(rc.getId(), new Player(rc, timeStamp));
	} // addNewPlayer()

	public String toString() {
		return "id: " + this.id +
				"event: " + this.name +
				"time: " + this.time +
				"type: " + this.eventName;
	} // toSTring()

	//Accessors and Mutators
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getTime() {return time;}
	public void setTime(String time) {this.time = time;}
	public String getMarketType() {return marketType;}
	public void setMarketType(String marketType) {this.marketType = marketType;}
	public String getEventName() {return eventName;}
	public void setEventName(String eventName) {this.eventName = eventName;}

	public String getType() {return eventName;}
	public void setType(String type) {this.eventName = type;}
	public Map<Integer, Player> getPlayers() {return players;}
	public void setPlayers(Map<Integer, Player> players) {this.players = players;}

} // class MarketData
