package sample;

import com.smartxls.WorkBook;

import java.awt.*;
import java.io.File;
import java.util.Random;

/**
 * Created by FuBaR on 4/11/2017.
 */
public class ChartsManager {


    public void printCharts(String[][] table, int width, int height) throws Exception {
        WorkBook workbook = new WorkBook();
        try {
            workbook.readXLSX("template.xlsx");
            for (int i = 0; i < width; i++) {
                workbook.setText(0, 0, i + 1, String.valueOf(table[i][0]));
                for (int j = 1; j < height; j++) {
                    workbook.setFormula(0, j, i + 1, table[i][j]);
                }
            }
            int option = WorkBook.sheetProtectionAllowFormatCells | WorkBook.sheetProtectionAllowUseAutoFilter;
//set sheet protection options
            workbook.setSheetProtected(0, option, "cavotecP262");
            workbook.setSheetProtected(0, true, "cavotecP262");
//enable protection for worksheet
//in default protection option

            //chart.setLinkRange();
            /*int left = 1;
            int top = 7;
            int right = 13;
            int bottom = 31;
            String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "x", "y", "z"};
            //create chart with it's location
            ChartShape chart = workbook.addChart(left, top, right, bottom);
            chart.setChartType(ChartShape.Column);
            //link data source, link each series to columns(true to rows).
            chart.setLinkRange("Data!$b$2:$" + alphabet[width] + "$2", false);
            //set axis title
            chart.setAxisTitle(ChartShape.XAxis, 0, "X-axis data");
            chart.setAxisTitle(ChartShape.YAxis, 0, "Y-axis data");

            ChartFormat seriesformat;
            for (int i = 0; i < width; i++) {
                chart.setSeriesName(i, table[i][0]);
                seriesformat = chart.getSeriesFormat(i);
                seriesformat.setSolid();
                seriesformat.setForeColor(getRandomInt());
                //((getRandomInt(), getRandomInt(), getRandomInt()));
                chart.setSeriesFormat(i, seriesformat);
            }

            //set series name
            chart.setTitle("PoN");

            //set plot area's color to darkgray
            ChartFormat chartFormat = chart.getPlotFormat();
            chartFormat.setSolid();
            chartFormat.setForeColor(Color.DARK_GRAY.getRGB());
            chart.setPlotFormat(chartFormat);

            //set series 0's color to blue

            //set chart title's font property
            ChartFormat titleformat = chart.getTitleFormat();
            titleformat.setFontSize(14 * 20);
            titleformat.setFontUnderline(true);
            chart.setTitleFormat(titleformat);
            java.io.FileOutputStream out = new java.io.FileOutputStream("chart.png");
            chart.writeChartAsPNG(workbook, out);

            out.close();
            Desktop.getDesktop().open(new File("chart.png"));
            //m_workbook.write("./Chart.xls");*/
            //workbook.exportPDF("out.pdf", null, false);
            workbook.writeXLSX("./output.xlsx");

            Desktop.getDesktop().open(new File("output.xlsx"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Color[] colors = {Color.RED, Color.BLUE, Color.CYAN, Color.green, Color.YELLOW, Color.orange, Color.pink, Color.magenta, Color.BLACK, Color.WHITE};
    Random random = new Random();

    private int getRandomInt() {

        return 50 + random.nextInt(150);
    }

}
