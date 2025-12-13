package com.aura.wake.data.alarm

import com.aura.wake.data.model.Alarm

interface AlarmScheduler {
    fun schedule(alarm: Alarm)
    fun cancel(alarm: Alarm)
}
