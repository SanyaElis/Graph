package ru.vsu.cs.course1.graph.graphic;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import ru.vsu.cs.course1.graph.GraphAlgorithms;
import ru.vsu.cs.course1.graph.GraphUtils;
import ru.vsu.cs.course1.graph.WeightedGraph;
import ru.vsu.cs.util.DrawUtils;
import ru.vsu.cs.util.JTableUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.*;

public class DemoGraph extends JFrame {
    private JPanel mainPanel;
    private JTable edgeTable;
    private JPanel paintPanelContainer;
    private JComboBox<Integer> variationOfTask;
    private JButton solutionButton;
    private JTextArea tempVertex;
    private JButton addWeight;
    private JButton cleanButton;
    private JButton weightPaintButton;
    private JComboBox<Integer> numberOfRobots;
    private JTextField firstPlace;
    private JTextField secondPlace;
    private JTextField thirdPlace;
    private JComboBox<Integer> speedFirst;
    private JComboBox<Integer> speedSecond;
    private JComboBox<Integer> speedThird;

    private PaintPlace myGraph;


    private static class PaintPlace extends JPanel {
        private int numberOfVertex = 0;
        private int DEFAULT_VERTEX_SIZE = 40;
        private int DEFAULT_FONT_SIZE = 11;
        private Color DEFAULT_EDGE_COLOR = Color.black;
        private Color DEFAULT_WEIGHT_COLOR = Color.WHITE;
        private Color DEFAULT_VERTEX_COLOR = Color.ORANGE;
        private Font font = null;
        public ArrayList<Vertex> vertexes;
        private Graphics g = super.getGraphics();

        private Font getFont(int size) {
            if (font == null || font.getSize() != size) {
                font = new Font("Comic Sans MS", Font.BOLD, size);
            }
            return font;
        }


        private void paintVertex(int x, int y, Graphics g) {
            g.setColor(DEFAULT_VERTEX_COLOR);
            g.fillOval(x, y, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE);
            g.setColor(Color.DARK_GRAY);
            g.drawOval(x, y, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE);

            g.setFont(getFont(DEFAULT_FONT_SIZE));
            g.setColor(DrawUtils.getContrastColor(DEFAULT_VERTEX_COLOR));
            DrawUtils.drawStringInCenter(g, font, "" + numberOfVertex, x, y, DEFAULT_VERTEX_SIZE, (int) Math.round(DEFAULT_VERTEX_SIZE * 0.95));
            vertexes.add(new Vertex(x, y, DEFAULT_VERTEX_COLOR, numberOfVertex));
            numberOfVertex++;
        }

        private void paintWeight(int[] edge, Graphics g) {
            g.setColor(DEFAULT_WEIGHT_COLOR);
            int x = (vertexes.get(edge[0]).x + vertexes.get(edge[1]).x) / 2;
            int y = (vertexes.get(edge[0]).y + vertexes.get(edge[1]).y) / 2;

            g.setFont(getFont(DEFAULT_FONT_SIZE));
            g.setColor(Color.RED);
            DrawUtils.drawStringInCenter(g, font, "" + edge[2], x, y, DEFAULT_FONT_SIZE, (int) Math.round(DEFAULT_FONT_SIZE * 0.95));
        }

        private void paintResult(int[] answer, Graphics g) {
            if (answer.length == 1) {
                g.setColor(Color.RED);
                g.drawOval(vertexes.get(answer[0]).x, vertexes.get(answer[0]).y, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE);
            } else if (answer.length == 2) {
                g.setColor(Color.RED);
                g.drawLine(vertexes.get(answer[0]).x + DEFAULT_VERTEX_SIZE / 2, vertexes.get(answer[0]).y + DEFAULT_VERTEX_SIZE / 2, vertexes.get(answer[1]).x + DEFAULT_VERTEX_SIZE / 2, vertexes.get(answer[1]).y + DEFAULT_VERTEX_SIZE / 2);
            }
        }

        public void drawWeight(int[] edge) {
            paintWeight(edge, super.getGraphics());
        }

        public void drawResult(int[] answer) {
            paintResult(answer, super.getGraphics());
        }

        public void drawVertex(int x, int y) {
            paintVertex(x, y, super.getGraphics());
        }

        public void drawEdge(Vertex v1, Vertex v2) {
            paintEdge(v1, v2, super.getGraphics());
        }

        public void drawRobot(int[] place) {
            for (Integer v : place) {
                paintRobot(v, super.getGraphics());
            }
        }

        private void paintRobot(int vertex, Graphics g) {
            g.setColor(Color.GREEN);
            g.drawOval(vertexes.get(vertex).x, vertexes.get(vertex).y, DEFAULT_VERTEX_SIZE, DEFAULT_VERTEX_SIZE);
        }

        private void paintEdge(Vertex v1, Vertex v2, Graphics g) {
            g.setColor(DEFAULT_EDGE_COLOR);
            g.drawLine(v1.x + DEFAULT_VERTEX_SIZE / 2, v1.y + DEFAULT_VERTEX_SIZE / 2, v2.x + DEFAULT_VERTEX_SIZE / 2, v2.y + DEFAULT_VERTEX_SIZE / 2);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            numberOfVertex = 0;
            vertexes = new ArrayList<>();
        }
    }

    public DemoGraph() {
        this.setTitle("Графы");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        JTableUtils.initJTableForArray(edgeTable, 60, true, true, false, false);

        edgeTable.setRowHeight(25);
        paintPanelContainer.setLayout(new BorderLayout());
        myGraph = new PaintPlace();
        paintPanelContainer.add(new JScrollPane(myGraph));

        variationOfTask.addItem(1);
        variationOfTask.addItem(2);

        numberOfRobots.addItem(2);
        numberOfRobots.addItem(3);

        speedFirst.addItem(1);
        speedFirst.addItem(2);

        speedSecond.addItem(1);
        speedSecond.addItem(2);

        speedThird.addItem(1);
        speedThird.addItem(2);

        myGraph.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    myGraphLeftMousePressed(x, y);
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    myGraphRightMousePressed(x, y);
                }
            }
        });

        addWeight.addActionListener(e -> {
            int[][] edgesWeight = new int[edges.size()][3];
            int k = 0;
            for (Edge edge : edges) {
                edgesWeight[k][0] = edge.vertex1;
                edgesWeight[k][1] = edge.vertex2;
                if (Objects.equals(variationOfTask.getSelectedItem(), 1)) {
                    edgesWeight[k][2] = 1;
                }
                k++;
            }
            JTableUtils.writeArrayToJTable(edgeTable, edgesWeight);
        });
        cleanButton.addActionListener(e -> {
            edges = new HashSet<>();
            myGraph.numberOfVertex = 0;
            myGraph.vertexes = new ArrayList<>();
            myGraph.repaint();
        });

        solutionButton.addActionListener(e -> {
            try {
                int[][] weight = JTableUtils.readIntMatrixFromJTable(edgeTable);
                assert weight != null;
                int[] speed;
                int[] place;
                if (Objects.equals(numberOfRobots.getSelectedItem(), 2)) {
                    speed = new int[]{(Integer) speedFirst.getSelectedItem(), (Integer) speedSecond.getSelectedItem()};
                    place = new int[]{Integer.parseInt(firstPlace.getText()), Integer.parseInt(secondPlace.getText())};
                } else {
                    speed = new int[]{(Integer) speedFirst.getSelectedItem(), (Integer) speedSecond.getSelectedItem(), (Integer) speedThird.getSelectedItem()};
                    place = new int[]{Integer.parseInt(firstPlace.getText()), Integer.parseInt(secondPlace.getText()), Integer.parseInt(thirdPlace.getText())};
                }
                WeightedGraph graph = GraphUtils.fromPanel(weight);
                myGraph.drawRobot(place);
                System.out.println("==========================");
                int[] result = GraphAlgorithms.findPlace(graph, speed, place);
                tempVertex.setText(Arrays.toString(result));
                myGraph.drawResult(result);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });
        weightPaintButton.addActionListener(e -> {
            try {
                int[][] weight = JTableUtils.readIntMatrixFromJTable(edgeTable);
                assert weight != null;
                for (int[] edge : weight) {
                    myGraph.drawWeight(edge);
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void myGraphLeftMousePressed(int x, int y) {
        myGraph.drawVertex(x, y);
    }

    private Vertex lastClick = null;
    public Set<Edge> edges = new HashSet<>();

    private void myGraphRightMousePressed(int x, int y) {
        for (Vertex v : myGraph.vertexes) {
            if (Math.pow(x - v.x - myGraph.DEFAULT_VERTEX_SIZE / 2., 2) + Math.pow(y - v.y - myGraph.DEFAULT_VERTEX_SIZE / 2., 2) <= myGraph.DEFAULT_VERTEX_SIZE * myGraph.DEFAULT_VERTEX_SIZE / 4.) {
                if (lastClick == null) {
                    lastClick = v;
                    tempVertex.setText("Сейчас запомнена вершина " + v.number);
                } else if (lastClick.number != v.number) {
                    myGraph.drawEdge(lastClick, v);
                    if (lastClick.number < v.number) {
                        edges.add(new Edge(lastClick.number, v.number));
                    } else {
                        edges.add(new Edge(v.number, lastClick.number));
                    }
                    lastClick = null;
                    tempVertex.setText("Ничего не запомнено");
                }
            }
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(10, 7, new Insets(0, 0, 0, 0), -1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        mainPanel.add(scrollPane1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 400), null, 0, false));
        edgeTable = new JTable();
        edgeTable.setPreferredScrollableViewportSize(new Dimension(200, 400));
        scrollPane1.setViewportView(edgeTable);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(2, 3, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(800, 400), null, 0, false));
        paintPanelContainer = new JPanel();
        paintPanelContainer.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(paintPanelContainer, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(800, 400), null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Задание графа");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Рёбра");
        mainPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(5, 0, 1, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(50, -1), null, 0, false));
        variationOfTask = new JComboBox();
        panel3.add(variationOfTask, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(525, 11), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Выберите задачу:");
        panel2.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Граф");
        mainPanel.add(label4, new GridConstraints(1, 3, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        solutionButton = new JButton();
        solutionButton.setText("Решение");
        mainPanel.add(solutionButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addWeight = new JButton();
        addWeight.setText("Добавить вес");
        mainPanel.add(addWeight, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel4, new GridConstraints(4, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cleanButton = new JButton();
        cleanButton.setText("Очистить");
        panel4.add(cleanButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        weightPaintButton = new JButton();
        weightPaintButton.setText("Нарисовать вес дороги");
        panel4.add(weightPaintButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        mainPanel.add(spacer3, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        mainPanel.add(spacer4, new GridConstraints(9, 4, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        mainPanel.add(spacer5, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        mainPanel.add(spacer6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel5, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        panel5.add(spacer7, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Выберите количество роботов");
        panel5.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        numberOfRobots = new JComboBox();
        panel5.add(numberOfRobots, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 12, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel6, new GridConstraints(6, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Введите местоположение 1");
        panel6.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        firstPlace = new JTextField();
        panel6.add(firstPlace, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("2");
        panel6.add(label7, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        secondPlace = new JTextField();
        panel6.add(secondPlace, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("3");
        panel6.add(label8, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        thirdPlace = new JTextField();
        panel6.add(thirdPlace, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(50, -1), null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Скорости 1");
        panel6.add(label9, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("2");
        panel6.add(label10, new GridConstraints(0, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("3");
        panel6.add(label11, new GridConstraints(0, 10, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        speedFirst = new JComboBox();
        panel6.add(speedFirst, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), null, 0, false));
        speedSecond = new JComboBox();
        panel6.add(speedSecond, new GridConstraints(0, 9, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), null, 0, false));
        speedThird = new JComboBox();
        panel6.add(speedThird, new GridConstraints(0, 11, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(40, -1), null, 0, false));
        tempVertex = new JTextArea();
        mainPanel.add(tempVertex, new GridConstraints(8, 4, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
