import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.io.PrintWriter;

public class Scrapper {

    public static void main(String[] args) {
        Thread [] jobbers = new Thread[8];

        jobbers[0] = new Thread(jobber(5000, 10000, Constants.RES_FILE_PATH[0], Constants.CREDENTIALS[2]));
        jobbers[1] = new Thread(jobber(10001, 15000, Constants.RES_FILE_PATH[1], Constants.CREDENTIALS[0]));
        jobbers[2] = new Thread(jobber(15001, 20000, Constants.RES_FILE_PATH[2], Constants.CREDENTIALS[1]));
        jobbers[3] = new Thread(jobber(20001, 25000, Constants.RES_FILE_PATH[3], Constants.CREDENTIALS[1]));
        jobbers[4] = new Thread(jobber(25001, 30000, Constants.RES_FILE_PATH[4], Constants.CREDENTIALS[1]));
        jobbers[5] = new Thread(jobber(30001, 35000, Constants.RES_FILE_PATH[5], Constants.CREDENTIALS[1]));
        jobbers[6] = new Thread(jobber(35001, 40000, Constants.RES_FILE_PATH[6], Constants.CREDENTIALS[1]));
        jobbers[7] = new Thread(jobber(40001, 45000, Constants.RES_FILE_PATH[7], Constants.CREDENTIALS[1]));

        jobbers[0].start();
//        jobbers[1].start();
//        jobbers[2].start();
//        jobbers[3].start();
//        jobbers[4].start();
//        jobbers[5].start();
//        jobbers[6].start();
//        jobbers[7].start();
    }

    private static Runnable jobber(int from, int to, String path, String credentials) {
        return () -> {
            WebDriver driver = Utilities.getDriver();

            PeopleTraversal traversal = new PeopleTraversal(driver);

            try {
              //  Utilities.authorise(driver, credentials);
                traversal.minTraverse(from, to, path , Constants.ALL_PEOPLE);
            } catch (InterruptedException | IOException | WebDriverException e) {
                System.out.println(e.getMessage());
            }
        };
    }

    private synchronized Runnable jobber(char from, char to, String credentials) {
        return () -> {
            try {
                WebDriver driver = Utilities.getDriver();

                Utilities.authorise(driver, credentials);

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
