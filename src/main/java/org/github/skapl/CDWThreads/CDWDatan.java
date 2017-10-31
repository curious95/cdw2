package org.github.skapl.CDWThreads;

public class CDWData {
	
	String partNum;
	String url;
	String title;
	String manufacturer;
	String cdwNum;
	String description;
	String listPrice;
	String salePrice;
	String imgURL;
	String imgFile;
	String depth;
	String height;
	String weight;
	String width;
	
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}	
	public String getPartNum() {
		return partNum;
	}
	public void setPartNum(String partNum) {
		this.partNum = partNum;
	}
	public String getCdwNum() {
		return cdwNum;
	}
	public void setCdwNum(String cdwNum) {
		this.cdwNum = cdwNum;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getListPrice() {
		return listPrice;
	}
	public void setListPrice(String listPrice) {
		this.listPrice = listPrice;
	}
	public String getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	public String getImgFile() {
		return imgFile;
	}
	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}
	public String getImgURL() {
		return imgURL;
	}
	public void setImgURL(String imgURL) {
		this.imgURL = imgURL;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public String toString(){
		return "{" + partNum
				+ " :: " + url
				+ " :: " + title
				+ " :: " + manufacturer
				+ " :: " + cdwNum
				+ " :: " + description
				+ " :: " + listPrice
				+ " :: " + salePrice
				+ " :: " + imgURL
				+ " :: " + imgFile
				+ " :: " + depth
				+ " :: " + height
				+ " :: " + weight
				+ " :: " + width + "}";
	}

}
