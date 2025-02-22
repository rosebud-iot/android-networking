package com.sweepr.networking;

import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public final class TestAwaiter implements ISpeedTestListener {
    private static final long TIMEOUT_MILLIS = 40000;
    private static final String TAG = "TestAwaiter";

    private final Semaphore sem = new Semaphore(0);

    private SpeedTestReport report;
    private SpeedTestError error;

    public TestAwaiter() {
        Log.d(TAG, "Starting testing");
    }

    @Nullable
    public SpeedTestReport getReport() {
        return this.report;
    }

    @Nullable
    public SpeedTestError getError() {
        return this.error;
    }

    @Override
    public void onCompletion(SpeedTestReport report) {
        Log.d(TAG, "Test completed with speed: " + report.getTransferRateBit());

        this.report = report;
        sem.release();
    }

    @Override
    public void onProgress(float percent, SpeedTestReport report) {
        Log.d(TAG, "Test progress: " + percent);
    }

    @Override
    public void onError(SpeedTestError speedTestError, String errorMessage) {
        Log.d(TAG, "Test failed with: " + errorMessage);

        this.error = speedTestError;
        sem.release();
    }

    public boolean acquire(long timeout) {
        try {
            return sem.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ex) {
            return false;
        }
    }

    public boolean acquire() {
        return acquire(TIMEOUT_MILLIS);
    }
}
