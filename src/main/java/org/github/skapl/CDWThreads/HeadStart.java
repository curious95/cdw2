package org.github.skapl.CDWThreads;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.lang.System.exit;

public class HeadStart {

    static boolean isImage = false;
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // TODO Auto-generated method stub
        isImage = false;
        if(args.length==0){
            isImage = false;
        } else if(args[0].equals("true")){
            isImage = true;
        }else{
            isImage = false;
        }


        //creating tmp and final data directory
        File dir = new File("tmp_data");
        if(dir.mkdir()){
            System.out.println("Temporary data directory was created successfully.");
        } else{
            System.out.println("Temporary data directory either already exists.");
        }

        dir = new File("final_data");
        if(dir.mkdir()){
            System.out.println("Final data directory was created successfully.");
        } else{
            System.out.println("Final data directory either already exists.");
        }

        dir = new File("img_data");
        if(dir.mkdir()){
            System.out.println("Image data directory was created successfully.");
        } else{
            System.out.println("Image data directory either already exists.");
        }


        //clearing data directories
        System.out.println("Clearing temporary data directory.");
        File folder = new File("tmp_data");
        for (File f : folder.listFiles()) {

            if (f.getName().startsWith("cdw-")) {
                f.delete();
            }
        }

        System.out.println("Clearing final data directory.");
        folder = new File("final_data");

        for (File f : folder.listFiles()) {

            if (f.getName().contains("_out")) {
                f.delete();
            }
        }

        System.out.println("Clearing image data directory.");
        folder = new File("img_data");

        for (File f : folder.listFiles()) {


                f.delete();

        }
        //exit(0);
        String templateURL = "https://www.cdw.com/shop/search/result.aspx?key=&searchscope=all&sr=1&outlet=1&MaxRecords=72&pCurrent=";

        WebDriver driver = new HtmlUnitDriver();
        driver.get("https://www.cdw.com/shop/search/result.aspx?key=&searchscope=all&sr=1&outlet=1&MaxRecords=72&pCurrent=1");

        //#main > div > div > div.search-main > div:nth-child(3) > div.search-pagination-list-container > a:nth-child(7)
        String[] findPage = driver.findElement(By.cssSelector("#main > div > div > div.search-main > div:nth-child(3) > div.search-pagination-list-container > a:nth-child(7)")).getAttribute("href").split("&");
        int page = 0;
        for(String o : findPage){
            if(o.contains("pCurrent")){
                String tmp[] = o.split("=");
                page = Integer.parseInt(tmp[1]);
                break;
            }
        }
        long startTime = System.currentTimeMillis();
        //page = 2;
        for(int pCurrent = 1; pCurrent<=page; pCurrent++){

            ExecutorService service = Executors.newCachedThreadPool();
            List<Callable<String>> callable = new ArrayList<Callable<String>>();
            List<Future<String>> future = new ArrayList<Future<String>>();

            for(int i=1; i<=2 && pCurrent<=page; i++){
                callable.add(new Page(templateURL, pCurrent, isImage));
                System.out.println("Starting for page :: "+pCurrent);
                future.add(service.submit(callable.get(i-1)));
                pCurrent++;
            }

            service.shutdown();
            for(Future<String> o : future){
                System.out.println(o.get());
            }
            //System.out.println("Shutdown");
            pCurrent--;
            System.gc();
            System.gc();
            Thread.sleep(3000);
			/*if(pCurrent==6)
				break;*/
        }



        JsonObject test = new JsonObject();
        Config cfg = new Config();
        List<CDWData> allData = new ArrayList<CDWData>();
        //List<CDWDatan> allDatan = new ArrayList<CDWDatan>();
        for(int i=1; i<=page; i++){

            try {

                CSVReader reader = new CSVReader(new FileReader(cfg.getProperty("out_path") + "/cdw-"+i+".csv"), ',', '"');



                ColumnPositionMappingStrategy<CDWData> beanStrategy = new ColumnPositionMappingStrategy<CDWData>();
                beanStrategy.setType(CDWData.class);
                String[] mapping = new String[] { "partNum", "url", "title", "manufacturer", "cdwNum", "description",
                        "listPrice", "salePrice", "imgURL", "imgFile", "depth", "height", "weight", "width" };
                beanStrategy.setColumnMapping(mapping);

                CsvToBean<CDWData> csvToBean = new CsvToBean<CDWData>();
                List<CDWData> rows = csvToBean.parse(beanStrategy, reader);

                allData.addAll(rows);



                reader.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println("Total data :: "+allData.size());

        CSVWriter allWriter = new CSVWriter(new FileWriter("final_data/all_out.csv"), ',', '"');



        for (int i = 0; i < allData.size(); i++) {

            CDWData tmp = allData.get(i);
            String[] record = { tmp.getPartNum(), tmp.getUrl(), tmp.getTitle(),
                    tmp.getManufacturer(), tmp.getCdwNum(), tmp.getDescription(),
                    tmp.getListPrice(), tmp.getSalePrice(), tmp.getImgURL(),
                    tmp.getImgFile(), tmp.getDepth(),
                    tmp.getHeight(), tmp.getWeight(), tmp.getWidth() };
            allWriter.writeNext(record);
            if (test.has(allData.get(i).getPartNum())) {
                if (test.get(allData.get(i).getPartNum()).getAsJsonObject().has(allData.get(i).getSalePrice())) {
                    int x = test.get(allData.get(i).getPartNum()).getAsJsonObject().get(allData.get(i).getSalePrice())
                            .getAsJsonObject().get("unique").getAsInt();
                    test.get(allData.get(i).getPartNum()).getAsJsonObject().get(allData.get(i).getSalePrice())
                            .getAsJsonObject().addProperty("unique", x + 1);
                } else {
                    JsonObject t1 = new JsonObject();
                    t1.addProperty("unique", 1);
                    t1.addProperty("val", i);
                    test.get(allData.get(i).getPartNum()).getAsJsonObject().add(allData.get(i).getSalePrice(), t1);
                }
            } else {
                JsonObject t1 = new JsonObject();
                t1.addProperty("unique", 1);
                t1.addProperty("val", i);
                JsonObject t2 = new JsonObject();
                t2.add(allData.get(i).getSalePrice(), t1);
                test.add(allData.get(i).getPartNum(), t2);
            }
        }

        allWriter.close();

        CSVWriter writer = new CSVWriter(new FileWriter("final_data/final_out.csv"), ',', '"');
        String[] header = {"Part Num", "Url", "Title", "Manufacturer", "Cdw Num", "Description", "List Price", "Sale Price", "Image URL", "Image File", "Quantity", "Depth", "Height", "Weight", "Width"};
        //String[] headern = {"Part Num", "Url", "Title", "Manufacturer", "Cdw Num", "Description", "List Price", "Sale Price", "Quantity", "Depth", "Height", "Weight", "Width"};
            writer.writeNext(header);


        for (Map.Entry<String, JsonElement> o : test.entrySet()) {

            for (Map.Entry<String, JsonElement> oo : o.getValue().getAsJsonObject().entrySet()) {

                FinalData final_data = new FinalData();
                int val = oo.getValue().getAsJsonObject().get("val").getAsInt();
                int unique = oo.getValue().getAsJsonObject().get("unique").getAsInt();
                CDWData tmp = allData.get(val);

                final_data.setPartNum(tmp.getPartNum());
                final_data.setUrl(tmp.getUrl());
                final_data.setTitle(tmp.getTitle());
                final_data.setManufacturer(tmp.getManufacturer());
                final_data.setCdwNum(tmp.getCdwNum());
                final_data.setDescription(tmp.getDescription());
                final_data.setListPrice(tmp.getListPrice());
                final_data.setSalePrice(tmp.getSalePrice());
                final_data.setImgURL(tmp.getImgURL());
                final_data.setImgFile(tmp.getImgFile());
                final_data.setDepth(tmp.getDepth());
                final_data.setHeight(tmp.getHeight());
                final_data.setWeight(tmp.getWeight());
                final_data.setWidth(tmp.getWidth());
                final_data.setQuantity("" + unique);

                String[] record = { final_data.getPartNum(), final_data.getUrl(), final_data.getTitle(),
                        final_data.getManufacturer(), final_data.getCdwNum(), final_data.getDescription(),
                        final_data.getListPrice(), final_data.getSalePrice(), final_data.getImgURL(),
                        final_data.getImgFile(), final_data.getQuantity(), final_data.getDepth(),
                        final_data.getHeight(), final_data.getWeight(), final_data.getWidth() };
                writer.writeNext(record);

            }

        }
        writer.close();
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time in milli second :: "+totalTime);
        System.out.println("Total time in minutes :: " + totalTime/60000);

    }

}
