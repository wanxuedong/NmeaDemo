# NmeaDemo
Ch34X + NMEA

本项目基于CH34xUARTDemo
添加了NMEA数据的缓存和协议解析

使用步骤

###CH34xCahce类
作用：用于传入一或多段NMEA数据，筛选处理规范的数据，并按单条完整NMEA数据回调回来。

1.开启缓存
```
CH34xCahce.getInstance().start();
```

2.添加数据处理监听
```
 CH34xCahce.getInstance().addListener(new OnCH34xListener() {
            @Override
            public void location(String nmea) {
            }
        });
```

3.接收NMEA数据
```
String nmea = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38";
CH34xCahce.getInstance().receive(nmea);
```


###NMEAParser类
作用：传入单条完整NMEA数据，解析成符合的格式，并在不同的格式回调中返回数据

1.传入需要解析NMEA
```
String nmea = "$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38";
NMEAParser.getInstance().parse(nmea);
```

2.添加回调监听
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
