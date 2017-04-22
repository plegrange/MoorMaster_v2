package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FuBaR on 4/11/2017.
 */
public class FileManager {
    private File selectedDirectory;
    public List<Ship> shipFiles, temp;
    private List<File> allFiles;
    public List<Month> months, filtered;

    public FileManager(File directory) {
        filtered = new ArrayList<>();
        selectedDirectory = directory;
        shipFiles = new ArrayList<>();
        for (File file : selectedDirectory.listFiles()) {

            //filter files
            readFile(file);
        }
        temp = cloneList(shipFiles);
        groupShipsByMonth();
        shipFiles = cloneList(temp);
        filtered = months;
        temp = new ArrayList<>();
        /*for (ShipFile shipFile : shipFiles) {
            shipFile.display();
        }*/
    }

    private List<Ship> cloneList(List<Ship> oldList) {
        List<Ship> newList = new ArrayList<>();
        for (Ship shipFile : oldList) {
            newList.add(shipFile);
        }
        return newList;
    }

    private void readFile(File file) {
        String shipName = "", dateMoored = "", duration = "";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String[] lineElements;
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(":");
                if (lineElements[0].startsWith("Name")) {
                    shipName = lineElements[1];
                    break;
                }
            }
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(":");
                if (lineElements[0].startsWith("Moored")) {
                    dateMoored = lineElements[1];
                    break;
                }
            }
            while ((line = bufferedReader.readLine()) != null) {
                lineElements = line.split(":");
                if (lineElements[0].startsWith("Mooring Time")) {
                    duration = lineElements[1];
                    break;
                }
            }
            fileReader.close();
            //System.out.println(shipName + "|" + dateMoored + "|" + duration);
            shipFiles.add(new Ship(shipName, dateMoored, duration));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void groupShipsByMonth() {
        months = new ArrayList<>();
        boolean[] indexes;
        Month newMonth;
        while (shipFiles.size() > 0) {
            Ship ship = shipFiles.get(0);
            newMonth = new Month(ship.month, ship.year);
            newMonth.addShip(ship);
            indexes = new boolean[shipFiles.size()];
            for (int i = 1; i < shipFiles.size(); i++) {

                if (ship.equals(shipFiles.get(i))) {
                    indexes[i] = true;
                } else indexes[i] = false;
            }
            for (int i = shipFiles.size() - 1; i > 0; i--) {
                if (indexes[i]) {
                    newMonth.addShip(shipFiles.remove(i));
                }
            }
            shipFiles.remove(0);
            months.add(newMonth);
        }
    }

    public ObservableList<Month> getObservableList() {
        return FXCollections.observableArrayList(months);
    }


    public List<Month> getMonths() {
        return months;
    }

    public void filterObservableList(int startYear, int endYear, String startMonth, String endMonth) {
        filtered = new ArrayList<>();
        for (Month month : months) {
            if (Integer.valueOf(month.getYear()) >= startYear && Integer.valueOf(month.getYear()) <= endYear) {
                filtered.add(month);
            }
        }
        boolean[] indexes = new boolean[months.size()];
        for (int i = 0; i < filtered.size(); i++) {
            Month month = filtered.get(i);
            if (Integer.valueOf(month.getYear()) < startYear || Integer.valueOf(month.getYear()) > endYear) {
                indexes[i] = true;
            } else indexes[i] = false;
        }
        for (int i = filtered.size() - 1; i >= 0; i--) {
            if (indexes[i]) {
                filtered.remove(i);
            } else {
                filterMonths(filtered.get(i), startMonth, endMonth);
            }
        }

    }

    String[] monthIndex = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public void filterMonths(Month month, String startMonth, String endMonth) {
        int start = 0, end = 11;
        for (int i = 0; i < monthIndex.length; i++) {
            if (startMonth.equals(monthIndex[i]))
                start = i;
            if (endMonth.equals(monthIndex[i]))
                end = i;
        }
        int m = 0;
        for (int i = 0; i < monthIndex.length; i++) {
            if (monthIndex[i].equals(month.getMonth())) {
                m = i;
                break;
            }
        }
        if (m < start || m > end) {
            filtered.remove(month);
        }
    }

    public ObservableList<Month> getObservableFiltered() {
        return FXCollections.observableArrayList(filtered);
    }

}
