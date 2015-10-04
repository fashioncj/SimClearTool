package me.chenjia.simcleartool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.btn_fix_mainact)Button btn_fix;
    @Bind(R.id.btn_info_mainact)Button btn_info;
    @Bind(R.id.tv_out_mainact)TextView tv_out;

    String string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_fix_mainact)
    void Btn_fix(){
        String command="sqlite3 /data/data/com.android.providers.telephony/databases/telephony.db 'delete from siminfo' " + "\n";
        boolean flag=RootCommand(command);
        command="sqlite3 /data/data/com.android.providers.telephony/databases/telephony.db 'delete from sqlite_sequence' " + "\n";
        RootCommand(command);
        /*
        * CREATE TABLE sqlite_sequence(name,seq)
        * */
        if(flag){
            final AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("修复成功");
            builder.setMessage("修复成功，是否重启查看效果？");
            builder.setNegativeButton("重启系统", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @OnClick(R.id.btn_info_mainact)
    void setBtn_info(){
        String command="sqlite3 /data/data/com.android.providers.telephony/databases/telephony.db 'select * from siminfo;' " + "\n";
        RootCommand(command);
    }

    public boolean RootCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
        //DataInputStream is = null;
        tv_out.setText("");
        int result=-1;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            //is = new DataInputStream(process.getInputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            String line="";
            string="";
            while((line=is.readLine())!=null){
                Log.i("is out---","2134"+line);
                string+=line+"\n";
            }
            process.waitFor();
            result = process.exitValue();
            Log.i("is out---",result+"");

        } catch (Exception e)
        {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            tv_out.append("ROOT REEOR" + e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                process.destroy();
            } catch (Exception e)
            {
                Log.d("*** DEBUG ***", "Root SUC-e ");
            }
        }
        if(result==0){
            tv_out.setText(string);
            tv_out.append("Success!/执行成功！");
            return true;
        }else{
            tv_out.setText("Faild,No ROOT?/失败，请先给root权限? ");
            return false;
        }

    }



}
