package com.example.imas.pcm;
import android.support.v7.app.AppCompatActivity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Button startRec, stopRec, playBack;
    TextView number1;
    Boolean recording;
    //private final static String STORETEXT="storetext.txt";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startRec = (Button)findViewById(R.id.startrec);
        stopRec = (Button)findViewById(R.id.stoprec);
        playBack = (Button)findViewById(R.id.playback);
        number1= (TextView) findViewById(R.id.number);
        stopRec.setEnabled(false);
        playBack.setEnabled(false);

        startRec.setOnClickListener(startRecOnClickListener);
        stopRec.setOnClickListener(stopRecOnClickListener);
        playBack.setOnClickListener(playBackOnClickListener);
    //number1.setText(minBufferSize);

    }



    OnClickListener startRecOnClickListener
            = new OnClickListener(){

        @Override
        public void onClick(View arg0) {

            Thread recordThread = new Thread(new Runnable(){

                @Override
                public void run() {
                    recording = true;
                    startRecord();
                }

            });

            recordThread.start();
            startRec.setEnabled(false);
            stopRec.setEnabled(true);

            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        }

    };

    OnClickListener stopRecOnClickListener
            = new OnClickListener(){

        @Override
        public void onClick(View arg0) {
            recording = false;

            playBack.setEnabled(true);
            stopRec.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
        }};

    OnClickListener playBackOnClickListener
            = new OnClickListener(){

        @Override
        public void onClick(View v) {
            playRecord();
            Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
        }

    };



   void startRecord(){


        File file = new File(Environment.getExternalStorageDirectory(), "sound.txt");

        try {
            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);

            int minBufferSize = AudioRecord.getMinBufferSize(11025,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            short[] audioData = new short[minBufferSize];
//            System.out.println("audio data : "+ Arrays.toString(audioData));
            // String[] s = new String[audioData.length];

            // OutputStreamWriter out=null;
            float[] audioFloats = new float[audioData.length];

//            for (int i = 0; i < audioData.length; i++) {
//                audioFloats[i] = ((float)Short.reverseBytes(audioData[i])/0x8000);
//                s[i]=Float.toString(audioFloats[i]);
//                out = new OutputStreamWriter(openFileOutput(STORETEXT, 0));
//
//                out.write(s[i]);
//
//
//
//            }

            //audioFloats[i] = ((float)Short.reverseBytes(audioData[i])/0x8000);


            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    11025,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize);

            audioRecord.startRecording();

            while(recording){
                int numberOfShort = audioRecord.read(audioData, 0, minBufferSize);
                for(int i = 0; i < numberOfShort; i++){
                    //dataOutputStream.writeShort(audioData[i]);
                    audioFloats[i] = ((float)Short.reverseBytes(audioData[i])/0x8000);
                    dataOutputStream.writeFloat(audioFloats[i]);

//                    s[i]=Float.toString(audioFloats[i]);
//                    out = new OutputStreamWriter(openFileOutput(STORETEXT, 0));
//
//                    out.write(s[i]);*/


                }


//                for (int i = 0; i < audioData.length; i++) {
//                    audioFloats[i] = ((float)Short.reverseBytes(audioData[i])/0x8000);
//              s[i]=Float.toString(audioFloats[i]);
//                out = new OutputStreamWriter(openFileOutput(STORETEXT, 0));
//
//                out.write(s[i]);
//                 }
//                out.close();

            }

            audioRecord.stop();


            System.out.println("audio float: "+Arrays.toString(audioFloats));
            dataOutputStream.close();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    void playRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "sound.txt");
        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    11025,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
