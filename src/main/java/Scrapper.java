import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.PrintWriter;

public class Scrapper {

    public static void main(String[] args) throws InterruptedException, IOException {
//        WebDriver driver = Utilities.getDriver();
//
//        PeopleTraversal traversal = new PeopleTraversal(driver);
//
//        Utilities.authorise(driver);
//
//        traversal.traverse(800, 1800);

        Thread [] jobbers = new Thread[1];

        for (int i = 0, j = 800; i < jobbers.length; i++, j+= 1000) {
            jobbers[i] = new Thread(jobber(904, j + 1000, Constants.FRIENDS_FILE_PATHS[i]), "Thread: ".concat(String.valueOf(i + 1)));
            jobbers[i].start();
        }
    }

    private static synchronized Runnable jobber(int from, int to, String path) {
        return () -> {
            WebDriver driver = Utilities.getDriver();

            PeopleTraversal traversal = new PeopleTraversal(driver);

            try {
                Utilities.authorise(driver);
                traversal.traverse(from, to, path);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        };
    }

    private synchronized Runnable jobber(char from, char to) {
        return () -> {
            try {
                WebDriver driver = Utilities.getDriver();

                Utilities.authorise(driver);

                Thread.sleep(1000);

                driver.navigate().to("https://vk.com/search?c%5Bper_page%5D=40&c%5Bq%5D=a&c%5Bsection%5D=people");
                Thread.sleep(1000);
                driver.findElement(By.xpath("//div[@id='search_filters_block']/div/div[17]/div[2]/div")).click();

                Thread.sleep(1000);

                scrape(driver, from, to);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void scrape(WebDriver driver, char from, char to) throws IOException, InterruptedException {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        PrintWriter printWriter = Utilities.getWriter(Constants.NAMES);

        for(char a = from; a <= to; a++) {
            for(char b = 'a'; b <= 'z'; b++) {
                for(char c = 'a'; c <= 'z'; c++) {
                    String query = String.valueOf(a) + b + c;
                    try {
                        driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).clear();
                        driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(query);
                        Thread.sleep(GenericScrapper.random.nextInt(1000));
                        driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(Keys.ENTER);
                        Thread.sleep(1000);
                        GenericScrapper.innerScrapper(driver, printWriter, executor);
                     //   driver.navigate().to("https://vk.com/search?c%5Bper_page%5D=40&c%5Bq%5D=a&c%5Bsection%5D=people");
                        System.out.println(query + " -------------------> Done!");

                    } catch (NoSuchElementException e) {
                        System.out.println(query + " -------------------> Error");
                    }
                }
            }
        }

        driver.quit();
        printWriter.close();
    }
}
