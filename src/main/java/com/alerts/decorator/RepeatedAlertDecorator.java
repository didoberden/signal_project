package com.alerts.decorator;

import java.util.Timer;
import java.util.TimerTask;
import com.alerts.Alert;
import com.alerts.AlertType;

/**
 * Decorator that adds repeating behavior to alerts.
 * Checks and re-checks alert conditions over a set interval.
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    
    private long repeatIntervalMs;
    private int repeatCount;
    private int maxRepeatCount;
    private Timer timer;
    
    /**
     * Creates a new RepeatedAlertDecorator.
     *
     * @param alert the alert to decorate
     * @param repeatIntervalMs interval between repeats in milliseconds
     * @param maxRepeatCount maximum number of times to repeat the alert
     */
    public RepeatedAlertDecorator(Alert alert, long repeatIntervalMs, int maxRepeatCount) {
        super(alert);
        this.repeatIntervalMs = repeatIntervalMs;
        this.maxRepeatCount = maxRepeatCount;
        this.repeatCount = 0;
        
        // Start the repeating timer
        scheduleNextRepeat();
    }
    
    /**
     * Schedules the next repeat of this alert.
     */
    private void scheduleNextRepeat() {
        this.timer = new Timer(true); // Run as daemon thread
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                repeatCount++;
                if (repeatCount <= maxRepeatCount) {
                    System.out.println("Alert repeated: " + getMessage() + " (" + repeatCount + "/" + maxRepeatCount + ")");
                    
                    // Schedule next repeat if we haven't reached max yet
                    if (repeatCount < maxRepeatCount) {
                        scheduleNextRepeat();
                    }
                }
            }
        }, repeatIntervalMs);
    }
    
    /**
     * Cancels all future repeats.
     */
    public void cancelRepeats() {
        if (timer != null) {
            timer.cancel();
        }
    }
    
    @Override
    public String getMessage() {
        return wrappedAlert.getMessage() + " [REPEAT " + repeatCount + "/" + maxRepeatCount + "]";
    }
}