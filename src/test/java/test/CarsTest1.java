package test;

import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CarsTest1 {

    private static final int WAIT_MAX = 4;
    static WebDriver driver;

    @BeforeClass
    public static void setup() {
        /*########################### IMPORTANT ######################*/
 /*## Change this, according to your own OS and location of driver(s) ##*/
 /*############################################################*/
        //System.setProperty("webdriver.gecko.driver", "C:\\diverse\\drivers\\geckodriver.exe");
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\micha\\Desktop\\skolearbejde\\soft\\test\\drivers\\chromedriver.exe");

        //Reset Database
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        //Reset Database 
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
    }

    @Test
    //Verify that page is loaded and all expected data are visible
    public void test1() throws Exception {
        System.out.println("test1");
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(5));
            return true;
        });
    }

    //Write 2002 in the filter text and verify that we only see two rows
    @Test
    public void test2() throws Exception {
        System.out.println("testFilter");
        WebElement element = driver.findElement(By.id("filter"));
        WebElement e = driver.findElement(By.tagName("tbody"));
        driver.findElement(By.id("filter")).sendKeys("2002");
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        Assert.assertThat(rows.size(), is(2));
    }

    //Clear the text in the filter text and verify that we have the original five rows
    @Test
    public void test3() throws Exception {
        System.out.println("testFilter2");
        WebElement element = driver.findElement(By.id("filter"));
        WebElement e = driver.findElement(By.tagName("tbody"));
        driver.findElement(By.id("filter")).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id("filter")).sendKeys(Keys.BACK_SPACE);
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        Assert.assertThat(rows.size(), is(5));
        Assert.assertThat(element.getText(), is(""));
    }

    /*Click the sort “button” for Year, and verify that the top row contains
        the car with id 938 and the last row the car with id = 940.
     */
    @Test
    public void test4() throws Exception {
        System.out.println("testSortYear");
        String[] temp;
        String[] temp1;
        WebElement e = driver.findElement(By.tagName("tbody"));
        driver.findElement(By.id("h_year")).click();
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        temp = rows.get(0).getText().split(" ");
        temp1 = rows.get(rows.size() - 1).getText().split(" ");
        String expected1 = temp[0];
        String expected2 = temp1[0];
        Assert.assertThat(rows.size(), is(5));
        Assert.assertThat(Integer.parseInt(expected1), is(938));
        Assert.assertThat(Integer.parseInt(expected2), is(940));
    }

    /*
    Press the edit button for the car with the id 938. Change the Description to "Cool car", and save changes.
    Verify that the row for car with id 938 now contains "Cool car" in the Description column
     */
    @Test
    public void test5() throws Exception {
        System.out.println("testEdit");
        WebElement e = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        rows.get(1).findElement(By.linkText("Edit")).click();
        WebElement descE = driver.findElement(By.name("description"));
        descE.sendKeys(Keys.CONTROL + "a");
        descE.sendKeys(Keys.BACK_SPACE);
        descE.sendKeys("Cool car");
        WebElement saveE = driver.findElement(By.id("save"));
        saveE.click();
        rows.get(rows.size() - 2).findElement(By.linkText("Edit")).click();
        descE.sendKeys(Keys.CONTROL + "a");
        descE.sendKeys(Keys.BACK_SPACE);
        descE.sendKeys("Cool car");
        saveE.click();
        List<WebElement> curr = rows.get(1).findElements(By.tagName("td"));
        Assert.assertThat(curr.get(5).getText(), is("Cool car"));
        curr = rows.get(rows.size() - 2).findElements(By.tagName("td"));
        Assert.assertThat(curr.get(5).getText(), is("Cool car"));
    }

    /*
    Click the new “Car Button”, and click the “Save Car” button. Verify that we have an error message with the text
    “All fields are required” and we still only have five rows in the all cars table.
     */
    @Test
    public void test6() throws Exception {
        System.out.println("testNewCarError");
        WebElement e = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        driver.findElement(By.id("new")).click();
        driver.findElement(By.id("save")).click();
        Assert.assertThat(driver.findElement(By.id("submiterr")).getText(), is("All fields are required"));
        Assert.assertThat(rows.size(), is(5));
    }

    /*
    Click the new Car Button, and add the following values for a new car
    a. Year: 2008
    b. Registered: 2002-5-5
    c. Make: Kia
    d. Model: Rio
    e. Description: As new
    f. Price: 31000
    Click “Save car”, and verify that the new car was added to the table with all the other cars.
     */
    @Test
    public void test7() throws Exception {
        System.out.println("testNewCar");
        driver.findElement(By.id("new")).click();
        driver.findElement(By.id("year")).sendKeys("2008");
        driver.findElement(By.id("registered")).sendKeys("2002-5-5");
        driver.findElement(By.id("make")).sendKeys("Kia");
        driver.findElement(By.id("model")).sendKeys("Rio");
        driver.findElement(By.id("description")).sendKeys("As new");
        driver.findElement(By.id("price")).sendKeys("31000");
        driver.findElement(By.id("save")).click();
        WebElement e = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        Assert.assertThat(rows.size(), is(6));
    }
}
