package org.mark.ipshow;

import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.text)).setText(
                "getLocalIpAddress" +
                IpUtils.getWiFiIpString(this) + "\n"
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                IpUtils.getLocalIpAddressv2();
            }
        }).start();
    }
}
