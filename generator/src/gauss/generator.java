package gauss;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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

public class generator {

	/**
	 * @state use javaPlot to plot the points
	 * @param null
	 * @author Jinxin(Brian) Yang
	 * @throws IOException
	 * @ID 1168646
	 * @XXYYSS = 16 86 46
	 * @date 9/19/2012
	 */
	public static void main(String[] args) throws IOException {
		double[][] xy = new double[100][2];
		GaussianGenerator myGauss = new GaussianGenerator();

		for (int i = 0; i < 100; i++) {
			myGauss.setStandardDeviation(46.0);
			myGauss.setMean(16.0);
			xy[i][0] = myGauss.generate();
			myGauss.setMean(86.0);
			xy[i][1] = myGauss.generate();
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

		for (int i = 0; i < 100; i++) {
			out.print(xy[i][0] + " " + xy[i][1] + "\n");
		}
		out.flush();
		fw.close();
		out.close();

		new generator().plot();
		new generator().plotToPNG();

		// plot
		fw = new FileWriter("./output.arff");
		out = new PrintWriter(fw);
		in1 = new FileWriter("./class1.data");
		out1 = new PrintWriter(in1);
		in2 = new FileWriter("./class2.data");
		out2 = new PrintWriter(in2);
		for (int i = 0; i < 100; i++) {
			if (i % 2 == 0) {
				out.print(xy[i][0] + "," + xy[i][1] + ",positive" + "\n");
				out1.print(xy[i][0] + " " + xy[i][1] + " 2" + "\n");

			} else {
				out.print(xy[i][0] + "," + xy[i][1] + ",negative" + "\n");
				out2.print(xy[i][0] + " " + xy[i][1] + " 2" + "\n");
			}

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

		// 5 parameters, 16 86 46
		double mux1 = 14, muy1 = 86, sigma1 = 46;
		double mux2 = 16, muy2 = 84, sigma2 = 46;
		double mux3 = 18, muy3 = 86, sigma3 = 46;
		double mux4 = 16, muy4 = 88, sigma4 = 46;
		double mux5 = 16, muy5 = 86, sigma5 = 40;

		// BigDecimal P1dash = likelihood(xy, mux1, muy1, sigma1);
		// BigDecimal P2dash = likelihood(xy, mux2, muy2, sigma2);
		// BigDecimal P3dash = likelihood(xy, mux3, muy3, sigma3);
		// BigDecimal P4dash = likelihood(xy, mux4, muy4, sigma4);
		// BigDecimal P5dash = likelihood(xy, mux5, muy5, sigma5);

		BigDecimal P1dash = normal(xy, 0, mux1, sigma1).multiply(
				normal(xy, 1, muy1, sigma1));
		BigDecimal P2dash = normal(xy, 0, mux2, sigma2).multiply(
				normal(xy, 1, muy2, sigma2));
		BigDecimal P3dash = normal(xy, 0, mux3, sigma3).multiply(
				normal(xy, 1, muy3, sigma3));
		BigDecimal P4dash = normal(xy, 0, mux4, sigma4).multiply(
				normal(xy, 1, muy4, sigma4));
		BigDecimal P5dash = normal(xy, 0, mux5, sigma5).multiply(
				normal(xy, 1, muy4, sigma5));

		System.out.println("P1dash: " + P1dash);
		System.out.println("P2dash: " + P2dash);
		System.out.println("P3dash: " + P3dash);
		System.out.println("P4dash: " + P4dash);
		System.out.println("P5dash: " + P5dash);

	}

	public void plot() { // Use Gnuplot to plot the points
		try {
			JavaPlot p = new JavaPlot();
			p.setTitle("Gaussian Distribution");
			p.getAxis("x").setLabel("X axis");
			p.getAxis("y").setLabel("Y axis");

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

	public static BigDecimal normal(double xy[][], int n, double mu,
			double sigma) {
		double likelihood = 1.0;
		BigDecimal ll = new BigDecimal(likelihood);

		double leftpart = 1 / (Math.sqrt(2 * Math.PI) * sigma);

		for (int i = 0; i < 100; i++) {
			double z = ((xy[i][n] - mu) * (xy[i][n] - mu)) / (sigma * sigma);
			double rightpart = Math.exp((-0.5) * z);
			double Pxy = leftpart * rightpart;
			BigDecimal pxy = new BigDecimal(Pxy);
			// System.out.println(Pxy);
			ll = ll.multiply(pxy);
		}

		return ll;
	}

	public static BigDecimal likelihood(double xy[][], double mux, double muy,
			double sigma) {
		double likelihood = 1.0;
		BigDecimal ll = new BigDecimal(likelihood);

		double leftpart = 1 / (2 * Math.PI * sigma * sigma);

		for (int i = 0; i < 100; i++) {
			double z = ((xy[i][0] - mux) * (xy[i][0] - mux) + (xy[i][1] - muy)
					* (xy[i][1] - muy))
					/ (sigma * sigma);
			double rightpart = Math.exp((-0.5) * z);
			double Pxy = leftpart * rightpart;
			BigDecimal pxy = new BigDecimal(Pxy);
			// System.out.println(Pxy);
			ll = ll.multiply(pxy);
		}

		return ll;
	}
}
