package org.github.skapl.CDWThreads;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.opencsv.CSVWriter;

public class Page implements Callable<String> {
	
	String URL = null;
	int page = -1;
	boolean isImage = false;
	
	public Page(String URL, int page, boolean isImage){
		this.URL = URL;
		this.page = page;
		this.isImage = isImage;
	}
	
	public String call() throws Exception {
		// TODO Auto-generated method stub
		ExecutorService service = Executors.newCachedThreadPool();
		
		WebDriver driver = new HtmlUnitDriver();
		//driver.get("https://www.cdw.com/shop/search/result.aspx?key=&searchscope=all&sr=1&outlet=1&MaxRecords=72&pCurrent=1");
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		//System.out.println(Thread.currentThread().getName() + " -> " + this.URL);
		driver.get(this.URL+page);
		
		WebElement div = driver.findElement(By.cssSelector("#main > div > div > div.search-main > div.search-results"));
		List<WebElement> results = div.findElements(By.cssSelector(".search-result"));
		//System.out.println(results.size());
		WebElement link = null;
		List<Callable<CDWData>> callable = new ArrayList<Callable<CDWData>>();
		List<Future<CDWData>> future = new ArrayList<Future<CDWData>>();
		
		for(int i=0; i<results.size(); i++){
			link = results.get(i).findElement(By.cssSelector("div.columns-right > div.column-2 > h2 > a"));
			String url = link.getAttribute("href");
			//System.out.println(link.getAttribute("href"));
			callable.add(new Individual(url));
			future.add(service.submit(callable.get(i)));
		}
		service.shutdown();
		
		//writing to file
		List<String[]> records = new ArrayList<String[]>();
		for(Future<CDWData> o : future){
			
			//System.out.println("From future -> "+o.get());
			if(o.get() != null){
				if(isImage){
				records.add(new String[] {o.get().getPartNum()
						,o.get().getUrl()
						,o.get().getTitle()
						,o.get().getManufacturer()
						,o.get().getCdwNum()
						,o.get().getDescription()
						,o.get().getListPrice()
						,o.get().getSalePrice()
						,o.get().getImgURL()
						,o.get().getImgFile()
						,o.get().getDepth()
						,o.get().getHeight()
						,o.get().getWeight()
						,o.get().getWidth()});
					} else {
					records.add(new String[] {o.get().getPartNum()
						,o.get().getUrl()
						,o.get().getTitle()
						,o.get().getManufacturer()
						,o.get().getCdwNum()
						,o.get().getDescription()
						,o.get().getListPrice()
						,o.get().getSalePrice()
						
						,o.get().getDepth()
						,o.get().getHeight()
						,o.get().getWeight()
						,o.get().getWidth()});}
			}
			
			Config cfg = new Config();
			CSVWriter writer = new CSVWriter(new FileWriter(cfg.getProperty("out_path")+"cdw-"+this.page+".csv"), ',', '"');
			writer.writeAll(records);
			writer.close();
			
		}
				
				//System.out.println("Finished");
		return "Finished Page :: " + this.page;
	}

	/*public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
		ExecutorService service = Executors.newCachedThreadPool();
		
		WebDriver driver = new HtmlUnitDriver();
		//driver.get("https://www.cdw.com/shop/search/result.aspx?key=&searchscope=all&sr=1&outlet=1&MaxRecords=72&pCurrent=1");
		
		driver.get("https://www.cdw.com/shop/search/result.aspx?key=&searchscope=all&sr=1&outlet=1&MaxRecords=72&pCurrent=2");
		// driver.navigate().refresh();
		
		WebElement div = driver.findElement(By.cssSelector("#main > div > div > div.search-main > div.search-results"));
		List<WebElement> results = div.findElements(By.cssSelector(".search-result"));
		System.out.println(results.size());
		WebElement link = null;
		List<Callable<CDWData>> callable = new ArrayList<Callable<CDWData>>();
		List<Future<CDWData>> future = new ArrayList<Future<CDWData>>();
		
		for(int i=0; i<results.size(); i++){
			link = results.get(i).findElement(By.cssSelector("div.columns-right > div.column-2 > h2 > a"));
			String url = link.getAttribute("href");
			//System.out.println(link.getAttribute("href"));
			callable.add(new Individual(url));
			future.add(service.submit(callable.get(i)));
		}
		service.shutdown();
		
		//writing to file
		List<String[]> records = new ArrayList<String[]>();
		for(Future<CDWData> o : future){
			
			System.out.println("From future -> "+o.get());
			if(o.get() != null){
				records.add(new String[] {o.get().getPartNum()
						,o.get().getUrl()
						,o.get().getTitle()
						,o.get().getManufacturer()
						,o.get().getCdwNum()
						,o.get().getDescription()
						,o.get().getListPrice()
						,o.get().getSalePrice()
						,o.get().getImgURL()
						,o.get().getImgFile()
						,o.get().getDepth()
						,o.get().getHeight()
						,o.get().getWeight()
						,o.get().getWidth()});
			}
			
			Config cfg = new Config();
			CSVWriter writer = new CSVWriter(new FileWriter(cfg.getProperty("out_path")+"xyz.csv"), ',', '"');
			writer.writeAll(records);
			writer.close();
			
		}
		
		System.out.println("Finished");
	}*/
}
