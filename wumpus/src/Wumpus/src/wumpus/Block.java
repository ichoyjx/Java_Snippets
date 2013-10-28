package wumpus;

public class Block {
	int x,y; // coordinate [15][15]
	String name; // Wumpus, Pit, Breeze...
	int utility; // U <- R + U'
	boolean fixU;
	int direction; // {up, right, down, left} = {0, 1, 2, 3}
	boolean visited;
	
	Block () {
		this.x=-1;
		this.y=-1;
		this.name = ".";
		this.utility = randomU(0,5000);
		this.fixU = false;
		this.visited = false;
	}
	
	int getID () {
		return this.x * 5 + this.y;
	}
	
	void setX (int x) {
		this.x=x;
	}
	
	int getX () {
		return this.x;
	}
	
	void setY (int y) {
		this.y=y;
	}
	
	int getY () {
		return this.y;
	}
	
	void setName (String name) {
		this.name = name;
	}
	
	String getName () {
		return this.name;
	}
	
	void setUtility (int U) {
		this.utility = U;
	}
	
	int getUtility () {
		return this.utility;
	}

	public static int randomU(int min, int max) { // can be divided by 100
		int tmp;
		//min = min/100;
		//max = max/100;
		tmp = (int) Math.round(Math.random() * (max - min) + min);
		return tmp; // *100
	}
	
	void setFixU(boolean fixU) {
		this.fixU = fixU;
	}
	
	boolean getFixU () {
		return this.fixU;
	}
	
	void setDirection (int d) {
		this.direction=d;		
	}
	
	int getDirection () {
		return this.direction;
	}
	
	String getArrow () { // {up, right, down, left} = {^, >, v, <}
		switch (this.direction) {
		case 0: return "^";
		case 1: return ">";
		case 2: return "v";
		case 3: return "<";
		default: return "error";
		}
	}
	
	void setVisited (boolean v) {
		this.visited = v;
	}
	
	boolean getVisited () {
		return this.visited;
	}
	
}