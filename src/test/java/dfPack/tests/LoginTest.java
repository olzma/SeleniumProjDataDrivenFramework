package dfPack.tests;

import dfPack.util.DataUtil;
import java.util.HashMap;
import net.bytebuddy.implementation.bind.annotation.Super;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import dfPack.base.BaseTest;
import dfPack.util.*;
import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import dfPack.util.ExtentManager;


public class LoginTest extends BaseTest{
	
	@BeforeClass
	@Override
	public void initialize() {
		super.initialize();
	}
	
	
	@DataProvider(name="getData")//can have a name or not, if dones't have name we call it with method name
	public Object[][] getData() {
		//get data test from excel so we know what data to use for a particular test
		Object[][] obj=null;
		
		try {
			String testcasesFile= prop.getProperty("xlsxFilePath");
			
			xlsxReader =new MyXLSReader(testcasesFile);
		    obj= DataUtil.getTestData(xlsxReader, "LoginTest", "Data");
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	@Test(dataProvider = "getData")
	public void siteLogin(HashMap<String,String> map) {
		eReport= ExtentManager.getInstance();
		eTest =eReport.startTest("Login test started");
		eTest.log(LogStatus.INFO, "Run the login operation");
		
		if(!DataUtil.isRunnable(xlsxReader, "LoginTest", "TestCases") || map.get("Runmode").equals("N")){
			//skipp in case is Not runnable
			eTest.log(LogStatus.INFO, "Is skipped because Runmode is set to N");
			throw new SkipException("Is skipped because Runmode is set to N");
		}
		
		//Automation code - start here
		openBrowser(map.get("Browser"));
		
		navigate("appURL");
		
		boolean actualResult=doLogin(map.get("Username"), map.get("Password"));
		
		boolean expectedResultValue=map.get("ExpectedResult").equalsIgnoreCase("Success") ? true: false;
		
		if(actualResult==expectedResultValue) {
			 reportPass("LoginTest got passed");
			 Assert.assertTrue(true);
		}else
	    	reportFail("LoginTest got failed");
	
		
	}
	
	@AfterMethod
	public void testClosure() {
		
		if(eReport!=null) {
		
			eReport.endTest(eTest);
			eReport.flush();
		}
		if(driver!=null)
			driver.quit();
	}
}
