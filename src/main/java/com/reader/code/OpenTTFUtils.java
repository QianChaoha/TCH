package com.reader.code;

import android.content.Context;
import android.widget.Toast;

import com.nativec.tools.ModuleManager;
import com.nativec.tools.SerialPort;
import com.nativec.tools.SerialPortFinder;
import com.reader.helper.ReaderHelper;
import com.uhf.uhf.R;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Data: 2019/1/1
 *
 * @author: cqian
 */
public class OpenTTFUtils {
    public static SerialPort mSerialPort = null;
    public static SerialPortFinder mSerialPortFinder;

    private static final String TTYS4 = "ttyS4 (rk_serial)";
    private static final int baud4 = 115200;


    static String[] entryValues = null;
    private static List<String> mPortList = new ArrayList<String>();
    private static int mPosPort = -1;

    public static void init() {
        mSerialPortFinder = new SerialPortFinder();
        entryValues = mSerialPortFinder.getAllDevicesPath();
        String[] lists = mSerialPortFinder.getAllDevices();
        for (int i = 0; i < lists.length; i++) {
            mPortList.add(lists[i]);
        }
    }
    public static void closeUHF() {
        if (mSerialPort != null)
            mSerialPort.close();
        mSerialPort = null;
    }
    public static boolean openUHF(Context context) {
        if (!connect(context, baud4, TTYS4)) {
            return false;
        }
        if (!ModuleManager.newInstance().setUHFStatus(true)) {
            throw new RuntimeException("UHF RFID power on failure,may you open in other" +
                    " Process and do not exit it");
        }
        return true;
    }


    private static boolean connect(Context context, int port, String tty) {

        init();

        if (mPortList.indexOf(tty) >= 0)
            mPosPort = mPortList.indexOf(tty);

        if (entryValues == null || entryValues.length == 0) {
            Toast.makeText(
                    context,
                    "设备不可用",
                    Toast.LENGTH_SHORT).show();
            return false;

        }
        if (mPosPort < 0) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.rs232_error),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        try {

            mSerialPort = new SerialPort(new File(entryValues[mPosPort]), port, 0);


            try {
                ReaderHelper.getDefaultHelper().setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }
            return true;
            //finish();
        } catch (SecurityException e) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_security),
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_unknown),
                    Toast.LENGTH_SHORT).show();
        } catch (InvalidParameterException e) {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.error_configuration),
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
