package com.phcnmd.v_panel

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.media.AudioManager
import android.content.Context
import android.view.View

class FloatingService : Service() {
    private lateinit var windowManager: WindowManager
    private var layout: LinearLayout? = null
    private lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // 1. Создаем основной контейнер
        layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL

            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 100f
                setColor(Color.parseColor("#CC000000")) // Темный фон (почти черный)
            }

            background = shape
            alpha = 0.6f // Пока ставим 60%, чтобы точно увидеть!

            setPadding(20, 40, 20, 40)
        }

        // Параметры для кнопок
        val btnParams = LinearLayout.LayoutParams(120, 150)

        val btnPlus = Button(this).apply {
            text = "+"
            textSize = 30f
            setTextColor(Color.WHITE)
            background = null
            layoutParams = btnParams
            setOnClickListener {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
            }
        }

        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(60, 3) // Чуть шире линию
            setBackgroundColor(Color.GRAY)
        }

        val btnMinus = Button(this).apply {
            text = "-"
            textSize = 35f
            setTextColor(Color.WHITE)
            background = null
            layoutParams = btnParams
            setOnClickListener {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
            }
        }

        layout?.addView(btnPlus)
        layout?.addView(divider)
        layout?.addView(btnMinus)

        // 4. ПАРАМЕТРЫ ОКНА (Важно!)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        params.x = 0

        // Попробуй добавить это, если все еще не видно (выводит на передний план)
        try {
            windowManager.addView(layout, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        layout?.let { windowManager.removeView(it) }
    }
}