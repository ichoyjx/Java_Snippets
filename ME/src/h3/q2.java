package h3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Random;

import javax.imageio.ImageIO;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.FileDataSet;
import com.panayotis.gnuplot.terminal.ImageTerminal;

// GaussianGenerator
class GaussianGenerator {
	public Random m_Random = new Random(123456789);
	public double m_Mean;
	protected double m_StandardDeviation;

	public double getMean() {
		return m_Mean;
	}

	public void setMean(double value) {
		m_Mean = value;
	}

	public double getStandardDeviation() {
		return m_StandardDeviation;
	}

	public void setStandardDeviation(double value) {
		m_StandardDeviation = value;
	}

	public double generate() {
		double gaussian = m_Random.nextGaussian();
		double value = m_Mean + (gaussian * m_StandardDeviation);
		return value;
	}
}

public class q2 {

	/**
	 * @state generate bi-variate Gaussian distribution and
	 * @state use javaPlot to plot the points
	 * @param null
	 * @author Jinxin(Brian) Yang
	 * @throws IOException
	 * @ID 1168646
	 * @XXYYZ = 68 64 6
	 * @sigma = 15 - Z = 9
	 * @date 10/31/2012
	 */

	public void plot() { // Use Gnuplot to plot the points
		try {
			JavaPlot p = new JavaPlot();
			p.setTitle("Gaussian Distribution");
			p.getAxis("x").setLabel("X_axis");
			p.getAxis("y").setLabel("Y_axis");

			p.setKey(JavaPlot.Key.TOP_RIGHT);

			p.addPlot(new FileDataSet(new File("./output.data")));
			p.plot();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void plotToPNG() {
		try {
			ImageTerminal png = new ImageTerminal();
			File pic = new File("./plot.png");
			try {
				pic.createNewFile();
				png.processOutput(new FileInputStream(pic));
			} catch (FileNotFoundException e) {
				System.err.print(e);
			} catch (IOException e) {
				System.err.print(e);
			}

			JavaPlot p = new JavaPlot();
			p.setTerminal(png);

			p.setTitle("Gaussian Distribution");
			p.getAxis("x").setLabel("X axis");
			p.getAxis("y").setLabel("Y axis");

			p.setKey(JavaPlot.Key.TOP_RIGHT);

			p.addPlot(new FileDataSet(new File("./output.data")));
			p.plot();

			try {
				ImageIO.write(png.getImage(), "png", pic);
			} catch (IOException ex) {
				System.err.print(ex);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static double phi(double G[], double mu[], double sigma[]) {
		double leftpart = 1 / (sigma[0] * sigma[1]);

		double z = ((G[0] - mu[0]) * (G[0] - mu[0])) / (sigma[0] * sigma[0])
				+ ((G[1] - mu[1]) * (G[1] - mu[1])) / (sigma[1] * sigma[1]);

		double rightpart = Math.exp((-0.5) * z);
		double Pxy = leftpart * rightpart;
		// System.out.println(Pxy);
		return Pxy;
	}

	public static double calGamma(double pi, double xy[], double mu1[],
			double sigma1[], double mu2[], double sigma2[]) {
		double gamma = pi * phi(xy, mu2, sigma2)
				/ ((1 - pi) * phi(xy, mu1, sigma1) + pi * phi(xy, mu2, sigma2));

		// System.out.println(gamma);
		return gamma;
	}

	public static double[] getInitMean(double[][] G1, double[][] G2) {
		double sum[] = { 0.0, 0.0 };
		for (int i = 0; i < 10; i++) {
			sum[0] = sum[0] + G1[i][0] + G2[i][0];
			sum[1] = sum[1] + G1[i][1] + G2[i][1];
		}
		double[] avg = { sum[0] / 20.0, sum[1] / 20.0 };
		// System.out.println(avg[0] + " " + avg[1]);
		return avg;
	}

	public static double[] getInitSigma(double[][] G1, double[][] G2) {
		double[] sum = { 0.0, 0.0 };
		double[] avg = new double[2];
		avg = getInitMean(G1, G2);
		for (int i = 0; i < 10; i++) {
			sum[0] = sum[0] + (G1[i][0] - avg[0]) * (G1[i][0] - avg[0]);
			sum[1] = sum[1] + (G1[i][1] - avg[1]) * (G1[i][1] - avg[1]);
			sum[0] = sum[0] + (G2[i][0] - avg[0]) * (G2[i][0] - avg[0]);
			sum[1] = sum[1] + (G2[i][1] - avg[1]) * (G2[i][1] - avg[1]);
		}
		avg[0] = Math.sqrt(sum[0] / 20.0);
		avg[1] = Math.sqrt(sum[1] / 20.0);

		// System.out.println(avg[0] + " " + avg[1] + "\n");
		return avg;
	}

	public static void main(String[] args) throws IOException {
		double[][] G1 = new double[10][2]; // first Gaussian-D
		double[][] G2 = new double[10][2]; // second Gaussian-D
		GaussianGenerator myGauss = new GaussianGenerator();

		for (int i = 0; i < 10; i++) {
			myGauss.setStandardDeviation(9.0); // first
			myGauss.setMean(68.0);
			G1[i][0] = myGauss.generate();
			myGauss.setStandardDeviation(9.0);
			myGauss.setMean(64.0);
			G1[i][1] = myGauss.generate();

			myGauss.setStandardDeviation(9.0); // second
			myGauss.setMean(68.0 + 2 * 9.0);
			G2[i][0] = myGauss.generate();
			myGauss.setStandardDeviation(9.0);
			myGauss.setMean(64.0 + 2 * 9.0);
			G2[i][1] = myGauss.generate();
		}

		FileWriter fw = null;
		PrintWriter out = null;
		FileWriter in1 = null;
		PrintWriter out1 = null;
		FileWriter in2 = null;
		PrintWriter out2 = null;

		// write into data file for plotting
		fw = new FileWriter("./output.data");
		out = new PrintWriter(fw);

		for (int i = 0; i < 10; i++) {
			out.print(G1[i][0] + " " + G1[i][1] + "\n");
			out.print(G2[i][0] + " " + G2[i][1] + "\n");
		}
		out.flush();
		fw.close();
		out.close();

		// plot
		new q2().plot();
		new q2().plotToPNG();

		fw = new FileWriter("./output.arff");
		out = new PrintWriter(fw);
		in1 = new FileWriter("./first.data");
		out1 = new PrintWriter(in1);
		in2 = new FileWriter("./second.data");
		out2 = new PrintWriter(in2);

		out.print("@relation bi-variate\n");
		out.print("@attribute xx real\n");
		out.print("@attribute yy real\n");
		out.print("@attribute which_one {first,second}\n");
		out.print("\n\n@data\n");

		for (int i = 0; i < 10; i++) {
			out.print(G1[i][0] + "," + G1[i][1] + ",first" + "\n");
			out.print(G2[i][0] + "," + G2[i][1] + ",second" + "\n");
			out1.print(G1[i][0] + " " + G1[i][1] + " 1" + "\n");
			out2.print(G2[i][0] + " " + G2[i][1] + " 1" + "\n");
		}
		out.flush();
		out1.flush();
		out2.flush();
		in1.close();
		in2.close();
		out1.close();
		out2.close();
		fw.close();
		out.close();

		double[] gamma = new double[20];
		double[] mu1 = G1[7];// G1[57];
		double[] mu2 = G2[2];// G2[22];
		double[] sigma1 = getInitSigma(G1, G2);
		double[] sigma2 = getInitSigma(G1, G2);
		double pi = 0.5;

		// output formating
		DecimalFormat outft = new DecimalFormat("##.0000");

		System.out.println("mu1x  mu1y  |  mu2x  mu2y    ||    sigma1x  sigma1y  |  sigma2x  sigma2y\n");
		System.out.println("Origin:");
		System.out.println("68.000  64.000  |  86.000  82.000    ||    9.000  9.000  |  9.000  9.000");
		System.out.println("Initial:");
		System.out.println(outft.format(mu1[0]) + " " + outft.format(mu1[1])
				+ " | " + outft.format(mu2[0]) + " " + outft.format(mu2[1])
				+ "  ||  " + outft.format(sigma1[0]) + " "
				+ outft.format(sigma1[1]) + " | " + outft.format(sigma2[0])
				+ " " + outft.format(sigma2[1]) + " | " + outft.format(pi));

		for (int r = 0; r < 100; r++) {
			System.out.println("\n" + (r + 1) + " iteration: ");
			for (int i = 0; i < 10; i++) {
				gamma[i] = calGamma(pi, G1[i], mu1, sigma1, mu2, sigma2);
				gamma[10 + i] = calGamma(pi, G2[i], mu1, sigma1, mu2, sigma2);
			}
			/*
			 * for (int j=0; j<20; j++) { System.out.print(gamma[j] + "  "); if
			 * ((j+1)%5 == 0) { System.out.print("\n"); } }
			 */
			mu1 = update_mu1(G1, G2, gamma);
			mu2 = update_mu2(G1, G2, gamma);
			sigma1 = update_sigma1(mu1, G1, G2, gamma);
			sigma2 = update_sigma2(mu2, G1, G2, gamma);
			pi = update_pi(gamma);

			System.out.println(outft.format(mu1[0]) + " "
					+ outft.format(mu1[1]) + " | " + outft.format(mu2[0]) + " "
					+ outft.format(mu2[1]) + "  ||  " + outft.format(sigma1[0])
					+ " " + outft.format(sigma1[1]) + " | "
					+ outft.format(sigma2[0]) + " " + outft.format(sigma2[1])
					+ " | " + outft.format(pi));
			
			for (int j=0; j<20; j++) {
				System.out.print(outft.format(gamma[j]) + "  ");
				if ((j+1)%5 == 0) {
					System.out.println();
				}
			}
		}

		System.out.println("done!");
	}

	public static double[] update_mu1(double[][] G1, double[][] G2,
			double[] gamma) {
		double[] new_mu1 = new double[2];
		double n_x = 0.0, domi = 0.0, n_y = 0.0;
		for (int i = 0; i < 10; i++) {
			n_x += (1 - gamma[i]) * G1[i][0];
			n_x += (1 - gamma[i + 10]) * G2[i][0];

			domi += 1 - gamma[i];
			domi += 1 - gamma[i + 10];

			n_y += (1 - gamma[i]) * G1[i][1];
			n_y += (1 - gamma[i + 10]) * G2[i][1];
		}
		new_mu1[0] = n_x / domi;
		new_mu1[1] = n_y / domi;
		return new_mu1;
	}

	public static double[] update_mu2(double[][] G1, double[][] G2,
			double[] gamma) {
		double[] new_mu2 = new double[2];
		double n_x = 0.0, n_y = 0.0, domi = 0.0;
		for (int i = 0; i < 10; i++) {
			n_x += gamma[i] * G1[i][0] + gamma[i + 10] * G2[i][0];
			n_y += gamma[i] * G1[i][1] + gamma[i + 10] * G2[i][1];
			domi += gamma[i] + gamma[i + 10];
		}
		new_mu2[0] = n_x / domi;
		new_mu2[1] = n_y / domi;
		return new_mu2;
	}

	public static double[] update_sigma1(double mu1[], double[][] G1,
			double[][] G2, double[] gamma) {
		double[] new_sigma1 = new double[2];
		double n_x = 0.0, n_y = 0.0, domi = 0.0;
		for (int i = 0; i < 10; i++) {
			n_x += (1 - gamma[i]) * (G1[i][0] - mu1[0]) * (G1[i][0] - mu1[0])
					+ (1 - gamma[i + 10]) * (G2[i][0] - mu1[0])
					* (G2[i][0] - mu1[0]);
			n_y += (1 - gamma[i]) * (G1[i][1] - mu1[1]) * (G1[i][1] - mu1[1])
					+ (1 - gamma[i + 10]) * (G2[i][1] - mu1[1])
					* (G2[i][1] - mu1[1]);
			domi += 1 - gamma[i] + 1 - gamma[i + 10];
		}
		new_sigma1[0] = Math.sqrt(n_x / domi);
		new_sigma1[1] = Math.sqrt(n_y / domi);
		return new_sigma1;
	}

	public static double[] update_sigma2(double mu2[], double[][] G1,
			double[][] G2, double[] gamma) {
		double[] new_sigma2 = new double[2];
		double n_x = 0.0, n_y = 0.0, domi = 0.0;
		for (int i = 0; i < 10; i++) {
			n_x += gamma[i] * (G1[i][0] - mu2[0]) * (G1[i][0] - mu2[0])
					+ gamma[i + 10] * (G2[i][0] - mu2[0]) * (G2[i][0] - mu2[0]);
			n_y += gamma[i] * (G1[i][1] - mu2[1]) * (G1[i][1] - mu2[1])
					+ gamma[i + 10] * (G2[i][1] - mu2[1]) * (G2[i][1] - mu2[1]);
			domi += gamma[i] + gamma[i + 10];
		}
		new_sigma2[0] = Math.sqrt(n_x / domi);
		new_sigma2[1] = Math.sqrt(n_y / domi);
		return new_sigma2;
	}

	public static double update_pi(double[] gamma) {
		double new_pi = 0.0;
		for (int i = 0; i < 20; i++) {
			new_pi += gamma[i];
		}
		return new_pi / 20.0;
	}

}
