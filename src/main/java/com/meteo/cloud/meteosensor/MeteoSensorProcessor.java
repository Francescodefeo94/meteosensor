package com.meteo.cloud.meteosensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

public class MeteoSensorProcessor  implements ItemProcessor<MeteoData, MeteoData> {

    private static final Logger log = LoggerFactory.getLogger(MeteoSensorProcessor.class);

    private final String date= LocalDateTime.now().format( new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('/')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('/')
            .appendValue(YEAR,4)
            .toFormatter());

    @Override
    public MeteoData process(final MeteoData meteo) throws Exception {
        log.info("row readed: "+ meteo.toString());
        if(meteo.getDate().equals(date)) {
            return meteo;
        }
        else
            return null;
    }

}
