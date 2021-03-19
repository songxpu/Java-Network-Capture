package pro.soft.util;

import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

//清洗PcapPacket packet

public class PacketProcess {

    private static Ethernet eth = new Ethernet();
    private static Ip4 ip4 = new Ip4();
    private static Ip6 ip6 = new Ip6();
    private static Tcp tcp = new Tcp();
    private static Udp udp = new Udp();
    private static Icmp icmp = new Icmp();


    //获取流量包的协议
    public static String getProtocol(PcapPacket packet){
        JProtocol[] protocols = JProtocol.values();//获取Jnetpcap能识别的所有协议
        for (int i=protocols.length-1;i>=0;i--){//逆序，从网络高层协议开始识别
            if (packet.hasHeader(protocols[i].getId())){
                return protocols[i].name();
            }
        }
        return "null";
    }
    //获取源Mac地址
    public static String getSrcMac(PcapPacket packet){
        packet.getHeader(eth);
        if (packet.hasHeader(eth)) { // 如果packet有eth头部
            return FormatUtils.mac(eth.source());
        }
        return "null";
    }
    //获取目的Mac地址
    public static String getDesMac(PcapPacket packet){
        packet.getHeader(eth);
        if (packet.hasHeader(eth)) { // 如果packet有eth头部
            return FormatUtils.mac(eth.destination());
        }
        return "null";
    }
    //ip有ip4，ip6
    //解析出源ip
    public static String getSrcIp(PcapPacket packet) {
        if (packet.hasHeader(ip4)) { // 如果packet有ip头部
            return FormatUtils.ip(ip4.source());
        } else if (packet.hasHeader(ip6)) {
            return FormatUtils.ip(ip6.source());
        } else return "null";
    }
    //解析出目的ip
    public static String getDestIp(PcapPacket packet) {

        if (packet.hasHeader(ip4)) { // 如果packet有ip头部
            return FormatUtils.ip(ip4.destination());
        } else if (packet.hasHeader(ip6)) {
            return FormatUtils.ip(ip6.destination());
        } else return "null";
    }
    //获取源端口
    public static int getSrcPort(PcapPacket packet){
        if (packet.hasHeader(tcp)){
            return tcp.source();
        }else if(packet.hasHeader(udp)){
            return udp.source();
        }
        return -1;
    }
    //获取目的端口
    public static int getDesPort(PcapPacket packet){
        if (packet.hasHeader(tcp)){
            return tcp.destination();
        }else if(packet.hasHeader(udp)){
            return udp.destination();
        }
        return -1;
    }
    //获取流量包大小
    public static int getSize(PcapPacket packet){
        return packet.size();
    }

    //获取HTTP数据包中的内容
    public static String getHttpContens(PcapPacket packet){
        Http http = new Http();
        packet.getHeader(http);
        return new String(http.getPayload());
    }
    //获取TCP数据包中payload部分内容，eg：FTP协议
    public static String getTcpContents(PcapPacket packet){
        Tcp tcp = new Tcp();
        packet.getHeader(tcp);
        if(tcp.getPayloadLength()>0)
            return new String(tcp.getPayload());
        return "NULL";//直接返回null会导致空指针的情况
    }
    //获取UDP数据包中的payload部分内容
    public static String getUdpContens(PcapPacket packet){
        Udp udp = new Udp();
        packet.getHeader(udp);
        if (udp.getPayloadLength()>0)
            return new String(udp.getPayload());
        return "NULL";
    }
}
