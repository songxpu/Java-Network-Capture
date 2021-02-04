package pro.soft.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class BarChartModel {
    public static int HttpNum=0;
    public static int TcpNum=0;
    public static int UdpNum=0;
    public static int IpNum=0;
    public static int IcmpNum=0;
    public static int ArpNum=0;
    public static int OtherNum=0;


    public ObservableList<String> BarChartList = FXCollections.observableArrayList();

    public ObservableList<String> getBarChartList() {
        return BarChartList;
    }

    public void setBarChartList(ObservableList<String> barChartList) {
        BarChartList = barChartList;
    }

    public ArrayList<String> getList(){
        ArrayList<String> list = new ArrayList<>();
        list.add(String.valueOf(TcpNum));
        list.add(String.valueOf(UdpNum));
        list.add(String.valueOf(IpNum));
        list.add(String.valueOf(IcmpNum));
        list.add(String.valueOf(ArpNum));
        list.add(String.valueOf(OtherNum));
        return list;
    }
}
