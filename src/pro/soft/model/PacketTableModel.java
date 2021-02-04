package pro.soft.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PacketTableModel {

    private ObservableList<PacketColumnModel> tableList = FXCollections.observableArrayList();

    public ObservableList<PacketColumnModel> getTableList() {
        return tableList;
    }

    public void setTableList(ObservableList<PacketColumnModel> tableList) {
        this.tableList = tableList;
    }
}
