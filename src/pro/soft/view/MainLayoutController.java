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
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import pro.soft.model.BarChartModel;
import pro.soft.model.CurPcapLabelModel;
import pro.soft.model.PacketColumnModel;
import pro.soft.model.PacketTableModel;
import pro.soft.service.BarChartService;
import pro.soft.service.JnetpCap;
import pro.soft.service.PacketCaptureService;
import pro.soft.service.TreeLabelConfig;
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
//    "开始过滤"按钮
    @FXML
    private Button Start_filter;
    @FXML
    private TextField filter_text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nowpcapLabel.textProperty().bindBidirectional(cplmodel.stringProperty());//绑定label和值
        cplmodel.setString("当前未选择监听网卡");
//        updateShow();//该线程用于右上角实时更新

        mainTable.setEditable(false);
        configTable();//配置表格各段的数据源
        mainTable.setItems(packetTableModel.getTableList());
        //主页column栏目点击事件
        mainTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                PacketColumnModel model = (PacketColumnModel) newValue;
                ArrayList<PcapPacket> packetsInfoList = PacketCaptureService.getPacketsInfoList();
                StringBuffer textContens = new StringBuffer("【数据包总览】\n"+packetsInfoList.get(model.getId()).toString()+"\n\n");
                if (PacketProcess.getProtocol(packetsInfoList.get(model.getId())).equals("TCP")){//TCP
                    textContens.append(new StringBuffer("【TCP数据包】\n"+PacketProcess.getTcpContents(packetsInfoList.get(model.getId()))+"\n\n"));
                }else if (PacketProcess.getProtocol(packetsInfoList.get(model.getId())).equals("HTTP")){//HTTP
                    textContens.append(new StringBuffer("【TCP数据包】\n"+PacketProcess.getTcpContents(packetsInfoList.get(model.getId()))+"\n\n"));
                    textContens.append(new StringBuffer("【HTTP数据包】\n"+PacketProcess.getHttpContens(packetsInfoList.get(model.getId()))+"\n\n"));
                } else if (PacketProcess.getProtocol(packetsInfoList.get(model.getId())).equals("UDP")){//UDP
                    textContens.append(new StringBuffer("【UDP数据包】\n"+PacketProcess.getUdpContens(packetsInfoList.get(model.getId()))+"\n\n"));
                }
                text_packetInfo.setText(new String(textContens));
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
    //DONE:需细分显示的情况。类似于ARP协议，仅是网络层协议，不应该显示传输层和应用层。
    private TreeItem configTreeView(PcapPacket pcapPacket){
        return TreeLabelConfig.InitTreeView(pcapPacket);
    }
    @FXML
    private void handleFilter(){
        System.out.println("开始过滤");
//        System.out.println(filter_text.getText()); //获取textFiled的值
        packetCaptureService.handleFilter(filter_text.getText());

    }
    //重置主页数据包显示
    @FXML
    private void handleReset(){
        packetCaptureService.handleReset();
    }

    //重置info_tree
    public void clearInfo_tree(){
        info_tree.setRoot(null);
    }
    //重置text_packetInfo
    public void clearText_packetInfo(){
        text_packetInfo.clear();
    }


}
