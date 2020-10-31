package ecnu.edu.wclong.pojo;

import java.math.BigInteger;

public class BigIntegerPathCode extends PathCode {
    private BigInteger value;

    @Override
    boolean equals(PathCode other) {
        BigInteger x = new BigInteger("231");

        return false;
    }

    @Override
    boolean isLessThan(long x) {
        return value.subtract(BigInteger.valueOf(x)).compareTo(BigInteger.ZERO) < 0;
    }
}
