package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FuBaR on 4/11/2017.
 */
public class Month {
    private SimpleStringProperty month, year, combokWh, TPTkWh, AMSkWh, comboCost, TPTCost, AMSCost, maxDemand, PoNCost, PoNkWh, berthMonthTotal;
    public List<Ship> ships;

    public Month(String month, String year) {
        this.month = new SimpleStringProperty(month);
        this.year = new SimpleStringProperty(year);
        this.combokWh = new SimpleStringProperty("0.0");
        this.TPTkWh = new SimpleStringProperty("0.0");

        this.AMSkWh = new SimpleStringProperty("0.0");
        this.comboCost = new SimpleStringProperty("0.0");
        this.TPTCost = new SimpleStringProperty("0.0");
        this.AMSCost = new SimpleStringProperty("0.0");
        this.maxDemand = new SimpleStringProperty("0.0");
        this.PoNCost = new SimpleStringProperty("0.0");
        this.berthMonthTotal = new SimpleStringProperty("0.0");
        this.PoNkWh = new SimpleStringProperty("0.0");
        ships = new ArrayList<>();
    }

    public void addShip(Ship newShip) {
        ships.add(newShip);
    }

    public String getCombokWh() {
        return combokWh.get();
    }

    public StringProperty combokWhProperty() {
        return combokWh;
    }

    public void setCombokWh(String combokWh) {
        this.combokWh.set(combokWh);
    }

    public String getTPTkWh() {
        return TPTkWh.get();
    }

    public StringProperty TPTkWhProperty() {
        return TPTkWh;
    }

    public void setTPTkWh(String TPTkWh) {
        this.TPTkWh.set(TPTkWh);
    }

    public String getAMSkWh() {
        return AMSkWh.get();
    }

    public StringProperty AMSkWhProperty() {
        return AMSkWh;
    }

    public void setAMSkWh(String AMSkWh) {
        this.AMSkWh.set(AMSkWh);
    }

    public String getComboCost() {
        return comboCost.get();
    }

    public StringProperty comboCostProperty() {
        return comboCost;
    }

    public void setComboCost(String comboCost, String max) {
        double val = Double.valueOf(comboCost) + Double.valueOf(max);
        this.comboCost.set(String.valueOf(val));
    }

    public String getTPTCost() {
        return TPTCost.get();
    }

    public StringProperty TPTCostProperty() {
        return TPTCost;
    }

    public void setTPTCost(String TPTCost) {
        this.TPTCost.set(TPTCost);
    }

    public String getAMSCost() {
        return AMSCost.get();
    }

    public StringProperty AMSCostProperty() {
        return AMSCost;
    }

    public void setAMSCost(String AMSCost) {
        this.AMSCost.set(AMSCost);
    }

    public String getMaxDemand() {
        return maxDemand.get();
    }

    public StringProperty maxDemandProperty() {
        return maxDemand;
    }

    public void setMaxDemand(String maxDemand) {
        this.maxDemand.set(maxDemand);
    }

    public String getPoNCost() {
        return PoNCost.get();
    }

    public StringProperty poNCostProperty() {
        return PoNCost;
    }

    public void setPoNCost(String poNCost) {
        this.PoNCost.set(poNCost);
    }

    public String getMonth() {
        return month.get();
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public String getYear() {
        return year.get();
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public String getBerthMonthTotal() {
        return berthMonthTotal.get();
    }

    public SimpleStringProperty berthMonthTotalProperty() {
        return berthMonthTotal;
    }

    public void setBerthMonthTotal(String berthMonthTotal) {
        this.berthMonthTotal.set(berthMonthTotal);
    }

    public String getPoNkWh() {
        return PoNkWh.get();
    }

    public SimpleStringProperty poNkWhProperty() {
        return PoNkWh;
    }

    public void setPoNkWh(String poNkWh) {
        this.PoNkWh.set(poNkWh);
    }
}
