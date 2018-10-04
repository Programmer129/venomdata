import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class PeopleTraversal {

    private static final String FILE_PATH = "/home/levani/IdeaProjects/vkscrapper/vkdata/names.txt";
    private static final String RES_FILE_PATH = "/home/levani/IdeaProjects/vkscrapper/vkdata/fullinfo1.txt";
    private static final String FRIENDS_FILE_PATH = "/home/levani/IdeaProjects/vkscrapper/vkdata/friends1.txt";
    private final WebDriver driver;

    PeopleTraversal(WebDriver driver) {
        this.driver = driver;
    }

    void traverse() throws InterruptedException, IOException {
        BufferedReader reader = getFileReader();
        PrintWriter writer = getWriter(RES_FILE_PATH);
        PrintWriter friendsWriter = getWriter(FRIENDS_FILE_PATH);

        driver.navigate().to("https://vk.com/search?c%5Bper_page%5D=40&c%5Bq%5D=Dmitry%20Kleschenok&c%5Bsection%5D=people");

        String name;
        int personId = 1;
        while (Objects.nonNull(name = reader.readLine())) {
            if(personId > 110) {
                String query = name.trim();

                System.out.println("Current ----------------> " + query);
                driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).clear();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(query);
                Thread.sleep(2000);
                driver.findElement(By.xpath("//div[@id='search_query_wrap']/div/div/input")).sendKeys(Keys.ENTER);
                Thread.sleep(1000);

                driver.findElement(By.xpath("//div[@id='results']/div/div[3]/div/a")).click();

                Thread.sleep(2000);

                try {
                    String info = innerScrapper(driver);
                    //   Files.write(Paths.get(RES_FILE_PATH), info.getBytes());
                    writer.println(info);
                } catch (NoSuchElementException e) {
                    System.out.println("No such element!");
                }

                scrapeFriendsPerPeople(driver, friendsWriter, personId);

                driver.navigate().back();
                driver.navigate().back();

                Thread.sleep(1000);
            }
            personId++;
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
        Scrapper scrapper = new Scrapper();
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        try {
            driver.findElement(By.xpath("//div[@id='narrow_column']/div[2]/aside/div/a[2]")).click();

            Thread.sleep(1000);

            scrapper.innerScrapper(driver, writer, executor, personId);
        } catch (NoSuchElementException | InterruptedException e) {
            System.out.println("No Element was found !");
        }
    }

    private BufferedReader getFileReader() throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(new File(FILE_PATH))));
    }

    private PrintWriter getWriter(String path) throws IOException {
        return new PrintWriter(new File(path));
    }
}
