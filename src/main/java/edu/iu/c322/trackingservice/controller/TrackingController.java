package edu.iu.c322.trackingservice.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import edu.iu.c322.trackingservice.model.Tracking;
import edu.iu.c322.trackingservice.model.TrackingUpdateRequest;
import edu.iu.c322.trackingservice.repository.TrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/trackings")
public class TrackingController {

    private final WebClient orderservice;

    private TrackingRepository trackingRepository;

    @Autowired
    public TrackingController(WebClient.Builder webClientBuilder,TrackingRepository trackingRepository) {
        orderservice = webClientBuilder.baseUrl("http://localhost:8083").build();
        this.trackingRepository = trackingRepository;
    }

    @GetMapping("/{orderId}/{itemId}")
    public ResponseEntity<Map<String, Object>> getTrackingByOrderIdAndItemId(@PathVariable int orderId, @PathVariable int itemId) {
        Optional<Tracking> tracking = Optional.ofNullable(trackingRepository.findByOrderIdAndItemId(orderId, itemId));
        if (tracking.isPresent()) {
            Tracking t = tracking.get();
            Map<String, Object> response = new HashMap<>();
            //only display the status and date
            response.put("status", t.getStatus());
            response.put("date", t.getDate());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateTrackingStatus(@PathVariable int orderId, @RequestBody TrackingUpdateRequest trackingStatusUpdateRequest) {
        String status = trackingStatusUpdateRequest.getStatus();

        List<Tracking> trackings = trackingRepository.findAllByOrderId(orderId);
        if (!trackings.isEmpty()) {
            for (Tracking tracking : trackings) {
                tracking.setStatus(status);
                tracking.setDate(LocalDate.now());
                trackingRepository.save(tracking);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(path = "/{orderId}", consumes = "application/json")
    public ResponseEntity<Void> addTrackingsForOrder(@PathVariable int orderId, @RequestBody TrackingUpdateRequest trackingRequest) {
        List<Integer> itemIds = trackingRequest.getItemIds();
        itemIds.stream()
                .filter(itemId -> trackingRepository.findByOrderIdAndItemId(orderId, itemId) == null)
                .map(itemId -> {
                    //Create new tracking record for item
                    Tracking tracking = new Tracking();
                    tracking.setOrderId(orderId);
                    tracking.setItemId(itemId);
                    tracking.setStatus("ordered");
                    tracking.setDate(LocalDate.now());
                    trackingRepository.save(tracking);
                    return tracking;
                })
                .count();

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
