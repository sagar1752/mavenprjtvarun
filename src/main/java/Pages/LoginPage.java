package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.relevantcodes.extentreports.ExtentTest;

import TestBase.TestBase;
import utility.commonFunc;

public class LoginPage   extends TestBase{
	
static commonFunc common = new utility.commonFunc();
	
	
	public static void loginclick()
	{
	
		
	WebElement signin =	driver.findElement(By.xpath("//a[contains(.,'Sign in')]"));
//	String strtag =signin.getTagName();
//		System.out.println(strtag);
//		signin.click();
		
		common.click(signin,  driver,  logger) ;
	}
	
	public static void veifyFbLink()
	{
	
	WebElement fb =	driver.findElement(By.xpath("//a[@id='fbmBtn']"));
	String strtag =fb.getText();
		System.out.println(strtag);
	}

}
