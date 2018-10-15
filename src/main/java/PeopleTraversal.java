import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        PrintWriter writer = Utilities.getWriter(Constants.RES_FILE_PATH[0]);
        PrintWriter friendsWriter = Utilities.getWriter(path);

        driver.navigate().to("https://vk.com/search?c%5Bper_page%5D=40&c%5Bq%5D=Dmitry%20Kleschenok&c%5Bsection%5D=people");

        String name;
        AtomicInteger personId = new AtomicInteger(1);
        while (Objects.nonNull(name = reader.readLine())) {
            String query = name.trim();
            if(query.length() == 0) {
                personId.incrementAndGet();
                continue;
            }
            String [] parts = query.split(" ");
            if(parts.length > 2 ||
                    parts.length < 1      ||
                    query.contains("Aaa") ||
                    query.contains("Aab") ||
                    query.contains("Aaf") ||
                    query.contains("Aag") ||
                    query.contains("Aaj") ||
                    query.contains("Aak") ||
                    query.contains("Aal") ||
                    query.contains("Aam") ||
                    query.contains("Aao") ||
                    query.contains("Aap") ||
                    query.contains("Aaq") ||
                    query.contains("Aar") ||
                    query.contains("Aas") ||
                    query.contains("Aat") ||
                    query.contains("Aau") ||
                    query.contains("Aav") ||
                    query.contains("Aax") ||
                    query.contains("Aay") ||
                    query.contains("Aaz")) {
                personId.incrementAndGet();
                continue;
            }
            if (personId.intValue() > from && personId.intValue() <= to) {
                try {
                    Utilities.log(query, Thread.currentThread().getName());
                    GenericScrapper.searchPerson(driver, query);

//                    String info = innerScrapper(driver);
//                    writer.println(info);
                } catch (WebDriverException e) {
                    System.out.println("No such element!");
                    personId.incrementAndGet();
                    continue;
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

    void minTraverse(int to, int from, String path, String readPath) throws IOException, InterruptedException {
        PrintWriter writer = Utilities.getWriter(path);
        BufferedReader reader = Utilities.getFileReader(readPath);

        driver.navigate().to("https://vk.com/search?c%5Bper_page%5D=40&c%5Bq%5D=Dmitry%20Kleschenok&c%5Bsection%5D=people");

        AtomicInteger personId = new AtomicInteger(1);
        String name;
        while ((name = reader.readLine()) != null) {
            if(personId.intValue() >= to && personId.intValue() <= from) {
                String query = name.trim();
                try {
                    GenericScrapper.searchPerson(driver, query);

                    Thread.sleep(1000);

                    String info = parseElementsList();

                    Thread.sleep(1000);

                    writer.println(info);

                    driver.navigate().back();
                } catch (WebDriverException e) {
                    System.out.println(e.getMessage());
                    writer.println("NAN");
                }
            }
            personId.incrementAndGet();
        }

        writer.close();
        reader.close();

        driver.quit();
    }

    private String parseElementsList() {
        List<WebElement> elements = new ArrayList<>();

        innerScrapper(driver, elements);

        StringBuilder result = new StringBuilder("{ ");
        for (WebElement element : elements) {
            if (element.getText() != null) {
                result.append(element.getText()).append(" ");
            }
        }

        return result.insert(result.length() - 1, " }").toString();
    }

    private void innerScrapper(WebDriver driver, List<WebElement> elements) {
        try {
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[1]/div")));
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[1]/div[2]/a[1]")));
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[1]/div[2]/a[2]")));
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[2]/div")));
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[2]/div[2]/a")));
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[3]/div")));
            elements.add(driver.findElement(By.xpath("//div[@id='page_info_wrap']/div[2]/div[3]/div[2]/a")));
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

        try {
            driver.findElement(By.cssSelector("#profile_short > div.profile_more_info > a > span.profile_label_more")).click();

            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[1]/div[1]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[1]/div[2]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[2]/div[1]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[2]/div[2]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[3]/div[1]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[3]/div[2]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[4]/div[1]")));
            elements.add(driver.findElement(By.xpath("//*[@id=\"profile_full\"]/div/div[2]/div[4]/div[2]")));
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    private void scrapeFriendsPerPeople(WebDriver driver, PrintWriter writer, int personId) throws InterruptedException {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        try {
            driver.findElement(By.xpath("//div[@id='narrow_column']/div[2]/aside/div/a[2]")).click();

            Thread.sleep(1000);

            GenericScrapper.innerScrapper(driver, writer, executor, personId);
        } catch (NoSuchElementException e) {
            System.out.println("No Such element !");
        }
    }
}
