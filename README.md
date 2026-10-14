# ⚡ ElectroBill: Responsive Electricity Bill Calculator

A fully responsive, modern web application for calculating electricity bills using progressive slab-based tariff rules, powered by **Java Servlet (Jakarta EE)**, **Apache Tomcat**, **HTML5**, **CSS3**, **JavaScript**, and **Maven**.

---

## 📋 Table of Contents
1. [Project Overview](#-project-overview)
2. [Electricity Tariff Rules](#-electricity-tariff-rules)
3. [Verified Test Cases & Calculations](#-verified-test-cases--calculations)
4. [Technology Stack](#-technology-stack)
5. [Project Structure](#-project-structure)
6. [Quick Start (One-Command Run)](#-quick-start-one-command-run)
7. [Deploying to Standalone Apache Tomcat](#-deploying-to-standalone-apache-tomcat)
8. [Importing into IntelliJ IDEA](#-importing-into-intellij-idea)
9. [Importing into Eclipse IDE](#-importing-into-eclipse-ide)
10. [Running Automated JUnit Tests](#-running-automated-junit-tests)
11. [College Lab & Viva-Voce Notes](#-college-lab--viva-voce-notes)

---

## 🌟 Project Overview
This project fulfills the college practical assignment requirements by providing a clean, professional, and responsive Electricity Bill Calculator. The calculation is strictly executed on the **server-side** by a Java Servlet (`ElectricityBillServlet`) using progressive tariff slabs. 

### Key Highlights:
- **Progressive Slab Billing**: Computes consumption incrementally across thresholds rather than applying a flat rate.
- **Modern Green & White Design**: Built with Google Fonts (*Outfit* and *Inter*), card-based layout, subtle shadows, and electricity-themed SVG icons.
- **Instant Client-Side & Server-Side Validation**: Rejects negative units, non-numeric values, or empty fields with clear messages.
- **Detailed Slab Breakdown**: Tables display units consumed in each slab, rate per unit, and subtotals.
- **Printable Bill Receipt**: Native `window.print()` functionality with optimized print stylesheet (`@media print`) that removes web UI chrome for clean invoice printing.
- **No Extra Charges**: Adheres strictly to the specification—no taxes, surcharges, or fixed charges are added.

---

## ⚡ Electricity Tariff Rules

Charges are calculated progressively across the following four slabs:

| Slab | Consumption Range | Rate per Unit (INR) |
| :--- | :--- | :--- |
| **Slab 1** | First 50 units (0 – 50) | **₹3.50** |
| **Slab 2** | Next 100 units (51 – 150) | **₹4.00** |
| **Slab 3** | Next 100 units (151 – 250) | **₹5.20** |
| **Slab 4** | Above 250 units (> 250) | **₹6.50** |

> **Crucial Rule:** Rates are applied progressively. A single rate is **never** applied to all units.

---

## 📊 Verified Test Cases & Calculations

The table below shows the exact step-by-step mathematical calculations for the required test cases:

| Units | Slab 1 (0–50 @ ₹3.50) | Slab 2 (51–150 @ ₹4.00) | Slab 3 (151–250 @ ₹5.20) | Slab 4 (>250 @ ₹6.50) | Total Payable (₹) |
| :---: | :---: | :---: | :---: | :---: | :---: |
| **0** | 0 × 3.50 = 0.00 | - | - | - | **₹0.00** |
| **50** | 50 × 3.50 = 175.00 | - | - | - | **₹175.00** |
| **51** | 50 × 3.50 = 175.00 | 1 × 4.00 = 4.00 | - | - | **₹179.00** |
| **150** | 50 × 3.50 = 175.00 | 100 × 4.00 = 400.00 | - | - | **₹575.00** |
| **151** | 50 × 3.50 = 175.00 | 100 × 4.00 = 400.00 | 1 × 5.20 = 5.20 | - | **₹580.20** |
| **250** | 50 × 3.50 = 175.00 | 100 × 4.00 = 400.00 | 100 × 5.20 = 520.00 | - | **₹1,095.00** |
| **251** | 50 × 3.50 = 175.00 | 100 × 4.00 = 400.00 | 100 × 5.20 = 520.00 | 1 × 6.50 = 6.50 | **₹1,101.50** |
| **300** | 50 × 3.50 = 175.00 | 100 × 4.00 = 400.00 | 100 × 5.20 = 520.00 | 50 × 6.50 = 325.00 | **₹1,420.00** |

All these test cases are verified via automated JUnit 5 unit tests (`mvn test`).

---

## 🛠️ Technology Stack
- **Frontend**: HTML5, Vanilla CSS3 (Custom Responsive Design System), Vanilla JavaScript (ES6+ Fetch API)
- **Backend**: Java Servlet API (`jakarta.servlet-api` 5.0.0 / Java 17)
- **JSON Processing**: Google Gson 2.10.1
- **Testing**: JUnit 5 (Jupiter 5.10.2)
- **Build Tool**: Apache Maven 3.9+
- **Application Server**: Apache Tomcat 10.x / 11.x

---

## 📁 Project Structure

```
electricity-bill-calculator/
├── pom.xml                                   # Maven configuration file
├── README.md                                 # Complete documentation & lab guide
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── electricity/
│   │   │           ├── EmbeddedTomcatServer.java        # Embedded Tomcat runner
│   │   │           ├── model/
│   │   │           │   ├── BillResult.java              # Complete bill result model
│   │   │           │   └── TariffSlab.java              # Individual slab breakdown model
│   │   │           ├── service/
│   │   │           │   └── ElectricityBillCalculator.java # Pure calculation engine
│   │   │           └── servlet/
│   │   │               └── ElectricityBillServlet.java   # HTTP POST Servlet handler
│   │   └── webapp/
│   │       ├── index.html                    # Responsive frontend user interface
│   │       ├── css/
│   │       │   └── style.css                 # Green & white modern stylesheet
│   │       ├── js/
│   │       │   └── script.js                 # AJAX POST controller & print logic
│   │       └── WEB-INF/
│   │           └── web.xml                   # Deployment descriptor
│   └── test/
│       └── java/
│           └── com/
│               └── electricity/
│                   └── ElectricityBillCalculatorTest.java # 20 Automated JUnit tests
```

---

## 🚀 Quick Start (One-Command Run)

You can launch and test the application directly using Maven without manually installing Tomcat:

```bash
# Navigate to the project root
cd electricity-bill-calculator

# Compile and start the embedded Tomcat 10 server
mvn compile exec:java
```

Once started, open your web browser at:
👉 **`http://localhost:8080/`** (or `http://localhost:8085/` if 8080 is in use)

---

## 📦 Deploying to Standalone Apache Tomcat

To deploy the application to an existing Apache Tomcat 10+ server:

1. **Build the WAR package**:
   ```bash
   mvn clean package
   ```
2. The compiled WAR file will be generated at:
   `target/electricity-bill-calculator.war`
3. **Deploy to Tomcat**:
   - Copy `target/electricity-bill-calculator.war` into your Tomcat installation's `webapps/` folder (e.g., `C:\apache-tomcat-10.1.x\webapps\`).
4. **Start Tomcat**:
   - On Windows: Run `<TOMCAT_HOME>\bin\startup.bat`
   - On Linux/macOS: Run `<TOMCAT_HOME>/bin/startup.sh`
5. **Access in Browser**:
   Open: **`http://localhost:8080/electricity-bill-calculator/`**

---

## 💻 Importing into IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Select **File -> Open...** and choose the `electricity-bill-calculator` directory containing `pom.xml`.
3. Choose **Open as Project**. IntelliJ will automatically resolve Maven dependencies.
4. **Method A: Direct Run via Main Class**:
   - Locate `src/main/java/com/electricity/EmbeddedTomcatServer.java`.
   - Right-click and choose **Run 'EmbeddedTomcatServer.main()'**.
5. **Method B: Using Tomcat Server Configuration**:
   - Go to **Run -> Edit Configurations...** -> Click `+` -> Choose **Tomcat Server -> Local**.
   - Under **Application server**, point to your local Tomcat 10 installation directory.
   - Go to the **Deployment** tab -> Click `+` -> Choose **Artifact...** -> Select `electricity-bill-calculator:war` or `war exploded`.
   - Set **Application context** to `/` or `/electricity-bill-calculator`.
   - Click **Apply** and **Run**.

---

## 💻 Importing into Eclipse IDE

1. Open **Eclipse IDE for Enterprise Java and Web Developers**.
2. Select **File -> Import... -> Maven -> Existing Maven Projects**.
3. Browse to the `electricity-bill-calculator` folder and click **Finish**.
4. Configure Apache Tomcat 10 in Eclipse:
   - Open the **Servers** tab (Window -> Show View -> Servers).
   - Right-click -> **New -> Server**.
   - Select **Apache -> Tomcat v10.0 Server** (or v10.1).
   - Point to your Tomcat installation path and JRE 17.
   - Add `electricity-bill-calculator` to the **Configured** list.
   - Click **Finish**.
5. Right-click the server in the Servers tab and click **Start**.
6. Access **`http://localhost:8080/electricity-bill-calculator/`**.

---

## 🧪 Running Automated JUnit Tests

To execute all unit tests and verify all 8 test cases plus edge validations:

```bash
mvn test
```

Expected output:
```text
[INFO] Running com.electricity.ElectricityBillCalculatorTest
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🎓 College Lab & Viva-Voce Notes

### Q1: Why is progressive slab calculation used instead of flat rate?
**Answer**: In progressive slab billing, consumption is divided into tiers. Consumers pay lower rates for basic consumption and higher rates only for excess units. Applying a single flat rate to the entire volume would cause an abrupt and unfair jump in the bill when crossing a threshold by even 1 unit.

### Q2: What is the difference between `javax.servlet` and `jakarta.servlet`?
**Answer**: Starting from Java EE's transition to Eclipse Foundation (Jakarta EE 9 and Tomcat 10+), the namespace changed from `javax.servlet` to `jakarta.servlet`. Tomcat 10 requires `jakarta.servlet-api`.

### Q3: Why handle the calculation on the server side instead of purely in JavaScript?
**Answer**: Client-side JavaScript can be manipulated or bypassed by users via browser DevTools. In billing and financial systems, server-side validation and calculation are essential for security, data integrity, and compliance.

### Q4: How does the application handle printing without printing the UI forms?
**Answer**: CSS3 `@media print` rules hide the header, input forms, buttons, and footer (`display: none !important`), leaving only the clean bill statement formatted for standard A4 paper or PDF export.
