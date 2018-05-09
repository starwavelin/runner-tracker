package demo.task;

import demo.model.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocationSimulator implements  Runnable {

    private long id;
    private AtomicBoolean cancel = new AtomicBoolean();

    private double speedInMps;
    private boolean shouldMove; // determine if a runner continues to run
    private boolean exportPositionsToMessaging = true;
    private int reportInterval = 500; // unit: ms

    private PositionInfo currentPosition = null;

    private List<Leg> legs;
    private String runningId;
    private RunnerStatus runnerStatus = RunnerStatus.NONE;

    private Point startPoint;
    private Date executionStartTime;

    private MedicalInfo medicalInfo;

    public LocationSimulator(GpsSimulatorRequest gpsSimulatorRequest) {
        this.setSpeed(gpsSimulatorRequest.getSpeed());
        this.shouldMove = gpsSimulatorRequest.isMove();
        this.exportPositionsToMessaging = gpsSimulatorRequest.isExportPositionsToMessaging();
        this.reportInterval = gpsSimulatorRequest.getReportInterval();
        this.runningId = gpsSimulatorRequest.getRunningId();
        this.runnerStatus = gpsSimulatorRequest.getRunnerStatus();
        this.medicalInfo = gpsSimulatorRequest.getMedicalInfo();
    }

    public void setSpeed(double speed) {
        this.speedInMps = speed;
    }

    @Override
    public void run() {
        try {
            executionStartTime = new Date();
            if (cancel.get()) {
                destroy();
                return;
            }
            // while loop simulating running without cancel process

        } catch(InterruptedException ie) {
            destroy();
            return;
        }
        destroy();
    }

    /**
     * help method for run()
     * handle the case of canceling running
     */
    private void destroy() { this.currentPosition = null; }
}
