package res;

import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller implements Initializable {

    @FXML
    private VBox image_background;

    @FXML
    private ImageView closeButton;

    @FXML
    private Text titleTextView;

    @FXML
    private Text filenameTextView;

    @FXML
    private Text artistnameTextView;

    @FXML
    private ImageView playpauseButton;

    @FXML
    private Slider trackSlider;

    @FXML
    private ImageView openButton;

    private Image playImage;
    private Image pauseImage;
    private File selectedFile;
    private MediaPlayer mediaPlayer;

    private Stage stage;
    private Duration duration;

    private TableView<Map> metadataTable = new TableView<>();
    private MapChangeListener<String, Object> metadataChangeListener;

    public Controller() {
        playImage = new Image(Controller.class.getResource("images/Play.png").toExternalForm());
        pauseImage = new Image(Controller.class.getResource("images/Pause.png").toExternalForm());
    }

    public void init(Stage stage) {
        stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeButton.setOnMouseClicked(e -> {
            System.out.println("Open button clicked");
            System.exit(0);
        });


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));

        openButton.setOnMouseClicked(e -> {
            System.out.println("Open button clicked");
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                Media media = null;

                Pattern spacePattern = Pattern.compile(" ");
                String selectedUrl = ("file://" + selectedFile.getAbsolutePath());
                Matcher matcher = spacePattern.matcher(selectedUrl);
                //selectedUrl = matcher.replaceAll("\\ ").replaceAll(" ", "%20");
                selectedUrl = "file:///" + selectedFile.getAbsolutePath().replace("\\", "/").replaceAll(" ", "%20");

                System.out.println(selectedUrl);
                media = new Media(selectedUrl);

                mediaPlayer = new MediaPlayer(media);

                trackSlider.setMin(0);
                trackSlider.setMax(mediaPlayer.getStopTime().toSeconds());

                mediaPlayer.setOnReady(() -> {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                    slide();
                    handlePlay();
                });

                mediaPlayer.statusProperty().addListener((observable, oldValue, newValue) -> {
                    System.out.println("State changed! to " + mediaPlayer.getStatus().toString());
                    if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                        openButton.setVisible(false);
                    }
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    openButton.setVisible(true);
                });

                mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    updateValues();
                });

                media.getMetadata().addListener(new MapChangeListener<String, Object>() {
                    public void onChanged(Change<? extends String, ? extends Object> ch) {
                        if (ch.wasAdded()) {
                            handleMetadata((String) ch.getKey(), ch.getValueAdded());

                        }
                    }

                    private void handleMetadata(String key, Object value) {
                        if(key.equals("title")){
                            titleTextView.setText((String)value);
                        }
                    }
                });
            }
        });

        playpauseButton.setOnMouseClicked(e -> {

        });
    }

    protected void updateValues() {
        if (trackSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                trackSlider.setDisable(duration.isUnknown());
                if (!trackSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !trackSlider.isValueChanging()) {
                    trackSlider.setValue(currentTime.divide(duration).toMillis() * 100.0D);
                }

            });
        }

    }

    private void handlePlay() {
        if (mediaPlayer.getMedia() == null) {
            Alert nomedia = new Alert(Alert.AlertType.ERROR);
            nomedia.setHeaderText(null);
            nomedia.setContentText("No media to play");
            nomedia.showAndWait();
        } else {
            if (selectedFile == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Media Found");
                alert.setHeaderText(null);
                alert.setContentText("You have Nothing Selected to play!");
                alert.showAndWait();
            }

            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    openButton.setVisible(true);
                } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED || mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
                    mediaPlayer.play();
                    slide();
                    play();
                    openButton.setVisible(false);
                }
            }
        }
    }

    private void play() {
        mediaPlayer.play();
    }

    private void stop() {
        mediaPlayer.stop();
    }

    public void slide() {
        System.out.println("Gonna slide ");
        trackSlider.valueProperty().addListener(ov -> {
            if (trackSlider.isValueChanging()) {
                if (duration != null) {
                    mediaPlayer.seek(duration.multiply(trackSlider.getValue() / 100.0D));
                }

                updateValues();
            }

        });
    }
}
