package pro.soft.service;

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

    private static int PacketIndex=0;
    private static StringBuilder errbuf = new StringBuilder(); // 获取错误信息

    public PacketCaptureService(PacketTableModel packetTableModel) {
        this.model = packetTableModel;
    }
    //用于停止线程中的循环：停止抓包
    public static boolean StopCapture=false;
    //存放完整的数据包信息
    private static ArrayList<PcapPacket> PacketsInfoList=new ArrayList<PcapPacket>();
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

                pcap.close();
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
}
