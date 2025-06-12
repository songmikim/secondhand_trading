package org.koreait.restaurant.services;

import org.junit.jupiter.api.Test;
import org.koreait.restaurant.entities.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class RestaurantInfoServiceTest {
    @Autowired
    private RestaurantInfoService infoService;

    @Test
    void test(){
        List<Restaurant> items = infoService.getNestest(35.097993954721815, 129.03321328510245, 20 );
        items.forEach(System.out::println);
    }
}


/*
navigator.geolocation.getCurrentPosition((position) => {
doSomething(position.coords.latitude, position.coords.longitude);
});*/
