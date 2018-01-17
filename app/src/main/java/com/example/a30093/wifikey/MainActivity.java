package com.example.a30093.wifikey;

import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class MainActivity extends AppCompatActivity {

    String cont;
    String[] show;
    ListView lv;
    ArrayAdapter<String> aa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cont = getWifiInfo();
        if (cont.isEmpty()){
            return;
        }
        lv = (ListView) findViewById(R.id.ListView);
        show = getShow(cont);
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, show);
        lv.setAdapter(aa);
    }

    public String[] getShow(String s) {
        String[] str = new String[10];
        for(int i = 0; i < 10; i++) {
            str[i] = "Name" + getStringContent(s, "account") + "\n"
                    + "Password" + getStringContent(s, "pass");
        }
        return str;
    }

    public String getStringContent(String s, String name) {
        StringBuffer avail = new StringBuffer();
        int i;
        int account = s.indexOf("ssid=");
        int password = s.indexOf("psk=");
        int other = s.indexOf("key");
        if(name == "account")
            for(i = account + 5; i < password - 1; i++)
                avail.append(s.charAt(i));
        if(name == "pass")
            for (i = password + 4; i < other - 1; i++)
                avail.append(s.charAt(i));
        cont = cont.substring(other);
        return String.valueOf(avail);
    }

    public String getWifiInfo() {
        StringBuffer sb = new StringBuffer();
        java.lang.Process process = null;
        DataOutputStream dos = null;
        DataInputStream dis = null;
        String line;

        try {
            //获取root环境
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dis = new DataInputStream(process.getInputStream());

            /*
            把用户的命令送给内核执行
            process实为root过的shell（系统的用户界面）用户与内核的交互
            命令包括显示指定文件内容以供读取和停止
            */
            dos.writeBytes("cat /data/misc/wifi/wpa_supplicant.conf\n");
            dos.writeBytes("exit\n");
            dos.flush();

            //从显示的文件内容中用BufferedReader读取到line中
            BufferedReader br = new BufferedReader(new InputStreamReader(dis, "UTF-8"));
            while ((line = br.readLine()) != null) sb.append(line);

            process.waitFor();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }finally {
            try {
                if(dos != null) dos.close();
                if(dis != null) dis.close();
                if(process != null) process.destroy();
            } catch (IOException e) {
            }
        }
        return line;
    }
}
