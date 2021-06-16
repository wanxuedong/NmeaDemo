# NmeaDemo
Ch34X + NMEA

本项目基于CH34xUARTDemo，添加了NMEA数据的缓存和协议解析

使用步骤:通过CH34xCahce和NMEAParser的搭配使用完成数据处理

### CH34xCahce类
作用：用于传入一或多段NMEA数据，筛选处理规范的数据，并按单条完整NMEA数据回调回来。

（1）开启缓存处理
```
CH34xCahce.getInstance().start();
```

（2）添加数据处理监听
```
 CH34xCahce.getInstance().addListener(new OnCH34xListener() {
            @Override
            public void location(String nmea) {
            }
        });
```

（3）接收NMEA数据
```
String nmea = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38";
CH34xCahce.getInstance().receive(nmea);
```

（4）停止缓存处理
```
CH34xCahce.getInstance().stop();
```


### NMEAParser类
作用：传入单条完整NMEA数据，解析成符合的格式，并在不同的格式回调中返回数据

（1）传入需要解析NMEA
```
String nmea = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38";
NMEAParser.getInstance().parse(nmea);
```

（2）添加回调监听
```
NMEAParser.getInstance().setNmeaHandler(new NMEAAbstractParser() {

            @Override
            public void onOriginal(String[] original) {
                super.onOriginal(original);
            }

            @Override
            public void onGGA(TalkerID talkerID, TimeData time, CoordinateData coordinateData, int satellites, float altitude) {
            }

            @Override
            public void onRMC(TalkerID talkerID, String state, TimeData time, CoordinateData coordinateData, float speed, float course) {
                super.onRMC(talkerID, state, time, coordinateData, speed, course);
            }

            @Override
            public void onGSA(TalkerID talkerID, String mode, String state, List<String> numbers, String pDop, String hDop, String vDop) {
                super.onGSA(talkerID, mode, state, numbers, pDop, hDop, vDop);

            }

            @Override
            public void onGSV(TalkerID talkerID, int satellites, List<SatellitesData> satellitesDataList) {
                super.onGSV(talkerID, satellites, satellitesDataList);
            }

            @Override
            public void onGLL(TalkerID talkerID, TimeData time, CoordinateData coordinateData, String state) {
                super.onGLL(talkerID, time, coordinateData, state);
            }

            @Override
            public void onVTG(TalkerID talkerID, float northCourse, float magneticCourse, float seaSpeed, float landSpeed) {
                super.onVTG(talkerID, northCourse, magneticCourse, seaSpeed, landSpeed);
            }
        });
```
每个回调的参数可以在接口中查看，都已注释

### 参考文档：
[doc/AN019_NMEA0183协议说明.pdf](https://github.com/wanxuedong/NmeaDemo/tree/master/doc)  
[doc/CASIC多模卫星导航接收机协议规范.pdf](https://github.com/wanxuedong/NmeaDemo/tree/master/doc)  
[doc/CH34xUart_Android_Dvlp_Guide.pdf](https://github.com/wanxuedong/NmeaDemo/tree/master/doc)

### NMEA示例数据
```
$GPGSV,3,3,11,26,03,102,,27,35,041,16,30,22,316,39,0*5D
$GBGSV,1,1,04,05,,,37,07,68,003,31,10,53,310,39,36,,,36,0*75
$GNRMC,010831.00,A,3201.76436,N,11855.00793,E,0.412,,070621,,,A,V*1F
$GNGGA,010831.00,3201.76436,N,11855.00793,E,1,06,4.10,-29.1,M,,M,,*46
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.05,1*01
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.05,4*05
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,32,08,66,009,34,0*66
$GPGSV,3,2,11,09,18,231,30,14,02,290,37,16,22,085,,21,54,154,,0*67
$GPGSV,3,3,11,26,03,102,,27,35,041,16,30,22,316,39,0*5D
$GBGSV,1,1,04,05,,,36,07,68,003,31,10,53,310,39,36,,,36,0*74
$GNRMC,010832.00,A,3201.76496,N,11855.00779,E,0.233,,070621,,,A,V*17
$GNGGA,010832.00,3201.76496,N,11855.00779,E,1,06,4.10,-29.2,M,,M,,*48
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.06,1*02
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.06,4*06
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,32,08,66,009,34,0*66
$GPGSV,3,2,11,09,18,231,30,14,02,290,37,16,22,085,,21,54,154,,0*67
$GPGSV,3,3,11,26,03,102,,27,35,041,16,30,22,316,40,0*53
$GBGSV,1,1,04,05,,,36,07,68,003,30,10,53,310,39,36,,,36,0*75
$GNRMC,010833.00,A,3201.76515,N,11855.00729,E,0.523,,070621,,,A,V*1F
$GNGGA,010833.00,3201.76515,N,11855.00729,E,1,06,4.10,-28.7,M,,M,,*42
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.06,1*02
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.06,4*06
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,33,08,66,009,34,0*67
$GPGSV,3,2,11,09,18,231,30,14,02,290,37,16,22,085,,21,54,154,,0*67
$GPGSV,3,3,11,26,03,102,,27,35,041,19,30,22,316,40,0*5C
$GBGSV,1,1,04,05,,,36,07,68,003,30,10,53,310,39,36,,,36,0*75
$GNRMC,010834.00,A,3201.76541,N,11855.00718,E,0.194,,070621,,,A,V*13
$GNGGA,010834.00,3201.76541,N,11855.00718,E,1,06,4.10,-29.2,M,,M,,*42
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.06,1*02
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.06,4*06
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,33,08,66,009,34,0*67
$GPGSV,3,2,11,09,18,231,29,14,02,290,37,16,22,085,,21,54,154,,0*6F
$GPGSV,3,3,11,26,03,102,,27,35,041,21,30,22,316,40,0*57
$GBGSV,1,1,04,05,,,36,07,68,003,30,10,53,310,39,36,,,36,0*75
$GNRMC,010835.00,A,3201.76598,N,11855.00742,E,0.254,,070621,,,A,V*16
$GNGGA,010835.00,3201.76598,N,11855.00742,E,1,06,4.10,-29.9,M,,M,,*43
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.06,1*02
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.06,4*06
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,33,08,66,009,33,0*60
$GPGSV,3,2,11,09,18,231,29,14,02,290,37,16,22,085,,21,54,154,,0*6F
$GPGSV,3,3,11,26,03,102,,27,35,041,22,30,22,316,40,0*54
$GBGSV,1,1,04,05,,,36,07,68,003,30,10,53,310,39,36,,,36,0*75
$GNRMC,010836.00,A,3201.76612,N,11855.00750,E,1.771,,070621,,,A,V*14
$GNGGA,010836.00,3201.76612,N,11855.00750,E,1,06,4.10,-30.2,M,,M,,*41
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.06,1*02
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.06,4*06
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,33,08,66,009,33,0*60
$GPGSV,3,2,11,09,18,231,28,14,02,290,36,16,22,085,,21,54,154,,0*6F
$GPGSV,3,3,11,26,03,102,,27,35,041,23,30,22,316,40,0*55
$GBGSV,1,1,04,05,,,36,07,68,003,30,10,53,310,39,36,,,36,0*75
$GNRMC,010837.00,A,3201.76573,N,11855.00758,E,0.390,,070621,,,A,V*13
$GNGGA,010837.00,3201.76573,N,11855.00758,E,1,06,4.10,-29.7,M,,M,,*41
$GNGSA,A,3,14,30,08,09,,,,,,,,,6.51,4.10,5.06,1*02
$GNGSA,A,3,10,07,,,,,,,,,,,6.51,4.10,5.06,4*06
$GPGSV,3,1,11,01,31,177,,04,09,199,,07,59,300,33,08,66,009,33,0*60
$GPGSV,3,2,11,09,18,231,29,14,02,290,36,16,22,085,,21,54,154,,0*6E
```