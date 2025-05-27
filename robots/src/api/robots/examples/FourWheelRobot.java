package api.robots.examples;

import api.robots.ExternalRobot;
import api.robots.RobotModel;

import java.awt.Graphics2D;
import java.awt.Color;

public class FourWheelRobot implements ExternalRobot
{
    @Override
    public void updateRobotPosition(RobotModel model, double velocity, double angularVelocity, double duration) {
        double newX = model.getRobotPositionX() + velocity * duration * Math.cos(model.getRobotDirection());
        double newY = model.getRobotPositionY() + velocity * duration * Math.sin(model.getRobotDirection());
        double newDirection = model.getRobotDirection() + angularVelocity * duration;
        model.updateRobotPosition(newX, newY, newDirection);
    }

    @Override
    public void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        g.setColor(Color.BLUE);
        g.fillOval(x - 15, y - 15, 30, 30);
        g.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++)
        {
            double angle = direction + i * Math.PI / 2;
            int wheelX = x + (int)(20 * Math.cos(angle));
            int wheelY = y + (int)(20 * Math.sin(angle));
            g.fillOval(wheelX - 5, wheelY - 5, 10, 10);
        }
    }

    @Override
    public String getRobotType()
    {
        return "Four-Wheel Robot";
    }
}
