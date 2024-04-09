package me.raven2r.translationview;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.raven2r.grevoc.core.Vocabulary;
import me.raven2r.grevoc.core.config.UserConfig;
import me.raven2r.grevoc.core.translator.Amazon;
import me.raven2r.grevoc.core.translator.Deepl;
import me.raven2r.grevoc.core.translator.Translates;

public class TranslatorView extends Application {
    public static final int WINDOW_WIDTH = 720;
    public static final int WINDOW_HEIGHT = 480;
    public static final int WINDOW_MIN_WIDTH = 480;
    public static final int WINDOW_MIN_HEIGHT = 360;

    // Registration scene
    TextField loginTextField = new TextField();
    TextField passwordTextField = new PasswordField();
    Button loginButton = new Button("Login");
    Button clearButton = new Button("Clear");
    Button registerButton = new Button("Register");
    String userName;

    // Info Label at the bottom
    Label infoLabel = new Label("");

    // Translator pane
    Translates amazonTranslator = new Amazon();
    Translates deeplTranslator = new Deepl();
    TextField translatorSourceText = new TextField();
    Button translatorTranslateButton = new Button("Translate");
    Button translatorLoadButton = new Button("Load");
    Label translatorDeeplTarget = new Label("[translation]");
    Label translatorAmazonTarget = new Label("[translation]");
    Label translatorOpenAITarget = new Label("[translation]");

    // Vocabulary
    UserConfig userConfig;
    Vocabulary vocabulary;

    // Settings


    @Override
    public void start(Stage primaryStage) {
        infoLabel.setId("info-label");
        infoLabel.setPadding(new Insets(10));

        translatorTranslateButton.setOnAction(actionEvent -> {
            fillTranslationLabels(translatorSourceText.getText(),
                    translatorDeeplTarget,
                    translatorAmazonTarget,
                    translatorOpenAITarget);
        });

        var loadButton = new Button("Load");
        loadButton.prefWidth(200);

        TabPane tabPane = new TabPane();
        Tab translatorTab = new Tab("Translator", makeTranslatorPane());
        Tab vocabularyTab = new Tab("Vocabulary");
        Tab settingsTab = new Tab("Settings");
        tabPane.getTabs().addAll(translatorTab, vocabularyTab, settingsTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        var rootVbox = new VBox(tabPane, infoLabel);
        rootVbox.setId("root");

        clearButton.setOnAction(event -> {
            loginTextField.clear();
            passwordTextField.clear();
        });
        loginButton.setOnAction(event -> {
            if(UserConfig.)
        });

        Scene loginScene = new Scene(makeLoginPane(), WINDOW_WIDTH, WINDOW_HEIGHT);
        loginScene.getStylesheets().add("style.css");

        Scene mainScene = new Scene(rootVbox, WINDOW_WIDTH, WINDOW_HEIGHT);
        mainScene.getStylesheets().add("style.css");

        primaryStage.setScene(loginScene);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.setTitle("Translator View");
        primaryStage.show();

//        var authStage = new Stage();
//        var authRoot = new VBox();
//        authStage.setScene(new Scene(authRoot, WINDOW_WIDTH, WINDOW_HEIGHT));
//        authStage.showAndWait();
    }


    private Pane makeLoginPane() {
        Label loginLabel = new Label("Login");
        Label passwordLabel = new Label("Password");

        var gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.addRow(0, loginLabel, loginTextField);
        gridPane.addRow(1, passwordLabel, passwordTextField);
        gridPane.addRow(2, registerButton, clearButton, loginButton);
        gridPane.addRow(3, infoLabel);

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
        var gridPane = new GridPane();
        return gridPane;
    }

    private Pane makeSettingsPane() {
        var gridPane = new GridPane();
        return gridPane;
    }

    private void fillTranslationLabels(String source, Label deepl, Label amazon, Label openai) {
        if(source.equals(""))
            return;

        deepl.setText(deeplTranslator.translate("de", "ru", source));
        amazon.setText(amazonTranslator.translate("de", "ru", source));
        openai.setText("No translator");
    }

    public static void main(String[] args) {
        launch(args);
    }
}