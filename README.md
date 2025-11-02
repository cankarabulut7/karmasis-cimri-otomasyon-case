# 🛒 CIMRI → Cheapest Offer → Add to Cart Automation  
**Java + Selenium + TestNG + WebDriverManager + Screen Recording + Logs**

This project automates the following case scenario:

| Step | Action |
|------|--------|
1️⃣ | Search a product on **Cimri**  
2️⃣ | Parse and sort marketplace offers by price  
3️⃣ | Open the **cheapest** offer  
4️⃣ | If it fails → **fallback to 2nd cheapest**  
5️⃣ | Add **2 units** to cart on the selected marketplace  
6️⃣ | Navigate to cart and read total price  
7️⃣ | Validate cart total ≈ `unitPrice * qty` (with tolerance)  
8️⃣ | Record video + generate execution logs  

> ⚙️ Login is intentionally **not used** (per case requirement changes)

---

## 📂 Project Structure

```
src
 └── main
     └── java
         ├── base               # Driver management (ThreadLocal, parallel ready)
         ├── pages              # Page Objects (Cimri, PttAVM)
         └── utils              # Config loader
 └── test
     ├── java/tests            # TestNG test
     └── resources             # config.properties
testng.xml                     # Cross-browser test suite
pom.xml                        # Dependencies
```

---

## 🚀 How to Run

### ✅ Run both **Chrome & Firefox in Parallel**
*(TestNG suite already configured)*

```bash
mvn -Dsurefire.suiteXmlFiles=testng.xml test
```

### ▶️ Run **only Chrome**
Edit `testng.xml` to keep only the Chrome `<test>` section, then run:

```bash
mvn test
```

### 💡 Browser override (optional)
You can force browser from CLI:

```bash
mvn test -Dbrowser=firefox
```

Supported values: `chrome`, `firefox`

---

## 🌐 Browsers Supported

| Browser | Status |
|--------|--------|
Chrome | ✅ Default  
Firefox | ✅ Parallel support  
Note: It is also supported for other desired browsers.
For Example:
> Add Edge by duplicating `<test>` block in `testng.xml`:
```xml
<parameter name="browser" value="edge"/>
```
---
## ⚙️ Configuration

`src/test/resources/config.properties`

```properties
product=Logitech M170 Kablosuz Mouse
```

---

## 🧠 Key Features

| Feature | Description |
|--------|-------------|
✅ Page Object Model | Clean + maintainable architecture  
✅ TestNG | Parallel cross-browser testing  
✅ WebDriverManager | No manual driver setup  
✅ ThreadLocal Driver | Safe parallel execution  
✅ Fallback Logic | 1st cheapest → 2nd cheapest  
✅ Soft Price Assertions | Handles shipping / rounding differences  
✅ Screen Recording | Video created for every test run  
✅ Execution Logging | Timestamped step-by-step logs  

---

## 📹 Video & Logs

| Output | Path |
|--------|------|
🎥 Test videos | `target/videos/`  
🧾 Test logs | `target/test.log`  

Log includes:
- Product search info
- Cimri offer results
- Vendor attempts
- Add to cart events
- Price validation
- Fallback messages

---

## 📎 Tools & Dependencies

- Java 17
- Selenium WebDriver
- TestNG
- WebDriverManager Bonigarcia
- SLF4J
- Monte Screen Recorder
- 
---

## 🎯 Deliverables Checklist (Case)

| Requirement | Delivered |
|-----------|----------|
Search on Cimri | ✅  
Find cheapest offer | ✅  
Try 2nd cheapest if 1st fails | ✅  
Add to cart (2 units) | ✅  
Validate total price | ✅  
Video recording | ✅  
Error logs | ✅  
Multi-browser (Chrome + Firefox) | ✅  
Clean code + POM + TestNG | ✅  

---
