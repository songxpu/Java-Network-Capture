package pro.soft.service;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;

public class PacketDump {
    private static PacketDump instance = new PacketDump();
    public static PacketDump getInstance(){
        return instance;
    }

    private Pcap pcap;

    public void setPcap(Pcap pcap){
        this.pcap = pcap;
    }
    public Pcap getPcap(){
        return pcap;
    }
    public void fileStore(){
        String ofile = "tmp-capture-file.cap";
        PcapDumper dumper = pcap.dumpOpen(ofile); // output file
        PacketCaptureService.getPacketsInfoList();
    }
}
