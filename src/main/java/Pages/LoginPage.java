package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import TestBase.TestBase;

public class LoginPage   extends TestBase{
	
	
	
	
	public static void loginclick()
	{
	
	WebElement signin =	driver.findElement(By.xpath("//a[contains(.,'Sign in')]"));
	String strtag =signin.getTagName();
		System.out.println(strtag);
		signin.click();
	}
	
	public static void veifyFbLink()
	{
	
	WebElement fb =	driver.findElement(By.xpath("//a[@id='fbmBtn']"));
	String strtag =fb.getText();
		System.out.println(strtag);
	}

}
