package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Deposit;

// Import the necessary FTC modules and classes
//import com.google.android.libraries.play.games.inputmapping.Input;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;


@TeleOp

public class Artemis_TeleOp extends LinearOpMode {

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

        boolean buttonIsReleased = true; // Handling debounce issues


        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        int latchPos = 0;

        // Variable to keep track of whether or not a cone is being transferred
        boolean isConeBeingTransferred = false;

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {
            driveTrain.runDriveTrain(robot, telemetry, this.gamepad1);


            // AUTOMATICALLY move the deposit systems
            if(this.gamepad2.dpad_right) {
                deposit.runDeposit(robot, 0, "High", telemetry);
                //deposit.depositHigh(robot);
            } else if (this.gamepad2.dpad_left) {
                deposit.runDeposit(robot, 0, "Medium", telemetry);
            }


            // MANUALLY move the deposit systems
            if (this.gamepad2.dpad_up) {
                deposit.runDeposit(robot, deposit.max, "Manual", telemetry);
            } else if (this.gamepad2.dpad_down) {
                deposit.runDeposit(robot, deposit.min, "Manual", telemetry);}
            else {
                deposit.runDeposit(robot, 0, "Update", telemetry);
            }

            // Intake Slides Manual Control
            if (!isConeBeingTransferred && gamepad2.left_stick_y < 0.1) {
                intake.runIntake(robot, intake.intakeOut, telemetry, gamepad2.left_stick_y);
            } else if (!isConeBeingTransferred && gamepad2.left_stick_y > -0.1) {
                intake.runIntake(robot, intake.intakeHome, telemetry, gamepad2.left_stick_y);
            } else {
                robot.intakeLeft.setPower(0);
                robot.intakeRight.setPower(0);
            }


            // Open/Close Claw Manual Control
            if(this.gamepad2.a) {intake.changeClaw(robot);}
            else {intake.buttonAReleased = true;}



//            if(this.gamepad2.left_bumper) {
//                robot.rotateClaw.setPosition(robot.rotateClaw.getPosition() + 0.005);
//            }
//            if(this.gamepad2.right_bumper) {
//                robot.rotateClaw.setPosition(robot.rotateClaw.getPosition() - 0.005);
//            }

            intake.updateRotateClaw(robot);

            // Transfer the cone from the claw to the deposit "cone-holder"
            if(this.gamepad2.left_trigger > 0.5) {
                isConeBeingTransferred = true;
                intake.coneTransfer(robot,"Start", telemetry, deposit);
            }

            if (isConeBeingTransferred) {
                isConeBeingTransferred = intake.coneTransfer(robot, "Update", telemetry, deposit);
            }

            telemetry.addData("Have the slides reached position? ", intake.SlidePositionReached);
            telemetry.addData("Cone Transferring? ", isConeBeingTransferred);

            if(this.gamepad2.y) {
                intake.resetToHome(robot, deposit);
            }

            if(robot.V4B_1.getPosition() > intake.V4B_HomePos && intake.movingToGround) {
                robot.claw.setPosition(intake.openClawPos);
                intake.movingToGround = false;
            }


            if(this.gamepad2.right_stick_y < 0 && robot.V4B_1.getPosition() >= intake.V4B_TransferPos-0.01) {
                robot.V4B_1.setPosition(robot.V4B_1.getPosition()-(Math.abs(this.gamepad2.right_stick_y/300)));
                robot.V4B_2.setPosition(robot.V4B_2.getPosition()-(Math.abs(this.gamepad2.right_stick_y/300)));

            } else if (this.gamepad2.right_stick_y > 0 && robot.V4B_1.getPosition() <= intake.V4B_HomePos+0.01) {
                robot.V4B_1.setPosition(robot.V4B_1.getPosition()+(Math.abs(this.gamepad2.right_stick_y/300)));
                robot.V4B_2.setPosition(robot.V4B_2.getPosition()+(Math.abs(this.gamepad2.right_stick_y/300)));
            }

            if(gamepad2.x) {
                deposit.controlLatch(robot, "Open");
            }

            telemetry.addData("V4B: ", robot.V4B_1.getPosition());
//            telemetry.addData("RotateClaw: ", robot.rotateClaw.getPosition());
//            telemetry.addData("Claw: ", robot.claw.getPosition());
//            telemetry.addData("SpinClaw: ", robot.spinClaw.getPosition());
            telemetry.addData("Latch: ", robot.latch.getPosition());
            telemetry.addData("Latch mode: ", deposit.latchMode(robot));
//            telemetry.addData("Robot: ", robot.enabled);
//            telemetry.addData("Deposit: ", (robot.depositLeft.getCurrentPosition() + robot.depositRight.getCurrentPosition()) / 2);
//            telemetry.addData("Deposit Current Draw", (robot.depositLeft.getCurrent(CurrentUnit.AMPS)+robot.depositRight.getCurrent(CurrentUnit.AMPS))/2);

            // Update the telemetry's information screen
            telemetry.update();

        }

    }

}