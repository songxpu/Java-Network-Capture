package pro.soft.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import org.jnetpcap.packet.PcapPacket;
import pro.soft.model.BarChartModel;
import pro.soft.model.PacketColumnModel;
import pro.soft.service.PacketCaptureService;

import java.net.URL;
import java.util.*;

public class IPStatisticsLayoutController implements Initializable {
    private ArrayList<PcapPacket> packetsInfoList;
    private ArrayList<PacketColumnModel> packetColumnModelArrayList;

    private Stage IPStage;
    @FXML
    private BarChart IPChart;
    private List<Map.Entry<String,Integer>> list;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        获取程序当前的数据包列表信息
        packetColumnModelArrayList = PacketCaptureService.getPacketColumnModelArrayList();
        if (packetColumnModelArrayList.size()!=0){
            InitBarchart();
        }
    }
    public void setIPStage(Stage IPStage) {this.IPStage = IPStage;}

    public void sort(){
        HashMap<String,Integer> hm = new HashMap<>();
        for(PacketColumnModel model :packetColumnModelArrayList){
            String protocal = model.getProtocol();
            if (protocal.equals("TCP")||protocal.equals("UDP")||protocal.equals("IP4")){
                String dstIp = model.getDesIp();
                if (hm.containsKey(dstIp)){
                    int count = hm.get(dstIp);
                    hm.put(dstIp,count+1);
                }else{
                    hm.put(dstIp,1);
                }
            }
        }
//        自定义排序规则
        Comparator<Map.Entry<String,Integer>> desValue = new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int i1 = o1.getValue();
                int i2 = o2.getValue();
                if (i1!=i2){
                    return (i1>i2)?-1:1;
                }else{
                    return 0;
                }
            }
        };
        Set<Map.Entry<String,Integer>> set = hm.entrySet();
        list = new ArrayList<Map.Entry<String,Integer>>(set);
        Collections.sort(list,desValue);
    }
//    初始化表格
    public void InitBarchart(){
        sort();
        CategoryAxis xAxis = (CategoryAxis) IPChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) IPChart.getYAxis();
        yAxis.setLabel("数量(个)");
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        Iterator<Map.Entry<String,Integer>> iterator = list.iterator();
        if (list.size()>6){
            for (int n=0;n<6;n++){
                Map.Entry<String,Integer> map = iterator.next();
                series1.getData().add(new XYChart.Data<>(map.getKey(),map.getValue()));
            }
        }else{
            for (int n=0;n<list.size();n++){
                Map.Entry<String,Integer> map = iterator.next();
                series1.getData().add(new XYChart.Data<>(map.getKey(),map.getValue()));
            }
        }
        IPChart.getData().addAll(series1);
    }
//    更新统计表
    public void updateBarchart(){
        IPChart.getData().clear();
        sort();
        Iterator<Map.Entry<String,Integer>> iterator = list.iterator();
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        if (list.size()>6){
            for (int n=0;n<6;n++){
                Map.Entry<String,Integer> map = iterator.next();
                series1.getData().add(new XYChart.Data<>(map.getKey(),map.getValue()));
            }
        }else{
            for (int n=0;n<list.size();n++){
                Map.Entry<String,Integer> map = iterator.next();
                series1.getData().add(new XYChart.Data<>(map.getKey(),map.getValue()));
            }
        }
        IPChart.getData().addAll(series1);
    }
//    关闭弹框
    public void closeChart(){
        this.IPStage.close();
    }
}
