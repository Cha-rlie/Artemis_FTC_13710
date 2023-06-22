package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes
import org.firstinspires.ftc.teamcode.hardware.DriveTrain;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Deposit;

// Import the necessary FTC modules and classes
//import com.google.android.libraries.play.games.inputmapping.Input;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

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
        intake.init(robot);
        deposit.init(robot);


        boolean buttonIsReleased = true; // Handling debounce issues

        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        int latchPos = 0;

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
            if(gamepad2.left_stick_y > 0.5) {
                intake.runIntake(robot, intake.intakeHome, telemetry);
            } else if(gamepad2.left_stick_y < -0.5) {
                intake.runIntake(robot, intake.intakeOut, telemetry);
            } else {
                robot.intakeLeft.setPower(0);
                robot.intakeRight.setPower(0);
            }

            // Rotate Claw Manual Control
            if(gamepad2.left_bumper) {intake.rotateClaw(robot,0.01);}
            else if (gamepad2.right_bumper) {intake.rotateClaw(robot,-0.01);}

            // Open/Close Claw Manual Control
            if(this.gamepad2.a) {intake.changeClaw(robot);}
            else {intake.buttonAReleased = true;}


            telemetry.addData("Robot: ", robot.enabled);
            // Update the telemetry's information screen
            telemetry.update();


        // Handling debounce issues
            if(this.gamepad2.x) {
                if (buttonIsReleased) {
                    buttonIsReleased = false;
                    if(!intake.groundIntake) {
                        robot.rotateClaw.setPosition(0.78);
                        intake.groundIntake = true;
                    } else if(intake.groundIntake) {
                        robot.rotateClaw.setPosition(0.68);
                        intake.groundIntake = false;
                    }
                }
            } else {
                buttonIsReleased = true;
            }


            // Transfer the cone from the claw to the deposit "cone-holder"
            if(this.gamepad2.left_trigger > 0.5) {
                intake.coneTransfer(robot);
            }
            robot.V4B_1.setPosition(robot.V4B_1.getPosition()+(this.gamepad2.right_stick_y/600));
            robot.V4B_2.setPosition(robot.V4B_2.getPosition()-(this.gamepad2.right_stick_y/600));
            telemetry.addData("V4B_1: ", robot.V4B_1.getPosition());
            telemetry.addData("V4B_2: ", robot.V4B_2.getPosition());
            telemetry.addData("RotateClaw: ", robot.rotateClaw.getPosition());
            telemetry.addData("Claw: ", robot.claw.getPosition());
            telemetry.addData("SpinClaw: ", robot.spinClaw.getPosition());
            telemetry.addData("Latch: ", robot.latch.getPosition());
            telemetry.addData("Robot: ", robot.enabled);
            telemetry.addData("Deposit: ", (robot.depositLeft.getCurrentPosition() + robot.depositLeft.getCurrentPosition()) / 2);

            // Update the telemetry's information screen
            telemetry.update();

        }

    }

}