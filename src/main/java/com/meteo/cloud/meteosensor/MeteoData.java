package com.meteo.cloud.meteosensor;

import lombok.*;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MeteoData {

    private String date;
    private String hour;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;
    private String column10;
    private String column11;
    private String column12;

}
