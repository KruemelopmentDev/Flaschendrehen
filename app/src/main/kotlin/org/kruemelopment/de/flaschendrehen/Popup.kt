package org.kruemelopment.de.flaschendrehen

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Random

class Popup : Service() {
    var windowManager: WindowManager? = null
    private var icon2: ImageView? = null
    private var mNotificationManager: NotificationManager? = null
    private var status: Notification? = null
    var myParams: WindowManager.LayoutParams? = null
    var view: FrameLayout? = null
    var sp2: SharedPreferences? = null
    private var random: Random? = null
    private var pivot = 0
    private var lastAngle = 0
    var spin = false
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == null) {
            showNotification()
            initstart()
        } else if (intent.action == "stop") {
            try {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } catch (ignored: Exception) {
            }
            stopSelf()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showNotification() {
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            status = NotificationCompat.Builder(this, "kanal").build()
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel("kanal", "Service", importance)
            mNotificationManager!!.createNotificationChannel(mChannel)
        }
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, FLAG_IMMUTABLE
        )
        val stop = Intent(applicationContext, Popup::class.java)
        stop.setAction("stop")
        val sstop = PendingIntent.getService(
            this, 0,
            stop,FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, "kanal")
        builder
            .setColor(ContextCompat.getColor(applicationContext,R.color.colorPrimary))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setColorized(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.logorund))
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.mini_flaschendrehen_running))
        builder.setSmallIcon(R.drawable.notifyicon)
        builder.addAction(NotificationCompat.Action(0, getString(R.string.stopp), sstop))
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, builder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else startForeground(1, builder.build())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initstart() {
        sp2 = getSharedPreferences("Einstellungen", 0)
        val position = sp2!!.getInt("position", -1)
        val position2 = sp2!!.getInt("position2", -1)
        view = FrameLayout(applicationContext)
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.hgholz)
        val widt = bitmap.width
        val height = bitmap.height
        val newWidth = height.coerceAtMost(widt)
        val newHeight = if (height > widt) height - (height - widt) else height
        var cropW = (widt - height) / 2
        cropW = cropW.coerceAtLeast(0)
        var cropH = (height - widt) / 2
        cropH = cropH.coerceAtLeast(0)
        val cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight)
        val d: Drawable = BitmapDrawable(resources, cropImg)
        view!!.background = d
        icon2 = ImageView(applicationContext)
        icon2!!.setImageResource(R.drawable.bottle)
        val dm = Resources.getSystem().displayMetrics
        val width = dm.widthPixels / 3
        val layoutParams = FrameLayout.LayoutParams(width, width)
        layoutParams.gravity = Gravity.CENTER
        icon2!!.layoutParams = layoutParams
        myParams = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                width + 40,
                width + 40,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                width + 40,
                width + 40,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }
        if (position == -1) {
            myParams!!.x = dm.widthPixels / 2 - (width + 40) / 2
            myParams!!.y = dm.heightPixels / 2 - (width + 40) / 2
        } else {
            myParams!!.x = position2
            myParams!!.y = position
        }
        view!!.addView(icon2)
        windowManager!!.addView(view, myParams)
        view!!.setOnTouchListener(object : OnTouchListener {
            private var initialY = 0
            private var initialTouchY = 0f
            private var initialX = 0
            private var initialTouchX = 0f
            private var touchStartTime: Long = 0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (MotionEvent.ACTION_DOWN == event.action) {
                    initialY = myParams!!.y
                    initialTouchY = event.rawY
                    initialX = myParams!!.x
                    initialTouchX = event.rawX
                    touchStartTime = System.currentTimeMillis()
                }
                if (MotionEvent.ACTION_UP == event.action) {
                    val endtime = System.currentTimeMillis()
                    if (endtime - touchStartTime < 300) {
                        doclick()
                    }
                    val e = sp2!!.edit()
                    e.putInt("position", initialY + (event.rawY - initialTouchY).toInt())
                    e.putInt("position2", initialX + (event.rawX - initialTouchX).toInt())
                    e.apply()
                }
                if (event.action == MotionEvent.ACTION_MOVE) {
                    myParams!!.y = initialY + (event.rawY - initialTouchY).toInt()
                    myParams!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    windowManager!!.updateViewLayout(v, myParams)
                }
                return true
            }
        })
        random = Random()
        pivot = width / 2
    }

    fun doclick() {
        if (!spin) {
            val drehung = random!!.nextInt(5) + 4
            var angle = random!!.nextInt(361) + 360 * drehung
            val animRotate: Animation = RotateAnimation(
                (if (lastAngle == -1) 0 else lastAngle).toFloat(),
                angle.toFloat(),
                pivot.toFloat(),
                pivot.toFloat()
            )
            while (angle > 360) {
                angle -= 360
            }
            lastAngle = angle
            animRotate.duration = 1500
            animRotate.fillAfter = true
            icon2!!.startAnimation(animRotate)
            animRotate.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    spin = true
                }

                override fun onAnimationEnd(animation: Animation) {
                    spin = false
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }
    }

    override fun onDestroy() {
        windowManager!!.removeViewImmediate(view)
        super.onDestroy()
    }
}