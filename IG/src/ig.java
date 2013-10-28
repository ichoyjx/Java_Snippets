import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ig {

	/**
	 * @author Jinxin Yang
	 * @myUH 1168646
	 * @param args
	 * @throws IOException
	 */

	double[] age = new double[61]; // 3rd attribute
	double[] epss = new double[61]; // 6th
	double[] lvdd = new double[61]; // 7th
	double[] wmindex = new double[61]; // 9th
	
	int[] CLASS = new int[61];
	int[] class_0_1 = new int[2];

	double[][] thresholds = new double[4][9];

	/*
	 * export 4 numeric attributes and class to "echocardiogram.arff"
	 * "echocardiogram.arff" will be used in WEKA
	 */
	public void preprocess() throws IOException {
		FileReader reader = null;
		BufferedReader br = null;
		FileWriter fw = null;
		PrintWriter out = null;
		fw = new FileWriter("./echocardiogram.arff");
		out = new PrintWriter(fw);

		try {

			reader = new FileReader(new File("echocardiogram.data"));
			br = new BufferedReader(reader);

			String s = null; // every time, read a line
			String[] temp = null;

			out.print("@relation echocardiogram\n\n");
			out.print("@attribute age-at-heart-attack integer\n");
			out.print("@attribute epss real\n");
			out.print("@attribute lvdd real\n");
			out.print("@attribute wall-motion-index real\n");
			out.print("@attribute alive-at-1 {0,1}\n\n");
			out.print("@data\n");

			int i = 0;
			while ((s = br.readLine()) != null) {
				temp = s.split(",");
				if (temp[2].indexOf(".") >= 0 || temp[2].equals("?")
						|| temp[5].equals("?") || temp[6].equals("?")
						|| temp[8].equals("?") || temp[12].equals("?")) {
					continue; // eliminate the line whose attribute has a "?"
				}

				try {
					age[i] = Double.parseDouble(temp[2]);
					epss[i] = Double.parseDouble(temp[5]);
					lvdd[i] = Double.parseDouble(temp[6]);
					wmindex[i] = Double.parseDouble(temp[8]);
					CLASS[i] = Integer.parseInt(temp[12]);
					/*
					 * System.out.println(age[i] + "    " + epss[i] + "    " +
					 * lvdd[i] + "    " + wmindex[i] + "    " + temp[12]);
					 */
					i++;
				} catch (NumberFormatException e) {
					System.out.println(e);
				}

				out.print(temp[2] + "," + temp[5] + "," + temp[6] + ","
						+ temp[8] + "," + temp[12] + "\n");
			}
			out.flush();
			fw.close();
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				br.close();
			if (reader != null)
				reader.close();
		}

	}

	public double findMIN(double[] a) {
		double min = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
			}
		}
		return min;
	}

	public double findMAX(double[] a) {
		double max = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

	public double calBinSize(double[] a) {
		double range = findMAX(a) - findMIN(a);
		double size = range / 10.0;
		return size;
	}

	public void setThresholds() { // "age","epss","lvdd","wmindex"
		double[] min = { findMIN(age), findMIN(epss), findMIN(lvdd),
				findMIN(wmindex) };
		double[] size = { calBinSize(age), calBinSize(epss), calBinSize(lvdd),
				calBinSize(wmindex) };

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 9; j++) {
				thresholds[i][j] = min[i] + (j + 1) * size[i];
				// System.out.print(thresholds[i][j] + " ");
			}
			// System.out.println();
		}

		// System.out.println();
	}

	public int[][] countSeperateClass(double[] a, double threshold) {
		int[][] count = new int[2][2]; // [][0] stores 0, [][1] stores 1
		double min = findMIN(a);
		double max = findMAX(a);

		for (int i = 0; i < a.length; i++) {
			// [min, threshold)
			if (a[i] >= min && a[i] < threshold) {
				if (CLASS[i] == 0) {
					count[0][0]++;
				} else {
					count[0][1]++;
				}
			}
			// [threshold, MAX)
			if (a[i] >= threshold && a[i] <= max) {
				if (CLASS[i] == 0) {
					count[1][0]++;
				} else {
					count[1][1]++;
				}
			}
		}
		
		return count;
	}

	public void countEntireClass() { // how many 0s and 1s
		for (int i = 0; i < CLASS.length; i++) {
			if (CLASS[i] == 0) {
				class_0_1[0]++;
			} else {
				class_0_1[1]++;
			}
		}
	}

	public double log(double base, double N) {
		double result = 1.0 * Math.log10(N) / Math.log10(base);
		return result;
	}

	public double H(int[] a) {
		double P1 = a[0] * 1.0 / (a[0] + a[1]);
		double P2 = 1.0 - P1;
		double E = 0.0;
		if (P1 == 0.0 && P2 != 0.0) {
			E = -(P2 * log(2, P2));
		}
		if (P1 != 0.0 && P2 == 0.0) {
			E = -(P1 * log(2, P1));
		}
		if (P1 == 0.0 && P2 == 0.0) {
			E = 0.0;
		}
		if (P1 != 0.0 && P2 != 0.0) {
			E = -(P1 * log(2, P1) + P2 * log(2, P2));
		}

		return E;

	}

	public double IG(double[] a, double threshold) {
		double left = H(class_0_1);

		int[][] count = new int[2][2];
		count = countSeperateClass(a, threshold);

		int S1 = count[0][0] + count[0][1];
		int S2 = count[1][0] + count[1][1];
		int S = S1 + S2; // should be 61

		double right = -((S1 * 1.0 / S) * H(count[0]) + (S2 * 1.0 / S)
				* H(count[1]));
		return (left + right);
	}

	public static void main(String[] args) throws IOException {
		ig infoGain = new ig();
		infoGain.preprocess();
		infoGain.setThresholds();
		infoGain.countEntireClass();
		double[] maxIG = new double[] { -1000, -1000, -1000, -1000 };
		double[] thres = new double[4];
		double[] next = new double[4];

		for (int j = 0; j < 9; j++) {
			next[0] = infoGain.IG(infoGain.age, infoGain.thresholds[0][j]);
			next[1] = infoGain.IG(infoGain.epss, infoGain.thresholds[1][j]);
			next[2] = infoGain.IG(infoGain.lvdd, infoGain.thresholds[2][j]);
			next[3] = infoGain.IG(infoGain.wmindex, infoGain.thresholds[3][j]);

			System.out.println(next[0] + " " + next[1] + 
					" " + next[2] + " " + next[3]);
			System.out.println();

			for (int i = 0; i < 4; i++) {
				if (next[i] > maxIG[i]) {
					maxIG[i] = next[i];
					thres[i] = infoGain.thresholds[i][j];
				}
			}
		}

		String[] name = {"age-at-heart-attack","epss","lvdd","wall-motion-index"};
		double tempMax = maxIG[0];
		int MaxIndex = 0;
		for (int i = 0; i < 4; i++) {
			System.out.print("When " + name[i] + "\'s threshold is " + thres[i] + ", ");
			System.out.println("the IG is biggest: " + maxIG[i]);
			
			if (maxIG[i] > tempMax) {
				tempMax = maxIG[i];
				MaxIndex = i;
			}
		}
		
		System.out.println("\nRoot is " + name[MaxIndex]);
	}
}
