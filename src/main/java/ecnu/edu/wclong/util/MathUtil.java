package ecnu.edu.wclong.util;

public class MathUtil {
    public static long fastPow(int radix, int exponent) {
        if (radix == 0) return 0;
        long ans = 1;
        while (exponent != 0) {
            if ((exponent & 1) != 0) {
                ans *= radix;
            }
            exponent >>= 1;
            radix *= radix;
        }
        return ans;
    }
}
