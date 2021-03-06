import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Random;

class GenericScrapper {

    static final Random random = new Random();

    static void innerScrapper(WebDriver driver, PrintWriter printWriter, JavascriptExecutor executor, int id)throws InterruptedException, NoSuchElementException {
        int k = 0, limit = 15;
        OUTER_LOOP:for (int i = 0; i < 10; i++) {
            for (int j = 0; j < limit; j++) {
                WebElement person = driver.findElement(By.xpath("//div[@id='list_content']/div["+String.valueOf(i+1)+"]/div[" + String.valueOf(j+1) + "]/div[4]/div/a"));
                if(Objects.isNull(person)) {
                    break OUTER_LOOP;
                }
                String name = person.getText();
                printWriter.println(id + ": " + name);
            }

            for (int j = 0; j < 20; j++) {
                executor.executeScript("window.scrollBy(0,450)", "");
            }
            Thread.sleep(random.nextInt(500));
        }
    }

    static void innerScrapper(WebDriver driver, PrintWriter printWriter, JavascriptExecutor executor)throws InterruptedException, NoSuchElementException {
        int k = 0, limit = 20;
        OUTER_LOOP:for (int i = 0; i < 50; i++) {
            for (int j = 0; j < limit; j++) {
                WebElement person = driver.findElement(By.xpath("//div[@id='list_content']/div[1]/div["+String.valueOf(++k)+"]/div[4]/div/a"));
                if(Objects.isNull(person)) {
                    break OUTER_LOOP;
                }
                String name = person.getText();
                printWriter.println(name + "\n");
            }

            for (int j = 0; j < 20; j++) {
                executor.executeScript("window.scrollBy(0,450)", "");
            }
            limit = 19;
            Thread.sleep(random.nextInt(2000));
        }
    }

}
