package test.data;

import test.rawData.Rc;

public class Transaction {
	private int playerId;
	private long time;
	private double price;
	private double volume;
	
	//Constructor
	public Transaction() {
		time = 0;
		playerId = 0;
		price = 0.0;
		volume = 0.0;
	} // Constructor

	//Constructor
	public Transaction(Rc rc, long timeStamp) {
		time = timeStamp;
		playerId = rc.getId();
		price = rc.getLtp();
		volume = 0.0;
	} // Constructor(Rc)

	//Accessors and Mutators
	public int getPlayerId() {return playerId;}
	public void setPlayerId(int playerId) {this.playerId = playerId;}
	public double getPrice() {return price;}
	public void setPrice(double price) {this.price = price;}
	public double getVolume() {return volume;}
	public void setVolume(double volume) {this.volume = volume;}
	public long getTime() {return time;}
	public void setTime(long time) {this.time = time;}
} // class Transaction
