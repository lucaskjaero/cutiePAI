// The code was obtained from the link below and modified to record audio over a fixed amount of time
// http://www.java-gaming.org/index.php?topic=36723.0
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

public class Recorder {
   TargetDataLine targetLine;
   AudioFormat format = new AudioFormat(16000, 16, 1, true, true);

   public void start() {
      try {
          // Create and open the target data line to start recording
         DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
         targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
         targetLine.open(format);
         targetLine.start();

          // Store the audio into a .wave file with randomly generated universally unique ID as filename
          // TODO: Use recognizable filename
         AudioInputStream ais = new AudioInputStream(targetLine);
         AudioSystem.write(ais, AudioFileFormat.Type.WAVE,
               new File(UUID.randomUUID() + ".wav"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
       // Ask the user how long the recording will last (seconds)
       // TODO: Change the process so that it restarts automatically after fixed amount of time (eg. 10 sec.)
      new Recorder(Integer.parseInt(JOptionPane
                     .showInputDialog("How long do you want to record (seconds)")));
   }

   public Recorder(int time) {
      Thread stopper = new Thread(new Runnable() {
         public void run() {
            try {
                // Put the thread to sleep for [time] seconds
                // Meanwhile, the user's audio is being recorded in start()
               Thread.sleep(time*1000);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
             
             // After [time] seconds, stop the recording
            targetLine.stop();
            targetLine.close();
            JOptionPane.showMessageDialog(null, "Finished.");
         }
      });
       
       // Start the timer thread before calling start(), which starts the recording
      stopper.start();
      start();
   }
}
