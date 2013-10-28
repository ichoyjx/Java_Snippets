package h3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class q1 {

	/**
	 * @author Jinxin Yang
	 * @myUH 1168646
	 * @param args
	 * @throws IOException
	 */

	/*double[] RI = new double[214]; // 2nd attribute
	double[] Na = new double[214]; // 3rd
	double[] Mg = new double[214]; // 4th
	double[] Al = new double[214]; // 5th
	double[] Si = new double[214]; // 6th
	double[] K = new double[214]; // 7th
*/	int[] CLASS = new int[214]; // class
	String[] names = new String[] { "building_windows_float_processed",
			"building_windows_non_float_processed",
			"vehicle_windows_float_processed",
			"vehicle_windows_non_float_processed", "containers", "tableware",
			"headlamps" }; // all the names of class

	/*
	 * export 6 numeric attributes and class to "glass.arff" "glass.arff" will
	 * be used in WEKA
	 */
	public void preprocess() throws IOException {
		FileReader reader = null;
		BufferedReader br = null;
		FileWriter fw = null;
		PrintWriter out = null;
		fw = new FileWriter("./glass.arff");
		out = new PrintWriter(fw);

		try {

			reader = new FileReader(new File("glass.data"));
			br = new BufferedReader(reader);

			String s = null; // every time, read a line
			String[] temp = null;

			out.print("@relation glass\n\n");
			out.print("@attribute RI numeric\n");
			out.print("@attribute Na numeric\n");
			out.print("@attribute Mg numeric\n");
			out.print("@attribute Al numeric\n");
			out.print("@attribute Si numeric\n");
			out.print("@attribute K numeric\n");
			out.print("@attribute Ca numeric\n");
			out.print("@attribute Ba numeric\n");
			out.print("@attribute Fe numeric\n");
			out.print("@attribute type_of_glass ");
			out.print("{" + names[0]);
			for (int i = 1; i < 7; i++) {
				out.print(", " + names[i]);
			}
			out.print("}\n\n");
			out.print("@data\n");

			int i = 0;
			while ((s = br.readLine()) != null) {
				temp = s.split(",");

				try {
					/*RI[i] = Double.parseDouble(temp[1]);
					Na[i] = Double.parseDouble(temp[2]);
					Mg[i] = Double.parseDouble(temp[3]);
					Al[i] = Double.parseDouble(temp[4]);
					Si[i] = Double.parseDouble(temp[5]);
					K[i] = Double.parseDouble(temp[6]);*/
					CLASS[i] = Integer.parseInt(temp[10]);

					out.print(temp[1] + "," + temp[2] + "," + temp[3] + ","
							+ temp[4] + "," + temp[5] + "," + temp[6] + ","
							+ temp[7] + "," + temp[8] + "," + temp[9] + ","
							+ names[CLASS[i]-1] + "\n");
					i++;
				} catch (NumberFormatException e) {
					System.out.println(e);
				} catch (ArrayIndexOutOfBoundsException ex) {
					//System.out.println(ex);
				}
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

	public static void main(String[] args) throws IOException {
		q1 test = new q1();
		test.preprocess();
		System.out.println("done!");
	}
}
