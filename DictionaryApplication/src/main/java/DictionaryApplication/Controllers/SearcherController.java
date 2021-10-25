package DictionaryApplication.Controllers;

import DictionaryApplication.Alerts.Alerts;
import DictionaryApplication.DictionaryCommandLine.Dictionary;
import DictionaryApplication.DictionaryCommandLine.DictionaryManagement;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SearcherController implements Initializable {
    private Dictionary dictionary = new Dictionary();
    private DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private final String path = "src/main/resources/Utils/dictionaries.txt";
    ObservableList<String> list = FXCollections.observableArrayList();
    private Alerts alerts = new Alerts();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictionaryManagement.insertFromFile(dictionary, path);
        System.out.println(dictionary.size());
        dictionaryManagement.setTrie(dictionary);
        setListDefault(0);

        searchTerm.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (searchTerm.getText().isEmpty()) {
                    cancelBtn.setVisible(false);
                    setListDefault(0);
                } else {
                    cancelBtn.setVisible(true);
                    handleOnKeyTyped();
                }
            }
        });

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchTerm.clear();
                notAvailableAlert.setVisible(false);
                cancelBtn.setVisible(false);
                setListDefault(0);
            }
        });

        explanation.setEditable(false);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        notAvailableAlert.setVisible(false);
    }

    @FXML
    private void handleOnKeyTyped() {
        list.clear();
        String searchKey = searchTerm.getText().trim();
        list = dictionaryManagement.lookupWord(dictionary, searchKey);
        if (list.isEmpty()) {
            notAvailableAlert.setVisible(true);
            setListDefault(firstIndexOfListFound);
        } else {
            notAvailableAlert.setVisible(false);
            headerList.setText("Kết quả");
            listResults.setItems(list);
            firstIndexOfListFound = dictionaryManagement.searchWord(dictionary, list.get(0));
        }
    }

    @FXML
    private void handleMouseClickAWord(MouseEvent arg0) {
        String selectedWord = listResults.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            indexOfSelectedWord = dictionaryManagement.searchWord(dictionary, selectedWord);
            if (indexOfSelectedWord == -1) return;
            englishWord.setText(dictionary.get(indexOfSelectedWord).getWordTarget());
            explanation.setText(dictionary.get(indexOfSelectedWord).getWordExplain());
            headerOfExplanation.setVisible(true);
            explanation.setVisible(true);
            explanation.setEditable(false);
            saveBtn.setVisible(false);
        }
    }

    @FXML
    private void handleClickEditBtn() {
        explanation.setEditable(true);
        saveBtn.setVisible(true);
        alerts.showAlertInfo("Information", "Bạn đã cho phép chỉnh sửa nghĩa từ này!");
    }

    @FXML
    private void handleClickSoundBtn() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        Voice voice = VoiceManager.getInstance().getVoice("kevin16");
        if (voice != null) {
            voice.allocate();
            voice.speak(dictionary.get(indexOfSelectedWord).getWordTarget());
        } else throw new IllegalStateException("Cannot find voice: kevin16");
    }

    @FXML
    private void handleClickSaveBtn() {
        Alert alertConfirmation = alerts.alertConfirmation("Update", "Bạn chắc chắn muốn cập nhật nghĩa từ này ?");
        Optional<ButtonType> option = alertConfirmation.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.updateWord(dictionary, indexOfSelectedWord, explanation.getText(), path);
            alerts.showAlertInfo("Information", "Cập nhập thành công!");
        } else alerts.showAlertInfo("Information", "Thay đổi không được công nhận!");
        saveBtn.setVisible(false);
        explanation.setEditable(false);
    }

    @FXML
    private void handleClickDeleteBtn() {
        Alert alertWarning = alerts.alertWarning("Delete", "Bạn chắc chắn muốn xóa từ này?");
        alertWarning.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = alertWarning.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.deleteWord(dictionary, indexOfSelectedWord, path);
            refreshAfterDeleting();
            alerts.showAlertInfo("Information", "Xóa thành công");
        } else alerts.showAlertInfo("Information", "Thay đổi không được công nhận");
    }

    private void refreshAfterDeleting() {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(englishWord.getText())) {
                list.remove(i);
                break;
            }
        listResults.setItems(list);
        headerOfExplanation.setVisible(false);
        explanation.setVisible(false);
    }

    private void setListDefault(int index) {
        if (index == 0) headerList.setText("15 từ đầu tiên");
        else headerList.setText("Kết quả liên quan");
        list.clear();
        for (int i = index; i < index + 15; i++) list.add(dictionary.get(i).getWordTarget());
        listResults.setItems(list);
        englishWord.setText(dictionary.get(index).getWordTarget());
        explanation.setText(dictionary.get(index).getWordExplain());
    }

    @FXML
    private TextField searchTerm;

    @FXML
    private Button cancelBtn, saveBtn;

    @FXML
    private Label englishWord, headerList, notAvailableAlert;

    @FXML
    private TextArea explanation;

    @FXML
    private ListView<String> listResults;

    @FXML
    private Pane headerOfExplanation;
}
