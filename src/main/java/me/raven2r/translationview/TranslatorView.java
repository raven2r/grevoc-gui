package me.raven2r.translationview;

import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import me.raven2r.grevoc.core.Translation;
import me.raven2r.grevoc.core.Vocabulary;
import me.raven2r.grevoc.core.config.UserConfig;
import me.raven2r.grevoc.core.translator.Amazon;
import me.raven2r.grevoc.core.translator.Deepl;
import me.raven2r.grevoc.core.translator.Translates;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TranslatorView extends Application {
    public static final int WINDOW_WIDTH = 720;
    public static final int WINDOW_HEIGHT = 480;
    public static final int WINDOW_MIN_WIDTH = 480;
    public static final int WINDOW_MIN_HEIGHT = 360;

    // Registration scene, pane
    TextField loginTextField = new TextField();
    TextField passwordTextField = new PasswordField();
    Button loginButton = new Button("Login");
    Button clearButton = new Button("Clear");
    Button registerButton = new Button("Register");

    // Info Labels
    Label userNameLabel = new Label("");
    Label infoLabel = new Label("");
    Timer infoClearTimer;
    TimerTask infoClearTask;

    // Translator pane
    TextField translatorSourceText = new TextField();
    Button translatorTranslateButton = new Button("Translate");
    Button translatorLoadButton = new Button("Load");
    Label translatorDeeplTarget = new Label("[translation]");
    Label translatorAmazonTarget = new Label("[translation]");
    Label translatorOpenAITarget = new Label("[translation]");
    Translates amazonTranslator = new Amazon();
    Translates deeplTranslator = new Deepl();
    String lastSource = "";
    Translation lastTranslation;

    // Vocabulary pane
    UserConfig userConfig;
    Vocabulary vocabulary;

    // Settings pane



    @Override
    public void start(Stage primaryStage) {
        infoLabel.setId("info-label");
        infoLabel.setPadding(new Insets(10));

        Scene loginScene = new Scene(makeLoginPane(), WINDOW_WIDTH, WINDOW_HEIGHT);
        loginScene.getStylesheets().add("style.css");

        translatorTranslateButton.setOnAction(actionEvent -> {
            fillTranslationLabels(translatorSourceText.getText(),
                    translatorDeeplTarget,
                    translatorAmazonTarget,
                    translatorOpenAITarget);
        });

        var loadButton = new Button("Load");
        loadButton.prefWidth(200);

        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setTitle("Translator View");
        primaryStage.show();

        TabPane tabPane = new TabPane();
        Tab translatorTab = new Tab("Translator", makeTranslatorPane());
        Tab vocabularyTab = new Tab("Vocabulary", makeVocabularyPane());
        Tab settingsTab = new Tab("Settings", makeSettingsPane());
        tabPane.getTabs().addAll(translatorTab, vocabularyTab, settingsTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        var rootVbox = new VBox(userNameLabel, tabPane, infoLabel);
        rootVbox.setId("root");

        Scene mainScene = new Scene(rootVbox, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainScene.getStylesheets().add("style.css");

        clearButton.setOnAction(event -> {
            loginTextField.clear();
            passwordTextField.clear();
        });
        loginButton.setOnAction(event -> {
            if(loginTextField.getText().isEmpty() || passwordTextField.getText().isEmpty())
                return;

            if(UserConfig.validate(loginTextField.getText(), passwordTextField.getText())) {
                userNameLabel.setText(loginTextField.getText());
                userConfig = new UserConfig(loginTextField.getText(), passwordTextField.getText());
                vocabulary = new Vocabulary(userConfig);
                primaryStage.setScene(mainScene);
                infoSuccess("Successful login");
            }
            else
                infoFail("Incorrect credentials");
        });
        registerButton.setOnAction(event -> {
            if(loginTextField.getText().isEmpty() || passwordTextField.getText().isEmpty())
                return;

            if(UserConfig.register(loginTextField.getText(), passwordTextField.getText())) {
                infoSuccess("User registered successfully");
                loginButton.fire();
            }
            else
                infoFail("Couldn't register user");
        });

        infoClearTimer = new Timer();
        infoClearTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Performing task");
                Platform.runLater(() -> {
                    infoLabel.setText("");
                });
            }
        };
//        var authStage = new Stage();
//        var authRoot = new VBox();
//        authStage.setScene(new Scene(authRoot, WINDOW_WIDTH, WINDOW_HEIGHT));
//        authStage.showAndWait();
    }


    private Pane makeLoginPane() {
        Label loginLabel = new Label("Login");
        Label passwordLabel = new Label("Password");

        var gridPane = new GridPane();
        gridPane.prefWidth(WINDOW_WIDTH);
        gridPane.prefHeight(WINDOW_HEIGHT);
        gridPane.getColumnConstraints().add(new ColumnConstraints(200));
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.addRow(0, loginLabel, loginTextField);
        gridPane.addRow(1, passwordLabel, passwordTextField);
        gridPane.addRow(2, registerButton, clearButton, loginButton);
        gridPane.addRow(3, infoLabel);
        infoLabel.setEllipsisString("");

        return gridPane;
    }

    private Pane makeTranslatorPane() {
        var gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        Label deeplLabel = new Label("Deepl");
        deeplLabel.setId("main-translator");
        Label amazonLabel = new Label("Amazon Translate");
        Label openaiLabel = new Label("OpenAI");

        translatorSourceText.setPrefColumnCount(20);
        gridPane.add(translatorSourceText, 0, 0, 1 , 1);
        gridPane.add(translatorTranslateButton, 2, 0, 2 ,1);

        gridPane.add(deeplLabel, 0, 1, 2, 1);
        gridPane.add(translatorDeeplTarget, 2, 1, 2, 1);

        gridPane.add(amazonLabel, 0, 2, 2, 1);
        gridPane.add(translatorAmazonTarget, 2, 2, 2, 1);

        gridPane.add(openaiLabel, 0, 3, 2 ,1);
        gridPane.add(translatorOpenAITarget, 2,  3, 2, 1);

        gridPane.add(translatorLoadButton, 2, 6, 2, 1);

        return gridPane;
    }

    private Pane makeVocabularyPane() {
        var vocabularyTable = new TableView<Translation>();
        vocabularyTable.setPlaceholder(new Label("No rows to show"));

        var sourceColumn = new TableColumn<Translation, String>("Source");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        var targetColumn = new TableColumn<Translation, String>("Target");
        targetColumn.setCellValueFactory(new PropertyValueFactory<>("target"));
        var countColumn = new TableColumn<Translation, Integer>("Counter");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("counter"));
        vocabularyTable.getColumns().addAll(sourceColumn, targetColumn, countColumn);

        var loadVocabularyButton = new Button("Load");
        loadVocabularyButton.setOnAction(event -> {
            vocabulary.pullAllDBTranslations();
            vocabularyTable.getItems().addAll(vocabulary.getDBTranslations());

        });


        var candidatesTable = new TableView<Map>();
        var candidateColumn = new TableColumn<Map, String>("Candidate");
        var counterColumn = new TableColumn<Map, Integer>("Counter");
        candidatesTable.getColumns().addAll(candidateColumn, counterColumn);

        ObservableList<Map<String, Integer>> candidates = FXCollections.<Map<String, Integer>>observableArrayList();
        var loadCandidatesButton = new Button("Load");
        loadCandidatesButton.setOnAction(event -> {
            vocabulary.loadCandidatesFromFile();
            candidates.add(vocabulary.getCandidates());
        });

        var gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.addRow(0, vocabularyTable, candidatesTable);
        gridPane.addRow(1, loadVocabularyButton, loadCandidatesButton);

        return gridPane;
    }
    private Pane makeSettingsPane() {
        var gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        var deeplAPILabel = new Label("Deepl API key");
        var amazonSecretKeyLabel = new Label("Amazon Secret Key");
        var amazonAccessKeyLabel = new Label("Amazon Access Key");
        var openaiKeyLabel = new Label("OpenAI Key");
        var mainTranslatorLabel = new Label("Main translator");

        gridPane.addRow(0, deeplAPILabel);
        gridPane.addRow(1, amazonSecretKeyLabel);
        gridPane.addRow(2, amazonAccessKeyLabel);
        gridPane.addRow(3, openaiKeyLabel);
        gridPane.addRow(4, mainTranslatorLabel);

        return gridPane;
    }

    private void fillTranslationLabels(String source, Label deepl, Label amazon, Label openai) {
        if(source.equals("") || lastSource.equals(source))
            return;

        deepl.setText(deeplTranslator.translate("de", "ru", source));
        amazon.setText(amazonTranslator.translate("de", "ru", source));
        openai.setText("No translator");

        lastSource = source;
    }

    private void infoFail(String message) {
        infoLabel.setStyle("-fx-text-fill: DarkRed;");
        infoLabel.setText(message);
        infoClearTimer.schedule(infoClearTask, 10000l);
    }

    private void infoSuccess(String message) {
        infoLabel.setStyle("-fx-text-fill: DarkGreen");
        infoLabel.setText(message);
        infoClearTimer.schedule(infoClearTask, 10000l);
    }

    private void infoClear() {
        infoLabel.setText("");
    }

    public static void main(String[] args) {
        launch(args);
    }
}