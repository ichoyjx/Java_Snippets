package wumpus;

/*
 *@author: Jinxin Yang
 *@PSID: 1168646
 *@date: 12/1/2012
 */

public class Wumpus {

	private final int R_move = -10;
	private final int R_grab = -100;
	int Reward;
	private final double alpha = 1;
	private final double gamma = 1;
	Block[][] world = new Block[5][5];
	Agent agent = new Agent();
	int[][] path = new int[10][2]; // store the path: (4,4) -> ...

	Wumpus() {
		this.initWorld();
		this.initAgent();
		this.initPath();
		this.Reward = 0;
	}

	void initWorld() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				this.world[i][j] = new Block(); // very important
				this.world[i][j].setX(i);
				this.world[i][j].setY(j);
				this.world[i][j].setDirection(4);
			}
		}
		this.world[0][0].setName("Pit");
		this.world[0][4].setName("Pit");
		this.world[3][0].setName("Pit");
		this.world[0][2].setName("Gold");
		this.world[2][2].setName("Wumpus");
		this.world[4][4].setName("Start");

		this.world[0][0].setUtility(-500);
		this.world[0][4].setUtility(-500);
		this.world[3][0].setUtility(-500);
		this.world[0][2].setUtility(5000);
		this.world[2][2].setUtility(-1000);
		this.world[4][4].setUtility(0);

		this.world[0][0].setFixU(true);
		this.world[0][4].setFixU(true);
		this.world[3][0].setFixU(true);
		this.world[0][2].setFixU(true);
		this.world[2][2].setFixU(true);
		this.world[4][4].setFixU(true);
	}

	void initAgent() {
		this.agent.setX(4);
		this.agent.setY(4);
		this.agent.setLast(24);
	}

	void initPath() {
		for (int i = 0; i < 10; i++) {
			path[i][0] = -1;
			path[i][1] = -1;
		}
		path[0][0] = 4; // start from block (4,4)
		path[0][1] = 4;
	}

	void printWorld(String s) {
		System.out.println("\n" + s + " World\n");
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.format("%8s", this.world[i][j].getName());
			}
			System.out.println("\n");
		}
	}

	void printPolicy(int nextX, int nextY) {
		System.out.println("\nCurrent Policy\n");
		int cx = this.agent.getX(), cy = this.agent.getY(); // current
		// int lastMove = this.agent.getLast();

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (i == cx && j == cy) { // current block
					System.out.format("%7s", "*");
				} else if (i == nextX && j == nextY
						&& !(this.world[i][j].getName().equals("Gold"))) {
					System.out.format("%7s", this.world[i][j].getArrow());

				} else {
					System.out.format("%7s", this.world[i][j].getName());
				}

			}
			System.out.println("\n");
		}
	}

	void printU(String s) {
		System.out.println("\n" + s + " Utilities\n");
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.format("%6d", this.world[i][j].getUtility());
			}
			System.out.println("\n");
		}
	}

	void iteration() {
		int[] tmp = new int[3];
		int nextDir, nextX, nextY;
		int count = 0;
		int[][] old_world = new int[5][5];
		int[][] new_world = new int[5][5];
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				old_world[i][j] = 0;
			}
		}

		while (!checkConverge(old_world)) {
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					old_world[i][j] = this.world[i][j].getUtility();
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					if (!this.world[i][j].getFixU()) { // can be updated
						tmp = this.checkNext(i, j);
						nextDir = tmp[0];
						nextX = tmp[1];
						nextY = tmp[2];
						this.world[i][j].setDirection(nextDir);
						new_world[i][j] = (int) (this.world[i][j].getUtility() + alpha
								* (this.R_move + gamma
										* this.world[nextX][nextY].getUtility() - this.world[i][j]
											.getUtility()));
					} else {
						new_world[i][j] = this.world[i][j].getUtility();
					}
				}
			}

			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					this.world[i][j].setUtility(new_world[i][j]);
				}
			}

			count++;
			this.printU("after " + count + " iterations");
			// this.printPolicy();

		}
	}

	boolean checkConverge(int[][] old) {
		boolean flag = true;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (old[i][j] != this.world[i][j].getUtility()) {
					flag = false;
					return flag;
				}
			}
		}
		return flag;
	}

	int[] checkNext(int x, int y) { // x, y means current position
		int tmpX = x, tmpY = y, tmpU, max = -100000, maxIndex = -1;

		tmpX--; // try up
		if (!(tmpX < 0) && !this.world[tmpX][tmpY].getVisited()) { // no beyond
																	// [0][]
			tmpU = this.world[tmpX][tmpY].getUtility();
			if (tmpU > max) {
				max = tmpU;
				maxIndex = 0;
			}
		}

		tmpX = x;
		tmpX++;
		if (!(tmpX > 4) && !this.world[tmpX][tmpY].getVisited()) { // below
																	// [4][]
			tmpU = this.world[tmpX][tmpY].getUtility();
			if (tmpU > max) {
				max = tmpU;
				maxIndex = 2;
			}
		}

		tmpX = x;
		tmpY--;
		if (!(tmpY < 0) && !this.world[tmpX][tmpY].getVisited()) { // left of
																	// [][0]
			tmpU = this.world[tmpX][tmpY].getUtility();
			if (tmpU > max) {
				max = tmpU;
				maxIndex = 3;
			}
		}

		tmpY = y;
		tmpY++;
		if (!(tmpY > 4) && !this.world[tmpX][tmpY].getVisited()) { // right of
																	// [][4]
			tmpU = this.world[tmpX][tmpY].getUtility();
			if (tmpU > max) {
				max = tmpU;
				maxIndex = 1;
			}
		}

		int[] tmp = new int[3]; // [0]=maxIndex, [1]=targetX, [2]=targetY
		tmp[0] = maxIndex;
		switch (maxIndex) {
		case 0:
			tmp[1] = x - 1;
			tmp[2] = y;
			break;
		case 1:
			tmp[1] = x;
			tmp[2] = y + 1;
			break;
		case 2:
			tmp[1] = x + 1;
			tmp[2] = y;
			break;
		case 3:
			tmp[1] = x;
			tmp[2] = y - 1;
			break;
		}
		return tmp;
	}

	void printPath() {
		System.out.println("\nPath:\n");
		for (int i = 0; this.path[i][0] != -1; i++) {
			System.out
					.format("(%d,%d) -> ", this.path[i][0], this.path[i][1]);
			if ((i + 1) % 4 == 0) {
				System.out.println();
			}
		}
		System.out.println("Gold!");
	}

	void agentStart() {
		int oriX = this.agent.getX(), oriY = this.agent.getY(); // current
		int cx = oriX, cy = oriY;
		int[] tmp = new int[3];
		int nextX, nextY;

		this.printPolicy(4, 4);
		tmp = this.checkNext(cx, cy);

		int i = 1;
		while (!this.world[cx][cy].getName().equals("Gold")) {
			nextX = tmp[1];
			nextY = tmp[2];

			this.printPolicy(nextX, nextY);
			this.path[i][0] = nextX;
			this.path[i][1] = nextY;
			i++;

			this.agent.setX(nextX);
			this.agent.setY(nextY);
			this.agent.setLast(this.world[cx][cy].getID());
			this.world[cx][cy].setVisited(true);
			this.Reward += this.R_move;

			cx = nextX;
			cy = nextY;
			tmp = this.checkNext(cx, cy);
		}
		this.printPolicy(cx, cy);
		this.Reward += this.R_grab + 5000;
	}

	public static void main(String[] args) {
		Wumpus test = new Wumpus();
		test.printWorld("Initial");
		test.printU("Initial");
		test.iteration();
		test.agentStart();
		test.printU("Final");
		System.out.println("Total Reward: " + test.Reward);
		test.printPath();
	}

}
