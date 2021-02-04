package pro.soft.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jnetpcap.packet.PcapPacket;
import pro.soft.model.BarChartModel;
import pro.soft.model.CurPcapLabelModel;
import pro.soft.model.PacketColumnModel;
import pro.soft.model.PacketTableModel;
import pro.soft.service.BarChartService;
import pro.soft.service.JnetpCap;
import pro.soft.service.PacketCaptureService;
import pro.soft.util.PacketProcess;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;


public class MainLayoutController implements Initializable {
    //右上角实时更新信息Label
    @FXML
    private Label nowpcapLabel;
    //模型层
    public static CurPcapLabelModel cplmodel = new CurPcapLabelModel();
    //表格TableView，抓包时实时显示
    @FXML
    private TableView mainTable;
    //表格各字段
    @FXML
    private TableColumn table_id;
    @FXML
    private TableColumn table_time;
    @FXML
    private TableColumn table_length;
    @FXML
    private TableColumn table_protocol;
    @FXML
    private TableColumn table_srcmac;
    @FXML
    private TableColumn table_desmac;
    @FXML
    private TableColumn table_srcip;
    @FXML
    private TableColumn table_desip;
    @FXML
    private TableColumn table_srcport;
    @FXML
    private TableColumn table_desport;
    @FXML
    private TextArea text_packetInfo;//点击指定数据包，显示其报文内容
    @FXML
    public BarChart barChart;
    //统计表Service层
//    private static BarChartService barChartService = new BarChartService();

    @FXML
    private TreeView info_tree;

    //模型层：UI中的表格对应的数据
//    private PacketColumnModel packetColumnModel;
    private static PacketTableModel packetTableModel=new PacketTableModel();
    //Service层
    private static PacketCaptureService packetCaptureService=new PacketCaptureService(packetTableModel);
    private static BarChartModel barChartModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nowpcapLabel.textProperty().bindBidirectional(cplmodel.stringProperty());//绑定label和值
        cplmodel.setString("当前未选择监听网卡");
//        updateShow();//该线程用于右上角实时更新

        mainTable.setEditable(false);
        configTable();//配置表格各段的数据源
        mainTable.setItems(packetTableModel.getTableList());
        mainTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                PacketColumnModel model = (PacketColumnModel) newValue;
                ArrayList<PcapPacket> packetsInfoList = PacketCaptureService.getPacketsInfoList();
                text_packetInfo.setText(packetsInfoList.get(model.getId()).toString());
                //更新协议树
                TreeItem item = configTreeView(packetsInfoList.get(model.getId()));
                info_tree.setRoot(item);
            }
        });
        //统计表
        configBarChart();
//        updateBarChart();


    }

    //该方法用于左上角显示当前网卡的label实时更新
//    private void updateShow(){
//        new Thread(() -> {
//            while (true) {
//                if (JnetpCap.getInstance().getCurrent()!=null){
//                    try {
//                        Platform.runLater(() -> cplmodel.setString("当前监听网卡:"+JnetpCap.getInstance().getCurrent().getDescription()));
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    Platform.runLater(() -> cplmodel.setString("当前未监听网卡"));
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }

    //配置PacketColumnModel和表格各列的关系
    private void configTable(){
        table_id.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,Integer>("id"));
        table_time.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("time"));
        table_length.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("length"));
        table_protocol.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("protocol"));
        table_srcmac.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("srcMac"));
        table_desmac.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("desMac"));
        table_srcip.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("srcIp"));
        table_desip.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("desIp"));
        table_srcport.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("srcport"));
        table_desport.setCellValueFactory(new PropertyValueFactory<PacketColumnModel,String>("desport"));
    }
    //配置统计表的基本信息
    private void configBarChart(){
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("数量(个)");
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data("http",BarChartModel.HttpNum));
        series1.getData().add(new XYChart.Data("tcp",BarChartModel.TcpNum));
        series1.getData().add(new XYChart.Data("udp",BarChartModel.UdpNum));
        series1.getData().add(new XYChart.Data("icmp",BarChartModel.IcmpNum));
        series1.getData().add(new XYChart.Data("arp",BarChartModel.ArpNum));
        series1.getData().add(new XYChart.Data("others",BarChartModel.OtherNum));
        barChart.getData().addAll(series1);
    }
    //更新统计表
    public void updateBarChart() {
        final  String http = "http";
        final  String tcp = "tcp";
        final  String udp = "udp";
        final  String icmp = "icmp";
        final  String arp = "arp";
        final String others = "others";
        barChart.getData().clear();
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        series1.getData().add(new XYChart.Data(http,BarChartModel.HttpNum));
        series1.getData().add(new XYChart.Data(tcp,BarChartModel.TcpNum));
        series1.getData().add(new XYChart.Data(udp,BarChartModel.UdpNum));
        series1.getData().add(new XYChart.Data(icmp,BarChartModel.IcmpNum));
        series1.getData().add(new XYChart.Data(arp,BarChartModel.ArpNum));
        series1.getData().add(new XYChart.Data(others,BarChartModel.OtherNum));
        barChart.getData().addAll(series1);
    }

    //配置协议树
    private TreeItem configTreeView(PcapPacket pcapPacket){
        TreeItem rootItem = new TreeItem<>("协议结构");
        rootItem.setExpanded(true);

        TreeItem treeItem = new TreeItem<>("以太网链路层");
        treeItem.setExpanded(true);
        //
        TreeItem item_srcmac = new TreeItem<>("源网卡地址");
        item_srcmac.getChildren().add(new TreeItem<>(PacketProcess.getSrcMac(pcapPacket)));
        item_srcmac.setExpanded(true);
        //desc
        TreeItem item_descmac = new TreeItem<>("目的网卡地址");
        item_descmac.getChildren().add(new TreeItem<>(PacketProcess.getDesMac(pcapPacket)));
        item_descmac.setExpanded(true);
        treeItem.getChildren().add(item_srcmac);
        treeItem.getChildren().add(item_descmac);


        TreeItem treeItem2 = new TreeItem<>("网络层");
        treeItem2.setExpanded(true);
        //
        TreeItem item_srcip = new TreeItem<>("源IP地址");
        item_srcip.getChildren().add(new TreeItem<>(PacketProcess.getSrcIp(pcapPacket)));
        item_srcip.setExpanded(true);
        //desc
        TreeItem item_descip = new TreeItem<>("目的IP地址");
        item_descip.getChildren().add(new TreeItem<>(PacketProcess.getDestIp(pcapPacket)));
        item_descip.setExpanded(true);
        TreeItem item_type = new TreeItem<>("协议类型");
        item_type.getChildren().add(new TreeItem<>(PacketProcess.getProtocol(pcapPacket)));
        item_type.setExpanded(true);

        treeItem2.getChildren().add(item_type);
        treeItem2.getChildren().add(item_srcip);
        treeItem2.getChildren().add(item_descip);

        TreeItem treeItem3 = new TreeItem<>("传输层");
        treeItem3.setExpanded(true);
        //
        TreeItem item_proto = new TreeItem<>("协议");
        item_proto.getChildren().add(new TreeItem<>(PacketProcess.getProtocol(pcapPacket)));
        item_proto.setExpanded(true);
        //
        TreeItem item_srcport = new TreeItem<>("源端口");
        item_srcport.getChildren().add(new TreeItem<>(PacketProcess.getSrcPort(pcapPacket)));
        item_srcport.setExpanded(true);
        //desc
        TreeItem item_descport = new TreeItem<>("目的端口");
        item_descport.getChildren().add(new TreeItem<>(PacketProcess.getDesPort(pcapPacket)));
        item_descport.setExpanded(true);

        treeItem3.getChildren().add(item_proto);
        treeItem3.getChildren().add(item_srcport);
        treeItem3.getChildren().add(item_descport);

        TreeItem treeItem4 = new TreeItem<>("应用层");
        treeItem4.setExpanded(true);
        //
//        TreeItem item_proto = new TreeItem<>("协议");
//        item_proto.getChildren().add(new TreeItem<>(PacketProcess.getProtocol(pcapPacket)));
//        item_proto.setExpanded(true);

        rootItem.getChildren().add(treeItem);
        rootItem.getChildren().add(treeItem2);
        rootItem.getChildren().add(treeItem3);
        rootItem.getChildren().add(treeItem4);
        return rootItem;
    }




}
