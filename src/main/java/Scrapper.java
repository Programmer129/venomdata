import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Random;

public class Scrapper {

    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException, IOException {
        Scrapper scrapper = new Scrapper();

        WebDriver driver = scrapper.getDriver();

        PeopleTraversal traversal = new PeopleTraversal(driver);

        scrapper.authorise(driver);

        traversal.traverse();

//        Thread [] jobbers = new Thread[4];
//
//        jobbers[0] = new Thread(scrapper.jobber('a', 'h'));
//        jobbers[1] = new Thread(scrapper.jobber('i', 'n'));
//        jobbers[2] = new Thread(scrapper.jobber('o', 's'));
//        jobbers[3] = new Thread(scrapper.jobber('t', 'z'));
//
//        for (Thread jobber : jobbers) {
//            jobber.start();
//        }
    }

    private synchronized Runnable jobber(char from, char to) {
        return () -> {
            try {
                WebDriver driver = getDriver();

                authorise(driver);

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
        PrintWriter printWriter = getWriter();

        for(char a = from; a <= to; a++) {
            for(char b = 'a'; b <= 'z'; b++) {
                for(char c = 'a'; c <= 'z'; c++) {
                    String query = String.valueOf(a) + b + c;
                    try {
                        driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).clear();
                        driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(query);
                        Thread.sleep(random.nextInt(1000));
                        driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(Keys.ENTER);
                        Thread.sleep(1000);
                        innerScrapper(driver, printWriter, executor);
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

    void innerScrapper(WebDriver driver, PrintWriter printWriter, JavascriptExecutor executor, int id)throws InterruptedException, NoSuchElementException {
        int k = 0, limit = 15;
        OUTER_LOOP:for (int i = 0; i < 50; i++) {
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

    private void innerScrapper(WebDriver driver, PrintWriter printWriter, JavascriptExecutor executor)throws InterruptedException, NoSuchElementException {
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

    private void authorise(WebDriver driver) throws InterruptedException {
        driver.get("https://vk.com/");

        driver.findElement(By.id("index_email")).sendKeys("avtobolashvili1297@gmail.com");

        Thread.sleep(500);

        driver.findElement(By.id("index_pass")).sendKeys("gameri21");

        Thread.sleep(600);

        driver.findElement(By.id("index_login_button")).click();
    }

    private PrintWriter getWriter() throws IOException {
        File file = new File("/home/levani/IdeaProjects/vkscrapper/vkdata/names1.txt");
        return new PrintWriter(new FileWriter(file));
    }

    private WebDriver getDriver() {
        System.setProperty("webdriver.chrome.driver", "/home/levani/IdeaProjects/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");

        return new ChromeDriver(options);
    }
}
