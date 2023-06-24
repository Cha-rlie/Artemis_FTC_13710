package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Deposit;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {
    private static Intake instance = null;
    public boolean enabled;
    ElapsedTime mStateTime = new ElapsedTime();

    public boolean groundIntake = false; // Is the Claw facing the ground?
    boolean clawBackwards = false; // Is the Claw facing backwards?
    boolean clawState = true; // True = open
    public boolean buttonAReleased = true;

    // Transfer stoof
    public double V4B_HomePos = 0.580; // Position where V4B is ready for intaking
    public double V4B_TransferPos = 0.35; // Position where V4B is ready for transfer
    public double V4B_IdlePos = 0.38;
    public int intakeTransferPos = -150;
    public int depositTransferPos = 320;

    double rotateClaw_HomePos = 0.7379; // Position where RotateClaw is ready for intaking

    public double closedClawPos = 0.14; // Position where claw is closed
    public double openClawPos = 0.31; // Position where claw is open

    public double clawFowardPos = 0.88;
    public double clawBackwardsPos = 0.24;


    public int intakeHome = 0; // Fully contracted position
    public int intakeOut = -3000 ; // Fully extended position -2000

    public boolean SlidePositionReached;

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot) {
        robot.claw.setPosition(closedClawPos);
        mStateTime.reset();
        while(!(mStateTime.time() >= 1.0)) {

        }
        robot.rotateClaw.setPosition(rotateClaw_HomePos);
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

    public void runIntake(RobotHardware robot, int targetPosition, Telemetry telemetry) {
        robot.intakeLeft.setTargetPosition(targetPosition);
        robot.intakeRight.setTargetPosition(targetPosition);
        robot.intakeLeft.setPower(1);
        robot.intakeRight.setPower(1);
        robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        int avg = (robot.intakeLeft.getCurrentPosition()+robot.intakeRight.getCurrentPosition())/2;
        telemetry.addData("Intake: ", avg);
    }

    public void updateRotateClaw(RobotHardware robot) {
        robot.rotateClaw.setPosition(0.495438 * robot.V4B_1.getPosition() + 0.52);
    }

    boolean transferRunning = false;

    public boolean coneTransfer (RobotHardware robot, String mode, Telemetry telemetry, Deposit deposit) {
        // Variables to keep track of the progress of the function
        SlidePositionReached = false; // Holds whether desired position for intake slides has been reached
        boolean V4BPositionReached = false; // Holds whether desired position for v4b has been reached
        boolean DepositReached = false;


        // Do things just once at the beginning
        if (mode == "Start" && !transferRunning) {
            transferRunning = true;

            robot.claw.setPosition(closedClawPos);
        }

        if (!SlidePositionReached) {
            deposit.controlLatch(robot, "Open");
            robot.V4B_1.setPosition(V4B_TransferPos);
            robot.V4B_2.setPosition(V4B_TransferPos);
            robot.spinClaw.setPosition(clawBackwardsPos);

            runIntake(robot, intakeTransferPos, telemetry);

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

            if (robot.withinUncertainty(robot.depositLeft.getCurrentPosition(), depositTransferPos, 10)) {
                // If the v4b has reached the correct position...
                DepositReached = true;
            }
        }

        if (SlidePositionReached && V4BPositionReached && DepositReached) {
            deposit.controlLatch(robot,"Close");
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