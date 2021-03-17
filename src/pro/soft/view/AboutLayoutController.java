package pro.soft.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutLayoutController implements Initializable {

    @FXML
    private Text AboutText;

    private String content = "网络监听软件\n\n\npowerby CDUT 宋相普\n2021/4";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AboutText.setText(content);

    }

}
