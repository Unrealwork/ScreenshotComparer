
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shmgrinsky on 25.03.16.
 */
public class Application {
    public static void main (String[] args){
        Timer timer = new Timer();
        try {
            Date date = new Date();
            long delay = 5000;
            TimerTask task = new ScreenshotComparer(new URI("http://apps.axibase.com/chartlab/c1acecc0/1/"));
            timer.schedule(task, date);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
