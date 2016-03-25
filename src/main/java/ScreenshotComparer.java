import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.process.StandardStream;
import org.im4java.core.IMOperation;
import org.jboss.netty.util.Timeout;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.TimerTask;

/**
 * Created by shmgrinsky on 25.03.16.
 */
public class ScreenshotComparer extends TimerTask {
    private File previousScr;
    private WebDriver driver;
    private URI pageUri;
    private static final String PREV_SCR ="prev_scr";
    private static final String NEW_SCR ="new_scr";
    private static final String DIFF_SCR = "diff_scr";


    ScreenshotComparer(URI uri){
        driver = new FirefoxDriver();
        driver.get(uri.toString());
        previousScr = takeScreenshot(PREV_SCR);
    }

    private File takeScreenshot(String path){
        File scrFile = ((TakesScreenshot)driver).
                getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrFile;
    }


    private static boolean  compareImages (String exp, String cur, String diff) {
        // This instance wraps the compare command
        CompareCmd compare = new CompareCmd();

        // For metric-output
        compare.setErrorConsumer(StandardStream.STDERR);
        IMOperation cmpOp = new IMOperation();
        // Set the compare metric
        cmpOp.metric("mae");

        // Add the expected image
        cmpOp.addImage(exp);

        // Add the current image
        cmpOp.addImage(cur);

        // This stores the difference
        cmpOp.addImage(diff);

        try {
            // Do the compare
            compare.run(cmpOp);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    protected void finilize() {
        driver.close();
    }

    public void run(){
        File newScr = takeScreenshot(NEW_SCR);
        boolean isEqualsScreenshot = compareImages(NEW_SCR,PREV_SCR,DIFF_SCR);
        if (isEqualsScreenshot) {
            System.out.println("Screenshots are equals");
        } else {
            System.out.println("Screenshots are different");
        }
    }

}
