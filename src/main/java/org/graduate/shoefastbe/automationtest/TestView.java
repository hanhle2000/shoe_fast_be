package org.graduate.shoefastbe.automationtest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class TestView {
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriverManager.chromedriver().setup();
        System.out.println("ChromeDriver is set up successfully.");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }
    @Test(description = "Xem chi tiết đơn hàng")
    void viewDetailOrder(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        login();
        sleep(1000);
        WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("order")));
        orderBtn.click();
        sleep(1000);
        String initialUrl = driver.getCurrentUrl();
        WebElement idBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("card-title")));
        idBtn.click();
        wait.until(driver -> !driver.getCurrentUrl().equals(initialUrl));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotEquals("Success", initialUrl, currentUrl);
        sleep(2000);

    }
    @Test(description = "Thêm sản phẩm vào giỏ hàng thành công")
    public void testAddToCart() {
        driver.get("http://localhost:3000/sign-in");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        emailField.sendKeys("phuc1");
        passwordField.sendKeys("Phuc09122002@");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-outline-light")));
        loginBtn.click();
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
        WebElement addToCartField = driver.findElement(By.className("btn-outline-primary"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartField);
        sleep(3000);
        WebElement addSizeField = driver.findElement(By.className("form-check-input"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addSizeField);
        sleep(3000);
        List<WebElement> elements = driver.findElements(By.className("btn-outline-dark"));
        WebElement plusItemField = elements.get(1);
        Actions actions = new Actions(driver);
        actions.moveToElement(plusItemField).perform();
        sleep(1000);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", plusItemField);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 200)");
        sleep(2000);
        WebElement addField = driver.findElement(By.cssSelector(".btn.btn-primary.text-white"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addField);
        sleep(3000);
        WebElement goToCartField = driver.findElement(By.cssSelector(".btn.btn-primary.ml-2"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", goToCartField);
        sleep(3000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/cart"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals("http://localhost:3000/cart", currentUrl);
    }

    @Test( description = "Order đơn hàng")
    public void orderProduct(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        login();
        sleep(1000);
        WebElement cartBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("cart")));
        cartBtn.click();
        sleep(2000);
        WebElement checkBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("form-check-input")));
        checkBtn.click();
        js.executeScript("window.scrollTo(0, 600);");
        sleep(1000);
        WebElement buyBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        buyBtn.click();
        js.executeScript("window.scrollTo(0, 0);");
        sleep(1000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/checkout"));
        WebElement province = wait.until(ExpectedConditions.elementToBeClickable(By.name("province")));
        province.click();
        Select selectProvince = new Select(province);
        selectProvince.selectByVisibleText("Tỉnh Yên Bái");
        sleep(1000);
        WebElement district = wait.until(ExpectedConditions.elementToBeClickable(By.name("district")));
        district.click();
        Select selectDistrict = new Select(district);
        selectDistrict.selectByVisibleText("Thành phố Yên Bái");
        sleep(1000);

        WebElement ward = wait.until(ExpectedConditions.elementToBeClickable(By.name("ward")));
        ward.click();
        Select selectWard = new Select(ward);
        selectWard.selectByVisibleText("Phường Hồng Hà");
        js.executeScript("window.scrollBy(0, 200)");
        sleep(3000);
        WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("order-btn")));
        orderBtn.click();
        sleep(1000);
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-danger")));
        confirmBtn.click();
        sleep(1000);
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast-body")));
        String toastText = toastMessage.getText();

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals("Đặt hàng thành công", toastText);

    }
    @Test(description = "Case đăng nhập không thành công")
    public void testLoginFaild() {
        driver.get("http://localhost:3000/sign-in");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        emailField.sendKeys("phuc1");
        passwordField.sendKeys("1");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-outline-light")));
        loginBtn.click();
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals("http://localhost:3000/sign-in", currentUrl);
        sleep(1000);
        System.out.println("Đăng nhập thất bại");
    }

    @Test(description ="Đổi mật khẩu thất bại do sai mật khẩu" )
    public void changePasswordFaild() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        login();
        sleep(1000);
        WebElement dropdownBt = wait.until(ExpectedConditions.elementToBeClickable(By.className("dropdown__toggle")));
        dropdownBt.click();
        sleep(1000);
        WebElement infoButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("notification-item")));
        infoButton.click();
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/profile"));
        js.executeScript("window.scrollBy(0, 200)");
        sleep(1000);
        WebElement changePassBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("changepw")));
        changePassBtn.click();
        sleep(1000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/change-password"));
        js.executeScript("window.scrollBy(0, 300)");
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys("phuc1");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("Phuc9122002@");
        WebElement newPassField = driver.findElement(By.id("newPassword"));
        newPassField.sendKeys("Phuc09122002@");
        sleep(1000);
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-outline-light")));
        updateBtn.click();
        sleep(1000);
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast-body")));
        String toastText = toastMessage.getText();

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals("Sai mật khẩu, vui lòng thử lại", toastText);
        sleep(1000);

    }
    @Test(description = "Đổi mật khẩu thất bại do mật khẩu không đúng định dạng")
    public void changePasswordFaildValidPass() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        login();
        sleep(1000);
        WebElement dropdownBt = wait.until(ExpectedConditions.elementToBeClickable(By.className("dropdown__toggle")));
        dropdownBt.click();
        sleep(1000);
        WebElement infoButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("notification-item")));
        infoButton.click();
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/profile"));
        js.executeScript("window.scrollBy(0, 200)");
        sleep(1000);
        WebElement changePassBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("changepw")));
        changePassBtn.click();
        sleep(1000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/change-password"));
        js.executeScript("window.scrollBy(0, 300)");
        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys("phuc1");
        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("Phuc9122002@");
        WebElement newPassField = driver.findElement(By.id("newPassword"));
        newPassField.sendKeys("aloooo");
        sleep(1000);
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-outline-light")));
        updateBtn.click();
        sleep(1000);
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast-body")));
        String toastText = toastMessage.getText();

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals("Mật khẩu có ít nhất 8 ký tự bao gồm chữ hoa, chữ thường và ký tự đặc biệt!", toastText);
        sleep(1000);

    }

    @Test(description = "Đăng nhập thành công")
    public void testLoginSuccess() {
        driver.get("http://localhost:3000/sign-in");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        emailField.sendKeys("phuc1");
        passwordField.sendKeys("Phuc09122002@");
        sleep(2000);
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-outline-light")));
        loginBtn.click();
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals("http://localhost:3000/", currentUrl);
        sleep(1000);
        System.out.println("Đăng nhập thành công");
    }

    @Test(description = "Xem chi tiết thông tin cá nhân")
    public void viewInforUser() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login();
        sleep(1000);
        WebElement dropdownBt = wait.until(ExpectedConditions.elementToBeClickable(By.className("dropdown__toggle")));
        dropdownBt.click();
        sleep(1000);
        WebElement infoButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("notification-item")));
        infoButton.click();
        sleep(1000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/profile"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals("http://localhost:3000/profile", currentUrl);

    }

    @Test(description = "Cập nhật thông tin cá nhân thành công")
    public void updateInforUserSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login();
        sleep(1000);
        WebElement dropdownBt = wait.until(ExpectedConditions.elementToBeClickable(By.className("dropdown__toggle")));
        dropdownBt.click();
        sleep(1000);
        WebElement infoButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("notification-item")));
        infoButton.click();
        sleep(1000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/profile"));
        WebElement fullNameField = driver.findElement(By.id("fullName"));
        fullNameField.clear();
        fullNameField.sendKeys("Đỗ Văn Long");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 200)");
        sleep(2000);
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        updateBtn.click();
        sleep(3000);
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast-body")));
        String toastText = toastMessage.getText();

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals("Cập nhật thông tin thành công!", toastText);
        sleep(1000);
    }

    @Test(description = "Cập nhật thông tin cá nhân thất bại do trùng email")
    public void updateInforUserFail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        login();
        sleep(1000);
        WebElement dropdownBt = wait.until(ExpectedConditions.elementToBeClickable(By.className("dropdown__toggle")));
        dropdownBt.click();
        sleep(1000);
        WebElement infoButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("notification-item")));
        infoButton.click();
        sleep(1000);
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/profile"));
        WebElement emailField = driver.findElement(By.id("email"));
        emailField.clear();
        emailField.sendKeys("pphuc9122002@gmail.com");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 200)");
        sleep(2000);
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-primary")));
        updateBtn.click();
        sleep(3000);
        WebElement toastMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("Toastify__toast-body")));
        String toastText = toastMessage.getText();

        // Kiểm tra nội dung thông báo lỗi
        Assert.assertEquals("Email đã tồn tại", toastText);
        sleep(1000);
    }
    private void login() {
        driver.get("http://localhost:3000/sign-in");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));
        emailField.sendKeys("phuc1");
        passwordField.sendKeys("Phuc09122002@");
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-outline-light")));
        loginBtn.click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
