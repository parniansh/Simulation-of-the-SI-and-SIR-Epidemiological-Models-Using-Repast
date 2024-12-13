import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

import java.awt.BasicStroke;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SIRModelPlot {

    public static void main(String[] args) {
        // Path to your CSV file
        String filePath = "BA_network_1000_nodes_inf_0.20_rec_0.10_log.txt"; // Replace with the actual file path

        // Create the dataset
        DefaultCategoryDataset dataset = createDataset(filePath);

        // Create the chart
        JFreeChart chart = ChartFactory.createLineChart(
                "SIR Model Simulation Over Time", // Chart title
                "Step",                           // X-axis Label
                "Number of Nodes",                // Y-axis Label
                dataset                           // Dataset
        );

        customizeChart(chart);

        // Display the chart
        JFrame frame = new JFrame("SIR Model Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.add(new ChartPanel(chart));
        frame.setVisible(true);
    }

    private static DefaultCategoryDataset createDataset(String filePath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // Skip the header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int step = Integer.parseInt(parts[0]);
                int susceptible = Integer.parseInt(parts[1]);
                int infected = Integer.parseInt(parts[2]);
                int recovered = Integer.parseInt(parts[3]);

                dataset.addValue((Number)susceptible, "Susceptible (S)", step);
                dataset.addValue((Number)infected, "Infected (I)", step);
                dataset.addValue((Number)recovered, "Recovered (R)", step);
            }
        } catch (IOException e) {
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
        renderer.setSeriesStroke(2, new BasicStroke(3.0f)); // Recovered
    }
}
