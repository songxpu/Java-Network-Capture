package pro.soft.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PacketColumnModel {
    private IntegerProperty id=new SimpleIntegerProperty();
    private SimpleStringProperty time=new SimpleStringProperty();
    private SimpleStringProperty  length=new SimpleStringProperty();
    private SimpleStringProperty  protocol=new SimpleStringProperty();
    private SimpleStringProperty srcMac=new SimpleStringProperty();
    private SimpleStringProperty  desMac=new SimpleStringProperty();
    private SimpleStringProperty  srcIp=new SimpleStringProperty();
    private SimpleStringProperty  desIp=new SimpleStringProperty();
    private SimpleStringProperty  srcport=new SimpleStringProperty();
    private SimpleStringProperty  desport=new SimpleStringProperty();

    public PacketColumnModel() {
    }
    public PacketColumnModel(int id, String time, String length, String protocol, String srcMac, String desMac, String srcIp, String desIp, String srcport, String desport) {
        this.id.set(id);
        this.time.set(time);
        this.length.set(length);
        this.protocol.set(protocol);
        this.srcMac.set(srcMac);
        this.desMac.set(desMac);
        this.srcIp.set(srcIp);
        this.desIp.set(desIp);
        this.srcport.set(srcport);
        this.desport.set(desport);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty timeProperty() {
        return time;
    }

    public String getLength() {
        return length.get();
    }

    public SimpleStringProperty lengthProperty() {
        return length;
    }

    public String getProtocol() {
        return protocol.get();
    }

    public SimpleStringProperty protocolProperty() {
        return protocol;
    }

    public String getSrcMac() {
        return srcMac.get();
    }

    public SimpleStringProperty srcMacProperty() {
        return srcMac;
    }

    public String getDesMac() {
        return desMac.get();
    }

    public SimpleStringProperty desMacProperty() {
        return desMac;
    }

    public String getSrcIp() {
        return srcIp.get();
    }

    public SimpleStringProperty srcIpProperty() {
        return srcIp;
    }

    public String getDesIp() {
        return desIp.get();
    }

    public SimpleStringProperty desIpProperty() {
        return desIp;
    }

    public String getSrcport() {
        return srcport.get();
    }

    public SimpleStringProperty srcportProperty() {
        return srcport;
    }

    public String getDesport() {
        return desport.get();
    }

    public SimpleStringProperty desportProperty() {
        return desport;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public void setLength(String length) {
        this.length.set(length);
    }

    public void setProtocol(String protocol) {
        this.protocol.set(protocol);
    }

    public void setSrcMac(String srcMac) {
        this.srcMac.set(srcMac);
    }

    public void setDesMac(String desMac) {
        this.desMac.set(desMac);
    }

    public void setSrcIp(String srcIp) {
        this.srcIp.set(srcIp);
    }

    public void setDesIp(String desIp) {
        this.desIp.set(desIp);
    }

    public void setSrcport(String srcport) {
        this.srcport.set(srcport);
    }

    public void setDesport(String desport) {
        this.desport.set(desport);
    }

    @Override
    public String toString() {
        return "PacketColumnModel{" +
                "id=" + id +
                ", time=" + time +
                ", length=" + length +
                ", protocol=" + protocol +
                ", srcMac=" + srcMac +
                ", desMac=" + desMac +
                ", srcIp=" + srcIp +
                ", desIp=" + desIp +
                ", srcport=" + srcport +
                ", desport=" + desport +
                '}';
    }
}
