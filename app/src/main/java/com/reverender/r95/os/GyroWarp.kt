package com.reverender.r95.os

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.reverender.r95.audio.AudioBus
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Gyro-driven tape warp: the gyroscope's rotation rate bends the tape (Wow & Flutter / DSP chain),
 * shaking adds flutter, and the tilt (accelerometer) steers PLASMA_EQ's gyro core.
 * Runs while music plays, so it keeps working with the screen off; sensors stop when nothing listens.
 */
object GyroWarp : SensorEventListener {
    /** tilt of the phone in radians (for PLASMA_EQ's core), smoothed */
    @Volatile var tiltX = 0.0; private set
    @Volatile var tiltY = 0.0; private set
    @Volatile var playing = false
    private var registered = false
    private val thread by lazy { android.os.HandlerThread("r95-gyro").also { it.start() } }
    private val sm: SensorManager? by lazy { runCatching { OsPrefs.app.getSystemService(Context.SENSOR_SERVICE) as SensorManager }.getOrNull() }
    private var shake = 0f
    @Volatile private var baseX = 0.0
    @Volatile private var baseY = 0.0

    val available: Boolean get() = sm?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null || sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
    val hasGyro: Boolean get() = sm?.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null

    /** Call when the switches, the play state or the app's visibility change. */
    fun refresh() {
        val warp = runCatching { OsPrefs.gyroWarp }.getOrDefault(false)
        val plasma = runCatching { OsPrefs.gyroPlasma }.getOrDefault(false)
        val fg = com.reverender.r95.core.AppVisibility.foreground
        // the plasma tilt belongs to the desktop wallpaper: off while the VHS shell is the active style
        val want = (warp && (playing || fg)) || (plasma && fg && com.reverender.r95.core.AppVisibility.desktopActive)
        val fx = AudioBus.fx
        fx.gyroDepth = runCatching { OsPrefs.gyroDepth }.getOrDefault(0.6f)
        if (fx.gyroOn != warp) { fx.gyroOn = warp; if (!warp) { fx.gyroRate = 0f; fx.gyroShake = 0f }; fx.touch() }
        val m = sm ?: return
        if (want && !registered) {
            val h = android.os.Handler(thread.looper)
            m.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.let { m.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME, h) }
            m.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let { m.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME, h) }
            registered = true
        } else if (!want && registered) {
            m.unregisterListener(this); registered = false
            AudioBus.fx.gyroRate = 0f; AudioBus.fx.gyroShake = 0f
        }
    }

    override fun onSensorChanged(e: SensorEvent) {
        val fx = AudioBus.fx
        when (e.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                // roll (z) and pitch (x) both turn the reel; the larger one wins, keeping its sign
                val r = if (abs(e.values[2]) > abs(e.values[0])) e.values[2] else e.values[0]
                fx.gyroRate = if (abs(r) < 0.05f) 0f else r
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = e.values[0]; val y = e.values[1]; val z = e.values[2]
                val g = sqrt(x * x + y * y + z * z)
                val jolt = (abs(g - 9.81f) / 9f).coerceIn(0f, 1f)
                shake = maxOf(jolt, shake * 0.93f)
                fx.gyroShake = shake
                tiltX += (atan2(x.toDouble(), z.toDouble()) - tiltX) * 0.12
                tiltY += (atan2(y.toDouble(), z.toDouble()) - tiltY) * 0.12
                // how the phone is normally held drifts into the baseline, so only tilting moves the core
                baseX += (tiltX - baseX) * 0.004; baseY += (tiltY - baseY) * 0.004
                // without a gyroscope, tilting changes the rate instead
                if (!hasGyro) fx.gyroRate = (x / 3f).let { if (abs(it) < 0.15f) 0f else it }
            }
        }
    }

    private fun plasmaOn() = runCatching { OsPrefs.gyroPlasma }.getOrDefault(false) && registered
    /** PLASMA_EQ gyro core: extra rotation from tilting the phone */
    fun plasmaPitch(): Double = if (plasmaOn()) (tiltY - baseY) * 1.6 else 0.0
    fun plasmaRoll(): Double = if (plasmaOn()) (tiltX - baseX) * 1.6 else 0.0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
