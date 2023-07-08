package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
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
        double targetChange = 0; // Idk some temporary variable... I couldn't think of a better way to do this

        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        int latchPos = 0;

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {
            driveTrain.runDriveTrain(robot, telemetry, this.gamepad1);


            // AUTOMATICALLY move the deposit systems
            if(!intake.isConeBeingTransferred && (this.gamepad2.dpad_right || this.gamepad1.dpad_right)) {
                deposit.runDeposit(robot, 0, "High", telemetry);
                //deposit.depositHigh(robot);
            } else if (!intake.isConeBeingTransferred && (this.gamepad2.dpad_left || this.gamepad1.dpad_left)) {
                deposit.runDeposit(robot, 0, "Medium", telemetry);
            }


            // MANUALLY move the deposit systems
            if (!intake.isConeBeingTransferred && this.gamepad2.dpad_up) {
                deposit.runDeposit(robot, deposit.max, "Manual", telemetry);
            } else if (!intake.isConeBeingTransferred && this.gamepad2.dpad_down) {
                deposit.runDeposit(robot, deposit.min, "Manual", telemetry);}
            else if(!intake.isConeBeingTransferred){
                deposit.runDeposit(robot, 0, "Update", telemetry);
            }


            // Intake Slides Manual Control
            if (!intake.isConeBeingTransferred && !intake.cycleRunning && gamepad2.left_stick_y < 0.1) {
                intake.runIntake(robot, intake.intakeOut, telemetry, gamepad2.left_stick_y);
            } else if (!intake.isConeBeingTransferred && !intake.cycleRunning && gamepad2.left_stick_y > -0.1) {
                intake.runIntake(robot, intake.intakeHome, telemetry, gamepad2.left_stick_y);
            }


            // Open/Close Claw Manual Control
            if(this.gamepad2.a || this.gamepad1.a) {intake.changeClaw(robot);}
            else {intake.buttonAReleased = true;}

            // Prime/Close Latch Manual Control
            if(gamepad2.guide) {deposit.changeLatch(robot);
            } else {deposit.buttonReleased = true;}

            // Rotate claw into low junction scoring position
            if(gamepad2.x) {intake.scoreLow(robot);
            } else {intake.buttonXReleased = true;}

            // Automatically move rotate claw with v4b movement
            telemetry.addData("Rotation Encoder: ", intake.getRotationDegrees(intake.updateRotateClaw(robot, intake.increment), telemetry));

            // Set the claw to home position
            if(this.gamepad2.y) {
                intake.increment = intake.resetToHome(robot, deposit);
            }


            // Manual movement of the rotate claw servo
            if(!intake.isConeBeingTransferred && this.gamepad2.left_bumper && this.gamepad2.right_bumper) {
                intake.increment = 0.1;
            } else if(!intake.isConeBeingTransferred && this.gamepad2.left_bumper) {
                intake.increment -= 0.005;
            } else if(!intake.isConeBeingTransferred && this.gamepad2.right_bumper) {
                intake.increment += 0.005;
            }

            // Transfer the cone from the claw to the deposit "cone-holder"
            if(this.gamepad2.b) {
                intake.coneTransfer(robot,"Start", telemetry, deposit);
                intake.isConeBeingTransferred = true;
            }

            if (intake.isConeBeingTransferred) {
                intake.isConeBeingTransferred = intake.coneTransfer(robot, "Update", telemetry, deposit);
            }

            if(this.gamepad2.start) {
                intake.movingToIdle = true;
            }


            if(intake.movingToIdle) {
                intake.transferRunning = false;
                intake.isConeBeingTransferred = false;

                if(robot.V4B_1.getPosition() > intake.V4B_IdlePos) {
                    targetChange -= 0.0002;
                } else if(robot.V4B_1.getPosition() < intake.V4B_IdlePos) {
                    targetChange += 0.0002;
                }

                if (robot.V4B_1.getPosition() > intake.V4B_TransferPos + 0.05) {
                    robot.claw.setPosition(intake.closedClawPos);
                }

                if (robot.withinUncertainty(robot.V4B_1.getPosition(), intake.V4B_IdlePos, 0.005)) {
                    intake.movingToIdle = false;
                    robot.V4B_1.setPosition(intake.V4B_IdlePos);
                    robot.V4B_2.setPosition(intake.V4B_IdlePos);
                    robot.spinClaw.setPosition(intake.clawFowardPos);
                    targetChange = 0;
                } else {
                    robot.V4B_1.setPosition(robot.V4B_1.getPosition() + targetChange);
                    robot.V4B_2.setPosition(robot.V4B_1.getPosition() + targetChange);
                }
            }

            // Checks if the reset home function has been called
            if(intake.movingToGround) {
                intake.transferRunning = false;
                intake.isConeBeingTransferred = false;

                targetChange += 0.0008;

                if(robot.V4B_1.getPosition() > intake.V4B_TransferPos+0.05) {
                    robot.claw.setPosition(intake.closedClawPos);
                }

                if(robot.V4B_1.getPosition() > intake.V4B_TransferPos+0.1) {
                    if(intake.cycleRunning && intake.cycleTransferCompete) {
                        deposit.runDeposit(robot, 0, "High", telemetry);
                        intake.cycleRunning = false;
                        intake.cycleTransferCompete = false;
                        intake.cycle(robot, deposit, intake, telemetry, this.gamepad2);
                    }
                }
                if(robot.V4B_1.getPosition() > intake.V4B_HomePos) {
                    intake.movingToGround = false;
                    robot.V4B_1.setPosition(intake.V4B_HomePos);
                    robot.V4B_2.setPosition(intake.V4B_HomePos);
                    robot.claw.setPosition(intake.openClawPos);
                    robot.spinClaw.setPosition(intake.clawFowardPos);
                    targetChange = 0;
                } else {
                    robot.V4B_1.setPosition(robot.V4B_1.getPosition()+targetChange);
                    robot.V4B_2.setPosition(robot.V4B_1.getPosition()+targetChange);
                }
            }


            // Stop the claw getting caught
            if(robot.V4B_1.getPosition() < intake.V4B_HomePos - 0.16 && !intake.isConeBeingTransferred) {
                if (robot.spinClaw.getPosition() > intake.clawFowardPos) {
                    robot.spinClaw.setPosition(intake.clawFowardPos);
                }
                intake.restrictClawMovement = true;
            } else {
                intake.restrictClawMovement = false;
            }
            if(intake.restrictClawMovement && robot.claw.getPosition() > intake.openClawPos - 0.01) {
                robot.claw.setPosition(intake.closedClawPos);
            }


            // Manual movement of the v4b
            if(this.gamepad2.right_stick_y < 0 && robot.V4B_1.getPosition() >= intake.V4B_TransferPos-0.005) {
                robot.V4B_1.setPosition(robot.V4B_1.getPosition()-(Math.abs(this.gamepad2.right_stick_y/300)));
                robot.V4B_2.setPosition(robot.V4B_2.getPosition()-(Math.abs(this.gamepad2.right_stick_y/300)));

            } else if (this.gamepad2.right_stick_y > 0 && robot.V4B_1.getPosition() <= intake.V4B_HomePos+0.005) {
                robot.V4B_1.setPosition(robot.V4B_1.getPosition()+(Math.abs(this.gamepad2.right_stick_y/300)));
                robot.V4B_2.setPosition(robot.V4B_2.getPosition()+(Math.abs(this.gamepad2.right_stick_y/300)));
            }

            // Manual movement of the spin claw
            if(this.gamepad2.left_trigger > 0.1 && this.gamepad2.right_trigger > 0.1) {
                robot.spinClaw.setPosition(intake.clawFowardPos);

            } else if(this.gamepad2.left_trigger > 0.1) {
                robot.spinClaw.setPosition(robot.spinClaw.getPosition() + 0.01);
            } else if(this.gamepad2.right_trigger > 0.1) {
                robot.spinClaw.setPosition(robot.spinClaw.getPosition() - 0.01);
            }

            // Triggers the cycle function
            if(this.gamepad2.back) {
                intake.cycle(robot, deposit, intake, telemetry, this.gamepad2);
            }

            if(intake.cycleRunning) {
                intake.cycle(robot, deposit, intake, telemetry, this.gamepad2);
            }

            // telemetry.addData("Claw Distance: ", robot.clawDistance.getDistance(DistanceUnit.MM));

            telemetry.addData("Increment Value: ", intake.increment);

//            telemetry.addData("Motion Profiling Intake", intake.intakeMotionProfile(robot, robot.valueOff(robot.intakeLeft.getCurrentPosition(), intake.intakeOut)));
//            telemetry.addData("Closeness Value", robot.valueOff(robot.intakeLeft.getCurrentPosition(), intake.intakeOut));

            telemetry.addData("Intake: ", robot.intakeLeft.getCurrentPosition());

//            telemetry.addData("TransferRunning: ", intake.transferRunning);
//            telemetry.addData("IsConeBeingTransferred", intake.isConeBeingTransferred);

            telemetry.addData("V4B: ", robot.V4B_1.getPosition());
//            telemetry.addData("RotateClaw: ", robot.rotateClaw.getPosition());
//            telemetry.addData("Claw: ", robot.claw.getPosition());
//            telemetry.addData("SpinClaw: ", robot.spinClaw.getPosition());
//            telemetry.addData("Latch: ", robot.latch.getPosition());
//            telemetry.addData("Latch mode: ", deposit.latchMode(robot));
//            telemetry.addData("Robot: ", robot.enabled);
//            telemetry.addData("Deposit: ", (robot.depositLeft.getCurrentPosition() + robot.depositRight.getCurrentPosition()) / 2);
//            telemetry.addData("Deposit Current Draw", (robot.depositLeft.getCurrent(CurrentUnit.AMPS)+robot.depositRight.getCurrent(CurrentUnit.AMPS))/2);

            // Update the telemetry's information screen
            telemetry.update();

        }

    }

}