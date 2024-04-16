import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.xy.*;

public class StockAnalyzer extends JFrame {
    private JButton importButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;

    public StockAnalyzer() {
        setTitle("Stock Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        importButton = new JButton("Import CSV");
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCSV();
            }
        });
        add(importButton, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);

        chartPanel = new JPanel();
        add(chartPanel, BorderLayout.SOUTH);
    }

    private void importCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Scanner scanner = new Scanner(file)) {
                // Clear previous data
                tableModel.setRowCount(0);

                // Read data from CSV file
                boolean isFirstRow = true;
                XYSeries series = new XYSeries("Stock Index");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (isFirstRow) {
                        isFirstRow = false;
                        continue; // Skip header row
                    }
                    
                    Date date = dateFormat.parse(parts[0]);
                    double stockIndex = Double.parseDouble(parts[1]);
                    // Add data to table
                    tableModel.addRow(new Object[]{dateFormat.format(date), stockIndex});
                    // Add data to series for chart
                    series.add(date.getTime(), stockIndex);
                }

                // Display chart
                displayChart(series);

            } catch (IOException | NoSuchElementException | NumberFormatException | ParseException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error reading CSV file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayChart(XYSeries series) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Stock Index Trend", // chart title
                "Date", // x-axis label
                "Stock Index", // y-axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        this.chartPanel.removeAll();
        this.chartPanel.add(chartPanel);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StockAnalyzer analyzer = new StockAnalyzer();
                analyzer.setVisible(true);
            }
        });
    }
}
