package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class Controller {
    private ComboBox<String> endMonthPicker, endYearPicker, startMonthPicker, startYearPicker;
    private Button generateButton, directoryButton, settingsButton, checkButton;
    File selectedDirectory;
    private Label directoryLabel, feedback;
    FileManager fileManager;
    Parent password = null, rate = null;
    Scene passwordScene, rateScene;
    private TextField winterTxt, summerTxt;
    private String winterRate = "", summerRate = "";
    TableView tableView;
    TableColumn yearCol, monthCol, comboCol, TPTCol, AMSCol, comboCostCol, TPTCostCol, AMSCostCol, maxDemandCol, PoNCostCol;
    String url;
    Hyperlink hyperlink;

    private String calculate(double rate, String kWh) {
        double val = rate * Double.valueOf(kWh);
        val = Math.round(val * 100.0) / 100.0;
        return String.valueOf(val);
    }

    private String calculatePoN(String combo, String tpt, String ams) {
        double val = Double.valueOf(combo) - (Double.valueOf(tpt) + Double.valueOf(ams));
        val = Math.round(val * 100.0) / 100.0;
        return String.valueOf(val);
    }


    private String calckWh(double rate, String cost) {
        double val = Double.valueOf(cost) / rate;
        val = Math.round(val * 100.0) / 100.0;
        return String.valueOf(val);
    }

    public void connectToUI(Scene scene, Stage primaryStage) {
        feedback = (Label) scene.lookup("#feedback");
        tableView = (TableView) scene.lookup("#table");
        tableView.setEditable(true);
        ObservableList<TableColumn> columns = tableView.getColumns();
        columns.get(0).setCellValueFactory(new PropertyValueFactory<Month, String>("year"));

        columns.get(1).setCellValueFactory(new PropertyValueFactory<Month, String>("month"));
        ObservableList<TableColumn> kWhCols = columns.get(2).getColumns();
        ObservableList<TableColumn> costCols = columns.get(3).getColumns();
        kWhCols.get(0).setCellValueFactory(new PropertyValueFactory<Month, String>("combokWh"));
        kWhCols.get(0).setCellFactory(TextFieldTableCell.forTableColumn());
        kWhCols.get(0).setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                //assign combokWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setCombokWh(String.valueOf(event.getNewValue()));

                //get combokWh and max
                String month = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMonth();
                String kwh = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCombokWh();
                String max = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMaxDemand();

                //set comboCost including maxDemand
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setComboCost(calculate(getRate(month), kwh), max);

                //get values to calculate PoN cost
                String combo = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getComboCost();
                String tpt = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getTPTCost();
                String ams = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getAMSCost();

                //calculate and assign PoNCost and kWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNCost(calculatePoN(combo, tpt, ams));
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNkWh(calckWh(getRate(month),
                        ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPoNCost()));
                columns.get(0).setVisible(false);
                columns.get(0).setVisible(true);
            }
        });
        kWhCols.get(1).setCellFactory(TextFieldTableCell.forTableColumn());
        kWhCols.get(1).setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                //assign TPTkWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setTPTkWh(String.valueOf(event.getNewValue()));

                //calculate and assign TPTCost
                String month = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMonth();
                String kwh = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getTPTkWh();
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setTPTCost(calculate(getRate(month), kwh));

                //recalculate comboCost
                String combokWh = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCombokWh();
                String max = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMaxDemand();
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setComboCost(calculate(getRate(month), combokWh), max);

                //get values to calculate PoN
                String combo = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getComboCost();
                String tpt = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getTPTCost();
                String ams = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getAMSCost();

                //calculate and assign PoNCost and kWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNCost(calculatePoN(combo, tpt, ams));
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNkWh(calckWh(getRate(month),
                        ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPoNCost()));
                columns.get(0).setVisible(false);
                columns.get(0).setVisible(true);
            }
        });
        kWhCols.get(2).setCellFactory(TextFieldTableCell.forTableColumn());
        kWhCols.get(2).setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                //assign AMSkWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setAMSkWh(String.valueOf(event.getNewValue()));

                //calculate and assign AMSCost
                String month = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMonth();
                String kwh = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getAMSkWh();
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setAMSCost(calculate(getRate(month), kwh));

                //recalculate comboCost
                String combokWh = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCombokWh();
                String max = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMaxDemand();
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setComboCost(calculate(getRate(month), combokWh), max);

                //get values to calculate PoN
                String tpt = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getTPTCost();
                String ams = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getAMSCost();
                String combo = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getComboCost();

                //calculate and assign PoNCost and kWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNCost(calculatePoN(combo, tpt, ams));
                //String month = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMonth();
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNkWh(calckWh(getRate(month),
                        ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPoNCost()));
                columns.get(0).setVisible(false);
                columns.get(0).setVisible(true);
            }
        });
        kWhCols.get(1).setCellValueFactory(new PropertyValueFactory<Month, String>("TPTkWh"));
        kWhCols.get(2).setCellValueFactory(new PropertyValueFactory<Month, String>("AMSkWh"));

        costCols.get(0).setCellValueFactory(new PropertyValueFactory<Month, String>("comboCost"));
        costCols.get(1).setCellValueFactory(new PropertyValueFactory<Month, String>("TPTCost"));
        costCols.get(2).setCellValueFactory(new PropertyValueFactory<Month, String>("AMSCost"));

        columns.get(4).setCellFactory(TextFieldTableCell.forTableColumn());
        columns.get(4).setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
            @Override
            public void handle(TableColumn.CellEditEvent event) {
                //assign maxDemand
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setMaxDemand(String.valueOf(event.getNewValue()));

                //calculate and assign comboCost
                String month = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMonth();
                String kwh = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getCombokWh();
                String max = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getMaxDemand();
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setComboCost(calculate(getRate(month), kwh), max);
                
                // get values to calculate PoN
                String combo = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getComboCost();
                String tpt = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getTPTCost();
                String ams = ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getAMSCost();

                //calculate and assign PoNCost and kWh
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNCost(calculatePoN(combo, tpt, ams));
                ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).setPoNkWh(calckWh(getRate(month),
                        ((Month) event.getTableView().getItems().get(event.getTablePosition().getRow())).getPoNCost()));
                columns.get(0).setVisible(false);
                columns.get(0).setVisible(true);
            }
        });
        columns.get(4).setCellValueFactory(new PropertyValueFactory<Month, String>("maxDemand"));
        columns.get(5).setCellValueFactory(new PropertyValueFactory<Month, String>("PoNCost"));


        directoryLabel = (Label) scene.lookup("#directoryLbl");
        directoryButton = (Button) scene.lookup("#directoryBtn");
        startYearPicker = (ComboBox) scene.lookup("#startYearPicker");

        startMonthPicker = (ComboBox) scene.lookup("#startMonthPicker");

        endYearPicker = (ComboBox) scene.lookup("#endYearPicker");

        endMonthPicker = (ComboBox) scene.lookup("#endMonthPicker");

        generateButton = (Button) scene.lookup("#generateBtn");
        settingsButton = (Button) scene.lookup("#settingsBtn");
        checkButton = (Button) scene.lookup("#checkBtn");
        try {
            password = FXMLLoader.load(getClass().getResource("passwordScreen.fxml"));
            rate = FXMLLoader.load(getClass().getResource("editRates.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        passwordScene = new Scene(password, 300, 100);
        rateScene = new Scene(rate, 300, 100);
        PasswordField passwordTxt = (PasswordField) passwordScene.lookup("#passwordTxt");
        Button cancelBtn = (Button) passwordScene.lookup("#cancelBtn");
        Button acceptBtn = (Button) passwordScene.lookup("#acceptBtn");
        Button cancelEditBtn = (Button) rateScene.lookup("#cancelBtn");
        Button saveBtn = (Button) rateScene.lookup("#saveBtn");
        winterTxt = (TextField) rateScene.lookup("#winterTxt");
        summerTxt = (TextField) rateScene.lookup("#summerTxt");
        buildComboBoxes();
        try {
            readRates("rates.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // filtered = false;
                selectedDirectory = directoryChooser.showDialog(primaryStage);
                directoryLabel.setText(selectedDirectory.getAbsolutePath());
                fileManager = new FileManager(selectedDirectory);
                tableView.setItems(fileManager.getObservableList());
                columns.get(0).setVisible(false);
                columns.get(0).setVisible(true);

            }
        });
        generateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (checkFeedback())
                    try {
                        generateReport();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BiffException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
            }
        });
        checkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                filter();

            }
        });
        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage secondaryStage = new Stage();
                secondaryStage.setScene(passwordScene);
                secondaryStage.setTitle("Administrator Password");
                secondaryStage.show();
                cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        secondaryStage.close();
                    }
                });

                passwordTxt.setText("");
                acceptBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (passwordTxt.getText().equals("cavotecP262")) {
                            secondaryStage.setTitle("Rate Editor");
                            winterTxt.setText(winterRate);
                            summerTxt.setText(summerRate);
                            secondaryStage.setScene(rateScene);
                            secondaryStage.show();
                        }
                    }
                });
                cancelEditBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        secondaryStage.close();
                    }
                });
                saveBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        winterRate = winterTxt.getText();
                        summerRate = summerTxt.getText();
                        try {
                            saveFile("rates.txt", encrypt(Double.valueOf(winterRate), Double.valueOf(summerRate)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        secondaryStage.close();
                    }
                });

            }
        });

        hyperlink = (Hyperlink) scene.lookup("#link");
        hyperlink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                url = hyperlink.getText();
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void readRates(String fileName) throws Exception {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line == "") {
                winterRate = "139.32";
                summerRate = "74.2";
            } else {
                decrypt(line);
            }
            winterTxt.setText(winterRate);
            summerTxt.setText(summerRate);
        }
    }

    private String encrypt(double winter, double summer) throws Exception {
        return (Math.pow(winter, 2) + 5.5) + ":" + (Math.pow(summer, 2) + 17.5);
    }

    private void decrypt(String str) throws Exception {
        String[] elements = str.split(":");
        String winterStr = elements[0];
        String summerStr = elements[1];
        winterRate = String.valueOf(Math.sqrt(Double.valueOf(winterStr) - 5.5));
        summerRate = String.valueOf(Math.sqrt(Double.valueOf(summerStr) - 17.5));
    }

    private void saveFile(String fileName, String data) throws IOException {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {

            //String content = "winter=" + winterRate + "\n" + "summer=" + summerRate;

            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            bw.write(data);

            //System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }

    private double getRate(String month) {
        switch (month) {
            case "May":
            case "June":
            case "July":
            case "August":
                return Double.valueOf(winterRate) / 100.0;
            default:
                return Double.valueOf(summerRate) / 100.0;
        }
    }

    private void buildComboBoxes() {
        ObservableList<String> monthIndex =
                FXCollections.observableArrayList(
                        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        startMonthPicker.setItems(monthIndex);
        endMonthPicker.setItems(monthIndex);
        ObservableList<String> year = FXCollections.observableArrayList();
        for (int i = 2017; i < 2050; i++) {
            year.add(String.valueOf(i));
        }
        startYearPicker.setItems(year);
        endYearPicker.setItems(year);
    }

    OutputManager outputManager;
    ChartsManager chartsManager;

    private void generateReport() throws IOException, BiffException, WriteException {
        outputManager = new OutputManager();
        outputManager.write(fileManager.shipFiles, fileManager.filtered);
        chartsManager = new ChartsManager();
        try {
            chartsManager.printCharts(outputManager.table, fileManager.filtered.size(), 11);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filter() {
        if (startMonthPicker.getValue() == null || endMonthPicker.getValue() == null)
            return;
        if (startYearPicker.getValue() == null || endYearPicker.getValue() == null)
            return;
        int startYear = Integer.valueOf(startYearPicker.getValue());
        int endYear = Integer.valueOf(endYearPicker.getValue());
        String startMonth = startMonthPicker.getValue();
        String endMonth = endMonthPicker.getValue();
        fileManager.filterObservableList(startYear, endYear, startMonth, endMonth);
        tableView.setItems(fileManager.getObservableFiltered());
        filtered = true;
    }

    private boolean checkFeedback() {
        if (selectedDirectory == null) {
            feedback.setText("Select directory!");
            feedback.setTextFill(Color.RED);
            return false;
        }
        if (!filtered) {
            feedback.setText("Select and filter dates!");
            feedback.setTextFill(Color.RED);
            return false;
        }
        feedback.setText("Report generated!");
        feedback.setTextFill(Color.GREEN);
        return true;
    }

    private boolean filtered = false;
}
