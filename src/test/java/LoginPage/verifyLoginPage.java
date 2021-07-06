package LoginPage;

import org.testng.annotations.Test;
import org.testng.annotations.Test;

import Pages.LoginPage;

public class verifyLoginPage  extends LoginPage{
	
	@Test
	
	public void verifyLoginpage() {
		
		System.out.println("Step 1:  verify clik on login link");
		System.out.println("Step 1 Expected :  verify Login Page should display");
		loginclick();
		
//		System.out.println("Step 2:  verify facebook link");
//		System.out.println("Step 2 Expected :  should display FB Link");
//		veifyFbLink();
		
	}

}
