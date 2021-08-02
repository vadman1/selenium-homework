package org.example;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class FirstTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");

        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize(); // раскрытие окна браузера на максимальную ширину
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS); // ожидание загрузки страницы
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS); // ожидание появления элемента на странице

        wait = new WebDriverWait(driver, 10, 1000);

        driver.get("https://www.rgs.ru/");
    }

    @Test
    public void test() {

        closeFramePopup();
        WebElement btnCookieClose = driver.findElement(By.xpath("//div[contains(@data-bind, 'removeSelf')]"));
        btnCookieClose.click();

        WebElement btnMenu = driver.findElement(By.xpath("//a[contains(text(), 'Меню') and @data-toggle='dropdown']"));
        btnMenu.click();

        WebElement linkHealth = driver.findElement(By
                .xpath("//a[@href='https://www.rgs.ru/products/private_person/health/index.wbp']"));
        linkHealth.click();

        WebElement linkDms = driver.findElement(By
                .xpath("//a[@href='/products/private_person/health/dms/generalinfo/index.wbp']"));
        linkDms.click();

        WebElement titleDms = driver.findElement(By.tagName("h1"));
        Assert.assertEquals("Мы находимся не на странице добровольного медицинского страхования",
                "ДМС — добровольное медицинское страхование", titleDms.getText());

        WebElement btnSendRequest= driver.findElement(By.xpath("//a[contains(text(), 'Отправить заявку')]"));
        btnSendRequest.click();

        WebElement modalTitle = driver.findElement(By
                .xpath("//b[contains(text(), 'Заявка на добровольное медицинское страхование')]"));
        Assert.assertEquals("Мы находимся не на странице с заявкой на ДМС",
                "Заявка на добровольное медицинское страхование", modalTitle.getAttribute("textContent"));

        String fieldXPath = "//input[contains(@data-bind, '%s')]";

        WebElement lastNameField = driver.findElement(By.xpath(String.format(fieldXPath, "LastName")));
        fillInputField(lastNameField, "Иванов");

        WebElement firstNameField = driver.findElement(By.xpath(String.format(fieldXPath, "FirstName")));
        fillInputField(firstNameField, "Иван");

        WebElement middleNameField = driver.findElement(By.xpath(String.format(fieldXPath, "MiddleName")));
        fillInputField(middleNameField, "Иванович");

        new Select(driver.findElement(By
                .xpath("//select[contains(@data-bind, 'RegionsList')]"))).selectByVisibleText("Москва");

        WebElement phoneField = driver.findElement(By.xpath(String.format(fieldXPath, "Phone")));
        fillInputFieldPhone(phoneField, "(111) 111-11-11");

        WebElement emailField = driver.findElement(By.xpath(String.format(fieldXPath, "Email")));
        fillInputField(emailField, "qwertyqwerty");

        WebElement commentField = driver.findElement(By.xpath("//textarea[contains(@data-bind, 'Comment')]"));
        fillInputField(commentField, "Комментарий");

        WebElement checkboxPersonalData = driver.findElement(By
                .xpath("//input[@class='checkbox' and contains(@data-bind, 'IsProcessingPersonalData')]"));
        checkboxPersonalData.click();


        // Проверка заполнения полей
        Assert.assertEquals("Значения не совпадают", "Иванов",
                lastNameField.getAttribute("value"));
        Assert.assertEquals("Значения не совпадают", "Иван",
                firstNameField.getAttribute("value"));
        Assert.assertEquals("Значения не совпадают", "Иванович",
                middleNameField.getAttribute("value"));
        Assert.assertEquals("Значения не совпадают", "Москва", new Select(driver.findElement(By
                .xpath("//select[contains(@data-bind, 'RegionsList')]"))).getFirstSelectedOption().getText());
        Assert.assertEquals("Значения не совпадают", "+7 (111) 111-11-11",
                phoneField.getAttribute("value"));
        Assert.assertEquals("Значения не совпадают", "qwertyqwerty",
                emailField.getAttribute("value"));
        Assert.assertEquals("Значения не совпадают", "Комментарий",
                commentField.getAttribute("value"));


        WebElement btnSubmitForm = driver.findElement(By.xpath("//button[contains(@data-bind, 'SubmitForm')]"));
        btnSubmitForm.click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement errorEmail = driver.findElement(By.xpath("//span[text()='Введите адрес электронной почты']"));
        Assert.assertEquals("Проверка ошибки у поля Email не была пройдена",
                "Введите адрес электронной почты" , errorEmail.getText());
    }

    @After
    public void after(){
        driver.quit();
    }


    private void fillInputField(WebElement element, String value) {
        closeFramePopup();

        element.click();
        element.clear();
        element.sendKeys(value);
        Assert.assertEquals("Значения не совпадают", value, element.getAttribute("value"));
    }

    private void fillInputFieldPhone(WebElement element, String value) {
        closeFramePopup();

        element.click();
        element.sendKeys(value);
        Assert.assertEquals("Значения не совпадают", "+7 " + value, element.getAttribute("value"));
    }


    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    private void closeFramePopup() {
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        try {
            driver.switchTo().frame("fl-498072");
            WebElement btnFrameClose = driver.findElement(By.xpath("//div[@class='Ribbon-close']"));
            waitUtilElementToBeClickable(btnFrameClose);
            btnFrameClose.click();
            driver.switchTo().defaultContent();
        } catch (NoSuchElementException | NoSuchFrameException | ElementNotInteractableException ignore){
        } finally {
            driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        }

    }
}
