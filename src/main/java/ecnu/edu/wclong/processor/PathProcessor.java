package ecnu.edu.wclong.processor;

import ecnu.edu.wclong.pojo.LabelPath;
import org.neo4j.graphdb.Label;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathProcessor {

    private int labelSum;

    private int maxHop;

    private int labelIdCount;

    private Map<Integer, Long> prefixCodeMap;

    private Map<Integer, Long> radixMap;

    private Map<Label, Integer> labelIdMap;


    public PathProcessor(int labelSum, int maxHop) {
        this.labelSum = labelSum;
        this.maxHop = maxHop;
        this.labelIdCount = 0;
        this.initMap(labelSum, maxHop);
    }

    public boolean isSubPath(long code1, long code2) {
        if (code1 == code2) return true;

        long parent, child;
        if (code1 > code2) {
            parent = code1;
            child = code2;
        } else {
            parent = code2;
            child = code1;
        }

        int parentHop = getPathCodeHop(parent);
        int childHop = getPathCodeHop(child);
        long radix = radixMap.get(childHop + 1);
        return (parent - prefixCodeMap.get(parentHop)) % radix == child - prefixCodeMap.get(childHop);
    }

    public long encode(LabelPath path) {
        long code = 0;
        List<Label> labels = path.getLabelSequence();
        for (int i = 0; i < labels.size(); ++i) {
            Label label = labels.get(i);

            if (!labelIdMap.containsKey(label)) {
                labelIdMap.put(label, labelIdCount++);
            }

            int id = labelIdMap.get(label);
            code += id * radixMap.get(i - 1) - prefixCodeMap.get(i - 1) + prefixCodeMap.get(i);
        }
        return code;
    }

    private void initMap(int labelSum, int maxHop) {
        prefixCodeMap = new HashMap<>(maxHop + 2);
        radixMap = new HashMap<>(maxHop + 2);
        labelIdMap = new HashMap<>(labelSum);

        long prefixCode = labelSum;
        long radix = labelSum;
        prefixCodeMap.put(-1, 0L);
        prefixCodeMap.put(0, 0L);
        radixMap.put(-1, 1L);
        radixMap.put(0, radix);

        for (int i = 1; i <= maxHop; ++i) {
            prefixCodeMap.put(i, prefixCode);
            prefixCode = prefixCode + prefixCode * labelSum;
            radix *= radix;
            radixMap.put(i, radix);
        }
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
