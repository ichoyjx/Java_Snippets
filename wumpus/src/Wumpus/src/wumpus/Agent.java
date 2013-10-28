package wumpus;

public class Agent {
	int X; // current x
	int Y; // current y
	int lastMove; // previous move
	
	void setX (int x) {
		this.X=x;		
	}
	
	int getX () {
		return this.X;
	}
	
	void setY (int y) {
		this.Y=y;		
	}
	
	int getY () {
		return this.Y;
	}
	
	void setLast (int p) {
		this.lastMove=p;		
	}
	
	int getLast () {
		return this.lastMove;
	}


}
