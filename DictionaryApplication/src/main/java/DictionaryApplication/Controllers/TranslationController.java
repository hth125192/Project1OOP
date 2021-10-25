package DictionaryApplication.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class TranslationController implements Initializable {
    private String sourceLanguage = "en";
    private String toLanguage = "vi";
    private boolean isToVietnameseLang = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    handleOnClickTranslateBtn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sourceLangField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (sourceLangField.getText().trim().isEmpty()) translateBtn.setDisable(true);
                else translateBtn.setDisable(false);
            }
        });

        translateBtn.setDisable(true);
        toLangField.setEditable(false);
    }

    @FXML
    private void handleOnClickTranslateBtn() throws IOException {
        String rootAPI = "https://clients5.google.com/translate_a/t?client=dict-chrome-ex&sl=" + sourceLanguage + "&tl=" + toLanguage + "&dt=t&q=";
        String srcText = sourceLangField.getText();
        String urlString = rootAPI + srcText;
        urlString = urlString.replace(" ", "%20");

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = in.readLine()) != null) content.append(line);

        in.close();
        con.disconnect();

        JSONParser jsonParse = new JSONParser();
        try {
            JSONObject data = (JSONObject) jsonParse.parse(content.toString());
            JSONArray sentences = (JSONArray) data.get("sentences");
            JSONObject jsonObject = (JSONObject) sentences.get(0);
            String trans = (String) jsonObject.get("trans");
            toLangField.setText(trans);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML
    private void handleOnClickSwitchToggle() {
        sourceLangField.clear();
        toLangField.clear();
        if (isToVietnameseLang) {
            englishLabel.setLayoutX(426);
            vietnameseLabel.setLayoutX(104);
            sourceLanguage = "vi";
            toLanguage = "en";
        } else {
            englishLabel.setLayoutX(100);
            vietnameseLabel.setLayoutX(426);
            sourceLanguage = "en";
            toLanguage = "vi";
        }
        isToVietnameseLang = !isToVietnameseLang;
    }

    @FXML
    private TextArea sourceLangField, toLangField;

    @FXML
    private Button translateBtn;

    @FXML
    private Label englishLabel , vietnameseLabel;
}
