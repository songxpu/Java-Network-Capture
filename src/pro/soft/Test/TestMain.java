package pro.soft.Test;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Scanner;

public class TestMain {
    public static void main1(String[] args) {
        String source = "1 2 3";
        String[] sourceArray = source.split("\\s",4);
        for (int i = 0; i < sourceArray.length; i++) {
            System.out.println(sourceArray[i]);
        }
    }
    public static void main(String[] args) {

    }
}
