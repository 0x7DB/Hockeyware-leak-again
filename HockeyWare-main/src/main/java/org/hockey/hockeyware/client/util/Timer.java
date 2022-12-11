package org.hockey.hockeyware.client.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Timer {
    private long current;
    private long milliseconds;
    private long ticks;

    public Timer() {
        this.current = System.currentTimeMillis();
        milliseconds = -1;
        ticks = -1;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        // update ticks
        if (Minecraft.getMinecraft().player != null) {
            ticks++;
        }

        else {
            // reset time
            milliseconds = -1;
            ticks = -1;
        }
    }

    public long getMilliseconds() {
        if (milliseconds <= 0) {
            return 0;
        }

        return System.currentTimeMillis() - milliseconds;
    }

    public boolean hasReached(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }

    public boolean hasReached(final long delay, boolean reset) {
        if (reset)
            reset();
        return System.currentTimeMillis() - this.current >= delay;
    }

    public void reset() {
        this.current = System.currentTimeMillis();
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - this.current;
    }

    public boolean passedTime(long time, Format format) {
        switch (format) {
            case MILLISECONDS:
            default:
                return (System.currentTimeMillis() - milliseconds) >= time;
            case SECONDS:
                return (System.currentTimeMillis() - milliseconds) >= (time * 1000);
            case TICKS:
                return ticks >= time;
        }
    }

    public boolean sleep(final long time) {
        if (time() >= time) {
            reset();
            return true;
        }
        return false;
    }

    public long time() {
        return System.currentTimeMillis() - current;
    }

    public void resetTime() {
        // reset our values
        milliseconds = System.currentTimeMillis();
        ticks = 0;
    }

    public enum Format {

        /**
         * Time in milliseconds
         */
        MILLISECONDS,

        /**
         * Time in seconds
         */
        SECONDS,

        /**
         * Time in ticks
         */
        TICKS
    }
}
