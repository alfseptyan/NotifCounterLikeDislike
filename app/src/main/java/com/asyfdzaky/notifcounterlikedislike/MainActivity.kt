package com.asyfdzaky.notifcounterlikedislike

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.asyfdzaky.notifcounterlikedislike.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val channelId = "TEST_NOTIF"
    private val notifId = 90

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateLikeDislike() // Memperbarui nilai counter secara real-time
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateLikeDislike()


        // Register receiver untuk mendengar perubahan counter
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(updateReceiver, IntentFilter("ACTION_UPDATE_COUNTERS"))
        updateLikeDislike()
        //Fungsi registerReceiver() ini akan membuat aplikasi mendengarkan setiap broadcast yang dikirimkan dengan action ini dan kemudian menjalankan onReceive() di updateReceiver.
        //Dengan menggunakan IntentFilter("ACTION_UPDATE_COUNTERS"), aplikasi Anda hanya akan mendengarkan broadcast dengan action tertentu, yaitu "ACTION_UPDATE_COUNTERS", dan tidak akan mendengarkan broadcast lainnya.


        binding.btnNotif.setOnClickListener {

            val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                    NotificationManager
            // Intent untuk aksi like
            val likeIntent = Intent(this, NotifReceiver::class.java).apply {
                action = "ACTION_LIKE"
            }
            val likePendingIntent =
                PendingIntent.getBroadcast(this, 0, likeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            // Intent untuk aksi dislike
            val dislikeIntent = Intent(this, NotifReceiver::class.java).apply {
                action = "ACTION_DISLIKE"
            }
            val dislikePendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                dislikeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.img_2)
                .setContentTitle("Counter Like dan dislike")
                .setContentText("Hitung Like!!")
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(
                            BitmapFactory.decodeResource(
                                resources,
                                R.drawable.img_3
                            )
                        )
                )
                .setAutoCancel(true)
                .addAction(0, "Like", likePendingIntent) // Aksi untuk Like
                .addAction(0, "Dislike", dislikePendingIntent) // Aksi untuk Dislike

            // Menampilkan notifikasi
            notifManager.notify(notifId, builder.build())
        }

    }
    private  fun updateLikeDislike(){
        val prefs: SharedPreferences = getSharedPreferences("CounterPrefs", Context.MODE_PRIVATE)
        val likeCount = prefs.getInt("LIKE_COUNT", 0)
        val dislikeCount = prefs.getInt("DISLIKE_COUNT", 0)

        binding.likeCounter.text = "$likeCount"
        binding.dislikeCounter.text = "$dislikeCount"
    }
    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)// Unregister receiver saat activity dihancurkan
    }
}