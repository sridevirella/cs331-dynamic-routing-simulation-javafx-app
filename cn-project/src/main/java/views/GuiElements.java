package views;

import handler.RouterHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Router;
import utils.FileReadWrite;
import utils.Initializer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class GuiElements {

    private BorderPane borderPane;

    private static Button selectButton;
    private static Button changeCostButton;
    private static Label selectionLabel;
    private static VBox mainVBox;
    private static TextArea textArea;
    private static ComboBox<String> routerNamesCB;
    private static ComboBox<String> changeCostCB;
    private static ComboBox<String> neighboursCB;
    private static int neighbourIndex;
    private static int routerIndex;
    private static TextField textField;
    private static Map<String, StringBuilder> routingTableDataMap;

    public GuiElements(Stage stage) {

        borderPane = new BorderPane();
        initButtons(stage);
        initOtherViews();
        initAndAddNodes();
    }

    private void initButtons(Stage stage) {

        selectButton = new Button("Select a File");
        selectButton.setPadding(new Insets(5,10,5,10));
        setListener(stage);
        changeCostButton = new Button("Change Cost");
        changeCostButton.setStyle("-fx-background-insets: 0 0 -1 0, 0, 1, 2; -fx-background-color: #6495ED");
    }

    private void initOtherViews() {

        addLabelProperties();
        routerNameCBProperties();
        changeCostCBProperties();
        neighboursCBProperties();
    }

    private void neighboursCBProperties() {

        neighboursCB = new ComboBox<>();
        neighboursCB.setPromptText("---Select a Neighbour---");
        neighboursCB.setVisible(true);
    }

    private void changeCostCBProperties() {

        changeCostCB = new ComboBox<>();
        changeCostCB.setPromptText("---Select a Router---");
    }

    private void routerNameCBProperties() {

        routerNamesCB = new ComboBox<>();
        routerNamesCB.setPromptText("---Select a Router---");
    }

    private void addLabelProperties() {

        selectionLabel = new Label("No file Selected");
        selectionLabel.setPadding(new Insets(5,10,0,10));
        textArea = new TextArea();
        textField = new TextField();
    }

    private void initAndAddNodes() {

        HBox FileSelectionHBox = getFileSelectionHBox();

        Label changeCostLabel = getChangeCostLabel();
        Label costFieldLabel = getCostLabel();

        VBox changeCostVBox = new VBox();
        changeCostVBox.getChildren().addAll(changeCostLabel, changeCostCB, neighboursCB, costFieldLabel, textField, changeCostButton);
        changeCostVBox.setPadding(new Insets(60,10,0,10));
        changeCostVBox.setSpacing(20);

        HBox routerSelectionHBox = new HBox();
        routerSelectionHBox.getChildren().add(routerNamesCB);
        routerSelectionHBox.setPadding(new Insets(20, 20, 20 ,20));
        routerSelectionHBox.setAlignment(Pos.CENTER);

        addNodesToMainVBox(FileSelectionHBox, changeCostVBox, routerSelectionHBox);
    }

    private Label getCostLabel() {

        return new Label("Enter Cost");
    }

    private Label getChangeCostLabel() {

        Label changeCostLabel = new Label("Change Cost");
        changeCostLabel.setStyle("-fx-font-weight: bold");
        return changeCostLabel;
    }

    private HBox getFileSelectionHBox() {

        HBox FileSelectionHBox = new HBox();
        FileSelectionHBox.getChildren().addAll(selectButton, selectionLabel);
        FileSelectionHBox.setPadding(new Insets(20,20,20,20));
        return FileSelectionHBox;
    }

    private void addNodesToMainVBox(HBox fileSelectionHBox, VBox changeCostVBox, HBox routerSelectionHBox) {

        mainVBox = new VBox();
        mainVBox.getChildren().addAll(fileSelectionHBox, changeCostVBox);
        mainVBox.setPadding(new Insets(20,20,20,20));
        mainVBox.setStyle("-fx-border-color: black; -fx-border-insets: 5; -fx-border-width: 3; -fx-border-style: solid;" );

        VBox textAreaVbox = new VBox();
        textAreaVbox.setPadding(new Insets(10, 20, 60, 20));
        textAreaVbox.getChildren().add(textArea);
        double prefWidth = 1.0;
        double prefHeight = 500.0;
        textArea.setPrefSize(prefWidth, prefHeight);

        borderPane.setLeft(mainVBox);
        borderPane.setTop(routerSelectionHBox);
        borderPane.setCenter(textAreaVbox);
    }

    private void setListener(Stage stage) {

        selectButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                try {
                    getFileFromUser(stage);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getFileFromUser(Stage stage) throws IOException {

        FileChooser fileChooser = getFileChooser();

        File file = fileChooser.showOpenDialog(stage);
        if (file != null)
            selectionLabel.setText(file.getName() + "  selected");

        FileReadWrite.createFile(file.getPath(), file.getName());
        initSimulator(file.getName());
    }

    private FileChooser getFileChooser() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter(".txt files only", "*.txt");
        fileChooser.getExtensionFilters().add(extension);
        return fileChooser;
    }


    private void initSimulator(String fileName) throws IOException {

        List<Router> routerList = Initializer.getRoutersList(fileName);
        RouterHandler.setRoutersCount(routerList.size());
        routerList.forEach( router -> { new RouterHandler(router).builder(); });
        handleSelections(routerList);
    }

    private void handleSelections(List<Router> routerList) {

        initialSelection(routerList);
        changeCostSelection(routerList);
        neighbourCBListener();
        changeCostButtonListener(routerList);
    }

    private void changeCostButtonListener(List<Router> routerList) {

        changeCostButton.setOnMouseClicked(event -> {

            new RouterHandler(routerList.get(getRouterIndex())).changeCost(getNeighbourIndex(), Double.parseDouble(textField.getText()));
            routingTableDataMap = routerList.stream().collect(Collectors.toMap(Router::getLabel, RouterHandler::displayTable));
        });

//        changeCostButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//
//                new RouterHandler(routerList.get(getRouterIndex())).changeCost(getNeighbourIndex(), Double.parseDouble(textField.getText()));
//                routingTableDataMap = routerList.stream().collect(Collectors.toMap(Router::getLabel, RouterHandler::displayTable));
//            }
//        });
    }

    private void neighbourCBListener() {

        neighboursCB.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null)
                setNeighbourIndex(neighboursCB.getSelectionModel().getSelectedIndex());
        });
    }

    private void changeCostSelection(List<Router> routerList) {

        Map<String, List<String>> changeCostDataMap = routerList.stream().collect(Collectors.toMap(Router::getLabel, Router::getNeighbors));
        changeCostCB.getItems().addAll(FXCollections.observableArrayList( changeCostDataMap.keySet() ).sorted(
                ((o1, o2) -> Integer.compare(Integer.parseInt(o1.substring(1)), Integer.parseInt(o2.substring(1))) )
        ));
        changeCostCBListener(changeCostDataMap);
    }

    private void changeCostCBListener(Map<String, List<String>> changeCostDataMap) {

        changeCostCB.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                neighboursCB.getItems().clear();
                textArea.clear();
                setRouterIndex(Integer.parseInt(newValue.substring(1))-1);
                neighboursCB.getItems().addAll(FXCollections.observableArrayList(changeCostDataMap.get(newValue)));
            }
        });
    }

    private void initialSelection(List<Router> routerList) {

        routingTableDataMap = routerList.stream().collect(Collectors.toMap(Router::getLabel, RouterHandler::displayTable));
        routerNamesCB.getItems().addAll(FXCollections.observableArrayList( routingTableDataMap.keySet()).sorted(
                ((o1, o2) -> Integer.compare(Integer.parseInt(o1.substring(1)), Integer.parseInt(o2.substring(1))) )
        ));
        routerNamesCB();
    }

    private void routerNamesCB() {

        routerNamesCB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textArea.clear();
                textArea.setText(routingTableDataMap.get(newValue).toString());
            }
        });
    }

    private int getNeighbourIndex() {
        return neighbourIndex;
    }

    private void setNeighbourIndex(int index) {
        neighbourIndex = index;
    }

    private int getRouterIndex() {
        return routerIndex;
    }

    private void setRouterIndex(int index) {
        routerIndex = index;
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }
}
