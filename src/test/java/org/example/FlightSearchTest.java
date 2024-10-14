package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class FlightSearchTest {
    private static WebDriver driver;
    private final String baseUrl = "https://www.booking.com/flights/index.en-gb.html";

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C://Users//tusha//Downloads//chromedriver-win64//chromedriver-win64//chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(baseUrl);
        driver.findElement(By.xpath("//label[@for='search_type_option_ONEWAY']//span[@class='InputRadio-module__field___wQiXd']")).click();
    }

    @Test
    public void testValidInputs() {
        searchFlights("DEL", "BOM", true); // Search should proceed
        Assert.assertTrue(verifyFlightDisplayed(), "Flights not displayed for today");
    }

    @Test
    public void testInvalidInputHandling() {
        searchFlights("INVALID", "INVALID", true); // Search should proceed
        Assert.assertTrue(verifyErrorDisplayed(), "Error message not displayed for invalid inputs");
    }

    @Test
    public void testSwapFunctionality() {
        searchFlights("DEL", "BOM", false); // Search should NOT proceed
        WebElement swapButton = driver.findElement(By.xpath("//button[@title='Switch origin and return destinations ']"));
        swapButton.click();
        System.out.println("swap button clicked");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        WebElement fromCity = driver.findElement(By.xpath("//div[@class='SegmentHorizontal-module__destination___paTRj']//button[1]"));
        String fromCitySwapped = fromCity.getText();
        System.out.println("Swapped in from City:"+fromCitySwapped);
        WebElement toCity = driver.findElement(By.xpath("(//button[@type='button'])[5]"));
        String swappedToCity = toCity.getText();
        System.out.println("Swapped in to city: " + swappedToCity);
        Assert.assertEquals(fromCitySwapped, "BOM Chhatrapati Shivaji International Airport Mumbai", "Swap functionality not working");
        Assert.assertEquals(swappedToCity, "DEL Delhi International Airport", "Swap functionality not working");
    }

    private void searchFlights(String fromCity, String toCity, boolean clickSearchButton) {
        // Click on the from Destination
        driver.findElement(By.xpath("//div[@class='SegmentHorizontal-module__destination___paTRj']//button[1]")).click();
        driver.findElement(By.xpath("//span[@class='Text-module__root--variant-body_2___QdAaF Tags-module__item___ESG4A']")).click();
        WebElement fromInput = driver.findElement(By.xpath("//input[@placeholder='Airport or city']"));
        fromInput.click();
        fromInput.sendKeys(fromCity);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            WebElement fromSuggestion = driver.findElement(By.xpath("//*[@id=\"flights-searchbox_suggestions\"]/li[1]"));
            assertTrue(fromSuggestion.getText().equals("DEL Delhi International Airport\nNew Delhi, Delhi NCR, India"), "Expected suggestion not found!");
            fromSuggestion.click();
        } catch (NoSuchElementException e) {
            System.out.println("No suggestion found. Proceeding to next step.");
        }

        // Click on the To Destination
        driver.findElement(By.xpath("(//button[@type='button'])[5]")).click();
        WebElement toInput = driver.findElement(By.xpath("//input[@placeholder='Airport or city']"));
        toInput.sendKeys(toCity);
        try {
            WebElement toSuggestion = driver.findElement(By.xpath("//li[@class='List-module__location___w04Kf']"));
            assertTrue(toSuggestion.getText().equals("BOM Chhatrapati Shivaji International Airport Mumbai\n" +
                    "Mumbai, Maharashtra, India"), "Expected suggestion not found!");
            toSuggestion.click();
        } catch (NoSuchElementException e) {
            System.out.println("No suggestion found. Proceeding to next step.");
        }

        // Click on the date input to open the calendar
        WebElement dateInput = driver.findElement(By.xpath("//div[@class='SegmentHorizontal-module__date___xOIh6']"));
        dateInput.click();
        driver.findElement(By.xpath("//span[@aria-label='15 November 2024']")).click();

        // Conditionally click on the search button
        if (clickSearchButton) {
            WebElement searchButton = driver.findElement(By.xpath("//span[@class='Button-module__text___sRRzg']"));
            searchButton.click();
            System.out.println("Search button clicked.");
        } else {
            System.out.println("Skipping search button click for testSwapFunctionality.");
        }
    }

    private boolean verifyFlightDisplayed() {
        return driver.findElements(By.id("TAB-BEST")).size() > 0;
    }

    private boolean verifyErrorDisplayed() {
        return driver.findElements(By.xpath("//*[@id=\"location_input_to_0_error\"]/div/div")).size() > 0;
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser after each test
        if (driver != null) {
            driver.quit();
        }
    }
}
