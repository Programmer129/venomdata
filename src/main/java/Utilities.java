import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.time.LocalDate;

class Utilities {

    static void log(String text, String thread) {
        System.out.println("Current name: "
                .concat(text).concat("!!!   Thread:  ")
                .concat(thread).concat("!!! Time: ")
                .concat(LocalDate.now().toString())
                .concat("!!! ->>>>>"));
    }

    static String getProjectPath() {
        return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    }

    static BufferedReader getFileReader(String path) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
    }

    static PrintWriter getWriter(String path) throws IOException {
        return new PrintWriter(new File(path));
    }

    static WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", Utilities.getProjectPath().concat("/chromedriver"));

        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");

        return new ChromeDriver(options);
    }

    static void authorise(WebDriver driver, String credential) throws InterruptedException {
        String [] auth = credential.split(" ");
        driver.get("https://vk.com/");

        driver.findElement(By.id("index_email")).sendKeys(auth[0]);

        Thread.sleep(500);

        driver.findElement(By.id("index_pass")).sendKeys(auth[1]);

        Thread.sleep(600);

        driver.findElement(By.id("index_login_button")).click();
    }
}
