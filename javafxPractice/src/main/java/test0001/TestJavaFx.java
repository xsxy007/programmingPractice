package test0001;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestJavaFx extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) throws Exception {

        AnchorPane ap = new AnchorPane();
        ap.setStyle("-fx-background-color: #83ffc5");

        // 按钮
        Button button = new Button("button1");
        // 水平布局
        HBox box = new HBox();
        // 竖直布局
        VBox vBox = new VBox();



        Scene scene = new Scene(ap);
        primaryStage.setScene(scene);
        primaryStage.setWidth(500);
        primaryStage.setHeight(500);
        primaryStage.show();
    }
}
