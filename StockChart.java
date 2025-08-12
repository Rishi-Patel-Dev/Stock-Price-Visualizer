import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StockChart extends JPanel {
    private List<Double> stock1Prices;
    private List<Double> stock2Prices;
    private final String stock1 = "NVDA";
    private final String stock2 = "TSLA";
    private final int PADDING = 50;

    public StockChart() {
        stock1Prices = downloadStockPrices(stock1);
        stock2Prices = downloadStockPrices(stock2);
    }

    private List<Double> downloadStockPrices(String stockSymbol) {
        List<Double> prices = new ArrayList<>();
        String urlStr = "https://raw.githubusercontent.com/OntarioTech-CS-program/w25-lab08-Stock-Datasets/refs/heads/main/data/" + stockSymbol.toLowerCase() + ".us.csv";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(urlStr).openStream()))) {
            String line;
            br.readLine(); //skips header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length > 4) {
                    prices.add(Double.parseDouble(values[4]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prices;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawAxes(g2d);
        plotLines(g2d, stock1Prices, Color.GREEN);
        plotLines(g2d, stock2Prices, Color.RED);
        drawLegend(g2d, new String[]{stock1, stock2}, new Color[]{Color.GREEN, Color.RED});
    }

    private void drawAxes(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(Color.BLACK);
        g2d.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING); //x axis
        g2d.drawLine(PADDING, height - PADDING, PADDING, PADDING); //y-axis
    }

    private void plotLines(Graphics2D g2d, List<Double> prices, Color color) {
        if (prices.isEmpty()) return;

        int width = getWidth() - 2 * PADDING;
        int height = getHeight() - 2 * PADDING;
        double maxPrice = prices.stream().mapToDouble(v -> v).max().orElse(1);

        g2d.setColor(color);
        for (int i = 1; i < prices.size(); i++) {
            int x1 = PADDING + (i - 1) * width / prices.size();
            int y1 = (int) ((1 - (prices.get(i - 1) / maxPrice)) * height) + PADDING;
            int x2 = PADDING + i * width / prices.size();
            int y2 = (int) ((1 - (prices.get(i) / maxPrice)) * height) + PADDING;
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawLegend(Graphics g2d, String[] labels, Color[] colours) {
        final int width = 50;
        final int height = 30;
        final int rowHeight = 50;
        final int x = 150;
        int y = 100;

        for (int i = 0; i < colours.length; i++) {
            g2d.setColor(colours[i]);
            g2d.fillRect(x, y, width, height);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, height);

            g2d.drawString(labels[i], x + 60, y + 20);
            y += rowHeight;
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Stock Performance");
        StockChart panel = new StockChart();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
