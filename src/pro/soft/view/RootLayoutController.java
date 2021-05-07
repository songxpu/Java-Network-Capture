package pro.soft.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapHandler;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import pro.soft.MainApp;
import pro.soft.service.JnetpCap;
import pro.soft.service.PacketCaptureService;
import pro.soft.util.AlertDialog;
import pro.soft.util.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本类控制菜单栏相关事件
 * */
public class RootLayoutController {
    //选择网卡弹框
    AnchorPane SelectLayout;
    //菜单对象
    Menu menu;
    //选项“关于”
    @FXML
    MenuItem about;
    @FXML
    MenuItem startCapture;
    @FXML
    MenuItem stopCapture;
    @FXML
    MenuItem storeFile;

    //"关于"弹框
    BorderPane AboutLayout;

    //回调MainApp
    private MainApp mainApp;

    //MainApp初始化时调用
    public void setMainApp(MainApp mainApp) {
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
    public void handleStartCapture() {
        System.out.println("点击开启抓包");
        PacketCaptureService.StopCapture = false;
        PacketCaptureService.capturePacket();

    }

    //关闭抓包事件
    public void handleStopCapture() {
        System.out.println("停止抓包");
        PacketCaptureService.StopCapture = true;
//        JnetpCap.getInstance().delPcap();

    }

    //统计数据包
    public void handleCreateBarChart() {
        this.mainLayoutController.updateBarChart();
    }

    //保存流量包
    public void filestore() {
        if (PacketCaptureService.getPacketsInfoList().size() == 0) {
            AlertDialog.getInstance().Error_Warning("当前数据包为空，无法保存文件！");
        }
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择保存的路径");
        File dir = dirChooser.showDialog(mainApp.getPrimaryStage());//获取文件夹File对象

        String ofile = dir.getAbsolutePath()+"\\"+TimeUtil.getLocalTimeForFile() + ".pcap";//文件的完整路径
        PcapDumper dumper = JnetpCap.getInstance().getPcap().dumpOpen(ofile); // output file
        if(dumper==null){
            AlertDialog.getInstance().Error_Warning("保存路径不合法，不可包含中文路径！");
            return;
        }
        PcapHandler<PcapDumper> dumpHandler = new PcapHandler<PcapDumper>() {
            public void nextPacket(PcapDumper dumper, long seconds, int useconds,
                                   int caplen, int len, ByteBuffer buffer) {
                dumper.dump(seconds, useconds, caplen, len, buffer);
            }
        };
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JnetpCap.getInstance().getPcap().loop(PacketCaptureService.getPacketsInfoList().size(), dumpHandler, dumper);
                File file2 = new File(ofile);
                dumper.close(); // 如果dumper不关闭，那么输出文件是没法删除的
                JnetpCap.getInstance().getPcap().close();
                //创建成功弹框
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新JavaFX的主线程的代码放在此处
                        AlertDialog.getInstance().Info_Display("文件"+file2.getAbsolutePath()+"创建成功！");
                    }
                });
            }
        });
    }

    //MainLayoutController赋值过来
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }

    //点击"关于"
    public void handleAbout() throws IOException, InterruptedException {
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

    //导入流量文件
    public void InputPacket() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PCAP", "*.pcap")
        );
        File pcapfile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (pcapfile==null){
            return;
        }
        final StringBuilder errbuf = new StringBuilder(); // For any error msgs
        final String file = pcapfile.getAbsolutePath();//注意：路径不支持中文

        Pcap pcap = Pcap.openOffline(file, errbuf);
        if (pcap == null) {//异常情况，弹框提示
            AlertDialog.getInstance().Error_Warning("数据包异常！\n①pcap文件路径不可包含中文字符。");
            return;
        }
        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {
            public void nextPacket(PcapPacket packet, String user) {
                System.out.printf("Received at %s caplen=%-4d len=%-4d %s\n",
                        new Date(packet.getCaptureHeader().timestampInMillis()),
                        packet.getCaptureHeader().caplen(), // Length actually captured
                        packet.getCaptureHeader().wirelen(), // Original length
                        user // User supplied object
                );
                PacketCaptureService.getPacketsInfoList().add(packet);//添加完整数据包
            }
        };
        int len=0;
        while(true){
            try {
                pcap.loop(1, jpacketHandler, "jNetPcap rocks!");
                len++;
                if (len>PacketCaptureService.getPacketsInfoList().size()){
                    break;
                }
            } catch (Exception e){
                System.out.println(e);
            };
        }
        PacketCaptureService.HandleDataFromWholeToColumn();
//        about.setDisable(true);测试：菜单选项禁止点击
        pcap.close();

    }
    //探测用户密码
    public void FindPassword(){
        PacketCaptureService.SniffPwd();
    }
    //清空数据包
    //DONE:清空时报空指针错误，但没有影响程序运行。
    public void handleClearAll(){
       if (PacketCaptureService.clearAll()){//true:清空成功
            //依次重置右侧协议树、协议详细栏、流量统计区域
           mainLayoutController.clearInfo_tree();
           mainLayoutController.clearText_packetInfo();
           mainLayoutController.updateBarChart();
           AlertDialog.getInstance().Info_Display("数据清空成功！");
       }else{//清空失败
           AlertDialog.getInstance().Error_Warning("数据清空失败！");
       }

    }

    public void handleIPStatistics() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/IPStatisticsLayout.fxml"));
        AnchorPane IPLayout = (AnchorPane) loader.load();
        //创建弹出窗口的Stage
        Stage aboutStage = new Stage();
        aboutStage.setTitle("IP流量统计");
        aboutStage.initModality(Modality.WINDOW_MODAL);
        aboutStage.initOwner(mainApp.getPrimaryStage());//指定父窗口
        Scene scene = new Scene(IPLayout);
        aboutStage.setScene(scene);
        //设置弹出窗口的控制器
        IPStatisticsLayoutController controller = loader.getController();
        controller.setIPStage(aboutStage);//将窗口Stage对象传给控制器
        // 显示弹框并等待用户选择
        aboutStage.showAndWait();
    }
}

