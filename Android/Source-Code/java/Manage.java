package amrsaber.smarthome;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Manage extends Activity {

    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage);

        findViewById(R.id.sys_on).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.blue.write((int)'F');
                } catch (IOException e) {
                    e.printStackTrace();
                    msg("Could not Send");
                }
            }
        });

        findViewById(R.id.sys_off).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.blue.write((int)'B');
                } catch (IOException e) {
                    e.printStackTrace();
                    msg("Could not Send");
                }
            }
        });

        findViewById(R.id.count_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = getCount();
                if(val != -1) {
                    try {
                        MainActivity.blue.write((int) 'S');
                        Thread.sleep(100);
                        MainActivity.blue.write(val);
                    } catch (IOException e) {
                        e.printStackTrace();
                        msg("Could not Send");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.count_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isListening) return;
                try {
                    MainActivity.blue.write((int)'G');
                    isListening = true;
                    new ListenWorker().execute();
                } catch (IOException e) {
                    msg("Could not send");
                    e.printStackTrace();
                }

            }
        });


    }

    /*@Override
    public void onPause(){
        super.onPause();
        try {
            MainActivity.blue.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private int getCount(){
        EditText e = (EditText)findViewById(R.id.count);
        String s = e.getText().toString();
        if(s.equals("")) return -1;
        int val = Integer.parseInt(s);
        if(val > 250){
            msg("Too much people");
            return -1;
        }
        return val;
    }

    private void msg(String s){
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
    }


    private class ListenWorker extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                return MainActivity.blue.read();

            } catch (IOException e) {
                e.printStackTrace();
                msg("Could not receive data");
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer result){
            //msg("Received " + result);
            if(result == -1) return;
            String s;
            if(result == 0){
                s = "0";
            }else if(result == 1){
                s = "1 Person";
            }else{
                s = String.valueOf(result) + " People";
            }
            ((TextView)findViewById(R.id.count_show)).setText(s);
            isListening = false;
        }
    }


}
