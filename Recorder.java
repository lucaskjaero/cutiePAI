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
         DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
         targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
         targetLine.open(format);
         targetLine.start();

         AudioInputStream ais = new AudioInputStream(targetLine);
         AudioSystem.write(ais, AudioFileFormat.Type.WAVE,
               new File(UUID.randomUUID() + ".wav"));
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      new Recorder(Integer.parseInt(JOptionPane
                     .showInputDialog("How long do you want to record (seconds)")));
   }

   public Recorder(int time) {
      Thread stopper = new Thread(new Runnable() {
         public void run() {
            try {
               Thread.sleep(time*1000);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
            targetLine.stop();
            targetLine.close();
            JOptionPane.showMessageDialog(null, "Finished.");
         }
      });
      stopper.start();
      start();
   }
}
