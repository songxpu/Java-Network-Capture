package pro.soft;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pro.soft.view.MainLayoutController;
import pro.soft.view.RootLayoutController;

import java.io.IOException;

public class MainApp extends Application {
    private BorderPane rootLayout;
    private Stage primaryStage;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("网络监听软件");
        //退出程序结束子线程
        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
        //禁止缩放
        primaryStage.setResizable(false);

        //载入菜单栏
        initRootLayout();
//        showMainUI();

        primaryStage.show();
    }

    private void initRootLayout(){
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            //加载菜单栏控制器
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(MainApp.class.getResource("view/MainLayout.fxml"));
            AnchorPane mainLayout = (AnchorPane) loader2.load();
            MainLayoutController mainLayoutController = loader2.getController();
            // Set person overview into the center of root layout.
            rootLayout.setCenter(mainLayout);
            controller.setMainLayoutController(mainLayoutController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //载入RootLayout.fxml文件
//    private void initRootLayout(){
//        try {
//            // Load root layout from fxml file.
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
//            rootLayout = (BorderPane) loader.load();
//            //加载菜单栏控制器
//            RootLayoutController controller = loader.getController();
//            controller.setMainApp(this);
//
//            // Show the scene containing the root layout.
//            Scene scene = new Scene(rootLayout);
//            primaryStage.setScene(scene);
//            primaryStage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    //载入主界面UI
//    public void showMainUI() {
//        try {
//            // Load person overview.
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(MainApp.class.getResource("view/MainLayout.fxml"));
//            AnchorPane mainLayout = (AnchorPane) loader.load();
//
//            // Set person overview into the center of root layout.
//            rootLayout.setCenter(mainLayout);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public Stage getPrimaryStage(){
        return this.primaryStage;
    }

}
