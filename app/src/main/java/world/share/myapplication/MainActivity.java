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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import world.share.myapplication.ch34xcache.CH34xCahce;
import world.share.myapplication.ch34xcache.OnCH34xListener;
import world.share.myapplication.neamparser.NMEAParser;
import world.share.myapplication.neamparser.constant.TalkerID;
import world.share.myapplication.neamparser.data.CoordinateData;
import world.share.myapplication.neamparser.data.TimeData;
import world.share.myapplication.neamparser.handler.NMEAAbstractParser;

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

    private TextView openButton, configButton,clearButton,writeButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.driver = new CH34xUARTDriver(
                (UsbManager) getSystemService(Context.USB_SERVICE), this,
                ACTION_USB_PERMISSION);
        initUI();
        if (!MyApp.driver.UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("您的手机不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认",
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持常亮的屏幕的状态
        writeBuffer = new byte[512];
        readBuffer = new byte[512];
        isOpen = false;
        configButton.setEnabled(false);
        writeButton.setEnabled(false);
        activity = this;

        //打开流程主要步骤为ResumeUsbList，UartInit
        openButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!isOpen) {
                    int retval = MyApp.driver.ResumeUsbPermission();
                    if (retval == 0) {
                        //Resume usb device list
                        retval = MyApp.driver.ResumeUsbList();
                        if (retval == -1)// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
                        {
                            Toast.makeText(MainActivity.this, "Open failed!",
                                    Toast.LENGTH_SHORT).show();
                            MyApp.driver.CloseDevice();
                        } else if (retval == 0) {
                            if (MyApp.driver.mDeviceConnection != null) {
                                if (!MyApp.driver.UartInit()) {//对串口设备进行初始化操作
                                    Toast.makeText(MainActivity.this, "Initialization failed!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(MainActivity.this, "Device opened",
                                        Toast.LENGTH_SHORT).show();
                                isOpen = true;
                                openButton.setText("Close");
                                configButton.setEnabled(true);
                                writeButton.setEnabled(true);
                                new readThread().start();//开启读线程读取串口接收的数据
                            } else {
                                Toast.makeText(MainActivity.this, "Open failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setIcon(R.drawable.icon);
                            builder.setTitle("未授权限");
                            builder.setMessage("确认退出吗？");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            });
                            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub

                                }
                            });
                            builder.show();

                        }
                    }
                } else {
                    openButton.setText("Open");
                    configButton.setEnabled(false);
                    writeButton.setEnabled(false);
                    isOpen = false;
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    MyApp.driver.CloseDevice();
                }
            }
        });

        configButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (MyApp.driver.SetConfig(baudRate, dataBit, stopBit, parity,//配置串口波特率，函数说明可参照编程手册
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
                byte[] to_send = toByteArray(writeText.getText().toString());        //以16进制发送
//				byte[] to_send = toByteArray2(writeText.getText().toString());		//以字符串方式发送
                int retval = MyApp.driver.WriteData(to_send, to_send.length);//写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
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
        MyApp.driver.CloseDevice();
        super.onDestroy();
    }

    //处理界面
    private void initUI() {
        readText = (TextView) findViewById(R.id.ReadValues);
        writeText = (EditText) findViewById(R.id.WriteValues);
        configButton = (TextView) findViewById(R.id.configButton);
        writeButton = (Button) findViewById(R.id.WriteButton);
        openButton = (TextView) findViewById(R.id.open_device);
        clearButton = (Button) findViewById(R.id.clearButton);

        baudSpinner = (Spinner) findViewById(R.id.baudRateValue);
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

//        parser.parse("$GNRMC,091529.00,A,3201.75538,N," +
//                "11854.95529,E,0.000,,030621,,,A," +
//                "V*13" +
//                "$GNGGA,091529.00,3201.7553" +
//                "8,N,11854.95529,E,1,16,1.16,66.0" +
//                ",M,,M,,*6C" +
//                "$GNGSA,A,3,02,05,12," +
//                "20,25,06,09,17,19,195,,,2.00,1.1" +
//                "6,1.63,1*3C" +
//                "$GNGSA,A,3,13,27,05" +
//                ",08,28,33,,,,,,,2.00,1.16,1.63,4" +
//                "*04" +
//                "$GPGSV,3,1,11,02,61,353,40," +
//                "05,39,252,31,06,49,062,17,09,25," +
//                "050,20,0*6B" +
//                "$GPGSV,3,2,11,12,26" +
//                ",260,24,13,12,184,,17,14,145,14," +
//                "19,36,145,15,0*62" +
//                "$GPGSV,3,3,11" +
//                ",20,27,254,36,25,14,295,44,195,6" +
//                "4,130,14,0*60" +
//                "$GBGSV,2,1,06,05," +
//                "17,253,36,08,57,357,30,13,51,319" +
//                ",38,27,58,325,36,0*77" +
//                "$GBGSV,2," +
//                "2,06,28,62,116,26,33,39,042,25,0" +
//                "*76");
//        parser.parse("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
//        CH340Cahce.getInstance().receive("$GPRMC,163407.000,A,5004.7485,N,01423.8956,E,0.04,36.97,180416,,*38");
        CH34xCahce.getInstance().receive("$GNRMC,091529.00,A,3201.75538,N,\n" +
                "11854.95529,E,0.000,,030621,,,A,\n" +
                "V*13\n" +
                "$GNGGA,091529.00,3201.7553\n" +
                "8,N,11854.95529,E,1,16,1.16,66.0\n" +
                ",M,,M,,*6C\n" +
                "$GNGSA,A,3,02,05,12,\n" +
                "20,25,06,09,17,19,195,,,2.00,1.1\n" +
                "6,1.63,1*3C\n" +
                "$GNGSA,A,3,13,27,05\n" +
                ",08,28,33,,,,,,,2.00,1.16,1.63,4\n" +
                "*04\n" +
                "$GPGSV,3,1,11,02,61,353,40,\n" +
                "05,39,252,31,06,49,062,17,09,25,\n" +
                "050,20,0*6B\n" +
                "$GPGSV,3,2,11,12,26\n" +
                ",260,24,13,12,184,,17,14,145,14,\n" +
                "19,36,145,15,0*62\n" +
                "$GPGSV,3,3,11\n" +
                ",20,27,254,36,25,14,295,44,195,6\n" +
                "4,130,14,0*60\n" +
                "$GBGSV,2,1,06,05,\n" +
                "17,253,36,08,57,357,30,13,51,319\n" +
                ",38,27,58,325,36,0*77\n" +
                "$GBGSV,2,\n" +
                "2,06,28,62,116,26,33,39,042,25,0\n" +
                "*76");
        CH34xCahce.getInstance().start();
        CH34xCahce.getInstance().addListener(new OnCH34xListener() {
            @Override
            public void location(String nmea) {
                NMEAParser.getInstance().parse(nmea);
                NMEAParser.getInstance().setNmeaHandler(new NMEAAbstractParser() {
                    @Override
                    public void onGGA(TalkerID talkerID, TimeData time, CoordinateData coordinateData, int satellites, float altitude) {
                        readText.setText(talkerID + "   卫星数量 : " + satellites + "  :  标准时间 : " + time.toString() + " : 坐标 : " + coordinateData.toString());
                        Toast.makeText(MainActivity.this, talkerID + "   卫星数量 : " + satellites + "  :  标准时间 : " + time.toString() + " : 坐标 : " + coordinateData.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRMC(TalkerID talkerID, TimeData time, CoordinateData coordinateData, float speed, float course) {
                        super.onRMC(talkerID, time, coordinateData, speed, course);
                        readText.setText(talkerID + "   当前角度 : " + course + "  :  标准时间 : " + time.toString() + " : 坐标 : " + coordinateData.toString());
                        Toast.makeText(MainActivity.this, talkerID + "   当前角度 : " + course + "  :  标准时间 : " + time.toString() + " : 坐标 : " + coordinateData.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
                int length = MyApp.driver.ReadData(buffer, 4096);
                if (length > 0) {
//                    String recv = toHexString(buffer, length);        //以16进制输出
                    String recv = new String(buffer, 0, length);        //以字符串形式输出
//                    CH340Cahce.getInstance().receive(recv);
                    msg.obj = recv;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    /**
     * 将byte[]数组转化为String类型
     *
     * @param arg    需要转换的byte[]数组
     * @param length 需要转换的数组长度
     * @return 转换后的String队形
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
     * 将String转化为byte[]数组
     *
     * @param arg 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i] != ' ') {
                    NewArray[length] = array[i];
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
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
                /* 将 每个char的值每两个组成一个16进制数据 */
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
     * 将String转化为byte[]数组
     *
     * @param arg 需要转换的String对象
     * @return 转换后的byte[]数组
     */
    private byte[] toByteArray2(String arg) {
        if (arg != null) {
            // 1.先去除String中的' '，然后将String转换为char数组
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