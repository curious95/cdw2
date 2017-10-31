package org.github.skapl.CDWThreads;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;

public class Unique {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		JsonObject test = new JsonObject();
		Config cfg = new Config();
		try {

			CSVReader reader = new CSVReader(new FileReader(cfg.getProperty("out_path") + "cat.csv"), ',', '"');
			
			ColumnPositionMappingStrategy<CDWData> beanStrategy = new ColumnPositionMappingStrategy<CDWData>();
			beanStrategy.setType(CDWData.class);
			String[] mapping = new String[] { "partNum", "url", "title", "manufacturer", "cdwNum", "description",
					"listPrice", "salePrice", "imgURL", "imgFile", "depth", "height", "weight", "width" };
			beanStrategy.setColumnMapping(mapping);

			CsvToBean<CDWData> csvToBean = new CsvToBean<CDWData>();
			List<CDWData> rows = csvToBean.parse(beanStrategy, reader);

			

			for (int i = 0; i < rows.size(); i++) {

				if (test.has(rows.get(i).getPartNum())) {
					if (test.get(rows.get(i).getPartNum()).getAsJsonObject().has(rows.get(i).getSalePrice())) {
						int x = test.get(rows.get(i).getPartNum()).getAsJsonObject().get(rows.get(i).getSalePrice())
								.getAsJsonObject().get("unique").getAsInt();
						test.get(rows.get(i).getPartNum()).getAsJsonObject().get(rows.get(i).getSalePrice())
								.getAsJsonObject().addProperty("unique", x + 1);
					} else {
						JsonObject t1 = new JsonObject();
						t1.addProperty("unique", 1);
						t1.addProperty("val", i);
						test.get(rows.get(i).getPartNum()).getAsJsonObject().add(rows.get(i).getSalePrice(), t1);
					}
				} else {
					JsonObject t1 = new JsonObject();
					t1.addProperty("unique", 1);
					t1.addProperty("val", i);
					JsonObject t2 = new JsonObject();
					t2.add(rows.get(i).getSalePrice(), t1);
					test.add(rows.get(i).getPartNum(), t2);
				}
			}

			CSVWriter writer = new CSVWriter(new FileWriter(cfg.getProperty("out_path") + "final_out.csv"), ',', '"');
			for (Map.Entry<String, JsonElement> o : test.entrySet()) {

				for (Map.Entry<String, JsonElement> oo : o.getValue().getAsJsonObject().entrySet()) {
					FinalData final_data = new FinalData();
					int val = oo.getValue().getAsJsonObject().get("val").getAsInt();
					int unique = oo.getValue().getAsJsonObject().get("unique").getAsInt();
					CDWData tmp = rows.get(val);

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

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
