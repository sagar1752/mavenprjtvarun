package TestBase;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class TestBase {
	
	public static WebDriver driver = null;
	
	@BeforeSuite
	
	public void start ( ) {
		
		
		System.setProperty("webdriver.chrome.driver","/Users/sagarshinde/git/chromedriver");
	 driver = new ChromeDriver();

		
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
	   driver.get("https://www.livemint.com");
	    
		}
		
	@AfterSuite
	//Test cleanup
	public void TeardownTest()
    {
        TestBase.driver.quit();
    }
		
		
	
}
