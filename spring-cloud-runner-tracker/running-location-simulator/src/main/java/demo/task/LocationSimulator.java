package demo.task;

import demo.model.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LocationSimulator implements  Runnable {

    private long id;
    private AtomicBoolean cancel = new AtomicBoolean();

    private double speedInMps;
    private double shouldMove; // determine if a runner continues to run
    private boolean exportPositionsToMessaging = true;
    private int reportInterval = 500; // unit: ms

    private PositionInfo currentPosition = null;

    private List<Leg> legs;
    private RunnerStatus runnerStatus = RunnerStatus.NONE;
    private String runningId;

    private Point startPoint;
    private Date executionStartTime;

    private MedicalInfo medicalInfo;

    @Override
    public void run() {

    }
}
