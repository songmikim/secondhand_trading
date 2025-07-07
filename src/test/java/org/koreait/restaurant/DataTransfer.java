package org.koreait.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.Test;
import org.koreait.restaurant.entities.Restaurant;
import org.koreait.restaurant.repositories.RestaurantRepository;
import org.locationtech.proj4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootTest
public class DataTransfer {

    @Autowired
    private RestaurantRepository repository;

    @Test
    void process() throws Exception {
        CsvMapper mapper = new CsvMapper();

        try (FileInputStream fis = new FileInputStream("C:/restaurant/data.csv");
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr, 16000)) {
            int cnt = 0;
            String line = null;
            while((line = br.readLine()) != null) {
                cnt++;
                if (cnt == 1) continue;

                try {
                    if (line.endsWith(",")) {
                        line = line.substring(0, line.lastIndexOf(","));
                    }
                    line = line.replace("?,", "\",");

                    List<String> item = mapper.readValue(line, new TypeReference<>() {
                    });

                    String operation = item.get(8);
                    if (operation != null && operation.equals("폐업")) {
                        continue;
                    }

                    String zipcode = item.get(17);
                    String address = item.get(18);
                    String roadAddress = item.get(19);
                    String zonecode = item.get(20);
                    String restaurant = item.get(21);
                    String category = item.get(28);
                    double loc1 = Double.parseDouble(item.get(26));
                    double loc2 = Double.parseDouble(item.get(27));

                    double[] pos = transformTMToWGS84(loc2, loc1);

                    double lat = pos[0];
                    double lon = pos[1];

                    System.out.printf("operation:%s, zipcode:%s, address:%s, roadAddress1:%s, zonecode: %s, restaurant: %s, category: %s, loc1: %s, loc2: %s%n", operation, zipcode, address, roadAddress, zonecode, restaurant, category, loc1, loc2);

                    Restaurant rest = new Restaurant();
                    rest.setZipcode(zipcode);
                    rest.setAddress(address);
                    rest.setZonecode(zonecode);
                    rest.setRoadAddress(roadAddress);
                    rest.setCategory(category);
                    rest.setName(restaurant);
                    rest.setLat(lat);
                    rest.setLon(lon);

                    repository.save(rest);

                } catch (Exception e) {
                    System.out.println("---- 오류 -----");
                    System.out.println(line);
                    e.printStackTrace();
                }

            }
        }
    }


    private double[] transformTMToWGS84(double lat, double lon) {
        CRSFactory crsFactory = new CRSFactory();

        // WGS84 좌표계 (EPSG:4326) - 위도 경도
        CoordinateReferenceSystem crsWGS84 = crsFactory.createFromName("EPSG:4326");

        // TM 중부원점 좌표계 (EPSG:2097)
        CoordinateReferenceSystem crsTM = crsFactory.createFromParameters("EPSG:2097", "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43");

        // CoordinateTransformFactory 생성
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();

        // 좌표 변환 객체 생성
        //CoordinateTransform transform = ctFactory.createTransform(crsWGS84, crsTM);
        CoordinateTransform transform = ctFactory.createTransform(crsTM, crsWGS84);
        // 변환할 좌표 설정
        ProjCoordinate sourceCoordinate = new ProjCoordinate(lon, lat);
        ProjCoordinate targetCoordinate = new ProjCoordinate();

        // 좌표 변환 수행
        transform.transform(sourceCoordinate, targetCoordinate);

        return new double[]{targetCoordinate.y, targetCoordinate.x};
    }
}