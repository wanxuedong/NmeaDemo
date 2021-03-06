package world.share.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import world.share.myapplication.ch34xcache.CH34xCahce;
import world.share.myapplication.ch34xcache.OnCH34xListener;
import world.share.myapplication.neamparser.NMEAParser;
import world.share.myapplication.neamparser.constant.TalkerID;
import world.share.myapplication.neamparser.data.CoordinateData;
import world.share.myapplication.neamparser.data.SatellitesData;
import world.share.myapplication.neamparser.data.TimeData;
import world.share.myapplication.neamparser.handler.NMEAAbstractParser;
import world.share.myapplication.neamparser.message.MessageProduct;

/**
 * @author wanxuedong  2021/6/5
 */
public class MainActivity extends Activity {

    public static final String TAG = "cn.wch.wchusbdriver";
    private static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";

    public readThread handlerThread;
    protected final Object ThreadLock = new Object();
    private TextView readText;
    private EditText writeText;
    private Spinner baudSpinner;
    private Spinner stopSpinner;
    private Spinner dataSpinner;
    private Spinner paritySpinner;
    private Spinner flowSpinner;
    private boolean isOpen;
    private Handler handler;
    private int retval;
    private MainActivity activity;

    private TextView openButton, configButton, clearButton, writeButton;

    public byte[] writeBuffer;
    public byte[] readBuffer;
    public int actualNumBytes;

    public int numBytes;
    public byte count;
    public int status;
    public byte writeIndex = 0;
    public byte readIndex = 0;

    public int baudRate;
    public byte baudRate_byte;
    public byte stopBit;
    public byte dataBit;
    public byte parity;
    public byte flowControl;

    public boolean isConfiged = false;
    public boolean READ_ENABLE = false;
    public SharedPreferences sharePrefSettings;
    public String act_string;

    private CH34xUARTDriver driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        driver = new CH34xUARTDriver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);
        initUI();
        if (!driver.UsbFeatureSupported())// ????????????????????????USB HOST
        {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("??????")
                    .setMessage("?????????????????????USB HOST?????????????????????????????????")
                    .setPositiveButton("??????",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    System.exit(0);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// ??????????????????????????????
        writeBuffer = new byte[512];
        readBuffer = new byte[512];
        isOpen = false;
        configButton.setEnabled(false);
        writeButton.setEnabled(false);
        activity = this;

        //???????????????????????????ResumeUsbList???UartInit
        openButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!isOpen) {
                    int retval = driver.ResumeUsbPermission();
                    if (retval == 0) {
                        //Resume usb device list
                        retval = driver.ResumeUsbList();
                        if (retval == -1)// ResumeUsbList??????????????????CH34X??????????????????????????????
                        {
                            Toast.makeText(MainActivity.this, "Open failed!",
                                    Toast.LENGTH_SHORT).show();
                            driver.CloseDevice();
                        } else if (retval == 0) {
                            if (driver.mDeviceConnection != null) {
                                if (!driver.UartInit()) {//????????????????????????????????????
                                    Toast.makeText(MainActivity.this, "Initialization failed!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(MainActivity.this, "Device opened",
                                        Toast.LENGTH_SHORT).show();
                                isOpen = true;
                                openButton.setText("????????????");
                                configButton.setEnabled(true);
                                writeButton.setEnabled(true);
                                new readThread().start();//??????????????????????????????????????????
                            } else {
                                Toast.makeText(MainActivity.this, "Open failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setIcon(R.drawable.ic_launcher_background);
                            builder.setTitle("????????????");
                            builder.setMessage("??????????????????");
                            builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            });
                            builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            });
                            builder.show();

                        }
                    }
                } else {
                    openButton.setText("????????????");
                    configButton.setEnabled(false);
                    writeButton.setEnabled(false);
                    isOpen = false;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    driver.CloseDevice();
                }
            }
        });

        configButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (driver.SetConfig(baudRate, dataBit, stopBit, parity,//?????????????????????????????????????????????????????????
                        flowControl)) {
                    Toast.makeText(MainActivity.this, "Config successfully",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Config failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        writeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                byte[] to_send = toByteArray(writeText.getText().toString());        //???16????????????
//				byte[] to_send = toByteArray2(writeText.getText().toString());		//????????????????????????
                int retval = driver.WriteData(to_send, to_send.length);//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                if (retval < 0) {
                    Toast.makeText(MainActivity.this, "Write failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                readText.append((String) msg.obj);
            }
        };

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroy() {
        isOpen = false;
        driver.CloseDevice();
        super.onDestroy();
    }

    //????????????
    private void initUI() {
        readText = findViewById(R.id.ReadValues);
        writeText = findViewById(R.id.WriteValues);
        configButton = findViewById(R.id.configButton);
        writeButton = findViewById(R.id.WriteButton);
        openButton = findViewById(R.id.open_device);
        clearButton = findViewById(R.id.clearButton);

        baudSpinner = findViewById(R.id.baudRateValue);
        ArrayAdapter<CharSequence> baudAdapter = ArrayAdapter
                .createFromResource(this, R.array.baud_rate,
                        R.layout.my_spinner_textview);
        baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        baudSpinner.setAdapter(baudAdapter);
        baudSpinner.setGravity(0x10);
        baudSpinner.setSelection(5);
        /* by default it is 9600 */
        baudRate = 9600;

        /* stop bits */
        stopSpinner = (Spinner) findViewById(R.id.stopBitValue);
        ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter
                .createFromResource(this, R.array.stop_bits,
                        R.layout.my_spinner_textview);
        stopAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        stopSpinner.setAdapter(stopAdapter);
        stopSpinner.setGravity(0x01);
        /* default is stop bit 1 */
        stopBit = 1;

        /* data bits */
        dataSpinner = (Spinner) findViewById(R.id.dataBitValue);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter
                .createFromResource(this, R.array.data_bits,
                        R.layout.my_spinner_textview);
        dataAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        dataSpinner.setAdapter(dataAdapter);
        dataSpinner.setGravity(0x11);
        dataSpinner.setSelection(3);
        /* default data bit is 8 bit */
        dataBit = 8;

        /* parity */
        paritySpinner = (Spinner) findViewById(R.id.parityValue);
        ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter
                .createFromResource(this, R.array.parity,
                        R.layout.my_spinner_textview);
        parityAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        paritySpinner.setAdapter(parityAdapter);
        paritySpinner.setGravity(0x11);
        /* default is none */
        parity = 0;

        /* flow control */
        flowSpinner = (Spinner) findViewById(R.id.flowControlValue);
        ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter
                .createFromResource(this, R.array.flow_control,
                        R.layout.my_spinner_textview);
        flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        flowSpinner.setAdapter(flowAdapter);
        flowSpinner.setGravity(0x11);
        /* default flow control is is none */
        flowControl = 0;

        /* set the adapter listeners for baud */
        baudSpinner.setOnItemSelectedListener(new MyOnBaudSelectedListener());
        /* set the adapter listeners for stop bits */
        stopSpinner.setOnItemSelectedListener(new MyOnStopSelectedListener());
        /* set the adapter listeners for data bits */
        dataSpinner.setOnItemSelectedListener(new MyOnDataSelectedListener());
        /* set the adapter listeners for parity */
        paritySpinner
                .setOnItemSelectedListener(new MyOnParitySelectedListener());
        /* set the adapter listeners for flow control */
        flowSpinner.setOnItemSelectedListener(new MyOnFlowSelectedListener());

        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                readText.setText("");
            }
        });
//        CH340Cahce.getInstance().receive("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
//        CH34xCahce.getInstance().receive("$GNRMC,091529.00,A,3201.75538,N,\n" +
//                "11854.95529,E,0.000,,030621,,,A,\n" +
//                "V*13\n" +
//                "$GNGGA,091529.00,3201.7553\n" +
//                "8,N,11854.95529,E,1,16,1.16,66.0\n" +
//                ",M,,M,,*6C\n" +
//                "$GNGSA,A,3,02,05,12,\n" +
//                "20,25,06,09,17,19,195,,,2.00,1.1\n" +
//                "6,1.63,1*3C\n" +
//                "$GNGSA,A,3,13,27,05\n" +
//                ",08,28,33,,,,,,,2.00,1.16,1.63,4\n" +
//                "*04\n" +
//                "$GPGSV,3,1,11,02,61,353,40,\n" +
//                "05,39,252,31,06,49,062,17,09,25,\n" +
//                "050,20,0*6B\n" +
//                "$GPGSV,3,2,11,12,26\n" +
//                ",260,24,13,12,184,,17,14,145,14,\n" +
//                "19,36,145,15,0*62\n" +
//                "$GPGSV,3,3,11\n" +
//                ",20,27,254,36,25,14,295,44,195,6\n" +
//                "4,130,14,0*60\n" +
//                "$GBGSV,2,1,06,05,\n" +
//                "17,253,36,08,57,357,30,13,51,319\n" +
//                ",38,27,58,325,36,0*77\n" +
//                "$GBGSV,2,\n" +
//                "2,06,28,62,116,26,33,39,042,25,0\n" +
//                "*76");
        CH34xCahce.getInstance().start();
        CH34xCahce.getInstance().addListener(new OnCH34xListener() {
            @Override
            public void location(String nmea) {
                NMEAParser.getInstance().parse(nmea);
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
                        Toast.makeText(MainActivity.this, "RMC : " + talkerID + "   ???????????? : " + course + "  :  ???????????? : " + time.toString() + " : ?????? : " + coordinateData.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onGSA(TalkerID talkerID, String mode, String state, List<String> numbers, String pDop, String hDop, String vDop) {
                        super.onGSA(talkerID, mode, state, numbers, pDop, hDop, vDop);
                        Toast.makeText(MainActivity.this, "GSA : " + talkerID + "   ???????????? : " + ("M".equals(mode) ? "????????????" : "????????????") + "  :  ???????????? : " + state + " : ???????????? : " + numbers.size(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onGSV(TalkerID talkerID, int satellites, List<SatellitesData> satellitesDataList) {
                        super.onGSV(talkerID, satellites, satellitesDataList);
                        Toast.makeText(MainActivity.this, "GSV : " + talkerID + "   ???????????? : " + satellites, Toast.LENGTH_SHORT).show();
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
            }
        });
        MessageProduct messageProduct = new MessageProduct();
        String message = messageProduct.productCAS10(2);
        byte[] txt = toByteArray(message);
        return;
    }

    public class MyOnBaudSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            baudRate = Integer.parseInt(parent.getItemAtPosition(position)
                    .toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class MyOnStopSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            stopBit = (byte) Integer.parseInt(parent
                    .getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    public class MyOnDataSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            dataBit = (byte) Integer.parseInt(parent
                    .getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    public class MyOnParitySelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String parityString = new String(parent.getItemAtPosition(position)
                    .toString());
            if (parityString.compareTo("None") == 0) {
                parity = 0;
            }

            if (parityString.compareTo("Odd") == 0) {
                parity = 1;
            }

            if (parityString.compareTo("Even") == 0) {
                parity = 2;
            }

            if (parityString.compareTo("Mark") == 0) {
                parity = 3;
            }

            if (parityString.compareTo("Space") == 0) {
                parity = 4;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    public class MyOnFlowSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            String flowString = new String(parent.getItemAtPosition(position)
                    .toString());
            if (flowString.compareTo("None") == 0) {
                flowControl = 0;
            }

            if (flowString.compareTo("CTS/RTS") == 0) {
                flowControl = 1;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    private class readThread extends Thread {

        @Override
        public void run() {

            byte[] buffer = new byte[4096];

            while (true) {

                Message msg = Message.obtain();
                if (!isOpen) {
                    break;
                }
                int length = driver.ReadData(buffer, 4096);
                if (length > 0) {
//                    String recv = toHexString(buffer, length);        //???16????????????
                    String recv = new String(buffer, 0, length);        //????????????????????????
                    CH34xCahce.getInstance().receive(recv);
                    msg.obj = recv;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    /**
     * ???byte[]???????????????String??????
     *
     * @param arg    ???????????????byte[]??????
     * @param length ???????????????????????????
     * @return ????????????String??????
     */
    private String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }

    /**
     * ???String?????????byte[]??????
     *
     * @param arg ???????????????String??????
     * @return ????????????byte[]??????
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.?????????String??????' '????????????String?????????char?????? */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* ???char??????????????????????????????????????????????????? */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* ??? ??????char???????????????????????????16???????????? */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[]{};
    }

    /**
     * ???String?????????byte[]??????
     *
     * @param arg ???????????????String??????
     * @return ????????????byte[]??????
     */
    private byte[] toByteArray2(String arg) {
        if (arg != null) {
            // 1.?????????String??????' '????????????String?????????char??????
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }

            byte[] byteArray = new byte[length];
            for (int i = 0; i < length; i++) {
                byteArray[i] = (byte) NewArray[i];
            }
            return byteArray;

        }
        return new byte[]{};
    }
}