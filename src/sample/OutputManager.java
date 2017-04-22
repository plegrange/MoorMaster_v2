package sample;

import jxl.Cell;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Number;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * Created by FuBaR on 4/11/2017.
 */
public class OutputManager {

    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String outputDataFile;
    private List<Month> months;

    public OutputManager() {
    }

    public void write(List<Ship> ships, List<Month> months) {
        this.months = months;
        this.outputDataFile = months.get(0).getMonth() + months.get(0).getYear() + "-" + months.get(months.size() - 1).getMonth() + months.get(months.size() - 1).getYear() + ".xls";
        File file = new File(outputDataFile);
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
            workbook.createSheet("SCADA", 0);
            WritableSheet excelSheet = workbook.getSheet(0);
            workbook.createSheet("Months", 1);
            WritableSheet monthSheet = workbook.getSheet(1);
            createLabel(excelSheet);
            writeSCADASheet(ships, excelSheet);
            sheetAutoFitColumns(excelSheet);
            writeMonthSheet(months, monthSheet);
            sheetAutoFitColumns(monthSheet);
            workbook.write();
            workbook.setProtected(true);
            workbook.close();
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int rowIndex = 1, columnIndex = 0;
    private double accumulativeHours = 0;

    private void writeSCADASheet(List<Ship> list, WritableSheet sheet) throws WriteException {
        for (Ship shipFile : list) {
            addString(sheet, 0, rowIndex, shipFile.shipName);
            addString(sheet, 1, rowIndex, shipFile.getDateMoored());
            String temp = String.valueOf(shipFile.durationHours) + "h " + String.valueOf(shipFile.durationMinutes) + "m " + String.valueOf(shipFile.durationSeconds) + "s";
            addString(sheet, 2, rowIndex, temp);
            rowIndex++;
        }
        double temp = calculateTotalMonthHours(list);
        addNumber(sheet, 4, rowIndex, temp);
        addString(sheet, 3, rowIndex, hours + "h " + minutes + "m " + seconds + "s");
        // addNumber(sheet, 5, rowIndex, calculator.getMonthlyCost(list));
        accumulativeHours += temp;
        rowIndex++;
    }

    public String[][] table;

    private void writeMonthSheet(List<Month> months, WritableSheet sheet) throws WriteException {
        addLabel(sheet, 0, 0, "Month");
        addLabel(sheet, 0, 1, "Combo kWh");
        addLabel(sheet, 0, 2, "Combo Cost");
        addLabel(sheet, 0, 3, "TPT kWh");
        addLabel(sheet, 0, 4, "TPT Cost");
        addLabel(sheet, 0, 5, "AMS kWh");
        addLabel(sheet, 0, 6, "AMS Cost");
        addLabel(sheet, 0, 7, "Maximum Demand Charge");
        addLabel(sheet, 0, 8, "Berth Active Hours");
        addLabel(sheet, 0, 9, "PoN Cost");
        addLabel(sheet, 0, 10, "PoN kWh");
        table = new String[months.size()][11];
        for (int i = 0; i < months.size(); i++) {
            addLabel(sheet, i + 1, 0, months.get(i).getMonth() + "-" + months.get(i).getYear());
            table[i][0] = months.get(i).getMonth() + "-" + months.get(i).getYear();
            addNumber(sheet, i + 1, 1, Double.valueOf(months.get(i).getCombokWh()));
            table[i][1] = months.get(i).getCombokWh();
            addNumber(sheet, i + 1, 2, Double.valueOf(months.get(i).getComboCost()));
            table[i][2] = months.get(i).getComboCost();
            addNumber(sheet, i + 1, 3, Double.valueOf(months.get(i).getTPTkWh()));
            table[i][3] = months.get(i).getTPTkWh();
            addNumber(sheet, i + 1, 4, Double.valueOf(months.get(i).getTPTCost()));
            table[i][4] = months.get(i).getTPTCost();
            addNumber(sheet, i + 1, 5, Double.valueOf(months.get(i).getAMSkWh()));
            table[i][5] = months.get(i).getAMSkWh();
            addNumber(sheet, i + 1, 6, Double.valueOf(months.get(i).getAMSCost()));
            table[i][6] = months.get(i).getAMSCost();
            addNumber(sheet, i + 1, 7, Double.valueOf(months.get(i).getMaxDemand()));
            table[i][7] = months.get(i).getMaxDemand();


            Month temp = months.get(i);
            double berthActive = calculateTotalMonthHours(temp.ships);
            temp.setBerthMonthTotal(String.valueOf(berthActive));

            addNumber(sheet, i + 1, 8, Double.valueOf(months.get(i).getBerthMonthTotal()));
            table[i][8] = months.get(i).getBerthMonthTotal();
            addNumber(sheet, i + 1, 9, Double.valueOf(months.get(i).getPoNCost()));
            table[i][9] = months.get(i).getPoNCost();

            addNumber(sheet, i + 1, 10, Double.valueOf(months.get(i).getPoNkWh()));
            table[i][10] = months.get(i).getPoNkWh();
        }
    }

    int hours, minutes, seconds;

    private double calculateTotalMonthHours(List<Ship> list) {
        hours = 0;
        minutes = 0;
        seconds = 0;

        for (Ship shipFile : list)
            addShipToTotal(shipFile);

        minutes += seconds / 60;
        seconds = seconds % 60;
        hours += minutes / 60;
        minutes = minutes % 60;

        double secondsInMinutes = seconds * 1.0 / 60.0;
        double tempMinutes = minutes * 1.0 + secondsInMinutes;
        double minutesInHours = tempMinutes / 60.0;
        double tempHours = hours * 1.0 + minutesInHours;
        return tempHours;
    }

    private void addShipToTotal(Ship shipFile) {
        hours += shipFile.durationHours;
        minutes += shipFile.durationMinutes;
        seconds += shipFile.durationSeconds;
    }

    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(false);
        times.setLocked(true);

        // create create a bold font with underlines
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(false);
        timesBoldUnderline.setLocked(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers
        addLabel(sheet, 0, 0, "Ship Name");
        addLabel(sheet, 1, 0, "Date Moored");
        addLabel(sheet, 2, 0, "Duration");
        addLabel(sheet, 3, 0, "Total Hours");
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        jxl.write.Label label;
        label = new jxl.write.Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void sheetAutoFitColumns(WritableSheet sheet) {
        for (int i = 0; i < sheet.getColumns(); i++) {
            Cell[] cells = sheet.getColumn(i);
            int longestStrLen = -1;

            if (cells.length == 0)
                continue;

        /* Find the widest cell in the column. */
            for (int j = 0; j < cells.length; j++) {
                if (cells[j].getContents().length() > longestStrLen) {
                    String str = cells[j].getContents();
                    if (str == null || str.isEmpty())
                        continue;
                    longestStrLen = str.trim().length();
                }
            }

        /* If not found, skip the column. */
            if (longestStrLen == -1)
                continue;

        /* If wider than the max width, crop width */
            if (longestStrLen > 255)
                longestStrLen = 255;

            CellView cv = sheet.getColumnView(i);
            cv.setSize(longestStrLen * 256 + 100); /* Every character is 256 units wide, so scale it. */
            sheet.setColumnView(i, cv);
        }
    }

    private void addString(WritableSheet sheet, int column, int row,
                           String s) throws WriteException, RowsExceededException {
        jxl.write.Label label;
        label = new jxl.write.Label(column, row, s, times);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
                           double d) throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, d, times);
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        jxl.write.Label label;
        label = new jxl.write.Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }
}
