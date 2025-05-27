package api.robots;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class RobotModel extends Observable
{
    private volatile double robotPositionX = 100;
    private volatile double robotPositionY = 100;
    private volatile double robotDirection = 0;
    private volatile int targetPositionX = 150;
    private volatile int targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    public RobotModel()
    {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                updateRobotPosition(robotPositionX, robotPositionY, robotDirection);
            }
        }, 100);
    }

    public void updateRobotPosition(double x, double y, double direction)
    {
        this.robotPositionX = x;
        this.robotPositionY = y;
        this.robotDirection = direction;
        setChanged();
        notifyObservers();
    }

    public void setTargetPosition(int x, int y)
    {
        this.targetPositionX = x;
        this.targetPositionY = y;
        setChanged();
        notifyObservers();
    }

    public double getRobotPositionX()
    {
        return robotPositionX;
    }

    public double getRobotPositionY()
    {
        return robotPositionY;
    }

    public double getRobotDirection()
    {
        return robotDirection;
    }

    public int getTargetPositionX()
    {
        return targetPositionX;
    }

    public int getTargetPositionY()
    {
        return targetPositionY;
    }

    public double getMaxVelocity()
    {
        return maxVelocity;
    }

    public double getMaxAngularVelocity()
    {
        return maxAngularVelocity;
    }
}
