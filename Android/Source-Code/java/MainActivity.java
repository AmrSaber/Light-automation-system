package amrsaber.smarthome;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends Activity {

    protected static BluetoothManager blue;
    public static final int REQUEST_ENABLE_BT = 123;

    private Button lightOn, lightOff, gotoManager, getTemp, lightDef;

    private boolean isListening = false;
    private boolean forcedBtOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lightOn = (Button) findViewById(R.id.light_on);
        lightOff = (Button)findViewById(R.id.light_off);
        lightDef = (Button) findViewById(R.id.light_def);
        gotoManager = (Button) findViewById(R.id.manage_light);
        getTemp = (Button) findViewById(R.id.get_temp);

        lightOn.setEnabled(false);
        lightOff.setEnabled(false);
        lightDef.setEnabled(false);
        gotoManager.setEnabled(false);
        getTemp.setEnabled(false);

        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        blue = new BluetoothManager();

        //--------------------------

        findViewById(R.id.cnct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connected = false;
                try {
                    blue.getDevice("HC-05");
                    connected = true;
                } catch (IOException ioe) {
                    msg(ioe.getMessage());
                }

                if(connected){
                    try{
                        connect();
                        msg("Connected");
                    }catch(IOException ioe){
                        msg("Module not in range");
                    }
                }
            }

        });

        findViewById(R.id.dscnct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        findViewById(R.id.inf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Info.class));
            }
        });

        lightOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    blue.write((int)'s');
                    blue.write((int)'R');
                } catch (IOException e) {
                    msg("Could not send");
                    e.printStackTrace();
                }
            }
        });

        lightOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    blue.write((int)'s');
                    blue.write((int)'L');
                } catch (IOException e) {
                    msg("Could not send");
                    e.printStackTrace();
                }
            }
        });

        lightDef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    blue.write((int)'A');
                } catch (IOException e) {
                    msg("Could not send");
                    e.printStackTrace();
                }
            }
        });

        gotoManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity(new Intent(MainActivity.this,Manage.class ));}
        });

        getTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isListening) return;
                try {
                    blue.write((int)'s');
                    blue.write((int)'T');
                    blue.write((int)'r');
                    isListening = true;
                    new ListenWorker().execute();
                } catch (IOException e) {
                    msg("Could not send");
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        disconnect();
        if(forcedBtOn) {
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getBaseContext(),"This app is all about bluetooth and won't work without it", Toast.LENGTH_LONG).show();
            }else if(resultCode == RESULT_OK){
                forcedBtOn = true;
            }
        }
    }

    private void connect()throws IOException{
        blue.connect();
        lightOn.setEnabled(true);
        lightOff.setEnabled(true);
        lightDef.setEnabled(true);
        gotoManager.setEnabled(true);
        getTemp.setEnabled(true);

    }

    private void disconnect(){
        try {
            blue.close();
        } catch (IOException e) {
            msg("Could not Disconnect");
        }
        lightOn.setEnabled(false);
        lightOff.setEnabled(false);
        lightDef.setEnabled(false);
        gotoManager.setEnabled(false);
        getTemp.setEnabled(false);
    }

    private void msg(String message){
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class ListenWorker extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                return blue.read();

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
            String s = String.valueOf(result + " °C");
            ((TextView)findViewById(R.id.temp)).setText(s);
            isListening = false;
        }
    }
}
