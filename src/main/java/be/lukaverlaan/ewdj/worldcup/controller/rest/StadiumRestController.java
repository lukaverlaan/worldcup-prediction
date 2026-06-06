package be.lukaverlaan.ewdj.worldcup.controller.rest;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/stadiums")
public class StadiumRestController {

    private static final Map<String, Integer> CAPACITIES = Map.of(
        "4702", 87500,
        "3142", 82500,
        "2891", 70240,
        "6074", 80000,
        "5230", 65326
    );

    @GetMapping("/{code}/capacity")
    public Map<String, Object> getCapacity(@PathVariable String code) {
        Integer capacity = CAPACITIES.getOrDefault(code, 60000);
        return Map.of(
            "stadiumCode", code,
            "capacity", capacity
        );
    }
}
