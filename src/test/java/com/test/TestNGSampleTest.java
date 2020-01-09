
package com.test;

import io.github.bonigarcia.wdm.DriverManagerType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestNGSampleTest {

    WebDriver webDriver;
    @BeforeClass
    public void setWebDriver(){
        WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
        webDriver=new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        webDriver.get("https://www.google.com");
    }

    @BeforeMethod
    @Parameters({"firstCity","secondCity"})
    public void pageLoad(){
        webDriver.get("https://savvytime.com/converter/");
        webDriver.findElement(By.xpath("//input[@id='time-search']")).sendKeys("London");
        webDriver.findElements(By.xpath("//a[@data-id='united-kingdom-london']")).get(0).click();
        webDriver.findElement(By.xpath("//input[@id='time-search']")).sendKeys("New York");
        webDriver.findElements(By.xpath("//a[@data-id='ny-new-york-city']")).get(0).click();
    }
    @Test(description = "verifying Savvy Time web page")
     public void verifyPage() {     //opening "Savvy Time" web page and verifying whether the page is correct page or not
        String string=webDriver.findElement(By.xpath("//h5")).getText();
        Assert.assertTrue(string.contains("Savvy Time"),"Expected: 'Savvy Time', but found:"+string);
    }

    @Parameters({"firstCity"})
    @Test(description = "adding 1st time zone")
    public void firstCity(String firstCity) {   //testing whether the resultant page is same as searched page.
        String string=webDriver.findElement(By.xpath("//h1[@class='title']")).getText();
        Assert.assertTrue(string.contains(firstCity),"Expected: 'London, United Kingdom', but found:"+string);
    }

    @Parameters({"secondCity"})
    @Test(description = "adding 2nd time zone")
     public void secondCity(String secondCity) {     ////testing whether the resultant page is same as searched page.

        webDriver.findElement(By.xpath("//input[@id='time-search']")).sendKeys("New York");

        String string=webDriver.findElement(By.xpath("//h1[@class='title']")).getText();
        Assert.assertTrue(string.contains(secondCity),"Expected: 'London, United Kingdom to New York City, New York, USA', but found:"+string);
    }


    @Test(description = "checking time difference")
    public void timeDifference() {       //testing the time difference
        List<WebElement> webElements = webDriver.findElements(By.xpath("//input[@class='time ampm format12 form-control ui-timepicker-input']"));
        String time1 = webElements.get(0).getAttribute("value");
        String time2 = webElements.get(1).getAttribute("value");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa");
        try {
            Date date1 = simpleDateFormat.parse(time1);
            Date date2=simpleDateFormat.parse(time2);
//            System.out.println(time1+"  "+time2);
            List<WebElement> list=webDriver.findElements(By.xpath("//div[@class='tz-date']"));
            String day1=list.get(0).getText();
            String day2=list.get(1).getText();
            if(day1.equals(day2))
                Assert.assertEquals(Math.abs(date1.getTime()-date2.getTime())/60000,300);
            else
                Assert.assertEquals(Math.abs(date1.getTime()-date2.getTime())/60000,1140);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    @Test(description = "checking swapping")
    public void swappingCities(){
        List<WebElement> beforeSwap= webDriver.findElements(By.xpath("//a[@class='time-abb']"));
        List<String> list1=new ArrayList<String>();
        for(WebElement webElement:beforeSwap)
            list1.add(webElement.getText());

        webDriver.findElement(By.xpath("//a[@class='swap-tz btn']")).click();

        List<WebElement> afterSwap=webDriver.findElements(By.xpath("//a[@class='time-abb']"));
        List<String> list2=new ArrayList<String>();
        for (WebElement webElement:afterSwap)
            list2.add(webElement.getText());
        Collections.reverse(list2);
        Assert.assertEquals(list1,list2);
    }

    @Parameters({"firstCity"})
    @Test(description = "deleting time zone")
    public void deleteCity(String firstCity){
        List<WebElement> webElements=webDriver.findElements(By.xpath("//div[@class='table-time row']"));
        webElements.get(1).click();
        webDriver.findElement(By.xpath("//a[@class='delete-btn btn']")).click();
        String string=webDriver.findElement(By.xpath("//h1[@class='title']")).getText();
        Assert.assertTrue(string.contains(firstCity),"Expected: 'New York City, New York, USA', but found:"+string);
    }

    @AfterClass
    public void close(){
    webDriver.quit();
    }
}
