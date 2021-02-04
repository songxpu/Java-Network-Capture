package pro.soft.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pro.soft.MainApp;
import pro.soft.service.PacketCaptureService;

import java.io.IOException;

/**
 * 本类控制菜单栏相关事件
 * */
public class RootLayoutController {
    //选择网卡弹框
    AnchorPane SelectLayout;
    //菜单对象
    Menu menu;

    //回调MainApp
    private MainApp mainApp;
    //MainApp初始化时调用
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    private MainLayoutController mainLayoutController;

    //选择网卡事件
    public void handleSelectInterface() throws IOException {
        System.out.println("点击了[监听网卡]");
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

    }
    //统计数据包
    public void handleCreateBarChart(){
        this.mainLayoutController.updateBarChart();

    }

    //MainLayoutController赋值过来
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
    }
}
