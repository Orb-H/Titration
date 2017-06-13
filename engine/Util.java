package engine;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

	public static BigDecimal bdSqrt(BigDecimal b) {
		BigDecimal fx = BDConst.ONE.add(b.negate());
		BigDecimal fpx = BDConst.TWO;
		BigDecimal xn1 = fx.divide(fpx, BDConst.precision, RoundingMode.CEILING);
		xn1 = BDConst.ONE.add(xn1.negate());
		BigDecimal currentSquare = xn1.pow(2);
		BigDecimal currentPrecision = currentSquare.subtract(b).abs();
		if (currentPrecision.compareTo(BDConst.SQRT_PRE) <= -1) {
			return xn1;
		}
		return sqrtBigDecimal(b, xn1);
	}

	private static BigDecimal sqrtBigDecimal(BigDecimal b, BigDecimal xn) {
		try {
			BigDecimal fx = xn.pow(2).add(b.negate());
			BigDecimal fpx = xn.multiply(BDConst.TWO);
			BigDecimal xn1 = fx.divide(fpx, BDConst.precision, RoundingMode.CEILING);
			xn1 = xn.add(xn1.negate());
			BigDecimal currentSquare = xn1.pow(2);
			BigDecimal currentPrecision = currentSquare.subtract(b).abs();
			if (currentPrecision.compareTo(BDConst.SQRT_PRE) <= -1) {
				return xn1;
			}
			return sqrtBigDecimal(b, xn1);
		} catch (StackOverflowError e) {
			return xn;
		}
	}

	public static BigDecimal bdCbrt(BigDecimal b) {
		if (b.compareTo(BDConst.ZERO) < 0) {
			return bdCbrt(b.negate()).negate();
		}
		BigDecimal fx = BDConst.ONE.add(b.negate());
		BigDecimal fpx = BDConst.THREE;
		BigDecimal xn1 = fx.divide(fpx, BDConst.precision, RoundingMode.CEILING);
		xn1 = BDConst.ONE.add(xn1.negate());
		BigDecimal currentCube = xn1.pow(3);
		BigDecimal currentPrecision = currentCube.subtract(b).abs();
		if (currentPrecision.compareTo(BDConst.SQRT_PRE) <= -1) {
			return xn1;
		}
		return cbrtBigDecimal(b, xn1);
	}

	private static BigDecimal cbrtBigDecimal(BigDecimal b, BigDecimal xn) {
		try {
			BigDecimal fx = xn.pow(3).add(b.negate());
			BigDecimal fpx = xn.pow(2).multiply(BDConst.THREE);
			BigDecimal xn1 = fx.divide(fpx, BDConst.precision, RoundingMode.CEILING);
			xn1 = xn.add(xn1.negate());
			BigDecimal currentCube = xn1.pow(3);
			BigDecimal currentPrecision = currentCube.subtract(b).abs();
			if (currentPrecision.compareTo(BDConst.SQRT_PRE) <= -1) {
				return xn1;
			}
			return cbrtBigDecimal(b, xn1);
		} catch (StackOverflowError e) {
			return xn;
		}
	}

	public static BigDecimal bdLn(BigDecimal b) {
		if (b.compareTo(BDConst.ZERO) <= 0) {
			return null;
		} else {
			return log10TaylorBigDecimal(b).multiply(BDConst.LN10);
		}
	}

	public static BigDecimal bdLog(BigDecimal b) {
		if (b.compareTo(BDConst.ZERO) <= 0) {
			return null;
		} else {
			return log10TaylorBigDecimal(b);
		}
	}

	private static BigDecimal log10TaylorBigDecimal(BigDecimal b) {
		BigDecimal fx = BDConst.ZERO;
		int n = 1;
		BigDecimal tmp = BDConst.EIGHT;
		long scale = 0;
		while (b.compareTo(BDConst.ONE) < 0 || b.compareTo(BDConst.TEN) > 0) {
			if (b.compareTo(BDConst.TEN) > 0) {
				b = b.divide(BDConst.TEN);
				scale++;
			} else if (b.compareTo(BDConst.ONE) < 0) {
				b = b.multiply(BDConst.TEN);
				scale--;
			}
		}
		BigDecimal y = b.subtract(BDConst.ONE).divide(b.add(BDConst.ONE), BDConst.precision, RoundingMode.HALF_EVEN);
		try {
			while (fx.subtract(tmp)
					.divide((fx.compareTo(tmp) >= 0 ? fx : tmp), BDConst.precision, RoundingMode.HALF_EVEN).abs()
					.compareTo(BDConst.LOG_PRE) > 0 && n <= 2147483647) {
				tmp = fx;
				fx = fx.add(y.pow(2 * n - 1).multiply(BDConst.TWO).divide(new BigDecimal(2 * n - 1), BDConst.precision,
						RoundingMode.HALF_EVEN));
				n++;
			}
		} catch (StackOverflowError e) {
		}
		return fx.divide(BDConst.LN10, BDConst.precision, RoundingMode.HALF_EVEN).add(new BigDecimal(scale));
	}

	public static BigDecimal bdCos(BigDecimal b) {
		while (b.compareTo(BDConst.PI) > 0) {
			b = b.subtract(BDConst.TWOPI);
		}
		while (b.compareTo(BDConst.PI.negate()) < 0) {
			b = b.add(BDConst.TWOPI);
		}
		if (b.compareTo(BDConst.ZERO) < 0) {
			b = b.negate();
		}
		if (b.compareTo(BDConst.PI.divide(BDConst.TWO)) == 0) {
			return BDConst.ZERO;
		}
		return cosBigDecimal(b, BDConst.ONE, 1);
	}

	private static BigDecimal cosBigDecimal(BigDecimal b, BigDecimal x, int n) {
		try {
			BigDecimal cos = x;
			BigDecimal cos1 = cos.add(b.pow(2 * n).divide(fact(2 * n), BDConst.precision, RoundingMode.HALF_EVEN)
					.multiply(n % 2 == 0 ? BDConst.ONE : BDConst.ONE.negate()));
			if (cos1.subtract(cos).divide(cos1, BDConst.precision, RoundingMode.HALF_EVEN).abs()
					.compareTo(BDConst.SQRT_PRE) <= -1) {
				return cos1;
			}
			return cosBigDecimal(b, cos1, ++n);
		} catch (StackOverflowError e) {
			return x;
		}
	}

	public static BigDecimal bdSin(BigDecimal b) {
		while (b.compareTo(BDConst.PI) > 0) {
			b = b.subtract(BDConst.TWOPI);
		}
		while (b.compareTo(BDConst.PI.negate()) < 0) {
			b = b.add(BDConst.TWOPI);
		}
		if (b.compareTo(BDConst.ZERO) == 0) {
			return BDConst.ZERO;
		} else if (b.compareTo(BDConst.PI) == 0 || b.compareTo(BDConst.PI.negate()) == 0) {
			return BDConst.ZERO;
		}
		if (b.compareTo(BDConst.ZERO) < 0) {
			b = b.negate();
			return sinBigDecimal(b, b, 1).negate();
		}
		return sinBigDecimal(b, b, 1);
	}

	private static BigDecimal sinBigDecimal(BigDecimal b, BigDecimal x, int n) {
		try {
			BigDecimal sin = x;
			BigDecimal sin1 = sin
					.add(b.pow(2 * n + 1).divide(fact(2 * n + 1), BDConst.precision, RoundingMode.HALF_EVEN)
							.multiply(n % 2 == 0 ? BDConst.ONE : BDConst.ONE.negate()));
			if (sin1.subtract(sin).divide(sin1, BDConst.precision, RoundingMode.HALF_EVEN).abs()
					.compareTo(BDConst.SQRT_PRE) <= -1) {
				return sin1;
			}
			return sinBigDecimal(b, sin1, ++n);
		} catch (StackOverflowError e) {
			return x;
		}
	}

	public static BigDecimal bdTan(BigDecimal b) {
		while (b.compareTo(BDConst.HALFPI) > 0) {
			b = b.subtract(BDConst.PI);
		}
		while (b.compareTo(BDConst.HALFPI.negate()) < 0) {
			b = b.add(BDConst.PI);
		}
		if (b.compareTo(BDConst.ZERO) == 0) {
			return BDConst.ZERO;
		} else if (b.compareTo(BDConst.HALFPI) == 0 || b.compareTo(BDConst.HALFPI.negate()) == 0) {
			return null;
		}
		return bdSin(b).divide(bdCos(b), BDConst.precision, RoundingMode.HALF_EVEN);
	}

	public static BigDecimal bdAtan(BigDecimal b) {
		if (b.compareTo(BDConst.ZERO) == 0) {
			return BDConst.ZERO;
		} else if (b.compareTo(BDConst.ZERO) < 0) {
			b = b.negate();
			if (b.compareTo(BDConst.ONE) == 0) {
				return BDConst.PI.divide(BDConst.FOUR).negate();
			} else if (b.compareTo(BDConst.ONE) > 0) {
				return BDConst.HALFPI.subtract(atanBigDecimal(b, b, 1));
			}
			return atanBigDecimal(b, b, 1).negate();
		}
		if (b.compareTo(BDConst.ONE) == 0) {
			return BDConst.PI.divide(BDConst.FOUR);
		} else if (b.compareTo(BDConst.ONE) > 0) {
			return BDConst.HALFPI.subtract(atanBigDecimal(b, b, 1));
		}
		return atanBigDecimal(b, b, 1);
	}

	private static BigDecimal atanBigDecimal(BigDecimal b, BigDecimal x, int n) {
		try {
			BigDecimal atan = x;
			BigDecimal atan1 = atan
					.add(b.pow(2 * n + 1).divide(new BigDecimal(2 * n + 1), BDConst.precision, RoundingMode.HALF_EVEN)
							.multiply(n % 2 == 0 ? BDConst.ONE : BDConst.ONE.negate()));
			if (atan1.subtract(atan).divide(atan1, BDConst.precision, RoundingMode.HALF_EVEN).abs()
					.compareTo(BDConst.SQRT_PRE) <= -1) {
				return atan1;
			}
			return atanBigDecimal(b, atan1, ++n);
		} catch (StackOverflowError e) {
			return x;
		}
	}

	public static BigDecimal fact(int n) {
		BigDecimal tar = BDConst.ONE;
		if (n == 0) {
			return BDConst.ONE;
		} else if (n < 0) {
			return null;
		}
		while (n > 0) {
			tar = tar.multiply(new BigDecimal(n));
			n--;
		}
		return tar;
	}

	public static void main(String[] args) {
		System.out.println(bdAtan(BDConst.ONE));
	}

}
