package ecnu.edu.wclong.processor;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class PathProcessor {

    private int labelSum;

    private int maxHop;

    private Map<Integer, Long> prefixCodeMap;

    private Map<Integer, Long> radixMap;

    public PathProcessor(int labelSum, int maxHop) {
        this.labelSum = labelSum;
        this.maxHop = maxHop;
        this.initMap(labelSum, maxHop);
    }

    private void initMap(int labelSum, int maxHop) {
        prefixCodeMap = new HashMap<>(maxHop + 1);
        radixMap = new HashMap<>(maxHop + 1);

        long prefixCode = labelSum;
        long radix = labelSum;
        prefixCodeMap.put(0, 0L);
        radixMap.put(0, radix);

        for (int i = 1; i <= maxHop; ++i) {
            prefixCodeMap.put(i, prefixCode);
            prefixCode = prefixCode + prefixCode * labelSum;
            radix *= radix;
            radixMap.put(i, radix);
        }
    }

    public boolean isSubPath(long parent, long child) {
        int parentHop = getPathCodeHop(parent);
        int childHop = getPathCodeHop(child);
        long radix = radixMap.get(childHop + 1);
        return (parent - prefixCodeMap.get(parentHop)) % radix == child - prefixCodeMap.get(childHop);
    }

    private int getPathCodeHop(long pathCode) {
        for (int i = 0; i <= maxHop; ++i) {
            if (pathCode < radixMap.get(i)) return i;
        }
        throw new IllegalArgumentException("path code exceed");
    }

    private int getPathCodeHop(BigInteger pathCode) {
        BigInteger radix = BigInteger.valueOf(labelSum);
        BigInteger code = BigInteger.valueOf(labelSum);
        for (int i = 0; i <= maxHop; ++i) {
            if (pathCode.compareTo(code) < 0) return i;
            code = code.multiply(radix);
        }
        throw new IllegalArgumentException("path code exceed");
    }
}
