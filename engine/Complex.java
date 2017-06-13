package engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Complex {

	BigDecimal real;
	BigDecimal imag;

	BigDecimal r;
	BigDecimal theta;

	public Complex(BigDecimal real) {
		this.real = real;
		this.imag = BDConst.ZERO;
		r = real.abs();
		if (real.compareTo(BDConst.ZERO) < 0) {
			theta = BDConst.PI;
		}
	}

	public Complex(BigDecimal a, BigDecimal b, boolean isXY) {
		if (isXY) {
			real = a;
			imag = b;
			r = Util.bdSqrt(a.multiply(a).add(b.multiply(b)));
			theta = atan();
		} else {
			if (a.compareTo(BDConst.ZERO) < 0) {
				return;
			}
			r = a;
			theta = b;
			real = r.multiply(Util.bdCos(theta));
			imag = r.multiply(Util.bdSin(theta));
		}
	}

	public Complex add(Complex b) {
		return new Complex(real.add(b.real), imag.add(b.imag), true);
	}

	public Complex subtract(Complex b) {
		return this.add(b.negate());
	}

	public Complex negate() {
		return new Complex(real.negate(), imag.negate(), true);
	}

	public Complex multiply(Complex b) {
		return new Complex(r.multiply(b.r), theta.add(b.theta), false);
	}

	public Complex divide(Complex b) {
		return this.multiply(b.inverse());
	}

	public Complex inverse() {
		return new Complex(BDConst.ONE.divide(r, BDConst.precision, RoundingMode.HALF_EVEN), theta.negate(), false);
	}

	public String toString() {
		return real + " + " + imag + "i";
	}

	public Complex sqrt() {
		return new Complex(Util.bdSqrt(r), theta.divide(BDConst.TWO), false);
	}

	public Complex cbrt() {
		return new Complex(Util.bdCbrt(r), theta.divide(BDConst.THREE, BDConst.precision, RoundingMode.HALF_EVEN),
				false);
	}

	public Complex cos() {
		return new Complex(Util.bdCos(theta));
	}

	public Complex sin() {
		return new Complex(Util.bdSin(theta));
	}

	public Complex tan() {
		return new Complex(Util.bdTan(theta));
	}

	public BigDecimal atan() {
		if (real.compareTo(BDConst.ZERO) == 0) {
			return BDConst.PI.add(BDConst.PI.divide(BDConst.TWO).multiply(new BigDecimal(imag.signum())));
		}
		return Util.bdAtan(imag.divide(real, BDConst.precision, RoundingMode.HALF_EVEN));
	}

	public static void main(String[] args) {
		System.out.println(new Complex(BDConst.FOUR, BDConst.TWO, true).sqrt());
	}

}
