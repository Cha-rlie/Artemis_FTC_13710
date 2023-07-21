package org.firstinspires.ftc.teamcode.autonomous;

// Import the classes necessary to run RoadRunner

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
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

public class Artemis_Auto_Right extends LinearOpMode {

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
        deposit.failSafeActiviated = false;

        robot.V4B_1.setPosition(intake.V4B_IdlePos);
        robot.V4B_2.setPosition(intake.V4B_IdlePos);

        // Create RoadRunner objects
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        drive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Create the position that the RoadRunner will be told the robot starts at
        Pose2d startPos = new Pose2d(0, 0, Math.toRadians(0));
        drive.setPoseEstimate(startPos);

        // Create the RoadRunner TrajectorySequence that will get the robot to the cycling position
        TrajectorySequence driveToCyclingPosRight = drive.trajectorySequenceBuilder(startPos)
                .forward(56)
                .strafeRight(1)
                .turn(Math.toRadians(-103))
                .strafeLeft(1)
                //.back(1)
                .build();

        TrajectorySequence driveToLeftParkingPosRightAuto = drive.trajectorySequenceBuilder(driveToCyclingPosRight.end())
                .turn(Math.toRadians(103))
                .back(2)
                .strafeLeft(24)
                .build();

        TrajectorySequence driveToMiddleParkingPosRightAuto = drive.trajectorySequenceBuilder(driveToCyclingPosRight.end())
                .turn(Math.toRadians(103))
                .back(2)
                .build();

        TrajectorySequence driveToRightParkingPosRightAuto = drive.trajectorySequenceBuilder(driveToCyclingPosRight.end())
                .turn(Math.toRadians(103))
                .back(2)
                .strafeRight(24)
                .back(1)
                .build();

        // Set-up the camera view
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Artemis_Webcam"), cameraMonitorViewId);

        // Create an instance of the class to detect the AprilTags
        Artemis_AprilTag_Autonomous coneSleeveDetector = new Artemis_AprilTag_Autonomous(robot, telemetry, camera);

        // Get the desired parking location as a string
        String parkingLocation = coneSleeveDetector.getDetectedSide(telemetry);
        String lastSeenParkingLocation = "NOT FOUND";

        // Keep looking for a parking location either until one is found or the START button is pressed
        while (opModeInInit() && !isStopRequested()) {
            parkingLocation = coneSleeveDetector.getDetectedSide(telemetry);
            if (parkingLocation != "NOT FOUND") {lastSeenParkingLocation = parkingLocation;}
            opModeIsActive();
            telemetry.addData("Currently Detected Parking Location", parkingLocation);
            telemetry.addData("Last Seen Parking Location", lastSeenParkingLocation);
            telemetry.update();
        }

        // Wait for the START button to be pressed
        waitForStart();

        if (parkingLocation == "NOT FOUND" && lastSeenParkingLocation == "NOT FOUND") {
            parkingLocation = "MIDDLE";
        } else {
            parkingLocation = lastSeenParkingLocation;
        }

        // Move the robot to the position where it can cycle cones
        drive.followTrajectorySequence(driveToCyclingPosRight);

        // Start the deposit "High" scoring program
        deposit.runDeposit(robot, 0, "High", telemetry);

        while (deposit.automationWasSet || deposit.zeroWasTargetted) {
            deposit.runDeposit(robot, 0, "Update", telemetry);
        }
//
//        double stackHeight = 0.08;
//        double i = intake.V4B_HomePos - stackHeight;
//        int intakeAutoCycle = -2550;
//        int cycleNumber = 3;
//
//        for (int j = 0; j < cycleNumber; j++) {
//            if(deposit.failSafeActiviated) {
//                break;
//            }
//            i = i + (stackHeight / 5);
//            intake.increment = 0.1;
//            boolean cycleSlidesHaveReachedPos = false;
//
//            while (!cycleSlidesHaveReachedPos) {
//                telemetry.addData("Rotation Encoder: ", intake.getRotationDegrees(intake.updateRotateClaw(robot, intake.increment), telemetry));
//                robot.claw.setPosition(intake.openClawPos);
//                robot.V4B_1.setPosition(i);
//                robot.V4B_2.setPosition(i);
//                robot.spinClaw.setPosition(intake.clawFowardPos);
//
//                robot.intakeLeft.setTargetPosition(intakeAutoCycle);
//                robot.intakeRight.setTargetPosition(intakeAutoCycle);
//                robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                robot.intakeLeft.setPower(1);
//                robot.intakeRight.setPower(1);
//
//                if (robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeAutoCycle, 10)) {
//                    cycleSlidesHaveReachedPos = true;
//                } /*else if (((robot.distanceSensor.getDistance(DistanceUnit.CM) < 2)) && (Math.abs(robot.intakeLeft.getCurrentPosition()-intakeAutoCycle) < 20)) {
//                    cycleSlidesHaveReachedPos = true;
//                }*/
//
//            }
//
//            robot.claw.setPosition(intake.closedClawPos);
//            boolean adjustmentIntake = false;
//            int adjustmentIncrement = 200;
//
//            sleep(300);
//
//            while (!adjustmentIntake) {
//                robot.V4B_1.setPosition(intake.V4B_HomePos - 0.2);
//                robot.V4B_2.setPosition(intake.V4B_HomePos - 0.2);
//                robot.intakeLeft.setTargetPosition(intakeAutoCycle - adjustmentIncrement);
//                robot.intakeRight.setTargetPosition(intakeAutoCycle - adjustmentIncrement);
//                robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                robot.intakeLeft.setPower(1);
//                robot.intakeRight.setPower(1);
//
//                if (robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeAutoCycle - adjustmentIncrement, 10)) {
//                    adjustmentIntake = true;
//                }
//            }
//
//
//            sleep(200);
//
//            intake.coneTransfer(robot, "Start", telemetry, deposit);
//            intake.isConeBeingTransferred = true;
//
//            while (intake.isConeBeingTransferred) {
//                telemetry.addData("Rotation Encoder: ", intake.getRotationDegrees(intake.updateRotateClaw(robot, intake.increment), telemetry));
//                intake.isConeBeingTransferred = intake.coneTransfer(robot, "Update", telemetry, deposit);
//            }
//
//            intake.movingToGround = true;
//            double targetChange = 0;
//
//            sleep(300);
//
//            while (intake.movingToGround) {
//                telemetry.addData("Rotation Encoder: ", intake.getRotationDegrees(intake.updateRotateClaw(robot, intake.increment), telemetry));
//                intake.transferRunning = false;
//                intake.isConeBeingTransferred = false;
//
//                targetChange += 0.0008;
//
//                if (robot.V4B_1.getPosition() > intake.V4B_TransferPos + 0.05) {
//                    robot.claw.setPosition(intake.closedClawPos);
//                }
//
//                if (robot.V4B_1.getPosition() > i) {
//                    intake.movingToGround = false;
//                    robot.V4B_1.setPosition(i);
//                    robot.V4B_2.setPosition(i);
//                    robot.claw.setPosition(intake.openClawPos);
//                    robot.spinClaw.setPosition(intake.clawFowardPos);
//                    targetChange = 0;
//                } else {
//                    robot.V4B_1.setPosition(robot.V4B_1.getPosition() + targetChange);
//                    robot.V4B_2.setPosition(robot.V4B_1.getPosition() + targetChange);
//                }
//            }
//
//
//            sleep(300);
//
//            // Start the deposit "High" scoring program
//            deposit.runDeposit(robot, 0, "High", telemetry);
//
//            while (deposit.automationWasSet || deposit.zeroWasTargetted) {
//                telemetry.addData("Rotation Encoder: ", intake.getRotationDegrees(intake.updateRotateClaw(robot, intake.increment), telemetry));
//                deposit.runDeposit(robot, 0, "Update", telemetry);
//
//                if (!(j == cycleNumber-1)) {
//                    // Run to mid position
//                    robot.intakeLeft.setTargetPosition(intakeAutoCycle + 300);
//                    robot.intakeRight.setTargetPosition(intakeAutoCycle + 300);
//                    robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                    robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                    robot.intakeLeft.setPower(1);
//                    robot.intakeRight.setPower(1);
//                } else if (j == cycleNumber-1) {
//                    robot.V4B_1.setPosition(intake.V4B_IdlePos);
//                    robot.V4B_2.setPosition(intake.V4B_IdlePos);
//                }
//            }
//        }

            // Move the robot to the correct parking position
        if (parkingLocation == "LEFT") {
            drive.followTrajectorySequence(driveToLeftParkingPosRightAuto);
        } else if (parkingLocation == "MIDDLE" || parkingLocation == "NOT FOUND") {
            drive.followTrajectorySequence(driveToMiddleParkingPosRightAuto);
        } else if (parkingLocation == "RIGHT") {
            drive.followTrajectorySequence(driveToRightParkingPosRightAuto);
        }


    }
}
