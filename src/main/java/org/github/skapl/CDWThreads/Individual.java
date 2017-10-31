package org.github.skapl.CDWThreads;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Individual implements Callable<CDWData> {

	String URL = null;

	Individual(String URL) {
		this.URL = URL;
	}

	public CDWData call() {

		WebDriver driver = new HtmlUnitDriver();
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		driver.get(this.URL);

		CDWData row = new CDWData();
		/*System.out.println(driver.getTitle());
		System.out.println(
				driver.findElement(By.cssSelector("#AddToCart > div.bStockPriceWrapper > span.BstockOriginalPrice"))
						.getText().trim());*/
		boolean inStock = false;
		try {
			// #primaryProductAvailability > div.short-message-block
			// >
			// span.message.availability.in-stock > link
			inStock = driver
					.findElements(By.cssSelector(
							"#primaryProductAvailability > div.short-message-block > span.message.availability.in-stock > link"))
					.size() > 0;
			if (!inStock) {
				//System.out.println("NOT IN STOCK");
				return null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			// #primaryProductAvailability > div.short-message-block
			// >
			// span.message.availability.in-stock > link
			inStock = driver
					.findElements(By.cssSelector(
							"#primaryProductAvailability > div.short-message-block > span.message.availability.in-stock > link"))
					.size() > 0;
			if (!inStock) {
				driver.close();

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		row.setUrl(this.URL);
		try {
			String[] tmpNum = null;
			//System.out.println(driver.findElement(By.cssSelector("#primaryProductPartNumbers")).getText());
			tmpNum = driver.findElement(By.cssSelector("#primaryProductPartNumbers")).getText().split("\\|");
			//System.out.println(tmpNum[0]);
			row.setPartNum(tmpNum[0].split(":")[1].trim().replaceAll("-BSTK", ""));
			row.setCdwNum(tmpNum[1].split(":")[1].trim());
		} catch (Exception ex) {
			row.setPartNum("null");
			row.setCdwNum("null");
		}

		try {
			// #primaryProductName > span
			row.setTitle(driver.findElement(By.cssSelector("#primaryProductName > span")).getText().trim());
		} catch (Exception ex) {
			row.setTitle("null");
		}

		try {
			row.setManufacturer(driver.findElement(By.cssSelector("#manufacturerLogo")).getAttribute("text").trim());
		} catch (Exception ex) {
			row.setManufacturer("null");
		}

		try {
			// #availabilityAndPricingZone >
			// div.feature-list-container
			// >
			// div > ul
			row.setDescription(driver
					.findElement(By.cssSelector("#availabilityAndPricingZone > div.feature-list-container > div > ul"))
					.getText().replaceAll("\\n", ","));
		} catch (Exception ex) {
			row.setDescription("null");
		}

		try {
			// #AddToCart > div.bStockPriceWrapper >
			// span.BstockOriginalPrice
			row.setListPrice(
					driver.findElement(By.cssSelector("#AddToCart > div.bStockPriceWrapper > span.BstockOriginalPrice"))
							.getText().trim());
		} catch (Exception ex) {
			row.setListPrice("null");
		}

		try {
			// #singleCurrentItemLabel > span:nth-child(2)
			row.setSalePrice("$" + driver.findElement(By.cssSelector("#singleCurrentItemLabel > span:nth-child(2)"))
					.getAttribute("content").trim());
		} catch (Exception ex) {
			row.setSalePrice("null");
			// salePrice = "null";
			ex.printStackTrace();
		}

		try {
			// #primaryProductTop > div.productLeft >
			// div.image-gallery
			// >
			// div.main-media > div.main-image.has-zoom > img
			// #primaryProductTop > div.productLeft >
			// div.image-gallery
			// >
			// div.main-media > div.main-image > img
			String logo = driver
					.findElement(By.cssSelector(
							"#primaryProductTop > div.productLeft > div.image-gallery > div.main-media > div.main-image > img"))
					.getAttribute("src");

			row.setImgFile(row.getPartNum() + ".jpg");

			row.setImgURL(logo);

			if (row.getImgURL().contains("data:image/gif;base64,")) {
				row.setImgURL("https://" + row.getImgURL());
			}

		} catch (Exception ex) {
			// ex.printStackTrace();
			row.setImgFile("null");
			row.setImgURL("null");
		}

		try {
			row.setDepth("null");
			row.setHeight("null");
			row.setWeight("null");
			row.setWidth("null");

			List<WebElement> tmp = driver
					.findElements(By.cssSelector("#innerTSpec > table > tbody > tr > td.techspecdata"));
			for (int i = 0; i < tmp.size(); i++) {
				// System.out.println(tmp.get(i).getAttribute("innerHTML"));
				if (tmp.get(i).getAttribute("innerHTML").contains("Depth")) {
					row.setDepth(tmp.get(++i).getText().trim());
				} else if (tmp.get(i).getAttribute("innerHTML").contains("Height")) {
					row.setHeight(tmp.get(++i).getText().trim());
				} else if (tmp.get(i).getAttribute("innerHTML").contains("Weight")) {
					row.setWeight(tmp.get(++i).getText().trim());
				} else if (tmp.get(i).getAttribute("innerHTML").contains("Width")) {
					row.setWidth(tmp.get(++i).getText().trim());
				}
			}
			// System.out.println(tmp);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		//System.out.println(Thread.currentThread().getName()+" -> "+row.toString());
		driver.close();
		return row;
		
		

	}

}
