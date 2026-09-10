/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.scan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridCalculationUtil {
    public static void main(String[] stringArray) {
        for (int i = 1; i <= 32; ++i) {
            System.out.println("Panels " + i + " : " + GridCalculationUtil.calculateColsAndRows(i));
        }
    }

    static GridLayoutInfo calculateColsAndRows(int n) {
        int n2;
        int n3 = n2 = (int)Math.sqrt(n);
        while (n3 * n2 < n) {
            if (n3 > n2) {
                ++n2;
                continue;
            }
            ++n3;
        }
        List<Integer> list = Collections.emptyList();
        if (n3 > n2) {
            int n4 = n2;
            int n5 = n - n2 * n2;
            int n6 = n4 - n5 - 1;
            if (n6 > 0) {
                list = new ArrayList<Integer>();
            }
            for (int i = n2 - n6; i < n2; ++i) {
                list.add(i * n3);
            }
        }
        return new GridLayoutInfo(n3, n2, list);
    }

    static class GridLayoutInfo {
        public final int cols;
        public final int rows;
        public final List<Integer> fillers;

        public GridLayoutInfo(int n, int n2, List<Integer> list) {
            this.cols = n;
            this.rows = n2;
            this.fillers = Collections.unmodifiableList(list);
        }

        public String toString() {
            return "GridLayoutInfo [cols=" + this.cols + ", rows=" + this.rows + ", fillers=" + this.fillers + "]";
        }
    }
}

