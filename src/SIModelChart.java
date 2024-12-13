

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SIModelChart {
    public static void main(String[] args) {
        String logFilePath = "si_output_log.txt";

        // Create dataset from the log file
        DefaultCategoryDataset dataset = createDataset(logFilePath);

        // Create line chart
        JFreeChart chart = ChartFactory.createLineChart(
                "SI Model Simulation",  // Chart title
                "Time Steps",           // X-axis label
                "Number of Nodes",      // Y-axis label
                dataset                 // Dataset
        );

        // Customize the chart for thicker lines
        customizeChart(chart);

        // Display the chart in a JFrame
        JFrame frame = new JFrame("SI Simulation Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new ChartPanel(chart));
        frame.setVisible(true);
    }

    private static DefaultCategoryDataset createDataset(String logFilePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (BufferedReader reader = new BufferedReader(new FileReader(logFilePath))) {
        	 String line = reader.readLine(); // Skip the header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int step = Integer.parseInt(parts[0]);
                int susceptible = Integer.parseInt(parts[1]);
                int infected = Integer.parseInt(parts[2]);

                dataset.addValue((Number)susceptible, "Susceptible (S)", step);
                dataset.addValue((Number)infected, "Infected (I)", step);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return dataset;
    }


    private static void customizeChart(JFreeChart chart) {
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        // Set thicker strokes for each series
        renderer.setSeriesStroke(0, new BasicStroke(3.0f)); // Susceptible
        renderer.setSeriesStroke(1, new BasicStroke(3.0f)); // Infected
    }
}
