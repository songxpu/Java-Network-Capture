package pro.soft.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapHandler;
import pro.soft.MainApp;
import pro.soft.service.JnetpCap;
import pro.soft.service.PacketCaptureService;
import pro.soft.service.PacketDump;
import pro.soft.util.TimeUtil;
import sun.tools.jar.Main;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 本类控制菜单栏相关事件
 * */
public class RootLayoutController {
    //选择网卡弹框
    AnchorPane SelectLayout;
    //菜单对象
    Menu menu;
    //"关于"弹框
    BorderPane AboutLayout;

    //回调MainApp
    private MainApp mainApp;
    //MainApp初始化时调用
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    private MainLayoutController mainLayoutController;

    //选择网卡事件
    public void handleSelectInterface() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/DialogSelectInterface.fxml"));
        SelectLayout = (AnchorPane) loader.load();

        //创建弹出窗口的Stage
        Stage dialogStage = new Stage();
        dialogStage.setTitle("选择监听网卡");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(mainApp.getPrimaryStage());//指定父窗口
        Scene scene = new Scene(SelectLayout);
        dialogStage.setScene(scene);
        //设置弹出窗口的控制器
        DialogSelectInterfaceController controller = loader.getController();
        controller.setDialogSelectStage(dialogStage);//将窗口Stage对象传给控制器
        // 显示弹框并等待用户选择
        dialogStage.showAndWait();

    }
    //开启抓包事件
    public void handleStartCapture(){
        System.out.println("点击开启抓包");
        PacketCaptureService.StopCapture=false;
        PacketCaptureService.capturePacket();

    }
    //关闭抓包事件
    public void handleStopCapture(){
        System.out.println("停止抓包");
        PacketCaptureService.StopCapture=true;
//        JnetpCap.getInstance().delPcap();

    }
    //统计数据包
    public void handleCreateBarChart(){
        this.mainLayoutController.updateBarChart();
    }
    //保存流量包
    public void filestore(){
        String ofile = TimeUtil.getLocalTimeForFile()+".pcap";
        PcapDumper dumper = JnetpCap.getInstance().getPcap().dumpOpen(ofile); // output file
        PcapHandler<PcapDumper> dumpHandler = new PcapHandler<PcapDumper>() {
            public void nextPacket(PcapDumper dumper, long seconds, int useconds,
                                   int caplen, int len, ByteBuffer buffer) {
                dumper.dump(seconds, useconds, caplen, len, buffer);
            }
        };
        JnetpCap.getInstance().getPcap().loop(PacketCaptureService.getPacketsInfoList().size(), dumpHandler, dumper);
        File file2 = new File(ofile);
        System.out.printf("%s file has %d bytes in it!\n", ofile, file2.length());
        dumper.close(); // 如果dumper不关闭，那么输出文件是没法删除的
        JnetpCap.getInstance().getPcap().close();

    }

    //MainLayoutController赋值过来
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    //点击"关于"
    public void handleAbout() throws IOException {
        System.out.println("点击了关于");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/AboutLayout.fxml"));
        AboutLayout = (BorderPane) loader.load();
        //创建弹出窗口的Stage
        Stage aboutStage = new Stage();
        aboutStage.setTitle("关于");
        aboutStage.initModality(Modality.WINDOW_MODAL);
        aboutStage.initOwner(mainApp.getPrimaryStage());//指定父窗口
        Scene scene = new Scene(AboutLayout);
        aboutStage.setScene(scene);
        //设置弹出窗口的控制器
        AboutLayoutController controller = loader.getController();
//        controller.setAbouttStage(aboutStage);//将窗口Stage对象传给控制器
        // 显示弹框并等待用户选择
        aboutStage.showAndWait();

    }
}
