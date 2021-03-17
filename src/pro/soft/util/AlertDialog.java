package pro.soft.util;

import javafx.scene.control.Alert;

//用于警告等弹框信息
public class AlertDialog {
    //单例模式
    //创建一个Alert对象
    private static AlertDialog instance = new AlertDialog();
    //无参构造函数
    private AlertDialog(){};
    //返回唯一的对象
    public static AlertDialog getInstance(){
        return instance;
    }

    //错误类型提示框
    public void Error_Warning(String msg){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR); // 创建一个消息对话框
        alert.setHeaderText("错误"); // 设置对话框的头部文本
        alert.setContentText(msg);// 设置对话框的内容文本
        alert.show(); // 显示对话框
    }
    //通知类型提示框
    public void Info_Display(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // 创建一个消息对话框
        alert.setHeaderText("通知"); // 设置对话框的头部文本
        alert.setContentText(msg);
        alert.show(); // 显示对话框
    }
}
