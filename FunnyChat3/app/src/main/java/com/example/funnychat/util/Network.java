package com.example.funnychat.util;

import android.content.Context;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class Network {

    public static String determineCurrentIP (Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        if (ipAddress == 0) {
            return "127.0.0.1";
        } else {
            /*
                Числовое значение (10-чная сист.счисления):
                1108349100

                Битовое представление IP-адреса:
                10101100.00010000.00010000.01000010

                Человеко понятная запись:
                172.16.16.66
            */

            final String formattedIpAddress = String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),        // 172
                    (ipAddress >> 8 & 0xff),   // 16
                    (ipAddress >> 16 & 0xff),  // 16
                    (ipAddress >> 24 & 0xff)); // 66

            return formattedIpAddress;
        }
    }

}
