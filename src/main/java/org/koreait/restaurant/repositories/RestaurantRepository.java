package org.koreait.restaurant.repositories;

import org.koreait.restaurant.entities.Restaurant;
import org.springframework.data.repository.ListCrudRepository;

public interface RestaurantRepository extends ListCrudRepository<Restaurant, Long> {

}