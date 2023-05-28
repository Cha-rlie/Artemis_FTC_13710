package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Deposit;

// Import the necessary FTC modules and classes
//import com.google.android.libraries.play.games.inputmapping.Input;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;


@TeleOp

public class Artemis_TeleOp extends LinearOpMode {

    private RobotHardware robot = RobotHardware.getInstance();
    private Intake intake = Intake.getInstance();
    private Deposit deposit = Deposit.getInstance();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        intake.init(robot);
        deposit.init(robot);


        boolean buttonIsReleased = true; // Handling debounce issues


        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {
            // Initialise the input values from first controller for the drive base
            float xInput = (float)(this.gamepad1.right_stick_x*1.1); // Account for imperfect strafing
            float yInput = -this.gamepad1.right_stick_y; // One side needs to be reversed
            float rInput = this.gamepad1.left_stick_x;


            // Returns the largest denominator that the power of the motors must be divided by to keep their original ratio
            double ratioScalingDenominator = Math.max(Math.abs(yInput) + Math.abs(xInput) + Math.abs(rInput), 1);


            // Drive the drive base with mecanum code
            robot.frontLeft.setPower((yInput + xInput + rInput) / ratioScalingDenominator);
            robot.frontRight.setPower((yInput - xInput - rInput) / ratioScalingDenominator);
            robot.rearLeft.setPower((yInput - xInput + rInput) / ratioScalingDenominator);
            robot.rearRight.setPower((yInput + xInput - rInput) / ratioScalingDenominator);

            // AUTOMATICALLY move the deposit systems
            if(this.gamepad2.dpad_right) {
                deposit.runDeposit(robot, 0, "High", telemetry);
                //deposit.depositHigh(robot);
            }

            // Two values are held:
            // -

            int avg = (deposit.getDepositPosition(robot)[0] + deposit.getDepositPosition(robot)[1])/2;
            boolean withinRange = (deposit.automatedMoveTargetPosition < avg + deposit.encoderError) && (deposit.automatedMoveTargetPosition > avg - deposit.encoderError);
            boolean withinRangeOfZero = (deposit.min < avg + deposit.encoderError) && (deposit.min > avg - deposit.encoderError);

            /*if(deposit.AutomatedMove1 && withinRange) {
                deposit.AutomatedMove2 = true;
                if(deposit.AutomatedMove2 && withinRangeOfZero){
                    deposit.AutomatedMove1 = false;
                    deposit.AutomatedMove2 = false;
                }
            }*/

            // MANUALLY move the deposit systems
            if (this.gamepad2.dpad_up) {deposit.runDeposit(robot, deposit.max, "Manual", telemetry);}
            else if (this.gamepad2.dpad_down) {deposit.runDeposit(robot, deposit.min, "Manual", telemetry);}
            else {deposit.runDeposit(robot, (deposit.getDepositPosition(robot)[0] + deposit.getDepositPosition(robot)[1]) / 2, "Manual", telemetry);}

            // Intake Slides Manual Control
            if(gamepad2.left_stick_y > 0.5) {intake.runIntake(robot, intake.IntakeHome, telemetry);}
            else if(gamepad2.left_stick_y < -0.5) {intake.runIntake(robot, intake.IntakeOut, telemetry);}
            else {intake.runIntake(robot, intake.getIntakePosition(robot), telemetry);}

            // Rotate Claw Manual Control
            if(gamepad2.left_bumper) {intake.rotateClaw(robot,0.01);}
            else if (gamepad2.right_bumper) {intake.rotateClaw(robot,-0.01);}

            // Open/Close Claw Manual Control
            if(this.gamepad2.a) {intake.changeClaw(robot);}
            else {intake.AbuttonIsReleased = true;}


            // Handling debounce issues
//            if(this.gamepad2.x) {
//                if (buttonIsReleased) {
//                    buttonIsReleased = false;
//                    if(!GroundIntake) {
//                        robot.RotateClaw.setPosition(0.78);
//                        GroundIntake = true;
//                    } else if(GroundIntake) {
//                        robot.RotateClaw.setPosition(0.68);
//                        GroundIntake = false;
//                    }
//                }
//            } else {
//                buttonIsReleased = true;
//            }




            /*
            if(this.gamepad2.left_trigger > 0.5) {
                intake.coneTransfer(IntakeLeft, IntakeRight, V4B_1, V4B_2, Claw, SpinClaw, RotateClaw);

            } else if (this.gamepad2.right_trigger > 0.5) {
                boolean SlidePositionReached = false;

                while(!SlidePositionReached) { // While the slides aren't in the correct position
                    V4B_1.setPosition(V4B_1_HomePos);
                    V4B_2.setPosition(V4B_2_HomePos);
                    SpinClaw.setPosition(ClawFowardPos);
                    RotateClaw.setPosition(RotateClaw_HomePos);

                    IntakeLeft.setTargetPosition(IntakeOut);
                    IntakeRight.setTargetPosition(IntakeOut);
                    IntakeLeft.setPower(1);
                    IntakeRight.setPower(1);

                    if ((IntakeLeft.getCurrentPosition()+IntakeRight.getCurrentPosition())/2 < TransferPosition+10 && (IntakeLeft.getCurrentPosition()+IntakeRight.getCurrentPosition())/2 > TransferPosition-10) {
                        // If the slides have reached the correct position...
                        SlidePositionReached = true;
                    }
                }
            }
*/

//            if(this.gamepad2.y) {
//                robot.V4B_1.setPosition(V4B_1_HomePos);
//                robot.V4B_2.setPosition(V4B_2_HomePos);
//                robot.RotateClaw.setPosition(RotateClaw_HomePos);
//            }

            /*robot.DepositLeft.setPower(1);
            robot.DepositRight.setPower(1);
            robot.DepositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.DepositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);*/

            deposit.runDeposit(robot,0, "Update", telemetry);

            robot.V4B_1.setPosition(robot.V4B_1.getPosition()+(this.gamepad2.right_stick_y*2/200));
            robot.V4B_2.setPosition(robot.V4B_2.getPosition()-(this.gamepad2.right_stick_y*2/200));
            telemetry.addData("V4B_1", robot.V4B_1.getPosition());
            telemetry.addData("V4B_2", robot.V4B_2.getPosition());
            telemetry.addData("RotateClaw", robot.rotateClaw.getPosition());
            telemetry.addData("Claw", robot.claw.getPosition());
            telemetry.addData("SpinClaw", robot.spinClaw.getPosition());
            telemetry.addData("Latch", robot.latch.getPosition());


            telemetry.addData("Robot: ", robot.enabled);
            // Update the telemetry's information screen
            telemetry.update();

        }

    }

}
