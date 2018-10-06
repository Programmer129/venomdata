import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

class PeopleTraversal {

    private final WebDriver driver;

    PeopleTraversal(WebDriver driver) {
        this.driver = driver;
    }

     void traverse(int from, int to, String path) throws InterruptedException, IOException {
        BufferedReader reader = Utilities.getFileReader(Constants.FILE_PATH);
        PrintWriter writer = Utilities.getWriter(Constants.RES_FILE_PATH);
        PrintWriter friendsWriter = Utilities.getWriter(path);

        driver.navigate().to("https://vk.com/search?c%5Bper_page%5D=40&c%5Bq%5D=Dmitry%20Kleschenok&c%5Bsection%5D=people");

        String name;
        AtomicInteger personId = new AtomicInteger(1);
        while (Objects.nonNull(name = reader.readLine())) {
            String query = name.trim();
            if(query.startsWith("Aaa") || query.endsWith("Aaa")) {
                continue;
            }
            if (personId.intValue() > from && personId.intValue() <= to) {
                try {
                    Utilities.log(query, Thread.currentThread().getName());
                    driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).clear();
                    Thread.sleep(1000);
                    driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(query);
                    Thread.sleep(2000);
                    driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(Keys.ENTER);
                    Thread.sleep(1000);

                    driver.findElement(By.xpath("//div[@id='results']/div/div[3]/div/a")).click();

                    Thread.sleep(2000);

//                    String info = innerScrapper(driver);
//                    writer.println(info);
                } catch (WebDriverException e) {
                    System.out.println("No such element!");
                }

                scrapeFriendsPerPeople(driver, friendsWriter, personId.intValue());

                driver.navigate().back();
                driver.navigate().back();

                Thread.sleep(1000);
            }
            personId.incrementAndGet();
        }

        reader.close();
        writer.close();
        friendsWriter.close();
    }

    private String innerScrapper(WebDriver driver) throws NoSuchElementException {
        List<WebElement> elements = new ArrayList<>();
        elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[1]/div")));
        elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[1]/div[2]/a[1]")));
        elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[2]/div")));
        elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[2]/div[2]/a[1]")));
        elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[3]/div")));
        elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[3]/div[2]/a[1]")));

        StringBuilder result = new StringBuilder("{ ");
        for (int i = 0; i < elements.size(); i++) {
            result.append(elements.get(i).getText()).append(i % 2 == 0 ? ":" : ",");
        }

        return result.insert(result.length() - 1, "}").toString();
    }

    private void scrapeFriendsPerPeople(WebDriver driver, PrintWriter writer, int personId) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        try {
            driver.findElement(By.xpath("//div[@id='narrow_column']/div[2]/aside/div/a[2]")).click();

            Thread.sleep(1000);

            GenericScrapper.innerScrapper(driver, writer, executor, personId);
        } catch (NoSuchElementException | InterruptedException e) {
            System.out.println("No Element was found !");
        }
    }
}
