import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;

public class Astroid implements Shape, Cloneable, Transferable, Serializable {

    private final int param ;
    private final double delta = 0.001;
    private final double startAngle = -Math.PI / 2 + delta;
    private final double endAngle = startAngle + 2 * Math.PI;
    private float centerX ;
    private float centerY ;

    public Astroid(int param, int centerX, int centerY) {
        this.param = param;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(0, 0, (int)centerX*2, (int)centerY*2);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Float(0, 0, centerX*2, centerY*2);
    }

    public int getCenterX() {
        return (int)centerX;
    }

    public int getCenterY() {
        return (int)centerY;
    }

    public double getR(double alpha) {
        return alpha*130;
    }

    @Override
    public boolean contains(double x, double y) {
        if (!getBounds().contains(x, y))
            return false;
        double x0 = x - getCenterX();
        double y0 = getCenterY() - y;
        if (x0 < 0 || y0 < 0)
            return false;
        double alpha = Math.atan(y0 / x0);
        double r = getR(alpha);
        return x0 * x0 + y0 * y0 < r * r;
    }

    @Override
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return getBounds().intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return getBounds().intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return contains(x, y) && contains(x + w, y) && contains(x, y + h)
                && contains(x + w, y + h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new PlotIterator(at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new PlotIterator(at, flatness);
    }

    public void translate(double x, double y) {
        centerX += x;
        centerY += y;
    }


    class PlotIterator implements PathIterator {
        AffineTransform transform;
        double flatness;
        double angle = startAngle;
        double step = 10;
        boolean done = false;

        public PlotIterator(AffineTransform transform, double flatness) {
            this.transform = transform;
            this.flatness = flatness;
        }

        public PlotIterator(AffineTransform transform) {
            this.transform = transform;
            this.flatness = 0;
        }

        @Override
        public int getWindingRule() {
            return WIND_NON_ZERO;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public void next() {
            if (done) {
                return;
            }
            if (flatness == 0) {
                step = 0.05;
            } else {
                step = flatness;
            }
            angle += step;
            if (angle >= endAngle) {
                done = true;
            }
        }

        @Override
        public int currentSegment(float[] coords) {
            coords[0] = (float) (param * Math.pow(Math. cos(angle), 3)) + centerX;
            coords[1] = -(float) (param * Math.pow(Math.sin(angle), 3)) + centerY;
            if (angle >= endAngle) done = true;
            if (angle == startAngle) return SEG_MOVETO;
            else return SEG_LINETO;
        }


        @Override
        public int currentSegment(double[] coords) {
            coords[0] = (float) (param * Math.pow(Math.cos(angle), 3)) + centerX;
            coords[1] = -(float) (param * Math.pow(Math.sin(angle), 3)) + centerY;
            if (angle >= endAngle) done = true;
            if (angle == startAngle) return SEG_MOVETO;
            else return SEG_LINETO;
        }
    }
    static DataFlavor decDataFlavor = new DataFlavor(Astroid.class, "Witch_of_Agnesi");

    // This is a list of the flavors we know how to work with
    private static DataFlavor[] supportedFlavors = {decDataFlavor, DataFlavor.stringFlavor};


    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors.clone();
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return (dataFlavor.equals(decDataFlavor) || dataFlavor.equals(DataFlavor.stringFlavor));
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
        if (dataFlavor.equals(decDataFlavor)) {
            return this;
        } else if (dataFlavor.equals(DataFlavor.stringFlavor)) {
            return toString();
        } else
            throw new UnsupportedFlavorException(dataFlavor);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) { // This should never happen
            return this;
        }
    }


    @Override
    public String toString() {
        return param + " " + centerX + " " + centerY ;
    }


    static Astroid getFromString(String line) {
        String[] arr = line.split(" ");
        return new Astroid(Integer.parseInt(arr[0]),
                Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
    }

}