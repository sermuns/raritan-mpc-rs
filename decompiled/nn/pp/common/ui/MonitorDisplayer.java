/*
 * Decompiled with CFR 0.152.
 */
package nn.pp.common.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class MonitorDisplayer
extends JPanel {
    private MonitorDisplayerModel model;
    private int ratio;
    private List<GDWithPad> xSortedList;
    private List<GDWithPad> ySortedList;
    private static final int PRIMARY_SCREEN_WIDTH_SIZE = 60;
    private static final Insets INSET = new Insets(10, 10, 10, 10);
    private static final int MONITOR_PAD = 8;
    private static final GDComparator GDXCOMPARATOR = new GDComparator(true);
    private static final GDComparator GDYCOMPARATOR = new GDComparator(false);
    private static final int STROKE_SIZE = 2;
    private static final BasicStroke SELECTED_PAINTER = new BasicStroke(2.0f);
    private static final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public MonitorDisplayer(MonitorDisplayerModel monitorDisplayerModel) {
        this.setModel0(monitorDisplayerModel);
        this.setBackground(Color.WHITE);
    }

    private void setModel0(MonitorDisplayerModel monitorDisplayerModel) {
        this.model = monitorDisplayerModel;
        GraphicsDevice graphicsDevice = monitorDisplayerModel.getDefaultScreenDevice();
        int n = graphicsDevice.getDefaultConfiguration().getBounds().width;
        this.ratio = n / 60;
        ArrayList<GDWithPad> arrayList = new ArrayList<GDWithPad>();
        int n2 = 0;
        for (GraphicsDevice object : monitorDisplayerModel.getGraphicsDevices()) {
            arrayList.add(new GDWithPad(object, "" + MonitorDisplayer.getDisplayAlphabet(n2)));
            ++n2;
        }
        this.xSortedList = MonitorDisplayer.getSortedGraphicsDevices(arrayList, GDXCOMPARATOR);
        this.ySortedList = MonitorDisplayer.getSortedGraphicsDevices(arrayList, GDYCOMPARATOR);
        int n3 = 0;
        for (int n4 : MonitorDisplayer.getXPad(this.xSortedList)) {
            this.xSortedList.get(n3++).setPadX(n4);
        }
        n3 = 0;
        for (int n4 : MonitorDisplayer.getYPad(this.ySortedList)) {
            this.ySortedList.get(n3++).setPadY(n4);
        }
        this.setPreferredSize(MonitorDisplayer.getPreferredSize(this.ratio, this.xSortedList, this.ySortedList));
    }

    public void setModel(MonitorDisplayerModel monitorDisplayerModel) {
        this.setModel0(monitorDisplayerModel);
        this.revalidate();
        this.repaint();
    }

    private static Dimension getPreferredSize(int n, List<GDWithPad> list, List<GDWithPad> list2) {
        Rectangle rectangle = new Rectangle();
        for (GDWithPad gDWithPad : list) {
            rectangle = rectangle.union(gDWithPad.getGd().getDefaultConfiguration().getBounds());
        }
        int n2 = rectangle.width / n;
        int n3 = rectangle.height / n;
        n2 += MonitorDisplayer.INSET.left + MonitorDisplayer.INSET.right;
        n3 += MonitorDisplayer.INSET.top + MonitorDisplayer.INSET.bottom;
        for (GDWithPad gDWithPad : list) {
            n2 += gDWithPad.getPadX();
        }
        for (GDWithPad gDWithPad : list2) {
            n3 += gDWithPad.getPadY();
        }
        return new Dimension(n2, n3);
    }

    private static int[] getXPad(List<GDWithPad> list) {
        int n = 0;
        int[] nArray = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Rectangle rectangle;
            GDWithPad gDWithPad = list.get(i);
            if (i != 0) {
                rectangle = gDWithPad.getGd().getDefaultConfiguration().getBounds();
                int n2 = -1;
                for (int j = 0; j < i; ++j) {
                    Rectangle rectangle2 = list.get(j).getGd().getDefaultConfiguration().getBounds();
                    if (rectangle.x + n != rectangle2.width + rectangle2.x + n) continue;
                    n2 = n2 < nArray[j] ? nArray[j] : n2;
                }
                if (n2 == -1) continue;
                nArray[i] = 8 + n2;
                continue;
            }
            rectangle = gDWithPad.getGd().getDefaultConfiguration().getBounds();
            n = rectangle.x < 0 ? Math.abs(rectangle.x) : 0;
        }
        return nArray;
    }

    private static int[] getYPad(List<GDWithPad> list) {
        int n = 0;
        int[] nArray = new int[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            Rectangle rectangle;
            GDWithPad gDWithPad = list.get(i);
            if (i != 0) {
                rectangle = gDWithPad.getGd().getDefaultConfiguration().getBounds();
                int n2 = -1;
                for (int j = 0; j < i; ++j) {
                    Rectangle rectangle2 = list.get(j).getGd().getDefaultConfiguration().getBounds();
                    if (rectangle.y + n != rectangle2.height + rectangle2.y + n) continue;
                    n2 = n2 < nArray[j] ? nArray[j] : n2;
                }
                if (n2 == -1) continue;
                nArray[i] = 8 + n2;
                continue;
            }
            rectangle = gDWithPad.getGd().getDefaultConfiguration().getBounds();
            n = rectangle.y < 0 ? Math.abs(rectangle.y) : 0;
        }
        return nArray;
    }

    public static <T> List<T> getSortedGraphicsDevices(List<T> list, Comparator<T> comparator) {
        ArrayList<T> arrayList = new ArrayList<T>(list);
        Collections.sort(arrayList, comparator);
        return arrayList;
    }

    public boolean isSelectedMonitor(GDWithPad gDWithPad) {
        GraphicsDevice graphicsDevice = this.model.getSelectedDevice();
        return graphicsDevice == null ? false : gDWithPad.getGd().getIDstring().equals(graphicsDevice.getIDstring());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        int n = -1;
        int n2 = 0;
        Dimension dimension = this.getSize();
        Dimension dimension2 = this.getPreferredSize();
        int n3 = (dimension.width - dimension2.width) / 2;
        int n4 = (dimension.height - dimension2.height) / 2;
        n3 = n3 < 0 ? 0 : n3;
        n4 = n4 < 0 ? 0 : n4;
        n3 += MonitorDisplayer.INSET.left;
        n4 += MonitorDisplayer.INSET.top;
        boolean bl = this.model.getSelectedDevice() == null;
        for (GDWithPad gDWithPad : this.xSortedList) {
            Object object;
            int n5;
            Rectangle rectangle = gDWithPad.getGd().getDefaultConfiguration().getBounds();
            if (n == -1) {
                n = rectangle.x < 0 ? Math.abs(rectangle.x) : 0;
                n5 = ((GDWithPad)this.ySortedList.get((int)0)).getGd().getDefaultConfiguration().getBounds().y;
                n2 = n5 < 0 ? Math.abs(n5) : 0;
            }
            n5 = n3 + gDWithPad.getPadX() + (n + rectangle.x) / this.ratio;
            int n6 = n4 + gDWithPad.getPadY() + (n2 + rectangle.y) / this.ratio;
            int n7 = rectangle.width / this.ratio;
            int n8 = rectangle.height / this.ratio;
            graphics.drawRect(n5, n6, n7, n8);
            if (!bl && this.isSelectedMonitor(gDWithPad)) {
                object = (Graphics2D)graphics;
                Stroke stroke = ((Graphics2D)object).getStroke();
                ((Graphics2D)object).setStroke(SELECTED_PAINTER);
                graphics.drawRect(n5 + 2, n6 + 2, n7 - 3, n8 - 3);
                ((Graphics2D)object).setStroke(stroke);
            }
            object = graphics.getFontMetrics();
            int n9 = SwingUtilities.computeStringWidth((FontMetrics)object, gDWithPad.getName());
            int n10 = ((FontMetrics)object).getHeight() - ((FontMetrics)object).getLeading();
            int n11 = n5 + (n7 - n9) / 2;
            int n12 = n6 + 1 + ((n8 - n10) / 2 + ((FontMetrics)object).getAscent() - ((FontMetrics)object).getDescent());
            graphics.drawString(gDWithPad.getName(), n11, n12);
        }
    }

    public static void main(String[] stringArray) {
        JFrame jFrame = new JFrame("MonitorDisplayerTest");
        jFrame.setDefaultCloseOperation(3);
        DefaultMonitorDisplayerModel defaultMonitorDisplayerModel = new DefaultMonitorDisplayerModel(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices(), 0);
        MonitorDisplayer monitorDisplayer = new MonitorDisplayer(defaultMonitorDisplayerModel);
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.getViewport().add(monitorDisplayer);
        jFrame.getContentPane().add(jScrollPane);
        jFrame.pack();
        jFrame.setVisible(true);
        defaultMonitorDisplayerModel.setSelectedDevice(1);
    }

    public static String getDisplayAlphabet(int n) {
        return String.valueOf(alphabets.charAt(n));
    }

    public static class DefaultMonitorDisplayerModel
    implements MonitorDisplayerModel {
        private GraphicsDevice[] gds;
        private int defaultGD;
        private int selectedIndex = -1;

        public DefaultMonitorDisplayerModel(GraphicsDevice[] graphicsDeviceArray, int n) {
            this.gds = graphicsDeviceArray;
            this.defaultGD = n;
        }

        public DefaultMonitorDisplayerModel(GraphicsDevice[] graphicsDeviceArray, int n, int n2) {
            this(graphicsDeviceArray, n);
            this.setSelectedDevice(n2);
        }

        @Override
        public GraphicsDevice getDefaultScreenDevice() {
            return this.gds[this.defaultGD];
        }

        @Override
        public List<GraphicsDevice> getGraphicsDevices() {
            return Arrays.asList(this.gds);
        }

        public String getGraphicsDeviceDisplayString(int n) {
            return MonitorDisplayer.getDisplayAlphabet(n);
        }

        @Override
        public GraphicsDevice getSelectedDevice() {
            if (this.selectedIndex >= 0) {
                return this.gds[this.selectedIndex];
            }
            return null;
        }

        public void setSelectedDevice(int n) {
            this.selectedIndex = n;
        }
    }

    public static interface MonitorDisplayerModel {
        public List<GraphicsDevice> getGraphicsDevices();

        public GraphicsDevice getDefaultScreenDevice();

        public GraphicsDevice getSelectedDevice();
    }

    private static class GDWithPad {
        private final GraphicsDevice gd;
        private int padX = 0;
        private int padY = 0;
        private final String name;

        public GDWithPad(GraphicsDevice graphicsDevice, String string) {
            this.gd = graphicsDevice;
            this.name = string;
        }

        private GraphicsDevice getGd() {
            return this.gd;
        }

        private void setPadX(int n) {
            this.padX = n;
        }

        private int getPadX() {
            return this.padX;
        }

        private void setPadY(int n) {
            this.padY = n;
        }

        private int getPadY() {
            return this.padY;
        }

        private String getName() {
            return this.name;
        }
    }

    private static class GDComparator
    implements Comparator<GDWithPad> {
        private boolean x;

        public GDComparator(boolean bl) {
            this.x = bl;
        }

        @Override
        public int compare(GDWithPad gDWithPad, GDWithPad gDWithPad2) {
            Rectangle rectangle = gDWithPad.getGd().getDefaultConfiguration().getBounds();
            Rectangle rectangle2 = gDWithPad2.getGd().getDefaultConfiguration().getBounds();
            if (this.x) {
                return rectangle.x < rectangle2.x ? 0 : 1;
            }
            return rectangle.y < rectangle2.y ? 0 : 1;
        }
    }
}

