package edu.iu.c322.trackingservice.repository;

import edu.iu.c322.trackingservice.model.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, Integer> {

    // Find a tracking entry by order ID and item ID
    Tracking findByOrderIdAndItemId(int orderId, int itemId);
    List<Tracking> findAllByOrderId(int orderId);


}