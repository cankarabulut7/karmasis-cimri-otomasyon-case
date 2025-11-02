package pages.cimri;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CimriOfferListPage {

    private static final Logger log = LoggerFactory.getLogger(CimriOfferListPage.class);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public CimriOfferListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public static class Offer {
        public final String merchant;
        public final double price;
        public final String url;
        public Offer(String merchant, double price, String url) {
            this.merchant = merchant; this.price = price; this.url = url;
        }
        @Override public String toString() { return merchant + " - " + price + " TL - " + url; }
    }

    public List<Offer> topOffers(int max) {
        By goToStoreSelector = By.xpath("//a[contains(.,'Mağazaya git') or contains(@title,'Mağazaya git')]");
        wait.until(ExpectedConditions.presenceOfElementLocated(goToStoreSelector));

        List<WebElement> goLinks = driver.findElements(goToStoreSelector);
        List<Offer> results = new ArrayList<>();

        for (WebElement go : goLinks) {
            try {
                String href = go.getAttribute("href");
                if (href == null || href.isBlank()) continue;

                WebElement card = findCard(go);
                Double price = extractPriceFrom(card);
                if (price == null) price = extractPriceFrom(go);
                if (price == null) continue;

                String merchant = extractMerchantFrom(card);
                if (merchant == null || merchant.isBlank()) {
                    merchant = hostToBrand(URI.create(href).getHost());
                }
                results.add(new Offer(merchant, price, href));

            } catch (Exception ignored) { }
        }

        results.sort(Comparator.comparingDouble(o -> o.price));
        if (results.isEmpty())
            throw new RuntimeException("No bids could be collected from search results.");

        log.info("CIMRI found {} offers. Top-3: {}", results.size(),
                results.subList(0, Math.min(3, results.size())));
        return results.subList(0, Math.min(max, results.size()));
    }

    private WebElement findCard(WebElement start) {
        By[] candidates = new By[] {
                By.xpath("./ancestor::article[1]"),
                By.xpath("./ancestor::div[contains(@class,'product') or contains(@class,'card') or contains(@class,'listing')][1]"),
                By.xpath("./ancestor::li[1]"),
                By.xpath("./ancestor::section[1]"),
        };
        for (By c : candidates) {
            try { return start.findElement(c); }
            catch (NoSuchElementException ignored) {}
        }
        return start.findElement(By.xpath("ancestor::body"));
    }

    private Double extractPriceFrom(WebElement scope) {
        List<By> priceLocators = List.of(
                By.xpath(".//*[contains(text(),'TL') or contains(.,'₺')]"),
                By.cssSelector(".price, [class*='price'], [data-testid*='price']")
        );
        Pattern p = Pattern.compile("([0-9]{1,3}(?:\\.[0-9]{3})*(?:,[0-9]{1,2})|[0-9]+(?:\\.[0-9]{1,2})?)");
        for (By loc : priceLocators) {
            for (WebElement e : scope.findElements(loc)) {
                String t = e.getText();
                if (t == null) continue;
                if (!(t.contains("TL") || t.contains("₺"))) continue;

                Matcher m = p.matcher(t.replaceAll("[^0-9.,]", ""));
                if (m.find()) {
                    String n = m.group(1);
                    String norm = n.replace(".", "").replace(",", ".");
                    try { return Double.parseDouble(norm); } catch (NumberFormatException ignored) {}
                }
            }
        }
        return null;
    }

    private String extractMerchantFrom(WebElement scope) {
        List<By> mselectors = List.of(
                By.cssSelector("img[alt]"),
                By.cssSelector("[class*='merchant'], [class*='store'], [data-testid*='merchant']"),
                By.xpath(".//*[contains(.,'Hepsiburada') or contains(.,'Trendyol') or contains(.,'PttAVM') or contains(.,'n11')]")
        );
        for (By sel : mselectors) {
            for (WebElement e : scope.findElements(sel)) {
                String alt = e.getAttribute("alt");
                String txt = e.getText();
                String cand = (alt != null && !alt.isBlank()) ? alt : txt;
                if (cand != null) {
                    cand = cand.trim();
                    if (!cand.isBlank() && cand.length() < 50) return cand;
                }
            }
        }
        return null;
    }

    private String hostToBrand(String host) {
        if (host == null) return "Undefined";
        host = host.toLowerCase(Locale.ROOT);
        String[] parts = host.split("\\.");
        if (parts.length >= 2) {
            String d = parts[parts.length - 2];
            return Character.toUpperCase(d.charAt(0)) + d.substring(1);
        }
        return host;
    }
}
