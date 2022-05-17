package com.meteo.cloud.meteosensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.*;

@Configuration
@EnableBatchProcessing
@EnableFeignClients(clients = MeteoSensorClient.class)
public class BatchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public MeteoSensorClient sensorClient;

    private final String date = LocalDateTime.now().format(new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('-')
            .appendValue(DAY_OF_MONTH, 2).toFormatter());


    @Bean
    public FlatFileItemReader<MeteoData> reader() {

        String fileName = LocalDateTime.now().format(new DateTimeFormatterBuilder()
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(YEAR, 4)
                .toFormatter()) + "C.txt";
        log.info("CLIENT HTTP: http://bitlinemeteo.com/meteocompact/unisannio/".concat(fileName));
        Resource dtResponse = sensorClient.getSensorData(fileName);

        return new FlatFileItemReaderBuilder<MeteoData>()
                .name("personItemReader")
                .resource(dtResponse)
                .delimited()
                .names("date", "hour", "column3", "column4", "column5", "column6", "column7", "column8", "column9", "column10", "column11", "column12")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<MeteoData>() {{
                    setTargetType(MeteoData.class);
                }})
                .build();
    }

    @Bean
    public MeteoSensorProcessor processor() {
        return new MeteoSensorProcessor();
    }

    @Bean
    public FlatFileItemWriter<MeteoData> writer() {

        FlatFileItemWriter<MeteoData> writer = new FlatFileItemWriter<>();

        String file_path = System.getProperty("FILE_PATH") + "/MS001-" + date + ".csv";
        Resource outputResource = new FileSystemResource(file_path);


        log.info("FILE OUTPUT PATH: ".concat(file_path));

        //Set output file location
        writer.setResource(outputResource);

        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(true);

        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<MeteoData>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<MeteoData>() {
                    {
                        setNames(new String[]{"date", "hour", "column8", "column11", "column12"});
                    }
                });
            }
        });
        writer.setForceSync(true);
        return writer;
    }

    @Bean
    public Job importUserJob(Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .build();
    }


    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<MeteoData, MeteoData>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
}
