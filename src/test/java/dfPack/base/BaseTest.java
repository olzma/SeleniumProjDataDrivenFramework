package dfPack.base;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import dfPack.util.MyXLSReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.sql.Driver;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.ClickAction;

public class BaseTest {
	
	public Properties prop=null;
	public WebDriver driver=null;
	public MyXLSReader xlsxReader=null;
	public ExtentReports eReport= null;
	public ExtentTest eTest =null;

	public void initialize(){
		
		if(prop==null) {//check if already initialize the prop file
		    prop= new Properties();
			try {
			
			FileInputStream fis= new FileInputStream(new File("src/test/resources/projectconfig.properties"));
			
			prop.load(fis);
			}catch (IOException ex) {
				System.out.println("Problem with properties file ");
				ex.printStackTrace();
			}
		}
	}
	
	public void openBrowser(String browserType) {
		
		eTest.log(LogStatus.INFO, "Opening the broswer "+browserType);
		
		if(browserType.equalsIgnoreCase("firefox")) {
			
			System.setProperty("webdriver.gecko.driver", prop.getProperty("firefoxDriverPath"));
			driver= new FirefoxDriver();
		
		}else if(browserType.equalsIgnoreCase("chrome")) {
			
			System.setProperty("webdriver.chrome.driver", prop.getProperty("chromeDriverPath"));
			driver= new ChromeDriver();
			
		}else if(browserType.equalsIgnoreCase("edge")) {
			
			System.setProperty("webdriver.edge.driver", prop.getProperty("edgeDriverPath"));
			driver= new EdgeDriver();
		}
		eTest.log(LogStatus.INFO, "Browser got opened");
		
		driver.manage().window().maximize();
		eTest.log(LogStatus.INFO, "Maximize the browser");
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
	}
	
	public void navigate(String url) {
		
		eTest.log(LogStatus.INFO, "navigate to the URL of the site: "+prop.getProperty(url));
		driver.get(prop.getProperty(url));
		
		
	}
	 
	public boolean doLogin(String username, String password) {
		
		//Click on the login link -click()
		click("LoginLink_classname");
		
		typeIt("EmailTextBox_id",username);
		
		typeIt("PasswordTextBox_id",password);
		
		click("SignInButton_id");
		
	    return isElementPresent("element_on_login_page_classname")? true : false;
			
	}
	
	public void click(String locatorKey) {
		
		WebElement element=getElement(locatorKey);
		element.click();
		eTest.log(LogStatus.INFO, locatorKey + " got clicked");
	}
	
	public void typeIt(String locatorKey,String text) {
		
		WebElement element =getElement(locatorKey);
		element.sendKeys(text);
		eTest.log(LogStatus.INFO, text + " got typed into "+ locatorKey);
	}
	
	public WebElement getElement(String locatorKey) {
		
		WebElement element=null;
		
		String locatorValue=prop.getProperty(locatorKey);
		
		try {
		
		if(locatorKey.endsWith("_id")) {
			
			element =driver.findElement(By.id(locatorValue));
			
		}else if(locatorKey.endsWith("_name")) {
			
			element =driver.findElement(By.name(locatorValue));
			
		}else if(locatorKey.endsWith("classname")) {
			
			element =driver.findElement(By.className(locatorValue));
			
		}else if (locatorKey.endsWith("_linktext")) {
			
			element =driver.findElement(By.linkText(locatorValue));
			
		}else if (locatorKey.endsWith("_cssselector")) {
			
			element =driver.findElement(By.cssSelector(locatorValue));
			
		}else if (locatorKey.endsWith("_xpath")) {
			
			element =driver.findElement(By.xpath(locatorValue));
			
		}
		} catch (Exception e) {
			// TODO: handle exception
			reportFail(locatorKey+ "holding the value " + locatorValue+ " is not on the web page to be reached");
		}
		
		return element;
	}
	
	public boolean isElementPresent(String locatorKey) {
		
		WebElement element= getElement(locatorKey);
		
		return element.isDisplayed();
		
	}
	
	public void reportPass(String message) {
		
		eTest.log(LogStatus.PASS, message);
		
		
	}
	
	public void reportFail(String message) {
		
		eTest.log(LogStatus.FAIL, message);
		takeScreenshot();
		Assert.fail(message);
		
	}
	
	public void takeScreenshot() {
		
		Date date =new Date();
		String screenShootFile=date.toString().replace(":", "_").replace(" ", "_")+".png";
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
			
			FileUtils.copyFile(srcFile, new File("screenshots//"+screenShootFile));
			
		}catch (IOException e) {
			
			e.printStackTrace();
		}
		
		eTest.log(LogStatus.INFO, "Screenshot-> "+
		eTest.addScreenCapture(System.getProperty("user.dir")+"//screenshots//"+screenShootFile));
								//System.getProperty("user.dir") this is path of the project (c:\workspace\...)
		
	}
	
}
