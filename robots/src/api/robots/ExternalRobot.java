package api.robots;

import java.awt.Graphics2D;

public interface ExternalRobot
{
    void updateRobotPosition(RobotModel model, double velocity, double angularVelocity, double duration);
    void drawRobot(Graphics2D g, int x, int y, double direction);
    String getRobotType();
}
