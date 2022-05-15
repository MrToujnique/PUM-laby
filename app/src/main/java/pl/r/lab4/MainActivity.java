package pl.r.lab4;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.sql.SQLOutput;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    EditText phoneNumber, sms;
    Button smsSend;
    TextView smsReceive;
    Button contacts;
    Button barCode;
    Button activities;

    String asimuth="";

    IntentFilter intentFilter;

    private static final int RESULT_PICK_CONTACT =3;
    private static final int PERMISSION_READ_CONTACT = 122;
    private static final int PERMISSION_SEND_SMS = 123;
    private static final int PERMISSION_RECEIVE_SMS = 124;
    private static final int RESULT_SCAN_CODE = 49374;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel okChannel = new NotificationChannel("SMS_SENT", "SMS_SENT", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel genericChannel = new NotificationChannel("GENERIC_FAIL", "GENERIC_FAIL", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel noServiceChannel = new NotificationChannel("NO_SERVICE", "NO_SERVICE", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel nullPDUChannel = new NotificationChannel("NULL_PDU", "NULL_PDU", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel radioOffChannel = new NotificationChannel("RADIO_OFF", "RADIO_OFF", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel receivedChannel = new NotificationChannel("SMS_RECEIVED", "SMS_RECEIVED", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel canceledChannel = new NotificationChannel("SMS_CANCELED", "SMS_CANCELED", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(okChannel);
            manager.createNotificationChannel(genericChannel);
            manager.createNotificationChannel(noServiceChannel);
            manager.createNotificationChannel(nullPDUChannel);
            manager.createNotificationChannel(radioOffChannel);
            manager.createNotificationChannel(receivedChannel);
            manager.createNotificationChannel(canceledChannel);
        }

        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_READ_CONTACT);
        }

        if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
        }

        if(checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS},
                    PERMISSION_RECEIVE_SMS);
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        smsSend = (Button) findViewById(R.id.bt_send_sms);
        phoneNumber = (EditText) findViewById(R.id.et_phone);
        sms = (EditText) findViewById(R.id.et_sms);
        contacts = (Button) findViewById(R.id.contactsButton);
        barCode = (Button) findViewById(R.id.barCodeButton);
        activities = (Button) findViewById(R.id.showActivitiesButton);

        contacts.setVisibility(GONE);
        barCode.setVisibility(GONE);

        activities.setOnClickListener(view -> {
            if((contacts.getVisibility() == GONE) && (barCode.getVisibility() == GONE))
            {
                contacts.setVisibility(VISIBLE);
                barCode.setVisibility(VISIBLE);
            }
            else if ((contacts.getVisibility() == VISIBLE) && (barCode.getVisibility() == VISIBLE))
            {
                contacts.setVisibility(GONE);
                barCode.setVisibility(GONE);
            }
        });


        contacts.setOnClickListener(new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent in = new Intent (Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult (in, RESULT_PICK_CONTACT);
            }
        });
        smsSend.setOnClickListener(view -> sendSMS(phoneNumber.getText().toString(),sms.getText() +asimuth.toString()));

        barCode.setOnClickListener(view -> scanCode());

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                asimuth= String.format(" \n|AZYMUT|\n|Z:%s Y:%s X:%s|",
                                event.values[2], event.values[1], event.values[0]);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        }, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private final BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            smsReceive = (TextView) findViewById(R.id.tv_receive_sms);
            smsReceive.setText(intent.getExtras().getString("sms"));
        }
    };

    @Override
    protected void onResume() {
        registerReceiver(intentReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(intentReceiver);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void sendSMS(String phoneNumber, String message) {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS wysłany", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder okNotification = new NotificationCompat.Builder(MainActivity.this, "SMS_SENT")
                        .setContentTitle("Wysłano SMS na nr: " + phoneNumber)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Treść: " + message));
                        NotificationManagerCompat managerCompat1 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat1.notify(1, okNotification.build());
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Błąd generyczny", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder genericNotification = new NotificationCompat.Builder(MainActivity.this, "GENERIC_FAIL")
                        .setContentTitle("Wystąpił błąd generyczny przy wysyłaniu SMS")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true);
                        NotificationManagerCompat managerCompat2 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat2.notify(2, genericNotification.build());
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Brak usługi", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder noServiceNotification = new NotificationCompat.Builder(MainActivity.this, "NO_SERVICE");
                        noServiceNotification.setContentTitle("Brak usługi SMS");
                        noServiceNotification.setSmallIcon(R.drawable.ic_launcher_foreground);
                        noServiceNotification.setAutoCancel(true);
                        NotificationManagerCompat managerCompat3 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat3.notify(3, noServiceNotification.build());
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder nullPDUNotification = new NotificationCompat.Builder(MainActivity.this, "NULL_PDU");
                        nullPDUNotification.setContentTitle("Null PDU");
                        nullPDUNotification.setSmallIcon(R.drawable.ic_launcher_foreground);
                        nullPDUNotification.setAutoCancel(true);
                        NotificationManagerCompat managerCompat4 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat4.notify(4, nullPDUNotification.build());
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio wyl.", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder radioOffNotification = new NotificationCompat.Builder(MainActivity.this, "RADIO_OFF");
                        radioOffNotification.setContentTitle("Radio wyl.");
                        radioOffNotification.setSmallIcon(R.drawable.ic_launcher_foreground);
                        radioOffNotification.setAutoCancel(true);
                        NotificationManagerCompat managerCompat5 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat5.notify(5, radioOffNotification.build());
                        break;
                }

            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS dostarczony", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder receivedNotification = new NotificationCompat.Builder(MainActivity.this, "SMS_RECEIVED");
                        receivedNotification.setContentTitle("Dostarczono SMS na nr: " + phoneNumber)
                        .setContentText("Treść: " + message)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true)
                                .setStyle(new NotificationCompat.BigTextStyle().bigText("Treść: " + message));
                        NotificationManagerCompat managerCompat6 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat6.notify(6, receivedNotification.build());
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS anulowany", Toast.LENGTH_SHORT).show();
                        NotificationCompat.Builder canceledNotification = new NotificationCompat.Builder(MainActivity.this, "SMS_CANCELED");
                        canceledNotification.setContentTitle("SMS anulowany");
                        canceledNotification.setSmallIcon(R.drawable.ic_launcher_foreground);
                        canceledNotification.setAutoCancel(true);
                        NotificationManagerCompat managerCompat7 = NotificationManagerCompat.from(MainActivity.this);
                        managerCompat7.notify(7, canceledNotification.build());
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        //System.out.println("ResultCode: " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
                case RESULT_SCAN_CODE:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        if (result.getContents() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(result.getContents());
                            builder.setTitle("Wyniki skanowania");
                            builder.setPositiveButton("Skanuj ponownie", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    scanCode();
                                }
                            }).setNegativeButton("Umieść w SMS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sms.append("Kod kreskowy: " + result.getContents());
                                    //finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                        else {
                            Toast.makeText(this, "Brak wyników", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
            }
        }
        }


    private void contactPicked(Intent data) {
        Cursor cursor = null;

        try {
            String phoneNo = "";
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null,null,null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            phoneNo = cursor.getString(phoneIndex);
            System.out.println(phoneNo);
            phoneNumber.setText(phoneNo);

        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Skanowanie kodu");
        integrator.initiateScan();
    }
}