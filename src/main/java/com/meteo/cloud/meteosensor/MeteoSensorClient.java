package com.meteo.cloud.meteosensor;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "meteoClient", value = "meteoClient", url = "http://bitlinemeteo.com")
public interface MeteoSensorClient {

    @GetMapping(value = "/meteocompact/unisannio/{fileName}", consumes = "application/json")
    Resource getSensorData(@PathVariable(name = "fileName") String fileName);
}
