package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class DriveTrain {

    // Drivetrain variables to manage and control instances of the class
    private static DriveTrain instance = null;
    public boolean enabled;
    public double botHeading;

    public void init(RobotHardware robot) {
        // Retrieve the IMU from the hardware map
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
                RevHubOrientationOnRobot.UsbFacingDirection.RIGHT));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        robot.imu.initialize(parameters);
    }

    // Function to return either a new or existing instance of this class
    public static DriveTrain getInstance() {
        if (instance == null) {
            instance = new DriveTrain();
        }
        instance.enabled = true;
        return instance;
    }

    public void runDriveTrain(RobotHardware robot, Telemetry telemetry, Gamepad gamepad1) {
        double y = -gamepad1.right_stick_y; // Remember, this is reversed!
        double x = gamepad1.right_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.left_stick_x;

        double speedModifier = 1; //Used to be 0.7

        if (gamepad1.left_bumper) {
            speedModifier = 0.3;
        } else if (gamepad1.right_bumper) {
            speedModifier = 1;
        }

        // This button choice was made so that it is hard to hit on accident,
        // it can be freely changed based on preference.
        // The equivalent button is start on Xbox-style controllers.
        if (gamepad1.guide) {
            robot.imu.resetYaw();
        }

        double botHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        robot.frontLeft.setPower(frontLeftPower*speedModifier);
        robot.rearLeft.setPower(backLeftPower*speedModifier);
        robot.frontRight.setPower(frontRightPower*speedModifier);
        robot.rearRight.setPower(backRightPower*speedModifier);

    }

}