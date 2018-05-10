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
            while (!Thread.interrupted()) {
                long startTime = new Date().getTime();

                if (this.currentPosition != null) {
                    if (shouldMove) {
                        moveRunningLocation(); // help method
                        currentPosition.setSpeed(speedInMps);
                    } else {  // meaning arriving destination
                        currentPosition.setSpeed(0.0);
                    }

                    // set runner status
                    currentPosition.setRunnerStatus(runnerStatus);

                    // set medical info based on runner status
                    final MedicalInfo medicalInfoToUse;
                    switch (runnerStatus) {
                        case SUPPLY_NOW:
                        case SUPPLY_SOON:
                        case STOP_NOW:
                            medicalInfoToUse = medicalInfo;
                            break;
                        default:
                            medicalInfoToUse = null;
                            break;
                    }

                    // construct currentPosition of type CurrentPosition that will be sent to Distributor
                    final CurrentPosition currentPosition = new CurrentPosition(
                        this.currentPosition.getRunningId(),
                        new Point(this.currentPosition.getPosition().getLatitude(),
                                this.currentPosition.getPosition().getLongitude()),
                        this.currentPosition.getRunnerStatus(),
                        this.currentPosition.getSpeed(),
                        this.currentPosition.getLeg().getHeading(),
                        medicalInfoToUse
                    );

                    // Send the currentPosition prepared to location distributor via REST API
                    // @TODO Class 12-1

                } // end of if (this.currentPosition != null)

                // wait until next position report
                sleep(startTime);
            } // end of while (!Thread.interrupted)
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

    /**
     * Detailed implementation of sleep(startTime) used in
     * the while (!Thread.interrupted) {} of run()
     * @param startTime
     * @throws InterruptedException
     */
    private void sleep(long startTime) throws InterruptedException {
        long endTime = new Date().getTime();
        long elapsedTime = endTime - startTime;

        // The actual time period of sleeping
        long sleepTime = (reportInterval - elapsedTime > 0) ? reportInterval - elapsedTime : 0;
        Thread.sleep(sleepTime);
    }


    // set new position of running location based on current position and running speed
    private void moveRunningLocation() {
        double distance = speedInMps * reportInterval / 1000.0; // convert to second
        double distanceFromStart = currentPosition.getDistanceFromStart() + distance;
        double excess = 0.0;

        for (int i = currentPosition.getLeg().getId(); i < legs.size(); i++) {
            Leg curLeg = legs.get(i);
            excess = (distanceFromStart > curLeg.getLength()) ? distanceFromStart - curLeg.getLength() : 0.0;

            // when the next position is within the current leg
            if (Double.doubleToRawLongBits(excess) == 0) {
                currentPosition.setDistanceFromStart(distanceFromStart);
                currentPosition.setLeg(curLeg);
                // @TODO Impl the new position calculation method in NavUtils class

                // for now
                Point newPosition = null;
                currentPosition.setPosition(newPosition);
                return;
            }

            // when the next position is beyond the current leg, update distanceFromStart and continue in loop
            distanceFromStart = excess;
        }

        // After the new position has been moved, we still need to simulate, we start from the beginning
        setStartPosition();
    }

    private void setStartPosition() {
        currentPosition = new PositionInfo();
        currentPosition.setRunningId(runningId);

        Leg leg = legs.get(0);
        currentPosition.setLeg(leg);
        currentPosition.setPosition(leg.getStartPosition());
        currentPosition.setDistanceFromStart(0.0);
    }
}
