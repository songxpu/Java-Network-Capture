package pro.soft.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jnetpcap.PcapIf;
import pro.soft.service.JnetpCap;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DialogSelectInterfaceController implements Initializable {

    private List<PcapIf> alldevs;

    //标记是否已经获取到所有的网卡列表，默认为false
    private Boolean hasGotAll = false;
    //标记是否已经点击确认
    private Boolean isClick = false;

    //下拉栏
    @FXML
    private ChoiceBox choiceBox = new ChoiceBox();
    //网卡详细信息（树型展示）
//    @FXML
//    private TextArea detailPcapIf;
    @FXML
    private TreeView treeView;
//    确认和取消按钮
    @FXML
    private Button enter;
    @FXML
    private Button cancel;
    //弹框的Stage
    private Stage DialogSelectStage;


    public void setDialogSelectStage(Stage dialogSelectStage) {
        DialogSelectStage = dialogSelectStage;
    }

    public Boolean getIsClick(){
        return this.isClick;
    }

    //显示选择栏选项
    private void setSelectList(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                alldevs = JnetpCap.getInstance().getAllInterface();//获取所有网络接口存入List<PcapIf> alldevs;中
                choiceBox.setItems(FXCollections.observableArrayList(JnetpCap.changeToSimple(alldevs)));
                if (alldevs.size()!=0){ hasGotAll = true; }
                //设置下拉框点击事件
                choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        JnetpCap.getInstance().setCurrentPcpIf(newValue.intValue());//设置当前网卡
//                detailPcapIf.setText(JnetpCap.getInstance().getCurrent().toString());//通过TextArea展示网卡详细信息
                        //网卡详细信息：通过TreeView来展示
                        TreeItem treeItem = getDetail(JnetpCap.getInstance().getCurrent());
                        treeView.setRoot(treeItem);
                        DialogSelectStage.show();
                    }
                });
            }
        });
    }
    //确认和取消事件
    public void HandleEnter() throws IOException {
        MainLayoutController.cplmodel.setString("当前监听网卡:"+JnetpCap.getInstance().getCurrent().getDescription());
        DialogSelectStage.close();
    }
    public void HandleCancel(){
        JnetpCap.getInstance().cancelCurrentPcapIf();
        DialogSelectStage.close();
    }

    //DialogSelectInterfaceController初始化方法
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setSelectList();
//        if (JnetpCap.getInstance().getCurrent()==null){//未设置软件当前网卡信息
//            setSelectList();//显示选择栏选项
//        }else {//已经设置了当前网卡信息->直接默认显示
//            choiceBox.setItems(FXCollections.observableArrayList(JnetpCap.getInstance().getCurrent().getDescription()+" | "+JnetpCap.getInstance().getCurrent().getName()));
//            choiceBox.getSelectionModel().selectFirst();
//            TreeItem treeItem = getDetail(JnetpCap.getInstance().getCurrent());
//            treeView.setRoot(treeItem);
//        }
    }
    //网卡详细信息TreeView
    //如何优雅的遍历出来？
    public TreeItem getDetail(PcapIf pcapIf){
        TreeItem treeItem = new TreeItem<>("网卡详细信息");
        treeItem.setExpanded(true);
        //name
        TreeItem item_name = new TreeItem<>("名称");
        item_name.getChildren().add(new TreeItem<>(pcapIf.getName().toString()));
        item_name.setExpanded(true);
        //desc
        TreeItem item_desc = new TreeItem<>("描述");
        item_desc.getChildren().add(new TreeItem<>(pcapIf.getDescription().toString()));
        item_desc.setExpanded(true);
        //address
        TreeItem item_address = new TreeItem<>("地址");
        item_address.setExpanded(true);

        //遍历出address
        int i=0;
        while (i<pcapIf.getAddresses().size()){
            //第二层树的子选项1
            TreeItem item_addr_ch_ch1 = new TreeItem<>("ip地址");
            item_addr_ch_ch1.setExpanded(true);
            //第二层树的子选项1的子树
            item_addr_ch_ch1.getChildren().add(new TreeItem<>(pcapIf.getAddresses().get(i).getAddr().toString()));
            //第二层树的子选项2
            TreeItem item_addr_ch_ch2 = new TreeItem<>("子网地址");
            item_addr_ch_ch2.setExpanded(true);
            //第二层树的子选项2的子树
            item_addr_ch_ch2.getChildren().add(new TreeItem<>(pcapIf.getAddresses().get(i).getNetmask().toString()));
            //第二层树的子选项3
            TreeItem item_addr_ch_ch3 = new TreeItem<>("广播地址");
            item_addr_ch_ch3.setExpanded(true);
            //第二层树的子选项3的子树
            item_addr_ch_ch3.getChildren().add(new TreeItem<>(pcapIf.getAddresses().get(i).getBroadaddr().toString()));
            //addresses下的第一部分（第二层树）
            TreeItem item_addr_ch = new TreeItem<>("地址"+i);
            item_addr_ch.setExpanded(true);
            item_addr_ch.getChildren().add(item_addr_ch_ch1);
            item_addr_ch.getChildren().add(item_addr_ch_ch2);
            item_addr_ch.getChildren().add(item_addr_ch_ch3);
            item_address.getChildren().add(item_addr_ch);
            i++;
        }
        treeItem.getChildren().add(item_name);
        treeItem.getChildren().add(item_desc);
        treeItem.getChildren().add(item_address);
        return treeItem;
    }



}
