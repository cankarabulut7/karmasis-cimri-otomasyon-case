package pages.marketplacepage;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class PttavmPage {

    private static final Logger log = LoggerFactory.getLogger(PttavmPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By BTN_QTY_PLUS    = By.id("product-detail-number-plus");
    private static final By BTN_ADD_TO_CART = By.id("product-detail-add-to-cart");
    private static final By BTN_CLOSE_DIALOG = By.xpath(
            "//*[@id=\"main\"]/div[4]/div/div/div[1]/div[3]/span/div[1]/div[2]/div[1]/div[1]/div[5]/div[2]/div/div[3]/button"
    );

    public PttavmPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean addToCartFromProductPage(int qty) {
        try {
            if (qty > 1) {
                WebElement plus = wait.until(ExpectedConditions.elementToBeClickable(BTN_QTY_PLUS));
                for (int i = 0; i < qty - 1; i++) {
                    plus.click();
                    wait.until(ExpectedConditions.elementToBeClickable(BTN_QTY_PLUS));
                }
                log.info("Quantity increased to {}", qty);
            }


            WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(BTN_ADD_TO_CART));
            addBtn.click();
            log.info("Clicked Add to Cart");

            try {
                WebElement close = new WebDriverWait(driver, Duration.ofSeconds(8))
                        .until(ExpectedConditions.elementToBeClickable(BTN_CLOSE_DIALOG));
                close.click();
                log.info("Add-to-cart dialog closed");
            } catch (TimeoutException te) {
                log.warn("Add-to-cart dialog not visible — continuing");
            }

            return true;

        } catch (Exception e) {
            log.error("addToCartFromProductPage error: {}", e.toString());
            return false;
        }
    }

    public double cartTotal() {
        String[] urls = {
                "https://www.pttavm.com/sepetim",
                "https://www.pttavm.com/sepet"
        };
        for (String u : urls) {
            try { driver.get(u); break; } catch (Exception ignored) {}
        }

        By totalSel = By.xpath(
                "//*[contains(translate(text(),'TOPLAM','toplam'),'toplam') or contains(text(),'Ara Toplam')]" +
                        "/following::*[contains(@class,'price') or contains(.,'₺')][1]"
        );

        WebElement totalEl = wait.until(ExpectedConditions.presenceOfElementLocated(totalSel));
        String raw = totalEl.getText()
                .replace("₺", "")
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.]", "")
                .trim();

        if (raw.isEmpty()) throw new RuntimeException("PTTAVM cart total not found");

        double total = Double.parseDouble(raw);
        log.info("Cart total parsed: {}", total);
        return total;
    }
}
