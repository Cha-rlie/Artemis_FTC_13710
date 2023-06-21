package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DriveTrain {

    // Drivetrain variables to manage and control instances of the class
    private static DriveTrain instance = null;
    public boolean enabled;

    public void init(RobotHardware robot) {

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
        // Initialise the input values from first controller for the drive base
        float xInput = (float)(gamepad1.right_stick_x*1.1); // Account for imperfect strafing
        float yInput = -gamepad1.right_stick_y; // One side needs to be reversed
        float rInput = gamepad1.left_stick_x;

        boolean slowSpeed = gamepad1.left_bumper;
        boolean fastSpeed = gamepad1.right_bumper;

        // Variable to change the percentage the speed should be set at
        double speedModifier;

        // Change the speedModifier based off the bumpers pressed on the gamepad
        if (slowSpeed && !fastSpeed) {
            speedModifier = 0.4;
        } else if (fastSpeed && !slowSpeed) {
            speedModifier = 1;
        } else {
            speedModifier = 0.7;
        }

        // Returns the largest denominator that the power of the motors must be divided by to keep their original ratio
        double ratioScalingDenominator = Math.max(Math.abs(yInput) + Math.abs(xInput) + Math.abs(rInput), 1);


        // Drive the drive base with mecanum code
        robot.frontLeft.setPower(((yInput + xInput + rInput) / ratioScalingDenominator)*speedModifier);
        robot.frontRight.setPower(((yInput - xInput - rInput) / ratioScalingDenominator)*speedModifier);
        robot.rearLeft.setPower(((yInput - xInput + rInput) / ratioScalingDenominator)*speedModifier);
        robot.rearRight.setPower(((yInput + xInput - rInput) / ratioScalingDenominator)*speedModifier);

    }

}
