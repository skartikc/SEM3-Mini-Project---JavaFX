package sample;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.media.SubtitleTrack;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.crypto.spec.PSource;
import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    MediaPlayer player;
    @FXML
    private BorderPane bp;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button playBtn;

    @FXML
    private Button prevBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Slider timeSlider;
    @FXML
    private Slider volumeslider;
    @FXML
    private Button muteBtn;
    @FXML
    private Button ssBtn;
   


    private double diff=10;






    @FXML
    void openVideomenu(ActionEvent event) {
        try {
            System.out.println("Open song clicked");
            FileChooser chooser = new FileChooser();
            File file = chooser.showOpenDialog(null);

            Media m = new Media(file.toURI().toURL().toString());

            if(player!= null){
                player.dispose();
            }

            player = new MediaPlayer(m);
            mediaView.setMediaPlayer(player);
            DoubleProperty width= mediaView.fitWidthProperty();
            DoubleProperty height=mediaView.fitHeightProperty();
            width.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
            height.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));

            /*mediaView.setFitWidth(2000);
            mediaView.setFitHeight(800);*/


            ///volumeslider
            volumeslider.setValue(player.getVolume()*100);
            volumeslider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    player.setVolume(volumeslider.getValue()/100);
                    try {
                        if ((volumeslider.getValue() / 100) == 0)
                            muteBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/soundofficon.png"))));
                        else
                            muteBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/soundonicon.png"))));
                    }
                    catch ( FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            ///time slider


            player.setOnReady(()->{
                timeSlider.setMin(0);
                timeSlider.setMax(player.getMedia().getDuration().toMinutes());


                timeSlider.setValue(0);
                try {
                    playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/play.png"))));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            });


            ///listener on player

            player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    //slider
                   Duration d= player.getCurrentTime();
                   timeSlider.setValue(d.toMinutes());


                }
            });
            ///for slider forwars

            timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if(timeSlider.isPressed()){
                        double val=timeSlider.getValue();
                        player.seek(new Duration(val*60*1000));
                    }
                }
            });

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void play(ActionEvent event) {

        try{
            MediaPlayer.Status status=player.getStatus();

            if(status==MediaPlayer.Status.PLAYING)
            {
                player.pause();
                ///playBtn.setText("Play");
                playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/play.png"))));

            }
            else
            {
                player.play();
                ///playBtn.setText("Pause");
                playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/pause.png"))));

            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/play.png"))));
            prevBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/previous.png"))));
            nextBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/next.png"))));
            muteBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/soundonicon.png"))));
            ssBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/snap.png"))));


            final Tooltip tooltiplay= new Tooltip();
            tooltiplay.setText("Play/Pause");
            playBtn.setTooltip(tooltiplay);

            final Tooltip tooltipnext= new Tooltip();
            tooltipnext.setText("Forward 10s");
            nextBtn.setTooltip(tooltipnext);

            final Tooltip tooltipprev= new Tooltip();
            tooltipprev.setText("10s backward");
            prevBtn.setTooltip(tooltipprev);


            final Tooltip tooltipvolume= new Tooltip();
            tooltipvolume.setText("Volume");
            volumeslider.setTooltip(tooltipvolume);


            final Tooltip tooltipmute= new Tooltip();
            tooltipmute.setText("Mute");
            muteBtn.setTooltip(tooltipmute);




            final Tooltip tooltipss= new Tooltip();
            tooltipss.setText("Screenshot");
            ssBtn.setTooltip(tooltipss);














        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void playBtnclick(ActionEvent event) {
        double d= player.getCurrentTime().toSeconds();
        d=d+diff;
        player.seek(new Duration(d*1000));

        

    }

    @FXML
    void rotateBtn(ActionEvent event) {
        double Rd=mediaView.getRotate();
        mediaView.setRotate(Rd+90);

    }
    @FXML
    void Restart(ActionEvent event) {
        player.seek(player.getStartTime());
        player.play();
    }
    @FXML
    void fastforward(ActionEvent event) {
        if(player.getRate()==0.5)
        {
            player.setRate(1);
        }
        else
            player.setRate(2);
    }
    @FXML
    void Slowmotion(ActionEvent event) {
        if(player.getRate()==2)
        {
            player.setRate(1);
        }
        else
        player.setRate(0.5);
    }


    @FXML
    void exit(ActionEvent event) {
        int result = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit?");
        if(result==JOptionPane.YES_OPTION)
            System.exit(0);
    }

    @FXML
    void prevBtnclick(ActionEvent event) {
        double d = player.getCurrentTime().toSeconds();
        d=d-diff;
        player.seek(new Duration(d*1000));
    }






    @FXML
    void snapshot(ActionEvent event) throws IOException {
        String s = ":";
        String path = "src/sample/screenshots/IMG " + new Date() + ".png";
        String newPath = path.replaceAll(s,".");
        System.out.println("User asks snap.");
        WritableImage img = mediaView.snapshot(new SnapshotParameters(), null);
        BufferedImage bufImg = SwingFXUtils.fromFXImage(img, null);
        ImageIO.write(bufImg, "png", new File(newPath));
    }
    @FXML
    void aboutthisplayer(ActionEvent event) {
        JOptionPane.showMessageDialog(null, "This video player includes all the basic features like play,pause skip and mute. " +
                "\nIt contains advance tools like screenshot,Skip to particular interaval etc." +
                "\nThis video player is created by Bhushan ,Kartik & Aryan as Mini-Project");
    }

    public static FileFilter getFileFilter() {
        String[] acceptableExtensions = new String[]{"mp3", "aiff", "wav", "mp4", "mpeg-4", "flv"};
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                for (String extension : acceptableExtensions) {
                    if (pathname.getName().toLowerCase().endsWith(extension))
                        return true;
                }
                return false;
            }
        };

    }


    @FXML
    void mute(ActionEvent event) {
        try {
            double x = player.getVolume();
            if (x != 0) {
                player.setVolume(0);
                muteBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/soundofficon.png"))));


            } else {
                player.setVolume(75);
                muteBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/sample/icons/soundonicon.png"))));

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }





}

