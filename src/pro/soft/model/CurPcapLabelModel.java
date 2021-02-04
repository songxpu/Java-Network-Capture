package pro.soft.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CurPcapLabelModel {
    private StringProperty string = new SimpleStringProperty();

    public String getString() {
        return string.get();
    }

    public StringProperty stringProperty() {
        return string;
    }

    public void setString(String string) {
        this.string.set(string);
    }
}
