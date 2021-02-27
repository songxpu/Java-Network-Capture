package pro.soft.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import pro.soft.model.BarChartModel;
import pro.soft.model.PacketColumnModel;
import pro.soft.model.PacketTableModel;
import pro.soft.util.PacketProcess;
import pro.soft.util.TimeUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

/**
 * service层
 * 负责抓包
 */
public class PacketCaptureService {
    private static PacketTableModel model;
    private static ArrayList<PacketColumnModel> packetColumnModelArrayList= new ArrayList<>();

    private static int PacketIndex=0;
    private static StringBuilder errbuf = new StringBuilder(); // 获取错误信息

    public PacketCaptureService(PacketTableModel packetTableModel) {
        this.model = packetTableModel;
    }
    //用于停止线程中的循环：停止抓包
    public static boolean StopCapture=false;
    //存放完整的数据包信息
    public static ArrayList<PcapPacket> PacketsInfoList=new ArrayList<PcapPacket>();
    //统计各类型流量包的数量
    private BarChartModel barChartModel=new BarChartModel();


//    //向table中加入数据
//    public static void loadTableList() {
//        for (int i = 0; i < 2000; i++) {
//            model.getTableList().add(new PacketColumnModel(i,"2021-07-10","10","443","asfddsafasdf",
//                    "sadfdsafdsaf","192.192.1.1","255.255.255.255","10","20"));
//            model.getTableList().add(new PacketColumnModel(++i,"2021-07-10","99999","11111","asfddsafasdf",
//                    "555555555555555","192.192.1.1","255.255.255.255","10","20"));
//        }
//    }

    //开启子线程去抓包
    public static void capturePacket(){
        //TODO:开启多线程抓包，原始包需要清洗（MAC、IP...）后存入列表中[√]
        //TODO:开启了子线程去抓包，怎么关闭呢？
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "多线程嘻嘻嘻");
                PcapIf device = JnetpCap.getInstance().getCurrent();
                int snaplen = 64 * 1024;
                // Capture all packets, no trucation 不截断的捕获所有包
                int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
                int timeout = 10 * 1000;           // 10 seconds in millis
                Pcap pcap =
                        Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
                if (pcap == null) {  // 如果获取的pcap是null，则返回相关的错误信息
                    System.err.printf("Error while opening device for capture: "
                            + errbuf.toString());
                    return;
                }
//                PacketDump.getInstance().setPcap(pcap);
                JnetpCap.getInstance().setPcap(pcap);
                PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
                    public void nextPacket(PcapPacket packet, String user) {
                        PacketColumnModel columnModel = new PacketColumnModel();
                        columnModel.setId(PacketIndex++);
                        columnModel.setTime(TimeUtil.getLocalTime());
                        columnModel.setLength(String.valueOf(PacketProcess.getSize(packet)));
                        columnModel.setProtocol(PacketProcess.getProtocol(packet));
                        columnModel.setSrcMac(PacketProcess.getSrcMac(packet));
                        columnModel.setDesMac(PacketProcess.getDesMac(packet));
                        columnModel.setSrcIp(PacketProcess.getSrcIp(packet));
                        columnModel.setDesIp(PacketProcess.getDestIp(packet));
                        columnModel.setSrcport(String.valueOf(PacketProcess.getSrcPort(packet)));
                        columnModel.setDesport(String.valueOf(PacketProcess.getDesPort(packet)));
                        PacketsInfoList.add(packet);//保存数据包的完整信息
                        packetColumnModelArrayList.add(columnModel);//保存tablecolumn信息
                        model.getTableList().add(columnModel);
                        //统计流量包数量
                        GetNums(packet);
                    }
                };

                try {
                    while (!StopCapture) {
                        try {
                            sleep(1);//不能持续运行，线程一定要sleep，不然异步的读取与写入会进入死循环，或者可以写一个锁
                        } catch (InterruptedException e) {
                            break;
                        }
                        pcap.loop(1, jpacketHandler, "什么哦这是");//开始循环，调用内类JpacketHandler
                    }
                } catch (Exception e) {
                    System.err.printf(e.toString());
                }

//                pcap.close();
            }
        });

//        以下可用，暂时注释保存
//        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
//        singleThreadExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread().getName() + "多线程嘻嘻嘻");
//                loadTableList();
//                try {
//                    Thread.sleep(2000);
//                    System.out.println("sleep finished");
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
    //返回PacketsInfoList
    public static ArrayList<PcapPacket> getPacketsInfoList() {
        return PacketsInfoList;
    }

    //统计各类型数据包的数量
    public static void GetNums(PcapPacket pcapPacket){
        String protocol = PacketProcess.getProtocol(pcapPacket);
        switch (protocol){
            case "HTTP":
                BarChartModel.HttpNum++;
                break;
            case "TCP":
                BarChartModel.TcpNum++;
                break;
            case "UDP":
                BarChartModel.UdpNum++;
                break;
            case "ICMP":
                BarChartModel.IcmpNum++;
                break;
            case "IP4":
                BarChartModel.TcpNum++;
                break;
            case "IP6":
                BarChartModel.TcpNum++;
                break;
            case "ARP":
                BarChartModel.ArpNum++;
                break;
            default:
                BarChartModel.OtherNum++;
        }
    }
    //“开始过滤”

    /**
     * src ip:只显示源IP是ip的分组，des ip同理
     *
     */
    public void handleFilter(String args){
        if(args.isEmpty()){
            System.out.println("空的,恢复原来的数据");
            model.setTableList(packetColumnModelArrayList);
        }else{
            String[] CommandArray = args.split("\\s");
            String protocol= CommandArray[0].toLowerCase();//协议
            String options = CommandArray[1];
            String operator = CommandArray[2];
            String nums = CommandArray[3];
            ArrayList<PacketColumnModel> List = new ArrayList<>();

            switch (protocol){
                case "icmp":
                    break;
                case "ip":
                    FilterIp(List,options,operator,nums);
                    break;
                case "tcp":
                    FilterTCP(List,options,operator,nums);
                    break;
                case "udp":
                    FilterUDP(List,options,operator,nums);
                    break;
                case "http":
                    break;
            }

//            System.out.println("非空");
//            ArrayList<PacketColumnModel> List = new ArrayList<>();
////            ObservableList<PacketColumnModel> tableList = model.getTableList();
//            FilterSrcIp(List,args);//筛选源IP地址为args的数据包
            ObservableList<PacketColumnModel> tableList = FXCollections.observableList(List);
            model.setTableList(tableList);
        }
    }
    //重置
    public void handleReset(){
        model.setTableList(packetColumnModelArrayList);
    }

    //筛选源IP地址为args的数据包
    private void FilterIp(ArrayList<PacketColumnModel> list,String ...args){
        String options = args[0];
        String operator = args[1];
        String nums = args[2];
        if (options.equals("src")){//查源IP
            for (int i=0;i<model.getTableList().size();i++){
                if (model.getTableList().get(i).getSrcIp()!="-1" && model.getTableList().get(i).getSrcIp().equals(nums)){
                    list.add(model.getTableList().get(i));
                }
            }
        }else if (options.equals("dst")){//查目的端口
            for (int i=0;i<model.getTableList().size();i++){
                if (model.getTableList().get(i).getDesIp()!="-1" && model.getTableList().get(i).getDesIp().equals(nums)){
                    list.add(model.getTableList().get(i));
                }
            }
        }
    }
    //过滤TCP数据包
    private void FilterTCP(ArrayList<PacketColumnModel> list,String ...args){
        String options = args[0];//port dstport srcport
        String operator = args[1];//选择
        String nums = args[2];//端口
        if (options.equals("srcport")){
            if (operator.equals("==")) {
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && model.getTableList().get(i).getSrcport().equals(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals(">=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) >= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals("<=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) <= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }
        }else if (options.equals("dstport")){
            if (operator.equals("==")) {
                for (int i = 0; i < model.getTableList().size(); i++) {
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && model.getTableList().get(i).getDesport().equals(nums)) {
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals(">=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && Integer.valueOf(model.getTableList().get(i).getDesport()) >= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals("<=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && Integer.valueOf(model.getTableList().get(i).getDesport()) <= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }
        }else if(options.equals("port")){
            if (operator.equals("==")) {
                for (int i = 0; i < model.getTableList().size(); i++) {
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && model.getTableList().get(i).getSrcport().equals(nums) || model.getTableList().get(i).getDesport().equals(nums)) {
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals(">=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) >= Integer.valueOf(nums) || Integer.valueOf(model.getTableList().get(i).getDesport()) >= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals("<=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("TCP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) <= Integer.valueOf(nums) || Integer.valueOf(model.getTableList().get(i).getDesport()) <= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }
        }
    }
    //过滤UDP数据包
    private void FilterUDP(ArrayList<PacketColumnModel> list,String ...args){
        String options = args[0];//port dstport srcport
        String operator = args[1];//选择
        String nums = args[2];//端口
        if (options.equals("srcport")){
            if (operator.equals("==")) {
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && model.getTableList().get(i).getSrcport().equals(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals(">=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) >= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals("<=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) <= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }
        }else if (options.equals("dstport")){
            if (operator.equals("==")) {
                for (int i = 0; i < model.getTableList().size(); i++) {
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && model.getTableList().get(i).getDesport().equals(nums)) {
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals(">=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && Integer.valueOf(model.getTableList().get(i).getDesport()) >= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals("<=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && Integer.valueOf(model.getTableList().get(i).getDesport()) <= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }
        }else if(options.equals("port")){
            if (operator.equals("==")) {
                for (int i = 0; i < model.getTableList().size(); i++) {
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && model.getTableList().get(i).getSrcport().equals(nums) || model.getTableList().get(i).getDesport().equals(nums)) {
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals(">=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) >= Integer.valueOf(nums) || Integer.valueOf(model.getTableList().get(i).getDesport()) >= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }else if(operator.equals("<=")){
                for (int i=0;i<model.getTableList().size();i++){
                    if (model.getTableList().get(i).getProtocol().equals("UDP") && Integer.valueOf(model.getTableList().get(i).getSrcport()) <= Integer.valueOf(nums) || Integer.valueOf(model.getTableList().get(i).getDesport()) <= Integer.valueOf(nums)){
                        list.add(model.getTableList().get(i));
                    }
                }
            }
        }
    }

}
