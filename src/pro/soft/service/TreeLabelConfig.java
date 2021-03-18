package pro.soft.service;

import javafx.scene.control.TreeItem;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import pro.soft.util.PacketProcess;

//用于配置主页右栏"树型"信息
public class TreeLabelConfig {

    public static TreeItem InitTreeView(PcapPacket pcapPacket){

        String protocol = PacketProcess.getProtocol(pcapPacket);
        //根节点
        TreeItem root = new TreeItem("协议树");
        root.setExpanded(true);

        //根节点1：协议结构
        TreeItem rootItem = new TreeItem<>("协议结构");
        rootItem.setExpanded(true);
        //链路层
        TreeItem treeItem = new TreeItem<>("以太网链路层");
        treeItem.setExpanded(false);

        TreeItem item_srcmac = new TreeItem<>("源网卡地址");
        item_srcmac.getChildren().add(new TreeItem<>(PacketProcess.getSrcMac(pcapPacket)));
        item_srcmac.setExpanded(true);
        //desc
        TreeItem item_descmac = new TreeItem<>("目的网卡地址");
        item_descmac.getChildren().add(new TreeItem<>(PacketProcess.getDesMac(pcapPacket)));
        item_descmac.setExpanded(true);
        treeItem.getChildren().add(item_srcmac);
        treeItem.getChildren().add(item_descmac);
        rootItem.getChildren().add(treeItem);

        //网络层
        TreeItem treeItem2 = new TreeItem<>("网络层");
        treeItem2.setExpanded(false);
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
        rootItem.getChildren().add(treeItem2);
        //传输层
        //若不是TCP、UDP、HTTP协议，则不展示传输层或应用层结构信息
        if (protocol=="UDP"){
            Udp udp = new Udp();
            pcapPacket.getHeader(udp);
            TreeItem treeItem3 = new TreeItem<>("传输层");
            treeItem3.setExpanded(false);
            TreeItem item_srcport = new TreeItem<>("源端口");
            item_srcport.getChildren().add(new TreeItem<>(PacketProcess.getSrcPort(pcapPacket)));
            item_srcport.setExpanded(true);
            TreeItem item_descport = new TreeItem<>("目的端口");
            item_descport.getChildren().add(new TreeItem<>(PacketProcess.getDesPort(pcapPacket)));
            item_descport.setExpanded(true);
            TreeItem item_length = new TreeItem<>("数据长度");
            item_length.getChildren().addAll(new TreeItem<>(udp.getPayloadLength()));
            TreeItem item_contens = new TreeItem<>("UDP数据内容");
            item_contens.setExpanded(false);
            item_contens.getChildren().addAll(new TreeItem<>(new String(udp.getPayload())));


            treeItem3.getChildren().addAll(item_srcport,item_descport,item_length,item_contens);
            rootItem.getChildren().addAll(treeItem3);
        }

        if(protocol=="TCP"||protocol=="HTTP"){
            TreeItem treeItem3 = new TreeItem<>("传输层");
            treeItem3.setExpanded(false);
            TreeItem item_srcport = new TreeItem<>("源端口");
            item_srcport.getChildren().add(new TreeItem<>(PacketProcess.getSrcPort(pcapPacket)));
            item_srcport.setExpanded(true);
            TreeItem item_descport = new TreeItem<>("目的端口");
            item_descport.getChildren().add(new TreeItem<>(PacketProcess.getDesPort(pcapPacket)));
            item_descport.setExpanded(true);
            TreeItem item_seqnum = new TreeItem<>("序号");
            item_seqnum.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).seq()));
            item_seqnum.setExpanded(true);
            TreeItem item_acknum = new TreeItem<>("确认号");
            item_acknum.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).ack()));
            item_acknum.setExpanded(true);
            TreeItem item_headerlen = new TreeItem<>("头部长度");
            item_headerlen.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).getLength()));
            item_headerlen.setExpanded(true);

            TreeItem item_flags = new TreeItem<>("标志");
            TreeItem item_urgflags = new TreeItem<>("URG");
            item_urgflags.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).flags_URG()));
            item_urgflags.setExpanded(true);
            item_flags.setExpanded(true);
            TreeItem item_ackflags = new TreeItem<>("ACK");
            item_ackflags.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).flags_ACK()));
            item_ackflags.setExpanded(true);
            TreeItem item_rstflags = new TreeItem<>("RST");
            item_rstflags.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).flags_RST()));
            item_rstflags.setExpanded(true);
            TreeItem item_synflags = new TreeItem<>("SYN");
            item_synflags.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).flags_SYN()));
            item_synflags.setExpanded(true);
            TreeItem item_finflags = new TreeItem<>("FIN");
            item_finflags.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).flags_FIN()));
            item_finflags.setExpanded(true);
            item_flags.getChildren().addAll(item_urgflags,item_ackflags,item_rstflags,item_synflags,item_finflags);

            TreeItem item_windows = new TreeItem<>("窗口");
            item_windows.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).window()));
            item_windows.setExpanded(true);

            TreeItem item_checknum = new TreeItem<>("校验和");
            item_checknum.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Tcp()).checksum()));
            item_checknum.setExpanded(true);
            //TCP协议携带的数据，在packet.tostring()中不显示
            TreeItem item_tcpdata = new TreeItem<>("TCP数据内容");
            item_tcpdata.getChildren().add(new TreeItem<>(new String(pcapPacket.getHeader(new Tcp()).getPayload())));
            item_tcpdata.setExpanded(false);

            treeItem3.getChildren().add(item_srcport);
            treeItem3.getChildren().add(item_descport);
            treeItem3.getChildren().add(item_seqnum);
            treeItem3.getChildren().add(item_acknum);
            treeItem3.getChildren().add(item_headerlen);
            treeItem3.getChildren().add(item_flags);
            treeItem3.getChildren().add(item_windows);
            treeItem3.getChildren().add(item_checknum);
            treeItem3.getChildren().add(item_tcpdata);

            rootItem.getChildren().add(treeItem3);
        }

        //应用层
        //若不是HTTP协议，则不显示
        if (protocol=="HTTP"){
            TreeItem treeItem4 = new TreeItem<>("应用层");
            treeItem4.setExpanded(false);
            //若不是
            if (pcapPacket.getHeader(new Http())!=null) {
                TreeItem item_header = new TreeItem<>("数据长度");
                item_header.getChildren().add(new TreeItem<>(pcapPacket.getHeader(new Http()).getPayloadLength()));
                item_header.setExpanded(true);

                TreeItem item_contens = new TreeItem<>("HTTP数据内容");
                item_contens.getChildren().add(new TreeItem<>(new String(pcapPacket.getHeader(new Http()).getPayload())));
                item_contens.setExpanded(false);

                treeItem4.getChildren().addAll(item_header,item_contens);
                rootItem.getChildren().add(treeItem4);

            }
        }



        //根节点2:密码探测
        TreeItem rootItem2 = new TreeItem<>("密码探测");
        rootItem2.setExpanded(true);
        if (pcapPacket.getHeader(new Tcp())!=null) {
            TreeItem item_contens = new TreeItem<>("TCP DATA");
            item_contens.getChildren().add(new TreeItem<>(new String(pcapPacket.getHeader(new Tcp()).getPayload())));
            item_contens.setExpanded(false);
            rootItem2.getChildren().add(item_contens);
        }
        if (pcapPacket.getHeader(new Http())!=null) {
            TreeItem item_contens = new TreeItem<>("HTTP DATA");
            item_contens.getChildren().add(new TreeItem<>(new String(pcapPacket.getHeader(new Http()).getPayload())));
            item_contens.setExpanded(false);
            rootItem2.getChildren().add(item_contens);
        }
        root.getChildren().addAll(rootItem,rootItem2);

        return root;
    }
}
