import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.lang.reflect.InvocationTargetException;

class Draw implements GraphSample {
    static final int WIDTH = 700, HEIGHT = 700;        // Size of our example
    private final Stroke StrokeToDraw = new StrokeToDraw(1.0f);

    public String getName() {return "Astroid";} // From GraphSample
    public int getWidth() { return WIDTH; }            // From GraphSample
    public int getHeight() { return HEIGHT; }
    private final Shape astroid = new Astroid(240, getWidth()/2,getHeight()/2);
    Shape line = new Line2D.Double((double) getWidth()/2 , 0, (double)getWidth()/2 , getHeight());
    Shape line2 = new Line2D.Double(0 , (double)getHeight()/2, getWidth() , (double)getHeight()/2);


    @Override
    public void draw(Graphics2D g, Component c) {
        g.setStroke(new BasicStroke(2.0f));
        g.draw(line);
        g.draw(line2);

        g.setStroke(StrokeToDraw);
        g.draw(astroid);
    }
}


class Main extends JFrame {

    public Main(final GraphSample[] examples) {
        super("Лаба 3 вар 9");

        Container cpane = getContentPane();
        cpane.setLayout(new BorderLayout());
        final JTabbedPane tpane = new JTabbedPane();
        cpane.add(tpane, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        for (GraphSample e : examples) {
            tpane.addTab(e.getName(), new GraphSamplePane(e));
        }
    }
    public static class GraphSamplePane extends JComponent {
        GraphSample example;
        Dimension size;

        public GraphSamplePane(GraphSample example) {
            this.example = example;
            size = new Dimension(example.getWidth(), example.getHeight());
            setMaximumSize( size );
        }

        public void paintComponent(Graphics g) {
            g.setColor(Color.white);
            g.fillRect(0, 0, size.width, size.height);
            g.setColor(Color.black);
            example.draw((Graphics2D) g, this);
        }

        public Dimension getPreferredSize() { return size; }
        public Dimension getMinimumSize() { return size; }
    }
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        GraphSample[] examples = new GraphSample[1];

        Class exampleClass = Draw.class;
        examples[0] = (GraphSample) exampleClass.getDeclaredConstructor().newInstance();

        Main f = new Main(examples);
        f.pack();
        f.setVisible(true);
    }
}
interface GraphSample {
    String getName();                      // Return the example name
    int getWidth();                        // Return its width
    int getHeight();                       // Return its height
    void draw(Graphics2D g, Component c);  // Draw the example
}