package org.firstinspires.ftc.teamcode.autonomous;

// Import the classes necessary to run RoadRunner

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.RoadRunnerStoof.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.RoadRunnerStoof.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.autonomous.sleeveDetection.Artemis_AprilTag_Autonomous;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.RobotHardware;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;


@Autonomous

public class Artemis_Auto_Just_Parking extends LinearOpMode {

    // Initialise instances of all the custom-made hardware classes/objects
    private RobotHardware robot = RobotHardware.getInstance();
    private DriveTrain driveTrain = new DriveTrain();
    private Intake intake = Intake.getInstance();
    private Deposit deposit = Deposit.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialise all the custom-made hardware objects
        robot.init(hardwareMap);
        driveTrain.init(robot);
        intake.init(robot, telemetry);
        deposit.init(robot, telemetry);

        // Create RoadRunner objects
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        Pose2d startPos = new Pose2d(0, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPos);

        TrajectorySequence autoJustParkLeft = drive.trajectorySequenceBuilder(startPos)
                .forward(36)
                .strafeLeft(19)
                .build();

        TrajectorySequence autoJustParkRight = drive.trajectorySequenceBuilder(startPos)
                .forward(36)
                .strafeRight(19)
                .back(1)
                .build();

        TrajectorySequence autoJustParkMiddle = drive.trajectorySequenceBuilder(startPos)
                .forward(38)
                .build();

        // Set-up the camera view
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Artemis_Webcam"), cameraMonitorViewId);

        Artemis_AprilTag_Autonomous coneSleeveDetector = new Artemis_AprilTag_Autonomous(robot, telemetry, camera);

        // Get the desired parking location as a string
        String parkingLocation = coneSleeveDetector.getDetectedSide(telemetry);

        while (!opModeIsActive() && parkingLocation == "NOT FOUND") {
            parkingLocation = coneSleeveDetector.getDetectedSide(telemetry);
            telemetry.addData("Place to park", parkingLocation);
            telemetry.update();
        }

        waitForStart();

        if (parkingLocation == "LEFT") {
            drive.followTrajectorySequence(autoJustParkLeft);
        } else if (parkingLocation == "MIDDLE" || parkingLocation == "NOT FOUND") {
            drive.followTrajectorySequence(autoJustParkMiddle);
        } else if (parkingLocation == "RIGHT") {
            drive.followTrajectorySequence(autoJustParkRight);
        }

        while (opModeIsActive()) {


        }
    }
}