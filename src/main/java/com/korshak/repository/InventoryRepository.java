package com.korshak.repository;


import com.korshak.model.InventoryItem;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableScan
@Repository
public interface InventoryRepository extends CrudRepository<InventoryItem, String> {
}
