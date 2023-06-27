package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Artemis_TeleOp;
import org.firstinspires.ftc.teamcode.hardware.Deposit;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.concurrent.TimeUnit;

public class Intake {
    private static Intake instance = null;
    public boolean enabled;
    ElapsedTime mStateTime = new ElapsedTime();

    public boolean groundIntake = false; // Is the Claw facing the ground?
    boolean clawBackwards = false; // Is the Claw facing backwards?
    public boolean clawState = true; // True = open
    public boolean buttonAReleased = true;
    public boolean buttonXReleased = true;
    public boolean movingToGround = false;

    // Transfer stoof
    public double V4B_HomePos = 0.580; // Position where V4B is ready for intaking
    public double V4B_TransferPos = 0.35; // Position where V4B is ready for transfer
    public double V4B_IdlePos = 0.38;
    public int intakeTransferPos = -400;
    public int depositTransferPos = 320;

    double rotateClaw_HomePos = 0.7379; // Position where RotateClaw is ready for intaking
    double rotateClaw_Ground = 0.5; // Position where RotateClaw is ready for ground intaking

    public double closedClawPos = 0; // Position where claw is closed
    public double openClawPos = 0.3; // Position where claw is open
    public double

    public double clawFowardPos = 0.709;
    public double clawBackwardsPos = 0.050;

    public int intakeHome = 0; // Fully contracted position
    public int intakeOut = -3000 ; // Fully extended position -2000

    public boolean SlidePositionReached;
    boolean V4BPositionReached;
    boolean DepositReached;

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot, Telemetry telemetry) {
        double current = 0;
        int exceededCount = 0;

        while(current <= 1) {
            current = (robot.intakeLeft.getCurrent(CurrentUnit.AMPS)+robot.intakeRight.getCurrent(CurrentUnit.AMPS))/2;
            robot.intakeLeft.setPower(0.2);
            robot.intakeRight.setPower(0.2);
            telemetry.addData("Current Draw: ", current);
            telemetry.update();
        }

        robot.intakeLeft.setPower(0);
        robot.intakeRight.setPower(0);

        telemetry.addData("Intake Reset", "");
        telemetry.update();

        robot.intakeLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.intakeRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.claw.setPosition(closedClawPos);

        mStateTime.reset();
        while(!(mStateTime.time() >= 1.0)) {

        }

        updateRotateClaw(robot, 0);
        robot.spinClaw.setPosition(clawFowardPos);
        robot.V4B_1.setPosition(V4B_HomePos);
        robot.V4B_2.setPosition(V4B_HomePos);
    }

    public void changeClaw(RobotHardware robot) {
        if (buttonAReleased) {
            buttonAReleased = false;
            if(!clawState) {
                robot.claw.setPosition(openClawPos);
                clawState = true;
            } else if(clawState) {
                robot.claw.setPosition(closedClawPos);
                clawState = false;
            }
        }
    }

    public void GroundIntake(RobotHardware robot) {
        if (buttonXReleased) {
            buttonXReleased = false;
            if(!groundIntake) {
                robot.rotateClaw.setPosition(rotateClaw_Ground);
                groundIntake = true;
            } else if(groundIntake) {
                robot.rotateClaw.setPosition(rotateClaw_HomePos);
                groundIntake = false;
            }
        }
    }

    public double resetToHome(RobotHardware robot, Deposit deposit) {
        robot.V4B_1.setPosition(V4B_HomePos);
        robot.V4B_2.setPosition(V4B_HomePos);
        robot.spinClaw.setPosition(clawFowardPos);
        movingToGround = true;
        deposit.heldPosition = 0;
        return(0);
    }

    public void runIntake(RobotHardware robot, int targetPosition, Telemetry telemetry, double power) {
        robot.intakeLeft.setTargetPosition(targetPosition);
        robot.intakeRight.setTargetPosition(targetPosition);
        robot.intakeLeft.setPower(power);
        robot.intakeRight.setPower(power);
        robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void updateRotateClaw(RobotHardware robot, double increment) {
        double gradient = -0.722063;
        double cintercept =0.957285 + increment;
        robot.rotateClaw.setPosition(gradient * robot.V4B_1.getPosition() + cintercept);
    }

    boolean transferRunning = false;

    public boolean coneTransfer (RobotHardware robot, String mode, Telemetry telemetry, Deposit deposit) {
        // Do things just once at the beginning
        if (mode == "Start" && !transferRunning) {
            transferRunning = true;

            robot.claw.setPosition(closedClawPos);

            SlidePositionReached = false; // Holds whether desired position for intake slides has been reached
            V4BPositionReached = false; // Holds whether desired position for v4b has been reached
            DepositReached = false;
        }

        if (!SlidePositionReached) {
            deposit.controlLatch(robot, "Prime");
            robot.V4B_1.setPosition(V4B_TransferPos);
            robot.V4B_2.setPosition(V4B_TransferPos);
            robot.spinClaw.setPosition(clawBackwardsPos);

            robot.intakeLeft.setTargetPosition(intakeTransferPos);
            robot.intakeRight.setTargetPosition(intakeTransferPos);
            robot.intakeLeft.setPower(1);
            robot.intakeRight.setPower(1);
            robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //runIntake(robot, intakeTransferPos, telemetry, 1);

            if (robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeTransferPos, 10)) {
                // If the slides have reached the correct position...
                SlidePositionReached = true;
            }
        }

        if(robot.withinUncertainty(robot.V4B_1.getPosition(), V4B_TransferPos, 0.005)) {
            // If the v4b has reached the correct position...
            V4BPositionReached = true;
        }


        if(SlidePositionReached && V4BPositionReached) {
            deposit.runDeposit(robot, depositTransferPos, "Manual", telemetry);

//            robot.depositLeft.setTargetPosition(depositTransferPos);
//            robot.depositRight.setTargetPosition(depositTransferPos);
//            robot.depositLeft.setPower(0.6);
//            robot.depositRight.setPower(0.6);
//            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            if (robot.withinUncertainty(robot.depositLeft.getCurrentPosition(), depositTransferPos, 10)) {
                // If the v4b has reached the correct position...
                DepositReached = true;
            }
        }

        if (SlidePositionReached && V4BPositionReached && DepositReached) {
            deposit.controlLatch(robot, "Close");
            robot.claw.setPosition(openClawPos - 0.08);
        }



        if(SlidePositionReached && V4BPositionReached && DepositReached && deposit.latchMode(robot) == "Close") {
            resetToHome(robot, deposit);
            transferRunning = false;
            return false;
        } else {
            return true;
        }

    }

    public void cycle(DcMotorEx IntakeLeft, DcMotorEx IntakeRight, int IntakeOut) {
        IntakeLeft.setTargetPosition(IntakeOut);
        IntakeRight.setTargetPosition(IntakeOut);
        IntakeLeft.setPower(1);
        IntakeRight.setPower(1);
    }

}