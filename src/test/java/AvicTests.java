import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

public class AvicTests {
    private WebDriver driver;
    private WebDriverWait waiter;
    private ChromeOptions options;

    @BeforeTest
    public void profileSetUp(){
        System.setProperty("webdriver.chrome.driver","src/main/resources/chromedriver.exe");
    }

    @BeforeMethod
    public void testSetUp(){
        driver = new ChromeDriver();
        waiter = new WebDriverWait(driver, Duration.ofSeconds(10));
        options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        driver.manage().window().maximize();
        driver.get("https://avic.ua");
    }

    @Test(priority = 1)
    public void titleOfThePageContainsSearchProduct(){
        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys("Samsung Galaxy S22");
        driver.findElement(By.xpath("//button[@class='button-reset search-btn']")).click();
        driver.findElements(By.xpath("//div[@class='prod-cart height']")).get(0).click();

        assertTrue(driver.getTitle().contains("Samsung Galaxy S22"));
    }

    @Test(priority = 2)
    public void dropDownListMoveToCategoriesAndPageWithCategoriesIsOpened() {
        Actions actions = new Actions(driver);

        actions.moveToElement(driver.findElements(By.xpath("//li[@class='parent js_sidebar-item']/a[contains(@href, 'telefonyi-i-aksessuaryi')]")).get(0))
                .build()
                .perform();

        actions.moveToElement(waiter.until(ExpectedConditions.visibilityOf(driver.findElements(By.xpath("//ul[@class='sidebar-list']//a[contains(@href, 'smartfonyi')]")).get(0))))
                .build()
                .perform(); //sometimes cursor can move to upper selection, so sometimes test drops

        waiter.until(ExpectedConditions.
                                visibilityOf(driver.
                                        findElement(By.xpath("//li[@class='single-hover-block']/a[contains(@href, 'smartfonyi/proizvoditel--samsung')]")))).click();

        assertTrue(waiter.until(ExpectedConditions.
                visibilityOf(driver.findElements(By.xpath("//div[contains(@class, 'prod-cart__descr')]")).get(0))).getText().contains("Samsung"));
    }

    @Test(priority = 3)
    public void productIsAddedToCart() throws InterruptedException {
        driver.findElement(By.xpath("//input[@id='input_search']")).sendKeys("Samsung Galaxy S22");
        driver.findElement(By.xpath("//button[@class='button-reset search-btn']")).click();
        driver.findElements(By.xpath("//a[@class='prod-cart__buy']")).get(0).click();

        Thread.sleep(500);
        String expectedPrice = driver.findElements(By.xpath("//div[@class='prod-cart__prise-new']")).get(0).getText();
        String actualPrice = waiter.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@class='item-total']/span[@class='prise']")))).getText();

        assertTrue(waiter.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@id='js_cart']//span[@class='name']")))).getText().contains("Samsung Galaxy S22"));
        assertEquals(expectedPrice, actualPrice);

    }

    @AfterMethod
    public void tearDown(){
        driver.quit();
    }
}
