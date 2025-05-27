package gui.components;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import api.robots.ExternalRobot;
import model.RobotModel;

public class GameVisualizer extends JPanel implements Observer
{
    private final Timer timer = initTimer();
    private final RobotModel robotModel;
    private ExternalRobot currentRobot;

    private boolean initialized = false;

    private static Timer initTimer()
    {
        return new Timer("events generator", true);
    }

    public GameVisualizer(RobotModel robotModel)
    {
        this.robotModel = robotModel;
        robotModel.addObserver(this);
        initialized = true;

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onRedrawEvent();
            }
        }, 0, 50);

        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                onModelUpdateEvent();
            }
        }, 0, 10);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });

        setDoubleBuffered(true);
    }

    public void setExternalRobot(ExternalRobot robot)
    {
        this.currentRobot = robot;
    }

    protected void setTargetPosition(Point p)
    {
        robotModel.setTargetPosition(p.x, p.y);
    }

    protected void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent()
    {
        if (!initialized) return;
        double distance = distance(robotModel.getTargetPositionX(), robotModel.getTargetPositionY(), robotModel.getRobotPositionX(), robotModel.getRobotPositionY());
        if (distance < 0.5)
        {
            return;
        }

        double velocity = robotModel.getMaxVelocity();
        double angleToTarget = angleTo(robotModel.getRobotPositionX(), robotModel.getRobotPositionY(), robotModel.getTargetPositionX(), robotModel.getTargetPositionY());
        double angularVelocity = 0;

        double angleDiff = asNormalizedRadians(angleToTarget - robotModel.getRobotDirection());
        if (angleDiff > Math.PI)
        {
            angleDiff -= 2 * Math.PI;
        }

        if (angleDiff < 0)
        {
            angularVelocity = -robotModel.getMaxAngularVelocity();
        }
        else
        {
            angularVelocity = robotModel.getMaxAngularVelocity();
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    private void moveRobot(double velocity, double angularVelocity, double duration)
    {
        if (currentRobot != null)
        {
            currentRobot.updateRobotPosition(robotModel, velocity, angularVelocity, duration);
        }
        else
        {
            velocity = applyLimits(velocity, 0, robotModel.getMaxVelocity());
            angularVelocity = applyLimits(angularVelocity, -robotModel.getMaxAngularVelocity(), robotModel.getMaxAngularVelocity());

            double newX = robotModel.getRobotPositionX() + velocity / angularVelocity *
                    (Math.sin(robotModel.getRobotDirection() + angularVelocity * duration) -
                            Math.sin(robotModel.getRobotDirection()));
            if (!Double.isFinite(newX))
            {
                newX = robotModel.getRobotPositionX() + velocity * duration * Math.cos(robotModel.getRobotDirection());
            }

            double newY = robotModel.getRobotPositionY() - velocity / angularVelocity *
                    (Math.cos(robotModel.getRobotDirection() + angularVelocity * duration) -
                            Math.cos(robotModel.getRobotDirection()));
            if (!Double.isFinite(newY))
            {
                newY = robotModel.getRobotPositionY() + velocity * duration * Math.sin(robotModel.getRobotDirection());
            }

            double newDirection = asNormalizedRadians(robotModel.getRobotDirection() + angularVelocity * duration);
            robotModel.updateRobotPosition(newX, newY, newDirection);
        }
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
        {
            angle += 2*Math.PI;
        }
        while (angle >= 2*Math.PI)
        {
            angle -= 2*Math.PI;
        }
        return angle;
    }

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(robotModel.getRobotPositionX()), round(robotModel.getRobotPositionY()), robotModel.getRobotDirection());
        drawTarget(g2d, robotModel.getTargetPositionX(), robotModel.getTargetPositionY());
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        if (currentRobot != null)
        {
            currentRobot.drawRobot(g, x, y, direction);
        }
        else
        {
            AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
            g.setTransform(t);
            g.setColor(Color.MAGENTA);
            fillOval(g, x, y, 30, 10);
            g.setColor(Color.BLACK);
            drawOval(g, x, y, 30, 10);
            g.setColor(Color.WHITE);
            fillOval(g, x + 10, y, 5, 5);
            g.setColor(Color.BLACK);
            drawOval(g, x + 10, y, 5, 5);
        }
    }

    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    @Override
    public void update(Observable o, Object arg)
    {
        repaint();
    }
}