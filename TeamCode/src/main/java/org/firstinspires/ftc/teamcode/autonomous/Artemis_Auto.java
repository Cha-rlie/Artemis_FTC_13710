package org.firstinspires.ftc.teamcode.autonomous;

// Import the necessary custom-made classes

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//import org.firstinspires.ftc.teamcode.AprilTagDetectionPipeline;
import org.firstinspires.ftc.teamcode.RoadRunnerStoof.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.RobotHardware;


@Autonomous

public class Artemis_Auto extends LinearOpMode {

    // Create instances of all the custom-made hardware classes/objects
    private RobotHardware robot = RobotHardware.getInstance();
    private DriveTrain driveTrain = new DriveTrain();
    private Intake intake = Intake.getInstance();
    private Deposit deposit = Deposit.getInstance();

    @Override
    public void runOpMode() {
        // Initialise all the custom-made hardware objects
        robot.init(hardwareMap);
        driveTrain.init(robot);
        intake.init(robot, telemetry);
        deposit.init(robot, telemetry);

        robot.frontLeft.setPower(0);
        robot.frontRight.setPower(0);
        robot.rearLeft.setPower(0);
        robot.rearRight.setPower(0);

        // Create RoadRunner objects
        SampleMecanumDrive RRDriveSystem = new SampleMecanumDrive(hardwareMap);

        // Produce all the RoadRunner trajectories
        Trajectory driveToCyclingPosition = RRDriveSystem.trajectoryBuilder(new Pose2d())
                .forward(58)
                .build();

        Trajectory driveToLeftParkingPosition = RRDriveSystem.trajectoryBuilder(new Pose2d())
                .strafeLeft(20)
                .build();

        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        RRDriveSystem.followTrajectory(driveToCyclingPosition);
        RRDriveSystem.turn(Math.toRadians(100));
//        intake.extendAndGrab(robot, deposit, 5, intake.intakeOut, telemetry);
//        intake.coneTransfer(robot,"Start", telemetry, deposit);
//        deposit.runDeposit(robot, 0, "High", telemetry);

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {

        }

    }
}