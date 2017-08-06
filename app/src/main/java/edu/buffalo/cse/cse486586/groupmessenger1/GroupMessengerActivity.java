package edu.buffalo.cse.cse486586.groupmessenger1;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.*;
import java.net.Socket;
import java.net.UnknownHostException;
import edu.buffalo.cse.cse486586.groupmessenger1.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import java.net.InetSocketAddress;
import java.io.PrintWriter;

public class GroupMessengerActivity extends Activity
{
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final int SERVER_PORT = 10000;
    EditText editText;
    String myPort;
    TextView localTextView;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        try
        {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }
        editText = (EditText) findViewById(R.id.editText1);
        localTextView = (TextView) findViewById(R.id.local_text_display);

        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(localTextView, getContentResolver()));




    }
    public void sendClicked(View v)
    {

        String msg = editText.getText().toString() + "\n";
        editText.setText(""); // This is one way to reset the input box.

        //localTextView.append("\t" + msg); // This is one way to display a string.
        //TextView remoteTextView = (TextView) findViewById(R.id.remote_text_display);
        //remoteTextView.append("\n");

        new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);

    }

    private class ServerTask extends AsyncTask<ServerSocket, String, Void>
    {
        ContentValues cv;
        ContentResolver cr;
        Uri.Builder ub;
        Uri uri;
        Socket s1;
        InputStream iss;
        OutputStream oss;
        PrintWriter pws;
        InputStreamReader isrs;
        BufferedReader brs;
        String acks="PA1_OK";
        String string_msg;
        @Override
        protected Void doInBackground(ServerSocket... sockets)
        {
            Log.i(TAG,"sever started");
            ServerSocket serverSocket = sockets[0];
            ub=new Uri.Builder();
            ub.authority("edu.buffalo.cse.cse486586.groupmessenger1.provider");
            ub.scheme("content");
            uri=ub.build();
            cr=getContentResolver();
            int key=0;
            try
            {
                while(true)
                {
                    s1 = serverSocket.accept();
                    Log.i(TAG,"reached1");
                    iss = s1.getInputStream();
                    isrs = new InputStreamReader(iss);
                    brs = new BufferedReader(isrs);
                    string_msg = brs.readLine();
                    //c.moveToFirst();
                    oss=s1.getOutputStream();
                    pws=new PrintWriter(oss);
                    pws.write(acks);
                    pws.flush();
                    Log.i(TAG,"reached2");
                    publishProgress(string_msg);
                    pws.close();
                    brs.close();
                    s1.close();
                    cv=new ContentValues();
                    cv.put("key",key);
                    cv.put("value",string_msg);
                    cr.insert(uri,cv);

                    //Log.i(TAG,"key="+key);
                    Cursor c;
                    c=cr.query(uri,null,key+"",null,null);
                    Log.i("value from cursor",c.getCount()+"");
                    Log.i(TAG,"value from cursor="+c.getString(0));
                    Log.i(TAG,"value from cursor="+c.getString(1));

                    key++;
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String...strings)
        {

            String strReceived = strings[0].trim();
            //TextView remoteTextView = (TextView) findViewById(R.id.remote_text_display);
            //remoteTextView.append(strReceived + "\t\n");
            //TextView localTextView = (TextView) findViewById(R.id.local_text_display);
            //localTextView.append("\n");

            localTextView.append(strReceived + "\t\n");


            try {
                //outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                //outputStream.write(string.getBytes());
                //outputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "File write failed");
            }

            return;
        }
    }
    private class ClientTask extends AsyncTask<String, Void, Void>
    {
        OutputStream osc;
        PrintWriter pwc;
        InputStream isc;
        InputStreamReader isrc;
        BufferedReader brc;
        String ports[]={"11108","11112","11116","11120","11124"};
        String remotePort;
        Socket socket;
        protected Void doInBackground(String... msgs) {
            try {

                String myport=msgs[1];
                for(int i=0;i<ports.length;i++)
                {
                    //if (myport.equals(ports[i]))
                    //{

                    //}
                    //else
                    //{
                        remotePort = ports[i];
                        //Log.i(TAG,remotePort);


                        socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(remotePort));

                        String msgToSend = msgs[0];
                        String ackc = "PA1_OK";
                        Log.i(TAG, socket.getSoTimeout() + "");
                        osc = socket.getOutputStream();
                        pwc = new PrintWriter(osc);
                        pwc.write(msgToSend);
                        pwc.flush();
                        Log.i(TAG, "client side reached1");
                        //os.close();
                        Log.i(TAG, "client side reached2");
                        isc = socket.getInputStream();
                        isrc = new InputStreamReader(isc);
                        Log.i(TAG, "client side reached3");
                        brc = new BufferedReader(isrc);
                        Log.i(TAG, "client side reached4");
                        Log.i(TAG, socket.isConnected() + "");
                        Log.i(TAG, socket.isInputShutdown() + "");
                        Log.i(TAG, socket.isOutputShutdown() + "");
                        String x = brc.readLine();
                        if (!(x.equals(""))) {
                            Log.i(TAG, "client side reached5");
                            if (x.equals(ackc)) {
                                Log.i(TAG, "client side reached6");
                                pwc.close();
                                brc.close();
                                socket.close();
                            }

                        }
                    //}
                  //  Log.i(TAG, socket.isClosed() + "");
                }
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                //Log.e(TAG, "ClientTask UnknownHostException");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }
}