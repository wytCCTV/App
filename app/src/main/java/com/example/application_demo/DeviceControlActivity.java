package com.example.application_demo;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class DeviceControlActivity extends AppCompatActivity {
    private static final String TAG = DeviceControlActivity.class.getSimpleName();
    private Button connectButton;
    Socket clientSocket;
    private Vibrator vibrator;
    private PrintWriter clientPrintWriter;
    private InputStreamReader clientInputStream;

    private Spinner ledSpeedSpinner;
    private Spinner StatusSpinner;


    private EditText Tempmax      ;
    private EditText Swmax   ;
    private EditText Goodsweightmin;

    private EditText Swmin;
    private EditText minuteInput;
    private EditText secondInput;
    private EditText hourInput;
    private EditText hourEnd;
    private EditText minuteEnd;
    private EditText secondEnd;

    private EditText  second3  ;
    private EditText minute3;
    private EditText hour3;
    private TextView selectedAnimalText;
    private String selectedAnimal = "猫咪";
    private int statusCode1 = 0;
    private int statusCode2 = 0;
    private int statusCode3 = 0;
    private int statusCode4 = 0;

    private int waterstatusCode = 0;
    private int ledstatusCode = 0;
    private int heaterstatusCode = 0;

    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        connectButton = (Button) findViewById(R.id.connectButton);
        vibrator = (Vibrator) DeviceControlActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        Tempmax = findViewById(R.id.Tempmax);
        Swmax = findViewById(R.id.Swmax);
        Swmin = findViewById(R.id.Swmin);
        Goodsweightmin = findViewById(R.id.Goodsweightmin);
        hourInput = findViewById(R.id.hourInput);
        minuteInput = findViewById(R.id.minuteInput);
        secondInput = findViewById(R.id.secondInput);
        hourEnd = findViewById(R.id.hourEnd);
        minuteEnd = findViewById(R.id.minuteEnd);
        secondEnd = findViewById(R.id.secondEnd);
        hour3 = findViewById(R.id.hour3);
        minute3 = findViewById(R.id.minute3);
        second3 = findViewById(R.id.second3);
        selectedAnimalText = findViewById(R.id.selectedAnimalText);

        Button selectAnimalButton = findViewById(R.id.selectAnimalButton);
        updateSelectedAnimalText();
        selectAnimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnimalSelector();
            }
        });

//        控制设备绑定

        ToggleButton WaterToggle = findViewById(R.id.WaterToggle);
        ToggleButton LedToggle = findViewById(R.id.LedToggle);
        ToggleButton HeaterToggle = findViewById(R.id.HeaterToggle);



        WaterToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理按钮状态变化的操作
                if (isChecked) {
                    waterstatusCode = 1;

                } else {
                    // 按钮为关闭状态
                    waterstatusCode = 0;
                }
            }
        });
//
        LedToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理按钮状态变化的操作
                if (isChecked) {
                    ledstatusCode = 1;

                } else {
                    // 按钮为关闭状态
                    ledstatusCode = 0;
                }
            }
        });

        HeaterToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 处理按钮状态变化的操作
                if (isChecked) {
                    heaterstatusCode = 1;

                } else {
                    // 按钮为关闭状态
                    heaterstatusCode = 0;
                }
            }
        });

        radioGroup = findViewById(R.id.radio_group);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRadioButton = findViewById(checkedId);

                // 获取选中的RadioButton的文本
                String selectedOption = selectedRadioButton.getText().toString();

                // 根据选中的选项执行相应的操作
                if (selectedOption.equals("主界面")) {
                    statusCode4  = 0;
                } else if (selectedOption.equals("时间")) {
                    statusCode4  = 1;
                } else if (selectedOption.equals("阈值")) {
                    statusCode4  = 2;
                }
                else if (selectedOption.equals("设备控制")) {
                    statusCode4  = 3;
                }
            }
        });
    }

    private void showAnimalSelector() {
        final String[] animalOptions = {"猫咪", "狗狗", "兔子", "仓鼠"};
        int checkedItem = 0;
        for (int i = 0; i < animalOptions.length; i++) {
            if (animalOptions[i].equals(selectedAnimal)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("选择动物")
                .setSingleChoiceItems(animalOptions, checkedItem, (dialog, which) -> {
                    selectedAnimal = animalOptions[which];
                    updateSelectedAnimalText();
                    Toast.makeText(DeviceControlActivity.this, "已选择：" + selectedAnimal, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateSelectedAnimalText() {
        selectedAnimalText.setText("当前动物：" + selectedAnimal);
    }


    /**
     * 重写事件分发
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                v.clearFocus();//清除Edittext的焦点从而让光标消失
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        boolean vinstanceof;
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);
            ;
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom) {
                //点击EditText的时候不做隐藏处理
                return false;
            } else {
                return true;
            }
        }
        //如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {

        if (token != null) {
            //若token不为空则获取输入法管理器使其隐藏输入法键盘
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    boolean isConnect = true;


    public void onConnectButtonClicked(View view) {
        connectButton.setEnabled(false);
        if (connectButton.getText().toString().equals("连接")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // clientSocket = new Socket(ipStr, port);
                        clientSocket = new Socket("192.168.4.1", 8080);
                        Log.i(TAG, "是否连接成功：" + clientSocket.isConnected());
                        clientSocket.setSoTimeout(30);
                        clientPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8")), true);
                        clientInputStream = new InputStreamReader(clientSocket.getInputStream());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectButton.setEnabled(true);
                                connectButton.setText("断开");

                            }
                        });
                        isConnect = true;
                        while (isConnect && !clientSocket.isClosed() && !clientSocket.isInputShutdown()) {
                            try {
                                char buff[] = new char[4096];
                                int rcvLen = clientInputStream.read(buff);
                                if (rcvLen != -1) {
                                    String rcvMsg = new String(buff, 0, rcvLen);
                                    Log.i(TAG, "run:收到消息: " + rcvMsg);
                                    //                                  smsg += rcvMsg;
                                    if(rcvMsg != null && rcvMsg.contains(",") && rcvMsg.contains(":")) {
                                        //消息处理
                                        String temp = rcvMsg.split(",")[0].split(":")[1];
                                        String sw = rcvMsg.split(",")[1].split(":")[1];
                                        String goods = rcvMsg.split(",")[2].split(":")[1];

                                        int fan = Integer.parseInt(rcvMsg.split(",")[3].split(":")[1]);
                                        int water = Integer.parseInt(rcvMsg.split(",")[4].split(":")[1]);
                                        int  buzzer = Integer.parseInt(rcvMsg.split(",")[5].split(":")[1].split("\r\n")[0]);



                                        //单片机向app发送数据，前后台绑定
                                        //温度
                                        TextView TempStatusTextView = findViewById(R.id.textViewTemp);
                                        TextView SwTextView = findViewById(R.id.Sw);
                                        TextView GoodsTextView = findViewById(R.id.Goods);
                                        TextView FanStatusTextView = findViewById(R.id.Fan);
                                        TextView Water1TextView = findViewById(R.id.Water1);
                                        TextView WarningStatusTextView = findViewById(R.id.Warning);




                                        String Buzzerstatus = "正常";
                                        switch (buzzer) {
                                            case 0:
                                                Buzzerstatus = "正常";
                                                break;
                                            case 1:
                                                Buzzerstatus = "温度过低";
                                                break;
                                            case 2:
                                                Buzzerstatus = "水位过高";
                                                break;
                                            case 3:
                                                Buzzerstatus = "水位过低";
                                                break;
                                            case 4:
                                                Buzzerstatus = "重量不足";
                                                break;

                                        }


                                        String Waterstatus = "关";
                                        switch (water) {
                                            case 0:
                                                Waterstatus = "关";
                                                break;
                                            case 1:
                                                Waterstatus = "开";
                                                break;
                                        }

                                        String Fanstatus = "关";
                                        switch (fan) {
                                            case 0:
                                                Fanstatus = "关";
                                                break;
                                            case 1:
                                                Fanstatus = "开";
                                                break;
                                        }


                                        //向前台填充数据
                                        TempStatusTextView.setText(temp+" ℃");
                                        SwTextView.setText(sw);
                                        GoodsTextView.setText(goods);
                                        Water1TextView.setText(Waterstatus);
                                        FanStatusTextView.setText(Fanstatus);
                                        WarningStatusTextView.setText(Buzzerstatus);



                                    }
                                    } else {
                                    break;
                                }
                            } catch (Exception e) {
                            }
//                                }
                        }
                        Log.e(TAG, "client socket close!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connectButton.setEnabled(true);
                                connectButton.setText("连接");
                            }
                        });
                        try {
                            clientSocket.close();
                        } catch (Exception e) {
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DeviceControlActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                connectButton.setEnabled(true);
                                connectButton.setText("连接");
                            }
                        });
                        Log.e(TAG, ("connectService:" + e.getMessage()));   //如果Socket对象获取失败，即连接建立失败，会走到这段逻辑
                    }
                }
            }).start();
        } else if (connectButton.getText().toString().equals("断开")) {
//                connectButton.setEnabled(true);
            isConnect = false;
        }
    }

    public void onExitClicked(View view) {
        if (clientSocket != null)  //关闭连接socket
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        finish();     //退出APP
    }


    //关闭程序掉用处理部分
    public void onDestroy() {
        super.onDestroy();
        if (clientSocket != null)  //关闭连接socket
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    public void onSendButtonClicked(View view) {
        if (clientSocket == null || clientPrintWriter == null) {
            Toast.makeText(this, "请先连接", Toast.LENGTH_SHORT).show();
            return;
        }

        vibrator.vibrate(new long[]{0, 100, 0, 0}, -1);


        String swMax =  Swmax.getText().toString();
        String swMin =  Swmin.getText().toString();
        String temp =  Tempmax.getText().toString();
        String goodMin =  Goodsweightmin.getText().toString();


        String hourText = hourInput.getText().toString();
        String minuteText = minuteInput.getText().toString();
        String secondText = secondInput.getText().toString();


        String hour = hourEnd.getText().toString();
        String minute = minuteEnd.getText().toString();
        String second = secondEnd.getText().toString();


        String hour1 = hour3.getText().toString();
        String minute1 = minute3.getText().toString();
        String second1 = second3.getText().toString();


        new Thread(new Runnable() {
            @Override
            public void run() {
                //如果用户选择的是主界面
           if(statusCode4 == 0){
               clientPrintWriter.println("mode:"+statusCode4+",");
               clientPrintWriter.flush();
           }
           //如果用户选择的是阈值设置
                if(statusCode4 == 1){
                    clientPrintWriter.println("mode:"+statusCode4+","
                            +"sec1:"+secondText +","
                            +"minute1:"+minuteText+","
                            +"hour1:"+hourText+","

                            +"sec2:"+second +","
                            +"minute2:"+minute+","
                            +"hour2:"+hour+","

                            +"sec3:"+second1 +","
                            +"minute3:"+minute1+","
                            +"hour3:"+hour1+","

                    );
                    clientPrintWriter.flush();
                }
                if(statusCode4 == 2){
                    clientPrintWriter.println("mode:"+statusCode4+","
                            +"sw_max:"+swMax +","
                            +"sw_min:"+swMin+","
                            +"weight_min:"+goodMin+","
                            +"temp_min:"+temp+","
                    );
                    clientPrintWriter.flush();
                }

                //如果用户选择的是设备控制
                if(statusCode4 == 3){
                    clientPrintWriter.println("mode:"+statusCode4+","
                            +"water_state:"+waterstatusCode+","
                            +"servo_state:"+ledstatusCode+","
                            +"heater_state:"+heaterstatusCode+","
                    );
                    clientPrintWriter.flush();
                }


            }

        }).start();
    }


}
