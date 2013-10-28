package h4;

public class genetic {

	/**
	 * @author Jinxin Yang
	 * @PSID 1168646
	 * @date 11/19/2012
	 * @fitness F = A^2 + 2AB + B
	 * @biggest-solution 2107
	 */

	final static int L = 80; // change examples number here
	int[][] hypo = new int[L][2];
	double Pco; // 0.8 or 0.6
	double Pmut; // 0.2 or 0.1
	int Rep; // = (1-Pco)L
	int Theta; // self-determined

	int[][] map = new int[L][2]; // [0] stores the index, [1] is fitness

	int[][] old_hypo = new int[L][2]; // for comparison of after each iteration

	// constructor
	genetic(double Pco, double Pmut, int Theta) {
		this.Pco = Pco;
		this.Pmut = Pmut;
		this.Theta = Theta;
		this.Rep = (10 - (int) (this.Pco * 10)) * L / 10;
		System.out.println("L = " + L);
		System.out.println("Pco = " + this.Pco);
		System.out.println("Pmut = " + this.Pmut);
		System.out.println("Threshold = " + this.Pco);
		System.out.println("Mask: 1111100000\n");

		System.out.format("Initial population set\n");
		for (int i = 0; i < L; i++) { // initial hypos, use not that good
										// solutions
			hypo[i][0] = random(6, 18, 0); // A is even, 0
			hypo[i][1] = random(6, 18, 1); // B is odd, 1
			System.out.format("(%2d,%2d) F=%4d\n", hypo[i][0], hypo[i][1],
					getFitness(hypo[i][0], hypo[i][1]));
		}
		System.out.println();
	}

	// randomly generate the pair (A is even, B is odd)
	public static int random(int min, int max, int eo) {
		int temp;
		if (eo == 0) { // even
			temp = (int) Math.round(Math.random() * (max - min) + min);
			if (temp % 2 != 0) { // Practical is odd
				temp--;
			}
			return temp;
		}
		if (eo == 1) { // odd
			temp = (int) Math.round(Math.random() * (max - min) + min);
			if (temp % 2 == 0) { // Practical is even
				temp++;
			}
			return temp;
		}
		return (int) Math.round(Math.random() * (max - min) + min); // not 1 or
																	// 0
	}

	// this compute the fitness
	public int getFitness(int A, int B) {
		return (A * A + 2 * A * B + B);
	}

	// convert integer to format binaries (with leading zeros)
	public String getBytesString(int a) {
		return fixLeadingZeros(Integer.toBinaryString(a));
	}

	// print current pair and fitness
	public void printHypos(String s) {
		System.out.println(s);
		for (int i = 0; i < L; i++) {
			System.out.format("(%2d,%2d) F=%4d\n", hypo[map[i][0]][0],
					hypo[map[i][0]][1], map[i][1]);
		}
		System.out.println();
	}

	// print detailed before and after mutation comparison
	public void printCmp(int[] mu) {
		int flag = 0;
		System.out.println("before      after      bits:");
		System.out.println("---------------------------------");
		for (int i = 0; i < L; i++) {
			System.out.format("(%2d,%2d)", old_hypo[map[i][0]][0],
					old_hypo[map[i][0]][1]);

			int N = (int) (10 * this.Pmut) * L / 10;
			for (int j = 0; j < N; j++) {
				if (i == mu[j]) {
					System.out.print("<-  ");
					flag = 1;
				}
			}
			if (flag == 0) {
				System.out.print("    ");
			} else {
				flag = 0;
			}

			System.out.format("(%2d,%2d)    ", hypo[map[i][0]][0],
					hypo[map[i][0]][1]);
			System.out.println(getBytesString(hypo[map[i][0]][0])
					+ getBytesString(hypo[map[i][0]][1]));

			if (i == this.Rep - 1) {
				System.out.println("---------------------------------");
			}
		}
		System.out.println();
	}

	public boolean fitness() { // F = A^2 + 2AB + B
		int A, B;
		for (int i = 0; i < L; i++) { // hypo[][0] is A, hypo[][1] is B
			A = hypo[i][0];
			B = hypo[i][1];
			map[i][0] = i;
			map[i][1] = A * A + 2 * A * B + B;
		}
		sort_fitness();
		for (int i = 0; i < L; i++) {
			if (map[i][1] >= this.Theta) {
				System.out.println("Best solution: ");
				System.out.format("(%2d,%2d)    ", hypo[map[i][0]][0],
						hypo[map[i][0]][1]);
				System.out.println(getBytesString(hypo[map[i][0]][0])
						+ getBytesString(hypo[map[i][0]][1]) + "\n");
				return true;
			}
		}
		return false;
	}

	// selection sorting
	public void sort_fitness() {
		int i, j, k, temp;
		for (i = 0; i < L; i++) {
			old_hypo[i][0] = hypo[i][0];
			old_hypo[i][1] = hypo[i][1];
		}

		for (i = 1; i < L; i++) {
			k = i - 1;
			for (j = i; j < L; j++) {
				if (map[j][1] > map[k][1])
					k = j;
			}

			temp = map[i - 1][1]; // exchange fitness
			map[i - 1][1] = map[k][1];
			map[k][1] = temp;

			temp = map[i - 1][0]; // exchange index
			map[i - 1][0] = map[k][0];
			map[k][0] = temp;
		}
	}

	public int[] crossover_mutate() { // hypo-> 10-> 2-> crossover-> 10-> hypo
		for (int i = this.Rep; i < L; i += 2) { // deal with a pair (h1,h2)
			// find the un-replicated one (sorted array in the map)
			String[] temp = new String[4];

			int j = map[i][0];
			temp[0] = Integer.toBinaryString(hypo[j][0]);
			temp[1] = Integer.toBinaryString(hypo[j][1]);

			j = map[i + 1][0];
			temp[2] = Integer.toBinaryString(hypo[j][0]);
			temp[3] = Integer.toBinaryString(hypo[j][1]);

			for (j = 0; j < 4; j++) {
				temp[j] = fixLeadingZeros(temp[j]);
			}

			// crossover mask: 1111100000
			j = map[i][0];
			hypo[j][0] = Integer.valueOf(temp[0], 2);
			hypo[j][1] = Integer.valueOf(temp[3], 2);

			j = map[i + 1][0];
			hypo[j][0] = Integer.valueOf(temp[2], 2);
			hypo[j][1] = Integer.valueOf(temp[1], 2);

		}

		// mutate number: Pmut * L
		int N = (int) (10 * this.Pmut) * L / 10;
		int[] rd = new int[N]; // for random number checking duplicated?
		int[] mu = new int[N];

		/*
		 * // test for (int i = 0; i < N; i++) { System.out.print(map[i][0] +
		 * " "); } System.out.println("\n");
		 */

		rd = this.initMutIndex();

		int index;
		String tmp;

		System.out.println("Mutation pairs:");
		for (int i = 0; i < N; i++) {

			index = this.searchIndex(rd[i]); // index of hypo[][]
			mu[i] = index;
			// mutation can happen in any position in chromosome
			tmp = fixLeadingZeros(Integer.toBinaryString(hypo[index][0]))
					+ fixLeadingZeros(Integer.toBinaryString(hypo[index][1]));

			System.out.println("(" + hypo[index][0] + "," + hypo[index][1]
					+ ")");
			System.out.println("before: " + tmp);
			tmp = this.mutate(tmp);
			System.out.println(" after: " + tmp + "\n");
			hypo[index][0] = Integer.valueOf(tmp.substring(0, 5), 2);
			hypo[index][1] = Integer.valueOf(tmp.substring(5, 10), 2);

		}
		System.out.println();

		/*
		 * // for test for (int i = 0; i < N; i++) { System.out.println(rd[i] +
		 * "-" + mu[i] + " "); } System.out.println("\n");
		 */

		return mu;
	}

	// determine which pairs will do the mutation
	private int[] initMutIndex() {
		int N = (int) (10 * this.Pmut) * L / 10;
		int[] rd = new int[N];

		for (int i = 0; i < N; i++) {
			while (true) {
				int which = random(0, L - 1, -1);
				if (searchInRep(which, rd, i)) {
					rd[i] = which;
					break;
				}
			}
		}
		return rd;
	}

	// to avoid the mutation pairs are not
	// in the replication ones and chosen ones
	private boolean searchInRep(int a, int rd[], int b) {
		for (int i = 0; i < this.Rep; i++) {
			if (a == map[i][0]) {
				return false;
			}
		}
		for (int i = 0; i < b; i++) {
			if (a == rd[i]) {
				return false;
			}
		}
		return true;
	}

	// mutate the bit
	private String mutate(String s) { // tested
		String str = s;
		int pos, tmp1, tmp2;
		String[] b = new String[3];
		while (true) {
			pos = random(0, 8, -1);
			b[0] = s.substring(0, pos);
			b[1] = s.substring(pos, pos + 1);
			b[2] = s.substring(pos + 1, 10);

			if (b[1].equals("1")) {
				b[1] = "0";
			} else {
				b[1] = "1";
			}
			str = b[0] + b[1] + b[2];
			tmp1 = Integer.valueOf(str.substring(0, 5), 2);
			tmp2 = Integer.valueOf(str.substring(5, 10), 2);

			// to control the correctness of A and B (even, odd, 6~27)
			if (tmp1 % 2 == 0 && tmp2 % 2 != 0 && tmp1 >= 6 && tmp1 < 27
					&& tmp2 > 6 && tmp2 <= 27) {
				break;
			}
		}
		return str;
	}

	// add the leading zeros to format the binary (5 bits for one integer)
	public String fixLeadingZeros(String ori) { // tested
		int len = ori.length();
		for (int k = len; k < 5; k++) {
			ori = "0" + ori;
		}
		return ori;
	}

	private int searchIndex(int p) {
		int i;
		for (i = 0; i < L; i++) {
			if (map[i][0] == p) {
				break;
			}
		}
		return i;
	}

	// one iteration process
	public int iteration() {
		int i = 0;
		boolean flag = false;
		while (true) {
			System.out.println(i + " iteration:\n");
			flag = this.fitness(); // check the theta value here
			if (flag) {
				System.out.println(i + " iterations reach the threshold");
				break;
			}

			this.sort_fitness(); // replicate

			this.printHypos("Before crossover");
			int[] mu = this.crossover_mutate(); // crossover and mutate
			this.printHypos("After crossover");

			this.printCmp(mu);

			i++;
		}
		System.out.println("All works done!\n");
		return i;
	}

	public static void main(String[] args) {
		int sum[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
		// Pco=0.8, Pmut=0.2, Threshold=2000, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.8, 0.2, 2000);
			sum[0] += test.iteration();
		}

		// Pco=0.8, Pmut=0.1, Threshold=2000, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.8, 0.1, 2000);
			sum[1] += test.iteration();
		}

		// Pco=0.6, Pmut=0.2, Threshold=2000, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.6, 0.2, 2000);
			sum[2] += test.iteration();
		}

		// Pco=0.6, Pmut=0.1, Threshold=2000, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.6, 0.1, 2000);
			sum[3] += test.iteration();
		}

		// Pco=0.8, Pmut=0.2, Threshold=1800, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.8, 0.2, 1800);
			sum[4] += test.iteration();
		}

		// Pco=0.8, Pmut=0.1, Threshold=1800, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.8, 0.1, 1800);
			sum[5] += test.iteration();
		}

		// Pco=0.6, Pmut=0.2, Threshold=1800, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.6, 0.2, 1800);
			sum[6] += test.iteration();
		}

		// Pco=0.6, Pmut=0.1, Threshold=1800, example=20
		for (int i = 0; i < 10; i++) {
			genetic test = new genetic(0.6, 0.1, 1800);
			sum[7] += test.iteration();
		}

		System.out.println("Average number of iterations:");
		for (int i = 0; i < 8; i++) { // average iterations
			System.out.println("Test " + i + ": " + sum[i] / 10);
		}
	}

}
