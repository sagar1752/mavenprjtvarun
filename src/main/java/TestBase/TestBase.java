package TestBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileBrowserType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobilePlatform;
import utility.SuiteListener;
import utility.config;
import utility.sendmail;
@Listeners({ SuiteListener.class })
public class TestBase {
	
	public static WebDriver driver = null;
	public static String path;
	public static String extentReportFile;
	public static ExtentReports extent;
	public static config testConfig;
	public static ExtentTest logger;
	public static String reqBrowser;
	public AndroidDriver<MobileElement> driver_m;

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "browser", "environment", "platformName", "os", "sharedDirectory", "mobileos", "appiumurl", "device",
			"resultsDir", "tomail", "cmsusername","cmspassword" })
	public void InitializeParameters(@Optional String browser, @Optional String environment,
			@Optional String platformName, @Optional String os, @Optional String sharedDirectory,
			@Optional String mobileos, @Optional String appiumurl, @Optional String device, @Optional String resultsdir,
			@Optional String tomail, @Optional String cmsusername, @Optional String cmspassword) throws IOException {
		testConfig = new config();
		config.BrowserName = browser;
		config.Environment = environment;
		config.PlatformName = platformName;
		config.os = os;
		config.mobileos = mobileos;
		config.appiumurl = appiumurl;
		config.SharedDirectory = sharedDirectory;
		config.ResultsDir = resultsdir;
		config.tomail = tomail;
		config.device = device;
		config.cmsusername= cmsusername;
		config.cmspassword=cmspassword;

		testConfig = new config(logger);
		reqBrowser = testConfig.getRunTimeProperty("browser");

		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss_SSS");
		Date now = new Date();
		String strDate = sdfDate.format(now);

		path = testConfig.getRunTimeProperty("ResultsDir") + testConfig.getRunTimeProperty("fileSeparator") + strDate;

		new File(path).mkdirs();
		extentReportFile = path + testConfig.getRunTimeProperty("fileSeparator")
				+ testConfig.getRunTimeProperty("reportfilename");
		File file = new File(extentReportFile);
		file.createNewFile();
		extent = new ExtentReports(extentReportFile, true);
		logger = extent.startTest("Configurations for : " + getSuiteName());
		logger.log(LogStatus.INFO, "Details for starting the suite",
				"1. Browser name : " + testConfig.getRunTimeProperty("browser") + "</br>2. Environment : "
						+ testConfig.getRunTimeProperty("environment") + "</br>3. Platform Name : "
						+ testConfig.getRunTimeProperty("platformName") + "</br>4. OS : "
						+ testConfig.getRunTimeProperty("os") + "</br>5. To Mail : "
						+ testConfig.getRunTimeProperty("style") // additional 
						+ testConfig.getRunTimeProperty("tomail"));
		extent.endTest(logger);
	 //	extent.config().addCustomStylesheet("/Users/sagarshinde/Downloads/data.css");
	}
	

	@SuppressWarnings("deprecation")
	@BeforeMethod(alwaysRun = true)
	public void startMethod(Method method) {
		Test test = method.getAnnotation(Test.class);
		if (test == null) {
			return;
		}
		String class_name = this.getClass().getName();
		logger = extent.startTest("Class_Name : " + class_name + "</br>" + "Test_Name : " + method.getName() + "</br>"
				+ "Test_Desc : " + test.description());
		logger.assignCategory(class_name);
		int flag = 1;
		try {

			if (testConfig.getRunTimeProperty("platformname").equalsIgnoreCase("desktop")) {
				if (reqBrowser.equalsIgnoreCase("notrequired")) {
					logger.log(LogStatus.INFO, "Browser required", "No");
					flag = 0;
				} else if (testConfig.getRunTimeProperty("os").equalsIgnoreCase("mac")) {
					System.setProperty("webdriver.chrome.driver", testConfig.getRunTimeProperty("SharedDirectory")
							+ testConfig.getRunTimeProperty("fileSeparator") + "chromedriver");

					driver = new ChromeDriver();
					System.out.println(driver);
				} else if (testConfig.getRunTimeProperty("os").equalsIgnoreCase("windows")) {
					if (reqBrowser.equalsIgnoreCase("chrome")) {
						System.setProperty("webdriver.chrome.driver", testConfig.getRunTimeProperty("SharedDirectory")
								+ testConfig.getRunTimeProperty("fileSeparator") + "chromedriver_windows.exe");
						driver = new ChromeDriver();
						System.out.println(driver);
					} 
//					else if (reqBrowser.equalsIgnoreCase("mozilla")) {
//						System.setProperty("webdriver.gecko.driver", testConfig.getRunTimeProperty("SharedDirectory")
//								+ testConfig.getRunTimeProperty("fileSeparator") + "geckodriver.exe");
//						FirefoxProfile profile = new FirefoxProfile();
//						profile.setPreference("network.proxy.type", ProxyType.SYSTEM.ordinal());
//						driver = new FirefoxDriver();
//					}
				}
			}

			else if (testConfig.getRunTimeProperty("platformname").equalsIgnoreCase("mobile")) {
				flag = 2;
				if (reqBrowser.equalsIgnoreCase("notrequired")) {
					logger.log(LogStatus.INFO, "Browser required", "No");
					flag = 0;
				} else if (testConfig.getRunTimeProperty("mobileos").equalsIgnoreCase("android")) {
					DesiredCapabilities capabilities = new DesiredCapabilities();
					if (testConfig.getRunTimeProperty("os").equalsIgnoreCase("mac")) {
						capabilities.setCapability("chromedriverExecutable",
								testConfig.getRunTimeProperty("SharedDirectory")
										+ testConfig.getRunTimeProperty("fileSeparator") + "chromedriver");
					} else if (testConfig.getRunTimeProperty("os").equalsIgnoreCase("windows")) {
						capabilities.setCapability("chromedriverExecutable",
								testConfig.getRunTimeProperty("SharedDirectory")
										+ testConfig.getRunTimeProperty("fileSeparator") + "chromedriver_windows.exe");
					}
					capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, MobileBrowserType.CHROME);
					capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobilePlatform.ANDROID);
					capabilities.setCapability(MobileCapabilityType.DEVICE_NAME,
							testConfig.getRunTimeProperty("device"));
					capabilities.setCapability("showChromedriverLog", true);
					URL url = null;
					try {
						url = new URL(testConfig.getRunTimeProperty("appiumurl"));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
					driver_m = new AndroidDriver<MobileElement>(url, capabilities);
				}

			}

		} catch (Exception e) {
			logger.log(LogStatus.FAIL, "Browser Open", e.getLocalizedMessage());
			extent.endTest(logger);
		}

		if (flag == 1) {
			logger.log(LogStatus.INFO, "Browser Open", "Browser opened successfully");
			//driver.manage().window().maximize();
			//driver.manage().deleteAllCookies();// Added by Sagar
			if (testConfig.getRunTimeProperty("url")!=null) {// Added by Sagar
				driver.get(testConfig.getRunTimeProperty("url"));
			}
			logger.log(LogStatus.INFO, "Browser Maximize", "Browser maximized sucessfully");
		}
	}

	@SuppressWarnings("deprecation")
	@AfterMethod(alwaysRun = true)
	public void endMethod(ITestResult result) throws IOException {
		try {
			if (result.getStatus() == ITestResult.FAILURE) {
				logger.log(LogStatus.FAIL, result.getName() + " : Test case failed due to : ", result.getThrowable());

				if (!reqBrowser.equalsIgnoreCase("notrequired")) {
					File scrf_a = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

					String encodedBase64 = null;
					FileInputStream fileInputStreamReader = null;
					try {
						fileInputStreamReader = new FileInputStream(scrf_a);
						byte[] bytes = new byte[(int) scrf_a.length()];
						fileInputStreamReader.read(bytes);
						encodedBase64 = new String(Base64.encodeBase64(bytes));
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					String final_file_path = "data:image/png;base64," + encodedBase64;
					String image_a = logger.addScreenCapture(final_file_path);
					logger.log(LogStatus.FAIL, "Test case failed. Please check - visible image", image_a);

					// ************** Commenting code for full page screenshot ******************
//					String newPath_b = path + "/" + result.getName() + "_2.jpg";
//					Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
//							.takeScreenshot(driver);
//					ImageIO.write(screenshot.getImage(), "jpg", new File(newPath_b));
//					String image_b = logger.addScreenCapture(newPath_b);
//					logger.log(LogStatus.FAIL, "Test case failed. Please check - full image", image_b);
				}
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				logger.log(LogStatus.PASS, result.getName() + " : Test case passed");
			} else {
				logger.log(LogStatus.SKIP, result.getName() + " : Test case skipped due to : ", result.getThrowable());
			}

		} catch (Exception e) {
			logger.log(LogStatus.WARNING, "Final test step failed due to : ", e.toString());
		} finally {
			if ((!reqBrowser.equalsIgnoreCase("notrequired"))
					&& (testConfig.getRunTimeProperty("platformname").equalsIgnoreCase("desktop"))) {
				//driver.quit();
				logger.log(LogStatus.INFO, "Browser Closed");
			} else if ((!reqBrowser.equalsIgnoreCase("notrequired"))
					&& (testConfig.getRunTimeProperty("platformname").equalsIgnoreCase("mobile"))) {
				//driver_m.quit();
				logger.log(LogStatus.INFO, "Browser Closed");
			}
			extent.endTest(logger);
		//	extent.config().addCustomStylesheet("/Users/sagarshinde/Downloads/data.css");
		}
	}

	@AfterSuite(alwaysRun = true)
	public void dumpParameters() throws IOException {
		//extent.config().addCustomStylesheet("/Users/sagarshinde/Downloads/data.css");// File.separator;
		extent.flush();
		extent.close();
		String bodyData = "1. Browser name : " + testConfig.getRunTimeProperty("browser") + "\n2. Environment : "
				+ testConfig.getRunTimeProperty("environment") + "\n3. Platform Name : "
				+ testConfig.getRunTimeProperty("platformName") + "\n4. OS : " + testConfig.getRunTimeProperty("os")
				+ "\n5. To Mail : " + testConfig.getRunTimeProperty("tomail")+ "\n6. style : " + testConfig.getRunTimeProperty("style") + "\n\n PFA report.";
		sendmail sendmail = new sendmail();
		sendmail.SendMail(testConfig.getRunTimeProperty("tomail"), testConfig.getRunTimeProperty("replyto"),
				"Automation Report : " + getSuiteName(), path, testConfig.getRunTimeProperty("reportemail"),
				testConfig.getRunTimeProperty("reportpassword"), bodyData);
	}

	public String getSuiteName() {
		ISuite suiteListner = SuiteListener.getAccess();
		String runningSuite = suiteListner.getName();
		return runningSuite;
	}
//	public void start ( ) {
//		
//		
//		System.setProperty("webdriver.chrome.driver","/Users/sagarshinde/git/chromedriver");
//	 driver = new ChromeDriver();
//
//		
//		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
//		
//		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
//	   driver.get("https://www.livemint.com");
//	    
//		}
//		
//	@AfterSuite
//	//Test cleanup
//	public void TeardownTest()
//    {
//        TestBase.driver.quit();
//    }
		
		
	
}
