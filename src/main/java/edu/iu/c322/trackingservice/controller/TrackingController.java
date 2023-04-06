package edu.iu.c322.trackingservice.controller;

import edu.iu.c322.trackingservice.model.Tracking;
import edu.iu.c322.trackingservice.repository.TrackingRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class TrackingController {
    private TrackingRepository repository;

    public TrackingController(TrackingRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/{orderId}/{itemId}")
    public List<Tracking> find(@PathVariable int orderId,
                               @PathVariable int itemId){

        return repository.findByOrderIdAndItemId(orderId, itemId);
    }

}
