package demo.controller;

import demo.domain.SupplyLocation;
import demo.domain.SupplyLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SupplyLocationController {

    private SupplyLocationRepository repository;

    @Autowired
    public SupplyLocationController(SupplyLocationRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/bulk/supplyLocations", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void upload(@RequestBody List<SupplyLocation> locations) {
        repository.save(locations);
    }

    @RequestMapping(value = "/purge", method = RequestMethod.DELETE)
    public void purge() {
        repository.deleteAll();
    }
}
