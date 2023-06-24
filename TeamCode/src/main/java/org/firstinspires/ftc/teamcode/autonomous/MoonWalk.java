package org.firstinspires.ftc.teamcode.autonomous;

// Import the necessary custom-made classes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.RobotHardware;


@Autonomous

public class MoonWalk extends LinearOpMode {

    private RobotHardware robot = RobotHardware.getInstance();
    private DriveTrain driveTrain = new DriveTrain();
    private Intake intake = Intake.getInstance();
    private Deposit deposit = Deposit.getInstance();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        driveTrain.init(robot);
        deposit.init(robot, telemetry);
        intake.init(robot, telemetry);

        robot.frontLeft.setPower(-0.7);
        robot.frontRight.setPower(-0.7);
        robot.rearLeft.setPower(-0.7);
        robot.rearRight.setPower(-0.7);

        intake.runIntake(robot, intake.intakeOut, telemetry);

        sleep(1000);

        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.rearLeft.setPower(0);
        robot.rearRight.setPower(0);

        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        // Variable to keep track of whether or not a cone is being transferred
        boolean isConeBeingTransferred = false;

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {

        }

    }
}