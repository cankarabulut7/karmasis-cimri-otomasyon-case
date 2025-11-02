package pages.cimri;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CimriSearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CimriSearchPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    public void open() {
        driver.get("https://www.cimri.com/");
    }

    public void acceptCookies() {
        try {
            WebElement cookieBtn = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(By.id("#cookiescript_accept")));
            cookieBtn.click();

        } catch (Exception ignored) { }
    }

    public void search(String query) {
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(By.className("C3KlU")));
        searchField.click();
        searchField.click();
        searchField.clear();
        searchField.sendKeys(query);
        searchField.sendKeys(Keys.ENTER);
    }
}
