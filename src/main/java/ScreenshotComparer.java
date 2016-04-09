import org.apache.commons.io.FileUtils;
import org.im4java.core.CompareCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.StandardStream;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
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
    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotComparer.class);
    private WebDriver driver;
    private static final String PREV_SCR = "screenshots/prev_scr.png";
    private static final String NEW_SCR = "screenshots/new_scr.png";
    private static final String DIFF_SCR = "screenshots/diff_scr.png";
    private Capabilities caps;
    private URI uri;

    ScreenshotComparer(URI uri) {
        caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        ((DesiredCapabilities) caps).setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "/usr/bin/phantomjs"
        );
        driver = new PhantomJSDriver(caps);
        driver.manage().window().maximize();
        this.uri = uri;
    }

    private File takeScreenshot(String path) {
        driver.get(uri.toString());
        WebDriverWait webDriverWait = new WebDriverWait(driver, 12, 180);
        ExpectedCondition<Boolean> condition = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return (Boolean) js.executeScript("return jQuery.active == 0");
            }
        };
        webDriverWait.until(condition);
        File scrFile = ((TakesScreenshot) driver).
                getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrFile;
    }


    private static boolean compareImages(String exp, String cur, String diff) {
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
        } catch (Exception ex) {
            return false;
        }
    }

    protected void finilize() {
        driver.close();
    }

    public void run() {
        LOG.info("Screenshoter initialized");
        takeScreenshot(NEW_SCR);
        LOG.info("Screenshot are taken and saved in {}", NEW_SCR);
        /*boolean isEqualsScreenshot = compareImages(NEW_SCR,PREV_SCR,DIFF_SCR);
        if (isEqualsScreenshot) {
            System.out.println("Screenshots are equals");
        } else {
            System.out.println("Screenshots are different");
        }*/
    }

}
