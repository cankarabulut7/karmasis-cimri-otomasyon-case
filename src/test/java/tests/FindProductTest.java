package tests;

import base.DriverFactory;
import base.ScreenRecorderUtil;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pages.cimri.CimriOfferListPage;
import pages.cimri.CimriSearchPage;
import pages.marketplacepage.PttavmPage;
import utils.Config;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

public class FindProductTest {

    private static final Logger log = LoggerFactory.getLogger(FindProductTest.class);

    WebDriver driver;
    ScreenRecorderUtil recorder;

    @Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void setup(Method method, @Optional String browser) throws Exception {
        if (browser != null) System.setProperty("browser", browser);
        driver = DriverFactory.getDriver();
        recorder = new ScreenRecorderUtil();
        recorder.start(method.getName());

        log.info("=== Test started: {} | browser={} ===", method.getName(), System.getProperty("browser", "chrome"));
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() throws Exception {
        try {
            recorder.stop();
        } finally {
            DriverFactory.quitDriver();
            log.info("=== Driver quit ===");
        }
    }

    @Test
    public void cimriCheapestSiteAddToCart() {
        final String product = Config.get("product");
        if (product == null || product.isBlank()) {
            throw new RuntimeException("Product is not defined in config (src/test/resources/config.properties)!");
        }
        log.info("Product from config: {}", product);

        CimriSearchPage cimri = new CimriSearchPage(driver);
        cimri.open();
        cimri.acceptCookies();
        cimri.search(product);

        CimriOfferListPage listPage = new CimriOfferListPage(driver);
        List<CimriOfferListPage.Offer> offers = listPage.topOffers(5);
        Assert.assertTrue(offers.size() >= 1, "Should be minimum one offer");
        log.info("CIMRI: {} have offer. Firstly 3: {}", offers.size(),
                offers.subList(0, Math.min(3, offers.size())));

        final int desiredQty = 2;
        boolean success = false;
        Exception lastError = null;

        for (int i = 0; i < Math.min(2, offers.size()); i++) {
            CimriOfferListPage.Offer offer = offers.get(i);
            String host = URI.create(offer.url).getHost();
            log.info("Try #{} → {} ({}, {} TL)", i + 1, host, offer.merchant, offer.price);

            try {
                driver.get(offer.url);

                if (host.contains("pttavm")) {
                    PttavmPage ptt = new PttavmPage(driver);

                    boolean added = ptt.addToCartFromProductPage(desiredQty);
                    if (!added) {
                        log.warn("PTTAVM: First try to add to cart failed");
                        added = ptt.addToCartFromProductPage(desiredQty);
                    }
                    Assert.assertTrue(added, "Add to cart failed");

                    double total = ptt.cartTotal();
                    log.info("PTTAVM: Cart Total = {}", total);

                    double expected = offer.price * desiredQty;
                    double tolerancePercent = 0.10;
                    double absoluteBuffer = 50.0;
                    double lowerBound = Math.max(0, expected - Math.max(expected * tolerancePercent, absoluteBuffer));
                    double upperBound = expected + Math.max(expected * tolerancePercent, absoluteBuffer);

                    log.info("Expect Total ≈ {} (qty={} * unit={}); acceptance: [{}, {}]",
                            expected, desiredQty, offer.price, lowerBound, upperBound);

                    Assert.assertTrue(total >= lowerBound && total <= upperBound,
                            String.format("Basket total is not within expected range! total=%.2f, expecedt≈%.2f ±(max(%%%d, ₺%.0f))",
                                    total, expected, (int)(tolerancePercent*100), absoluteBuffer));

                    success = true;
                } else {
                    log.info("Unsupported store: {} → Try", host);
                }

                if (success) {
                    log.info("Success store: {} ({})", host, offer.merchant);
                    break;
                } else {
                    log.warn("Failed store: {} → Try", host);
                }

            } catch (Exception e) {
                lastError = e;
                log.error("Error: {} → Error", e.toString());
            }
        }

        if (!success && lastError != null) {
            throw new RuntimeException("Adding/verifying failed in the first two stores!", lastError);
        }

        Assert.assertTrue(success, "The entire scenario fails");
        log.info("Scenario PASS ✅");
    }
}
