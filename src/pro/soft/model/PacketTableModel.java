package pro.soft.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class PacketTableModel {

    private ObservableList<PacketColumnModel> tableList = FXCollections.observableArrayList();

    public ObservableList<PacketColumnModel> getTableList() {
        return tableList;
    }

    public void setTableList(ObservableList<PacketColumnModel> tableList) {
//        this.tableList = tableList;
        this.tableList.clear();
        for (int i=0;i<tableList.size();i++){
            this.tableList.add(tableList.get(i));
        }
    }

    public void setTableList(ArrayList<PacketColumnModel> tableList) {
        this.tableList.clear();
        for (int i=0;i<tableList.size();i++){
            this.tableList.add(tableList.get(i));
        }
    }
}
