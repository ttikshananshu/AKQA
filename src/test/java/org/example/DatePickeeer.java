package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickeeer {
    private static WebDriver driver;
    private static final String baseUrl = "https://www.booking.com/flights/index.en-gb.html";

    public static void main(String[] args) {
        // Setup WebDriver
        System.setProperty("webdriver.chrome.driver", "C://Users//tusha//Downloads//chromedriver-win64//chromedriver-win64//chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // Navigate to Booking.com Flights
            driver.get(baseUrl);

            // Click on the departure date input to open the date picker
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'SegmentHorizontal-module__date___xOIh6')]")));
            dateInput.click();

            // Get the current date
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            String currentMonth = new SimpleDateFormat("MMMM").format(calendar.getTime());
            int currentYear = calendar.get(Calendar.YEAR);

            // Select the current date from the date picker
            selectCurrentDate(currentDay, currentMonth, currentYear);

        } finally {
            // Cleanup and close the driver
            driver.quit();
        }
    }

    private static void selectCurrentDate(int day, String month, int year) {
        // Navigate to the correct month/year in the date picker
        while (true) {
            // Get the month and year displayed on the date picker
            WebElement monthYearLabel = driver.findElement(By.xpath("//*[@id=\":R2aqgl95:\"]/div/div/div/div/div/div[1]/h3"));
            String[] monthYear = monthYearLabel.getText().split(" ");
            String displayedMonth = monthYear[0];
            int displayedYear = Integer.parseInt(monthYear[1]);
            // If the displayed month and year match the current month and year, select the day
            if (displayedMonth.equals(month) && displayedYear == year) {
                // Find and click the current day
                String dateString = String.format("%04d-%02d-%02d", year, calendarMonthToNumeric(month), day);
                WebElement dayElement = driver.findElement(By.xpath("//td[@data-date='" + dateString + "']"));

                if (dayElement.isDisplayed()) {
                    dayElement.click();
                    break;
                } else {
                    System.out.println("Day element not visible or not found.");
                    return;
                }
            } else {
                // Click the next button to go to the next month
                WebElement nextButton = driver.findElement(By.xpath("//*[@id=\":R2aqgl95:\"]/div/div/div/div/button[2]"));
                nextButton.click();
            }
        }
    }

    private static int calendarMonthToNumeric(String month) {
        switch (month) {
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June": return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
            default: return -1; // Invalid month
        }
    }
}
