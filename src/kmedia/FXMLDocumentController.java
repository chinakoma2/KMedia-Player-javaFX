
package kmedia;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static javafx.scene.media.MediaPlayer.Status.PAUSED;
import static javafx.scene.media.MediaPlayer.Status.READY;
import static javafx.scene.media.MediaPlayer.Status.STOPPED;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;




/**
 *
 * @author dhifalka
 */
public class FXMLDocumentController implements Initializable {
    
    private MediaPlayer mediaPlayer;
    private String filePath;
    @FXML
    private MediaView mediaView;
    @FXML
    private Slider sliderSound ;
    @FXML
    private Text currentTime ;
    @FXML
    private Text totalTime ;
    private Boolean fichierExiste = false;
    @FXML
    private JFXSlider seekSlider;
    @FXML
    private StackPane pane;
    @FXML
    private MenuItem mniOpen;
    @FXML
    private Button stopButton;
    @FXML
    private void handleButtonAction(ActionEvent event) { 
       // choix du fichier
       FileChooser fileChooser = new FileChooser();
       FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("select a File (*.mp4)","*.mp4");
       fileChooser.getExtensionFilters().add(filter);
       File file = fileChooser.showOpenDialog(null);
       if (file != null){
           filePath = file.toURI().toString();
           
           // creation du media player
           Media media = new Media(filePath);
           mediaPlayer = new MediaPlayer(media);
           fichierExiste = true;
           mediaView.setMediaPlayer(mediaPlayer);
           
           //formatage du mediaView avec la taille du Stack Pane   
           mediaView.fitWidthProperty().bind(pane.widthProperty());
           mediaView.fitHeightProperty().bind(pane.heightProperty());
           
           // initialisation du Slider de Son
           sliderSound.setValue(mediaPlayer.getVolume() * 100);
          
           // listner sur le changement du valeur de SliderSound 
           sliderSound.valueProperty().addListener(new InvalidationListener() {
               @Override
               public void invalidated(Observable observable) {
                   mediaPlayer.setVolume(sliderSound.getValue() / 100);
               }
           });
           // listner sur TimeProperty  pour afficher le duree passe du video sur seekSlider et currentTime (Type Text)
           mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
               @Override
               public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                   if ((!seekSlider.isValueChanging()) && (!seekSlider.isPressed()))
                       seekSlider.setValue(newValue.toMillis()/ (mediaPlayer.getTotalDuration().toMillis()/100));
                    currentTime.setText(formatTime(newValue));
                    
               }
           });
          
           // listner sur le changement du seekSlider pour afficher le video a patire du duree choisi sur seekSlider
           seekSlider.setOnMouseClicked(new EventHandler<MouseEvent>() {
               @Override
               public void handle(MouseEvent event) {
                   mediaPlayer.seek(Duration.millis( (mediaPlayer.getTotalDuration().toMillis()/100)* seekSlider.getValue() ));
               }
           });
          
          // Pour  assurer que la vidéo est prête
           mediaPlayer.setOnReady(new Runnable() {
               @Override
               public void run() {
                    totalTime.setText(formatTime(media.getDuration()));
                    seekSlider.setDisable(false);
                    mediaPlayer.play();  
               }
           });
           
       }
    }
    
    @FXML
    private void PlayVideo(ActionEvent event){
        if (fichierExiste){
            if((mediaPlayer.getStatus().equals(READY))||(mediaPlayer.getStatus().equals(PAUSED))){
                mediaPlayer.play();
                mediaPlayer.setRate(1);
            }
            else
            {
                mediaPlayer.pause();
            }
        }
    }
    
    @FXML
    private void PauseVideo(ActionEvent event){
        if(!mediaPlayer.getStatus().equals(PAUSED))
            mediaPlayer.pause();
    }
    
    @FXML
    private void StopVideo(ActionEvent event){
        if (fichierExiste)
            if(!mediaPlayer.getStatus().equals(STOPPED))
            mediaPlayer.stop();
    }
    
    @FXML
    private void FastVideo(ActionEvent event){
        if (fichierExiste)
            mediaPlayer.setRate(1.5);
    }
    
    @FXML
    private void FasterVideo(ActionEvent event){
        if (fichierExiste)
            mediaPlayer.setRate(2);
    }
    
    @FXML
    private void SlowVideo(ActionEvent event){
        if (fichierExiste)
            mediaPlayer.setRate(0.75);
    }  
    
    @FXML
    private void SlowerVideo(ActionEvent event){
        if (fichierExiste)
            mediaPlayer.setRate(0.5);
    }
    
    @FXML
    private void ExitVideo(ActionEvent event){
        System.exit(0);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void Mute(ActionEvent event){
          sliderSound.setValue(0);
      }
    public void TurnUpSound(ActionEvent event){
        if(sliderSound.getValue()!=100)
            if (sliderSound.getValue()<=90)
                sliderSound.setValue(sliderSound.getValue()+10);
            else
                sliderSound.setValue(100);
        
      }
    public void ReduceSound(ActionEvent event){
        if(sliderSound.getValue()!=0)
            if(sliderSound.getValue()>=10)
                sliderSound.setValue(sliderSound.getValue()-10);
            else
                sliderSound.setValue(0);
      }
    
    private static String formatTime( Duration duration) {
        int intDuration = (int)Math.floor(duration.toSeconds());
        int durationHours = intDuration / (60 * 60);
        if (durationHours > 0) {
           intDuration -= durationHours * 60 * 60;
        }
        int durationMinutes = intDuration / 60;
        int durationSeconds = intDuration  - durationMinutes * 60;       
        if (durationHours > 0) {
           return String.format("%d:%02d:%02d", 

              durationHours, durationMinutes, durationSeconds);
        } else {
            return String.format("%02d:%02d",
            durationMinutes, 
                  durationSeconds);
        }  
   }
}
