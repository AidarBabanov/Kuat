package kz.kuat.kuat;

public class ListItem {

    private CustomChart customChart;
    private int fieldNum;

    public ListItem(int fieldNum) {
        this.fieldNum = fieldNum;
    }

    public int getFieldNum() {
        return fieldNum;
    }

    public CustomChart getCustomChart() {
        return customChart;
    }

    public void setCustomChart(CustomChart customChart) {
        this.customChart = customChart;
    }
}