import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Scrapper {

    private static String [] FRIENDS = {
      Utilities.getProjectPath().concat("/vkdata/friends.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends1.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends2.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends3.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends4.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends5.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends6.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends7.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends8.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends9.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends10.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends11.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends12.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends13.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends14.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends15.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends16.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends17.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends18.txt"),
      Utilities.getProjectPath().concat("/vkdata/friends19.txt")
    };

    public static void main(String[] args) throws IOException {

        PrintWriter writer = Utilities.getWriter(Utilities.getProjectPath().concat("/vkdata/allpeople.txt"));

        for (String FRIEND : FRIENDS) {
            BufferedReader reader = Utilities.getFileReader(FRIEND);
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line.split(": ")[1]);
            }

            reader.close();
        }

        writer.close();

//        Thread [] jobbers = new Thread[2];
//
//        for (int i = 0, j = 800; i < jobbers.length; i++, j+= 4000) {
//            if(i == 0)
//                jobbers[i] = new Thread(jobber(4420, 8000, Constants.FRIENDS_FILE_PATHS[i], Constants.CREDENTIALS[i]), "Thread: ".concat(String.valueOf(i + 1)));
//            else
//                jobbers[i] = new Thread(jobber(9694, 11000, Constants.FRIENDS_FILE_PATHS[i], Constants.CREDENTIALS[i]), "Thread: ".concat(String.valueOf(i + 1)));
//            jobbers[i].start();
//        }
    }

    private static synchronized Runnable jobber(int from, int to, String path, String credentials) {
        return () -> {
            WebDriver driver = Utilities.getDriver();

            PeopleTraversal traversal = new PeopleTraversal(driver);

            try {
                Utilities.authorise(driver, credentials);
                traversal.traverse(from, to, path);
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
