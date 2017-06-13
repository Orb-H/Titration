package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class AnPolyApprox {

	final static double pre = 1E-15;

	public static List<Double> getRoot(double... coef) {
		List<Double> roots = new ArrayList<Double>();

		if (coef.length == 0) {
			return roots;
		}

		if (coef.length == 1) {
			roots.add(-coef[0]);
			return roots;
		}

		if (coef[coef.length - 1] == 0) {
			double[] newcoef = Arrays.copyOfRange(coef, 0, coef.length - 1);
			roots.addAll(getRoot(newcoef));
			roots.add(0.0);
			return roots;
		}

		double[] newcoef = Arrays.copyOfRange(coef, 0, coef.length - 1);
		for (int i = 0; i < coef.length - 1; i++) {
			newcoef[i] *= (coef.length - 1 - i) / (double) coef.length;
		}
		List<Double> rootsA = getRoot(newcoef);
		rootsA.sort(new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}

		});

		int n = rootsA.size();
		if (n == 0 && coef.length % 2 == 0) {
			return new ArrayList<Double>();
		} else if (n == 0 && coef.length % 2 == 1) {
			roots.add(app(0, coef));
			return roots;
		}
		double[] x = new double[n];
		double[] fx = new double[n];
		for (int i = 0; i < n; i++) {
			x[i] = rootsA.get(i);
			fx[i] = fx(x[i], coef);
		}
		if (fx[n - 1] <= 0) {
			if (fx[n - 1] == 0) {
				roots.add(x[n - 1]);
			} else {
				roots.add(app(x[n - 1] + 1, coef));
			}
		}
		for (int i = n - 2; i >= 0; i--) {
			if (fx[i] * fx[i + 1] <= 0) {
				if (fx[i] == 0) {
					roots.add(x[i]);
				} else {
					roots.add(app((x[i] + x[i + 1]) / 2, coef));
				}
			}
		}
		if (fx[0] * (coef.length % 2 == 0 ? -1 : 1) >= 0) {
			if (fx[0] == 0) {
				roots.add(x[0]);
			} else {
				roots.add(app(x[0] - 1, coef));
			}
		}

		return roots;
	}

	public static double fx(double x, double... coef) {
		double res = 1;
		for (int i = 0; i < coef.length; i++) {
			res *= x;
			res += coef[i];
		}
		return res;
	}

	public static double fpx(double x, double... coef) {
		double res = coef.length;
		for (int i = 0; i < coef.length - 1; i++) {
			res *= x;
			res += coef[i] * (coef.length - 1 - i);
		}
		return res;
	}

	public static double app(double xn, double... coef) {
		try {
			double fx = fx(xn, coef);
			double fpx = fpx(xn, coef);
			double xn1 = fx / fpx;
			xn1 = xn - xn1;
			if (Math.abs(xn1 - xn) < pre) {
				return xn1;
			}
			return app(xn1, coef);
		} catch (StackOverflowError e) {
			return xn;
		}
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		double fac = 0;
		List<Double> coef = new ArrayList<Double>();
		List<Double> roots = new ArrayList<Double>();

		try {
			while ((fac = sc.nextDouble()) == 0) {
			}
		} catch (Exception e) {
			System.err.println("I think it's not a polynomial...");
		}
		while (true) {
			try {
				coef.add(sc.nextDouble());
			} catch (Exception e) {
				sc.close();
				break;
			}
		}

		double[] d = new double[coef.size()];
		System.out.print(fac + "x^" + coef.size());
		for (int i = 0; i < coef.size(); i++) {
			System.out.print(" + " + coef.get(i) + "x^" + (coef.size() - i - 1));
			d[i] = coef.get(i) / fac;
		}
		System.out.println();
		List<Double> l = getRoot(d);
		while (l.size() != 0) {
			roots.addAll(l);
			int ls = l.size();
			double[] tmp = new double[d.length - ls];
			for (double r : l) {
				d[0] += r;
				for (int i = 1; i < d.length; i++) {
					d[i] += d[i - 1] * r;
				}
			}
			for (int i = 0; i < tmp.length; i++) {
				tmp[i] = d[i];
			}
			d = tmp;
			l = getRoot(d);
		}

		if (roots.size() == 0) {
			System.out.println("no root");
		} else {
			System.out.print("roots:");
			for (double r : roots) {
				System.out.println("\t" + r);
			}
		}
	}

}
