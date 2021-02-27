package pro.soft.service;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import pro.soft.model.PacketTableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Service层，负责获取网卡信息
 */
public class JnetpCap {
    private PcapIf currentPcpIf;//当前网卡对象
    private List<PcapIf> alldevsList;//所有网卡对象列表

    private Pcap pcap;//当前打开的网卡，用于保存流量包

    //单例模式
    private static JnetpCap instance = new JnetpCap();
    public static JnetpCap getInstance(){
        return instance;
    }

    public List<PcapIf> getAllInterface(){
        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // alldevs用来装载所有的network interface card，
        StringBuilder errbuf = new StringBuilder(); // 获取错误信息
        /***************************************************************************
         * 首先我们要来获取系统中的设备列表
         **************************************************************************/
        int r = Pcap.findAllDevs(alldevs, errbuf);
        /** 这个方法构造了可以用pcap_open_live()打开的所有网络设备
         * 这个列表中的元素都是 pcap_if_t，
         * name 一个指向设备名字的指针；
         * adderess 是一个接口的地址列表的第一个元素的指针；
         * flag 一个PCAP_IF_LOOPBACK标记接口是否是loopback的
         * 失败返回-1，成功返回0
         */

        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            // 如果获取失败，或者获取到列表为空，则输出错误信息，退出
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return null;
        }

        this.alldevsList=alldevs;//备份一份
        return alldevs;
    }
    //将list转换为另一种仅保留关键信息的list，便于显示在选择栏中
    public static ArrayList<String> changeToSimple(List<PcapIf> list){
        ArrayList<String> simplePcapIfList = new ArrayList<String>();
        for (PcapIf pcapIf : list){
            simplePcapIfList.add(pcapIf.getDescription()+" | "+pcapIf.getName());
        }
        return simplePcapIfList;
    }
    //设置当前用户选中的网卡
    public PcapIf getCurrent(){
        return this.currentPcpIf;
    }
    public void setCurrentPcpIf(int index){
        this.currentPcpIf = alldevsList.get(index);
        System.out.println("当前网卡："+currentPcpIf);
    }
    //取消当前网卡设置
    public void cancelCurrentPcapIf(){
        this.currentPcpIf = null;
    }

    public void setPcap(Pcap pcap){
        this.pcap = pcap;
    }
    public Pcap getPcap(){
        return pcap;
    }
    public void delPcap(){
        pcap.close();
    }

}
