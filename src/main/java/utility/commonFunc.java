package utility;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class commonFunc {


	private static String verificationHighlightColor = "magenta";
	private static String verificationHighlightColorYellow = "yellow";
	private static String verificationHighlightColorRed= "red";
	public static HSSFWorkbook workbook;
	public static HSSFSheet worksheet;
	public static DataFormatter formatter= new DataFormatter();
	private int elementWaitTime = 15;



	public void waitForPageLoaded(WebDriver driver, ExtentTest logger) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(expectation);
		} catch (Throwable error) {
			logger.log(LogStatus.FAIL, "Timeout waiting for Page Load Request to complete.");
		}
	}

	public Object[][] readExcel(String filepath, String sheetname) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filepath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		HSSFWorkbook wb = null;
		try {
			wb = new HSSFWorkbook(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HSSFSheet sheet = wb.getSheet(sheetname);
		int rowcount = sheet.getLastRowNum();
		HSSFRow row = sheet.getRow(0);
		int colcount = row.getLastCellNum();

		Object data[][] = new Object[rowcount][colcount];
		for (int i = 1; i <= rowcount; i++) {
			HSSFRow row_individual = sheet.getRow(i);
			for (int j = 0; j <= colcount - 1; j++) {
				data[i - 1][j] = row_individual.getCell(j);
			}
		}
		return data;
	}

	public void softAssert(String stepname, String expected, String actual, boolean result, ExtentTest logger) {
		if (Boolean.TRUE.equals(result)) {
			logger.log(LogStatus.PASS, stepname,
					"Expected value : " + expected + "</br>is equal to</br>actual value : " + actual);
		} else {
			logger.log(LogStatus.ERROR, stepname,
					"Expected value : " + expected + "</br>is not equal to</br>actual value : " + actual);
		}
	}

	public void hardAssert(String stepname, String expected, String actual, boolean result, ExtentTest logger) {
		if (Boolean.TRUE.equals(result)) {
			logger.log(LogStatus.PASS, stepname,
					"Expected value : " + expected + "</br>is equal to</br>actual value : " + actual);
		} else {
			logger.log(LogStatus.FAIL, stepname,
					"Expected value : " + expected + "</br>is not equal to</br>actual value : " + actual);
			Assert.fail();
		}
	}

	public boolean compareString(String expected, String actual, boolean ignorecase) {
		if (Boolean.TRUE.equals(ignorecase)) {
			if (expected.trim().equalsIgnoreCase(actual.trim())) {
				return true;
			} else {
				return false;
			}

		} else {
			if (expected.trim().equals(actual.trim())) {
				return true;
			} else {
				return false;
			}
		}
	}

	public void sendingKeys(WebElement we, String text, WebDriver driver) {
		if (checkPresenceOfElement(we, driver)) {
			we.click();
			we.clear();
			we.sendKeys(text);
		}
	}

	public void scrollToWebElement(WebElement we, WebDriver driver) {
		try {
			if (we.isDisplayed()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", we);

			}
		} catch (NoSuchElementException arg31) {

		}


	}

	public void scrollDown(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight)");
	}

	public void scrollUp(WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0,-document.body.scrollHeight);");
	}

	public void MouseOver(WebElement we, WebDriver driver) {
		Actions actObj = new Actions(driver);
		actObj.moveToElement(we).build().perform();
	}

	public boolean checkPresenceOfElement(WebElement we, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.visibilityOf(we));
		if (we.isDisplayed() && we.isEnabled()) {
			return true;
		}
		return false;
	}
	public void verifyElementPresentOnPage(WebElement locator, WebDriver driver, ExtentTest logger) {
		try {
			if (locator.isDisplayed()) {
				this.highlightElement( locator, verificationHighlightColor, driver, logger);
				logger.log(LogStatus.PASS, "verifyElementPresentOnPage: Element found. Element-> "  + locator+logger.addScreenCapture(capture(driver)));


			} else {
				logger.log(LogStatus.FAIL, "verifyElementPresentOnPage: Element NOT found. Element-> "  + locator+logger.addScreenCapture(capture(driver)));
			}
		} catch (Exception arg2) {
			logger.log(LogStatus.FAIL, "verifyElementPresentOnPage: Element NOT found. Element-> "  + locator);
		}

	}


	public void waitToBeClickable(WebElement we, WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 35);
		wait.until(ExpectedConditions.elementToBeClickable(we));
	}

	public List<String> returnHandleList(WebDriver driver) {
		Set<String> handles = driver.getWindowHandles();
		List<String> listHandle = new ArrayList<String>(handles);
		return listHandle;
	}

	public void selectFromDropdown(WebElement we, String text) {
		Select oSelect = new Select(we);
		oSelect.selectByVisibleText(text);
	}

	public long getCurrentDateAndTime() {
		long epoch = System.currentTimeMillis() / 1000;
		return epoch;
	}

	public void uploadFile(WebElement we, String filename, WebDriver driver) throws Exception {
		StringSelection s = new StringSelection(filename);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
		Robot robot = new Robot();

		robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
		robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
		robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
		robot.keyPress(java.awt.event.KeyEvent.VK_V);
		robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
		Thread.sleep(3000);
		robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
	}

	public Boolean isImageDisplayed(WebElement ImageFile, WebDriver driver) {
		Boolean ImagePresent = (Boolean) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
				ImageFile);
		return ImagePresent;

	}

	public void performDragAndDrop(WebElement source, WebElement destination, WebDriver driver) {
		Actions act = new Actions(driver);
		act.dragAndDrop(source, destination).build().perform();
	}

	public void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getAllFiles(file, fileList);
			}
		}
	}

	public String writeZipFile(File directoryToZip, List<File> fileList) {

		String zipFile = directoryToZip + ".zip";
		try {

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) { // we only zip files, not directories
					addToZip(directoryToZip, file, zos);
				}
			}
			zos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zipFile;
	}

	public void addToZip(File directoryToZip, File file, ZipOutputStream zos)
			throws FileNotFoundException, IOException {

		FileInputStream fis = new FileInputStream(file);
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.closeEntry();
		fis.close();
	}

	public LogEntries getConsoleErrors(WebDriver driver) throws InterruptedException {
		Logs logs = driver.manage().logs();
		Thread.sleep(5000);
		return logs.get(LogType.BROWSER);
	}

	public void categorizeConsoleErrors(LogEntries logEntries, ExtentTest logger) {
		List<String> severem = new ArrayList<String>();
		List<String> warningm = new ArrayList<String>();
		for (LogEntry logEntry : logEntries) {
			if (logEntry.getLevel().getName().equalsIgnoreCase("SEVERE")) {
				severem.add(logEntry.getMessage());
			} else if (logEntry.getLevel().getName().equalsIgnoreCase("WARNING")) {
				warningm.add(logEntry.getMessage());
			}
		}

		if (severem.size() > 0) {
			String lsts = new String();
			for (int i = 0; i < severem.size(); i++) {
				lsts = lsts + (i + 1) + ".  " + severem.get(i).toString() + "</br>";
			}
			logger.log(LogStatus.ERROR, "Console Errors - Severe", lsts);
		} else {
			logger.log(LogStatus.PASS, "Console Errors - Severe", "No error found");
		}
		if (warningm.size() > 0) {
			String lsts = new String();
			for (int i = 0; i < warningm.size(); i++) {
				lsts = lsts + (i + 1) + ".  " + warningm.get(i).toString() + "</br>";
			}
			logger.log(LogStatus.ERROR, "Console Errors - Warning", lsts);
		} else {
			logger.log(LogStatus.PASS, "Console Errors - Warning", "No error found");
		}

	}


	public static void takeScreenshotAtEndOfTest(WebDriver driver, ExtentTest logger) throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String currentDir = System.getProperty("user.dir");
		FileUtils.copyFile(scrFile, new File(currentDir + "/screenshots/" + System.currentTimeMillis() + ".png"));
		logger.addBase64ScreenShot(currentDir);
	}


	public void verifyPosition(WebElement targetElement, String position, WebElement relativeElement, WebDriver driver, ExtentTest logger) {

		logger.log(LogStatus.INFO,"<<<<< Start  OF verifyPosition()  Function >>>>>");
		if (isPresentAndDisplayed(targetElement,   driver,  logger) && isPresentAndDisplayed(relativeElement,  driver,  logger)) {
			Point tlocation = targetElement.getLocation();
			Point rlocation = relativeElement.getLocation();

			if (position.equals("isAbove")) {
				if (tlocation.y < rlocation.y)
					logger.log(LogStatus.PASS, "Target(y cordinate:" + tlocation.y
							+ ") is above relative(y-cordinate:" + rlocation.y + ") element");
				else
					logger.log(LogStatus.FAIL, "Target(y-cordinate:" + tlocation.y
							+ ") not above relative(y-cordinate:" + rlocation.y + ") element");
			} else if (position.equals("isRight")) {
				if (tlocation.x > rlocation.x)
					logger.log(LogStatus.PASS, "Target(x cordinate:" + tlocation.x
							+ ") is right side to relative(y-cordinate:" + rlocation.x + ") element");
				else
					logger.log(LogStatus.FAIL, "Target(x-cordinate:" + tlocation.x
							+ ") not on right side to relative(y-cordinate:" + rlocation.x + ") element");
			} else if (position.equals("isBelow")) {
				if (tlocation.y > rlocation.y)
					logger.log(LogStatus.PASS, "Target(y cordinate:" + tlocation.y
							+ ") is below relative(y-cordinate:" + rlocation.y + ") element");
				else
					logger.log(LogStatus.FAIL, "Target(y-cordinate:" + tlocation.y
							+ ") not below relative(y-cordinate:" + rlocation.y + ") element");
			} else if (position.equals("isLeft")) {
				if (tlocation.x < rlocation.x)
					logger.log(LogStatus.PASS, "Target(x cordinate:" + tlocation.x
							+ ") is left side to relative(x-cordinate:" + rlocation.x + ") element");
				else
					logger.log(LogStatus.FAIL, "Target(x-cordinate:" + tlocation.x
							+ ") not on left side to relative(y-cordinate:" + rlocation.x + ") element");
			} else
				logger.log(LogStatus.FAIL, "Element is not present");
			logger.log(LogStatus.INFO,"<<<<< End  OF verifyPosition()  Function >>>>>");
			logger.log(LogStatus.INFO,"<<<<End of 'verifyPosition' method>>>>");
		}
	}


	public boolean isPresentAndDisplayed(WebElement locator,  WebDriver driver, ExtentTest logger) {
		try {
			if (locator.isDisplayed()) {

				return true;
			} else {

				return false;
			}
		} catch (NoSuchElementException arg2) {

			return false;
		}
	}

	public void verifyLinkIsWorking(List<WebElement> webElement  , ExtentTest logger) throws IOException {

		logger.log(LogStatus.INFO,"<<<<< START:  OF verifyLinkIsWorking()  Function >>>>>");
		int count = webElement.size();
		String attributeName ="href";
		for (int i=0; i<=count-1;i++)
		{

			String textHref=  getAttributeOnLocator(webElement.get(i),attributeName, logger);
			System.out.println("webElement.get(i)"+webElement.get(i));
			URL url = new URL(textHref);
			HttpURLConnection objhttpURLConnection=(HttpURLConnection)url.openConnection();
			objhttpURLConnection.setConnectTimeout(3000);
			objhttpURLConnection.connect();
			if(objhttpURLConnection.getResponseCode()!=200)
				logger.log(LogStatus.FAIL, textHref+" Error*-* +"+objhttpURLConnection.getResponseCode());
			else
			{
				//This syntax will print http response code.
				logger.log(LogStatus.PASS, textHref+" Perfect*-* "+objhttpURLConnection.getResponseCode());
				//This syntax will print http response message
				logger.log(LogStatus.PASS, textHref+" *-* "+objhttpURLConnection.getResponseMessage());
			}
		}

		logger.log(LogStatus.INFO,"<<<<< END:  OF verifyLinkIsWorking()  Function >>>>>");

	}

	public String getAttributeOnLocator(WebElement locator ,String attributeName, ExtentTest logger) {
		String value = null;
		try {
			value = locator.getAttribute(attributeName);
			logger.log(LogStatus.PASS, "getTextOnLocator: Element  found. Element-> " + locator);
			return value.trim();
		} catch (NoSuchElementException arg3) {
			logger.log(LogStatus.FAIL, "getTextOnLocator: Element not found. Element-> " + locator);
			return null;
		} catch (Exception arg4) {
			logger.log(LogStatus.FAIL, "getTextOnLocator: Element not found. Element-> " + locator);
			return null;
		}
	}





	public void verifyLinkIsWorking(WebElement [] WEB , ExtentTest logger ) throws IOException {

		logger.log(LogStatus.INFO,"<<<<< START:  OF verifyLinkIsWorking()  Function >>>>>");
		String attributeName ="href";
		for (int i =0; i<WEB.length;i++)
		{

			String textHref=  getAttributeOnLocator(WEB[i],attributeName,  logger);
			URL url = new URL(textHref);
			HttpURLConnection objhttpURLConnection=(HttpURLConnection)url.openConnection();
			objhttpURLConnection.setConnectTimeout(3000);
			objhttpURLConnection.connect();
			if(objhttpURLConnection.getResponseCode()!=200)
				logger.log(LogStatus.FAIL, textHref+" Error*-* +"+objhttpURLConnection.getResponseCode());
			else
			{
				//This syntax will print http response code.
				logger.log(LogStatus.PASS, textHref+" Perfect*-* "+objhttpURLConnection.getResponseCode());
				//This syntax will print http response message
				logger.log(LogStatus.PASS, textHref+" *-* "+objhttpURLConnection.getResponseMessage());
			}
		}

		logger.log(LogStatus.INFO,"<<<<< END:  OF verifyLinkIsWorking()  Function >>>>>");

	}

	public void waitForElementAbsent(WebElement locator, WebDriver driver, ExtentTest logger) throws IOException {
		logger.log(LogStatus.INFO, "START: waitForElementAbsent () ");
		long startTime = System.currentTimeMillis();
		long totalWaitTime = 0L;
		try {

			//			logger.log(LogStatus.PASS, "waitForElementToAppear: Waited:  " + Long.toString(System.currentTimeMillis() - start)+ 
			//					  " for element-> " + locator+", Timeout = " +elementWaitTime+ " seconds"+
			//					 logger.addScreenCapture(capture(driver)));
			System.out.println("By.xpath(locator.toString():>>"+By.xpath(locator.toString()));
			startTime = System.currentTimeMillis();
			(new WebDriverWait(driver, 15)).ignoring(NoSuchElementException.class)
			.ignoring(TimeoutException.class).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locator.toString())));
			totalWaitTime = System.currentTimeMillis() - startTime;
			logger.log(LogStatus.PASS, "Total time for: "+locator+":>"+totalWaitTime+logger.addScreenCapture(capture(driver)));

		} catch (TimeoutException arg6) {
		}

		logger.log(LogStatus.INFO, "END: waitForElementAbsent () ");
	}

	public boolean verifyElementAbsentOnPage(WebElement webElement) {


		boolean flag=false;
		try {
			if (!webElement.isDisplayed()) {
				flag = true;
			} else {
				flag = false;
			}
		}
		catch(NoSuchElementException e)
		{
			flag = false;
		}
		return flag;
	}

	public  String GetText(WebElement element)
	{
		try {
			return element.getText();
		}catch (Exception e) {
			System.out.println("Exception Caught");
			throw new RuntimeException(e);
		}
	}


	public void verifyElementPresenceInsideParentElement(WebElement parentElementLocator, WebElement childElementLocator, WebDriver driver, ExtentTest logger) {
		WebElement parentElement = null;

		try {
			parentElement =parentElementLocator;
			System.out.println("parentElement:"+parentElement);
			this.highlightElement(parentElement, verificationHighlightColor, driver, logger);
		} catch (Exception arg7) {
			logger.log(LogStatus.FAIL,"verifyElementPresenceInsideParentElement: - Parent element not found.  Element-> "+parentElementLocator);
			return;
		}

		try {

			String value = childElementLocator.toString();
			String value1=null;
			WebElement child = null;
			if(value.contains("xpath")) {
				System.out.println("value:"+value);
				String result[]=value.split("xpath: ");
				value1= result[1].replace("]]", "]");
				child = parentElement.findElement(By.xpath(value1));
			}
			if(value.contains("css selector")) {
				System.out.println("value:"+value);
				String result[]=value.split("css selector: ");
				value1= result[1].replace("]", "");
				child = parentElement.findElement(By.cssSelector(value1));
			}


			highlightElement(child, verificationHighlightColorYellow, driver, logger);
			logger.log(LogStatus.PASS,"verifyElementPresenceInsideParentElement: verified: " + childElementLocator
					+ " is a descendant of: " + parentElementLocator);
		} catch (NoSuchElementException arg6) {
			logger.log(LogStatus.FAIL,"verifyElementPresenceInsideParentElement: - Element-> " + childElementLocator
					+ " is NOT a descendant of: " + parentElementLocator);
		}

	}

	public void highlightElement(WebElement locator) {
		this.highlightElement(locator, "yellow", null, null);
	}


	public void highlightElement(WebElement element, String color, WebDriver driver, ExtentTest logger) {
		if (!driver.getClass().getSimpleName().contains("Java")) {
			((JavascriptExecutor) driver).executeScript("arguments[0].style.outline=\' dotted " + color + "\'",
					new Object[]{element});
		}

	}

	public int generateRandomIntIntRange(int min, int max) {
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}


	public void verifyTextExistsOnElement(WebElement locator, String verificationText,WebDriver driver, ExtentTest logger) {
		//verificationText = this.getDataOrDefault(verificationText);

		try {
			String e = locator.getText();
			if (e.contains(verificationText)) {
				this.highlightElement(locator, verificationHighlightColorYellow, driver, logger);
				logger.log(LogStatus.PASS, "verifyTextExistsOnElement: Element-> " + locator+ " having text: " + e
						+ ", contains text:" + verificationText + logger.addScreenCapture(capture(driver)));
				//				logger.log(LogStatus.PASS,
				//						"verifyTextExistsOnElement: Element-> " + locator+ " having text: " + e
				//								+ ", contains text:" + verificationText);


			} else {
				this.highlightElement(locator, verificationHighlightColorRed, driver, logger);
				logger.log(LogStatus.FAIL, "verifyTextExistsOnElement: Element-> " + locator+ " having text: " + e
						+ ", does not contains text:" + verificationText + logger.addScreenCapture(capture(driver)));

				//				logger.log(LogStatus.FAIL,
				//						"verifyTextExistsOnElement: Element-> " + locator + " having text: " + e
				//								+ ",does not contains text:" + verificationText);
			}
		} catch (NoSuchElementException arg3) {
			logger.log(LogStatus.FAIL,
					"verifyTextExistsOnElement: Element not found. Element-> " + locator);
		} catch (Exception arg4) {
			arg4.printStackTrace();
			logger.log(LogStatus.FAIL,
					"verifyTextExistsOnElement: Exception caught. Refer stacktrace.");
		}

	}

	public void verifyTextExist(String parentString, String subString,ExtentTest logger) {
		if (parentString.toLowerCase().contains(subString.toLowerCase())) {
			logger.log(LogStatus.PASS, subString + " was found in " + parentString);
		} else {
			logger.log(LogStatus.FAIL, 
					subString + " was NOT found in " + parentString);
		}

	}

	public void verifyTextDoesNotExist(String parentString, String subString,ExtentTest logger) {
		if (parentString.toLowerCase().contains(subString.toLowerCase())) {
			logger.log(LogStatus.FAIL, 
					subString + " was found in " + parentString);
		} else {
			logger.log(LogStatus.PASS,  subString + " was NOT found in " + parentString);
		}

	}




	public void verifyText(String actual, String expected,ExtentTest logger) {

		try {
			if (actual != null && expected != null) {
				if (actual.trim().equalsIgnoreCase(expected.trim())) {
					logger.log(LogStatus.PASS, 
							"verifyText: The text matches the expected value. Text: " + expected);
				} else {
					logger.log(LogStatus.FAIL, 
							"verifyText: The text does NOT match the expected value. Actual: " + actual + ", Expected: "
									+ expected);
				}
			} else {
				logger.log(LogStatus.FAIL, 
						"verifyText: The Actual/Expected value cannot be null. Actual: " + actual + ", Expected: "
								+ expected);
			}
		} catch (Exception arg3) {
			arg3.printStackTrace();
			logger.log(LogStatus.FAIL, 
					"verifyText: The text does NOT match the expected value. Actual: " + actual + ", Expected: "
							+ expected);
		}

	}

	public void doubleClick(WebElement locator,WebDriver driver, ExtentTest logger) {
		try {
			Actions e = new Actions(driver);
			e.doubleClick(locator);
			e.perform();
			logger.log(LogStatus.PASS,  "doubleClick-> " + locator);
		} catch (Exception arg2) {
			logger.log(LogStatus.FAIL, 
					"doubleClick-> " + locator + ", Exception: " + arg2.getMessage());
		}

	}

	public static void setUrl( String url,WebDriver driver) {
		driver.get(url);


	}

	//@DataProvider
	public static Object[][] readExcelNew(String file_location, String SheetName ) throws IOException
	{
		FileInputStream fileInputStream= new FileInputStream(file_location); //Excel sheet file location get mentioned here
		workbook = new HSSFWorkbook (fileInputStream); //get my workbook 
		worksheet=workbook.getSheet(SheetName);// get my sheet from workbook
		HSSFRow Row=worksheet.getRow(0);     //get my Row which start from 0   

		int RowNum = worksheet.getPhysicalNumberOfRows();// count my number of Rows
		int ColNum= Row.getLastCellNum(); // get last ColNum 

		Object Data[][]= new Object[RowNum-1][ColNum]; // pass my  count data in array

		for(int i=0; i<RowNum-1; i++) //Loop work for Rows
		{  
			HSSFRow row= worksheet.getRow(i+1);

			for (int j=0; j<ColNum; j++) //Loop work for colNum
			{
				if(row==null)
					Data[i][j]= "";
				else
				{
					HSSFCell cell= row.getCell(j);
					if(cell==null)
						Data[i][j]= ""; //if it get Null value it pass no data 
					else
					{
						String value=formatter.formatCellValue(cell);
						Data[i][j]=value; //This formatter get my all values as string i.e integer, float all type data value
					}
				}
			}
		}

		return Data;
	}

	public void verifyElementAbsentOnPage(WebElement locator,WebDriver driver, ExtentTest logger) throws IOException {

		try {
			//PageFactory.initElements(driver, locator);

			if (locator.isDisplayed()) {
				this.highlightElement(locator, verificationHighlightColor, driver, logger);

				logger.log(LogStatus.FAIL,"verifyElementAbsentOnPage: Element found on the page when not expected. Element-> "+locator+logger.addScreenCapture(capture(driver)));
			} else {
				logger.log(LogStatus.PASS,"verifyElementAbsentOnPage: Element found in the DOM but not displayed. Element->"+locator+logger.addScreenCapture(capture(driver)));
			}
		} catch (org.openqa.selenium.NoSuchElementException arg2) {
			logger.log(LogStatus.PASS,"verifyElementAbsentOnPage: Element not found on the page as expected. Element-> "+locator+ logger.addScreenCapture(capture(driver)));
		}

	}

	public static String capture(WebDriver driver) throws IOException
	{
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir") + "/screenshots/" + System.currentTimeMillis() + ".png" ;
		FileUtils.copyFile(scrFile, new File(destination));
		return destination;
	}

	public void click(WebElement locator, WebDriver driver, ExtentTest logger) {

		try {

			if (locator.isDisplayed()) {
				this.highlightElement(locator, verificationHighlightColorYellow, driver, logger);
				logger.log(LogStatus.PASS, "Element found on the page: " + logger.addScreenCapture(capture(driver)));
				locator.click();

			} else {
				logger.log(LogStatus.FAIL, " Element found in the DOM but not displayed :  " + logger.addScreenCapture(capture(driver)));
			}

		} catch (Exception arg4) {
			logger.log(LogStatus.FAIL, "can\\'t find: " + locator);
		}

	}

	public void jsClick(WebElement locator, WebDriver driver, ExtentTest logger) {

		try {

			if (locator.isDisplayed()) {
				this.highlightElement(locator, verificationHighlightColorYellow, driver, logger);
				logger.log(LogStatus.PASS, "Element found on the page: " + logger.addScreenCapture(capture(driver)));
				JavascriptExecutor executor = (JavascriptExecutor)driver;
				executor.executeScript("arguments[0].click();",locator);

			} else {
				logger.log(LogStatus.FAIL, " Element found in the DOM but not displayed :  " + logger.addScreenCapture(capture(driver)));
			}

		} catch (Exception arg4) {
			logger.log(LogStatus.FAIL, "can\\'t find: " + locator);
		}

	}

	 public void scrollElementIntoMiddle(WebElement locator, WebDriver driver) {
		String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
				+ "var elementTop = arguments[0].getBoundingClientRect().top;"
				+ "window.scrollBy(0, elementTop-(viewPortHeight/2));";

		((JavascriptExecutor) driver).executeScript(scrollElementIntoMiddle, locator);

	}



	public void waitForElementToAppear(WebElement locator , WebDriver driver, ExtentTest logger) {
		long start = System.currentTimeMillis();

		try {
			WebElement arg9999 = (WebElement) (new WebDriverWait(driver, 10L))
					.until(ExpectedConditions.elementToBeClickable(locator));

			logger.log(LogStatus.PASS, "waitForElementToAppear: Waited:  " + Long.toString(System.currentTimeMillis() - start)+ 
					" for element-> " + locator+", Timeout = " +elementWaitTime+ " seconds"+
					logger.addScreenCapture(capture(driver)));

		} catch (Exception arg5) {
			;
		}

	}


	public void waitUntilFileUpload(WebElement locator,int waitTime,  WebDriver driver, ExtentTest logger){
		logger.log(LogStatus.INFO, "Start of Function waitUntilFileUpload() >>>>>>>>>>>>>>>>>>>>");
		long start = System.currentTimeMillis();
		Boolean flag = false;
		if(waitTime>60)
			waitTime=60;
		logger.log(LogStatus.INFO,"Wait for "+waitTime+" seconds or the time taken by object to appear, whichever is less");
		try {

			for (int i = 1; i <= (waitTime / 10); i++) {
				waitForElementToAppear(locator,  driver,  logger);
				flag = isPresentAndDisplayed(locator,   driver,  logger);
				if (flag) {
					String waitedFor = Long.toString((System.currentTimeMillis() - start) / 1000);
					logger.log(LogStatus.PASS,  "waitUntilFileUpload: Element: "+ locator+ ", found in " + waitedFor + " seconds.");
					break;
				}
			}

		}
		catch (Exception e) {
			logger.log(LogStatus.FAIL,"waitUntilFileUpload: Element not found:"+locator);
		}
		if(!flag)
		{
			logger.log(LogStatus.FAIL, "waitUntilFileUpload: Element not found:"+locator);	
		}

		logger.log(LogStatus.INFO,"<<<<<<<<< END of Function waitUntilFileUpload()");
	}

	public void verifyCSSValue(WebElement locator, String cssProp, String expectedValue,   WebDriver driver, ExtentTest logger) throws IOException {
		String actualValue = null;

		try {
			this.highlightElement(locator, verificationHighlightColor, driver, logger);
			actualValue = locator.getCssValue(cssProp);
			if (actualValue.equals(expectedValue)) {
				logger.log(LogStatus.PASS,
						"verifyCSSValue: The value in the CSS property equals the expected value. Element-> "
								+ locator + ", CSS Property name: " + cssProp + ", attribute value: "
								+ actualValue+ logger.addScreenCapture(capture(driver)));
			} else {
				logger.log(LogStatus.FAIL,
						"verifyCSSValue: The value in the CSS property does not equal the expected value. Element-> "
								+ locator + ", CSS Property name: " + cssProp + ", expected value: "
								+ expectedValue + ", actual value: " + actualValue+ logger.addScreenCapture(capture(driver)));
			}
		} catch (Exception arg5) {
			logger.log(LogStatus.FAIL,
					"verifyCSSValue: Element not found. Element-> " + locator + ", attribute name: "
							+ cssProp + ", Expected value: " + expectedValue+ logger.addScreenCapture(capture(driver)));
		}

	}




}
