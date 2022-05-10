//package pl.r.lab4;
//import android.annotation.TargetApi;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.ContextWrapper;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.Build;
//import androidx.annotation.RequiresApi;
//import androidx.core.app.NotificationCompat;
//
///** * Helper class to manage notification channels, and create notifications. */
//class NotificationHelper extends ContextWrapper {
//    private NotificationManager manager;
//    public static final String PRIMARY_CHANNEL = "default";
//    public static final String SECONDARY_CHANNEL = "second";
//
//    /** * Registers notification channels, which can be used later by individual no-tifications. * * @param ctx The application context */
//    @TargetApi(Build.VERSION_CODES.Q)
//    public NotificationHelper(Context ctx) {
//        super(ctx);
//        NotificationChannel chan1 =
//                new NotificationChannel(PRIMARY_CHANNEL, "Kanał domyślny", NotificationManager.IMPORTANCE_DEFAULT);
//        chan1.enableLights(false);
//    chan1.setShowBadge(false);
//    chan1.setLightColor(Color.GREEN);
//    chan1.setAllowBubbles(false);
//    long[] longs = {45L, 1007L};
//    chan1.setVibrationPattern(longs);
//    chan1.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//    getManager().createNotificationChannel(chan1);
//    NotificationChannel chan2 =
//            new NotificationChannel(SECONDARY_CHANNEL, "Drugi kanał powiadomień", NotificationManager.IMPORTANCE_HIGH);
//    chan2.setLightColor(Color.BLUE);
//    chan2.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//    getManager().createNotificationChannel(chan2); }
//    /** * Get a notification of type 1 * <p> * Provide the builder rather than the notification it's self as useful for making notification * changes. * * @param title the title of the notification * @param body the body text for the notification * @return the builder as it keeps a reference to the notification (since API 24) */
//    @RequiresApi(api = Build.VERSION_CODES.Q)
//    public NotificationCompat.Builder getNotification1(String title, String body, String text2) {
//        Intent activityIntent = new Intent(this, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
//        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
//        broadcastIntent.putExtra("toastMessage", body);
//        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        return new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
//                .setContentTitle(title)
//                .setSubText(text2)
//                .setContentText(body)
//                .setSmallIcon(getSmallIcon())
//                .setStyle(new NotificationCompat.InboxStyle()
//                        .addLine("This is line 1" + body)
//                        .addLine("This is line 2" + body)
//                        .addLine("This is line 3" + body)
//                        .addLine("This is line 4" + body)
//                        .addLine("This is line 5" + body + "5")
//                        .addLine("This is line 6" + body)
//                        .addLine("This is line 7" + body)
//        .setBigContentTitle("Big Content Title")
//                        .setSummaryText("Summary Text"))
//                .setColor(Color.RED)
//                .setContentIntent(contentIntent)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(false)
//                .setAutoCancel(true)
//                .addAction(R.mipmap.ic_launcher, body, actionIntent); } /** * Build notification for secondary channel. * * @param title Title for notification. * @param body Message for notification. * @return A Notification.Builder configured with the selected channel and de-tails */ @RequiresApi(api = Build.VERSION_CODES.Q) public NotificationCompat.Builder getNotification2(String title, String body, String text2) { // Instantiate the Image (Big Picture) style: Notification.BigPictureStyle picStyle = new Notification.BigPictur-eStyle(); // Convert the image to a bitmap before passing it into the style: Intent activityIntent = new Intent(this, MainActivity.class); PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0); Intent broadcastIntent = new Intent(this, NotificationReceiver.class); broadcastIntent.putExtra("toastMessage", body); Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawa-ble.lemur); PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT); return new NotificationCompat.Builder(getApplicationContext(), SECOND-ARY_CHANNEL) .setCategory(NotificationCompat.CATEGORY_MESSAGE) .setPriority(NotificationCompat.PRIORITY_HIGH) .setContentTitle(title) .setSubText(text2) .setLargeIcon(largeIcon) .setStyle(new NotificationCompat.BigTextStyle() .bigText(getString(R.string.long_dummy_text)) .setBigContentTitle("Big Content Title") .setSummaryText("Summary Text")) .setContentText(body) .setSmallIcon(getSmallIcon()) .setColor(Color.YELLOW) .setContentIntent(contentIntent) .setAutoCancel(true) .setOnlyAlertOnce(false) .addAction(R.mipmap.ic_launcher, body, actionIntent);
//    }
//}/** * Send a notification. * * @param id The ID of the notification * @param notification The notification object */ @RequiresApi(api = Build.VERSION_CODES.Q) public void notify(int id, NotificationCompat.Builder notification) { getManager().notify(id, notification.build()); } /** * Get the small icon for this app * * @return The small icon resource id */ @RequiresApi(api = Build.VERSION_CODES.Q) private int getSmallIcon() { return android.R.drawable.stat_notify_chat; } @RequiresApi(api = Build.VERSION_CODES.Q) private int getEmailIcon() { return android.R.drawable.ic_dialog_email; } /** * Get the notification manager. * <p> * Utility method as this helper works with it a lot. * * @return The system service NotificationManager */ @RequiresApi(api = Build.VERSION_CODES.Q) private NotificationManager getManager() { if (manager == null) { manager = (NotificationManager) getSystemService(Context.NOTIFICA-TION_SERVICE); } return manager; } }