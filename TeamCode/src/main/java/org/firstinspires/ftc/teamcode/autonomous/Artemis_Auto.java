package org.firstinspires.ftc.teamcode.autonomous;

// Import the classes necessary to run RoadRunner
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.RoadRunnerStoof.drive.SampleMecanumDrive;

// Import the custom-made hardware classes
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.RobotHardware;

// Import the custom-class to detect the cone sleeve side
import org.firstinspires.ftc.teamcode.autonomous.sleeveDetection.Artemis_AprilTag_Autonomous;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;


@Autonomous

public class Artemis_Auto extends LinearOpMode {

    // Initialise instances of all the custom-made hardware classes/objects
    private RobotHardware robot = RobotHardware.getInstance();
    private DriveTrain driveTrain = new DriveTrain();
    private Intake intake = Intake.getInstance();
    private Deposit deposit = Deposit.getInstance();

    // Set-up the camera view
    public int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
    public OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Artemis_Webcam"), cameraMonitorViewId);

    // Initialise the instance of the class to detect the cone sleeve's side
    private Artemis_AprilTag_Autonomous coneSleeveDetector = new Artemis_AprilTag_Autonomous(robot, telemetry, camera);

    // Get the desired parking location
    String parkingLocation = coneSleeveDetector.getDetectedSide(telemetry);

    @Override
    public void runOpMode() {
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

        Trajectory ArtemisAutoTesting = drive.trajectoryBuilder(startPos)
                .forward(40)
                .build();

        waitForStart();

        drive.followTrajectory(ArtemisAutoTesting);

        telemetry.addData("Place to park", parkingLocation);

        if(isStopRequested()) return;

        // Produce all the RoadRunner trajectories
//        Trajectory driveToCyclingPosition = RRDriveSystem.trajectoryBuilder(new Pose2d())
//                .forward(58)
//                .build();


//        TrajectorySequence ArtemisAutoTesting = drive.trajectorySequenceBuilder(new Pose2d(-34.14, -71.57, Math.toRadians(90.00)))
//                .splineTo(new Vector2d(-58.3600, -54), 4.3832)
//                .build();
//        drive.setPoseEstimate(ArtemisAutoTesting.start());


//        Trajectory driveToLeftParkingPosition = drive.trajectoryBuilder(driveToCyclingPosition.end().plus(new Pose2d(0,0, Math.toRadians(100))))
//                .strafeLeft(20)
//                .build();

        // Wait for the driver to click the "Play" button before proceeding



        // Drive the robot to its cycling position and rotate to the right angle

        //drive.turn(Math.toRadians(100));

       // Score the preloaded cone
//        boolean depositRun = false;
//        deposit.runDeposit(robot, 0, "High", telemetry);
//        while (!depositRun) {
//            depositRun = deposit.runDeposit(robot, 0, "Update", telemetry);
//        }
        // Cycling through the stack of cones
//        for (int coneToGrab = 5; coneToGrab < 1; coneToGrab--) {
//            intake.extendAndGrab(robot, deposit, coneToGrab, intake.intakeOut, telemetry);
//            intake.coneTransfer(robot, "Start", telemetry, deposit);
//            deposit.runDeposit(robot, 0, "High", telemetry);
//        }

        // Run until the "Stop" button is pressed


    }
}