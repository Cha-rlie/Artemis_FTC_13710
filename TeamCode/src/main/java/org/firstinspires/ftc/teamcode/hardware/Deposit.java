package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.lang.Math;

public class Deposit {
    private static Deposit instance = null;
    public boolean enabled;
    public int heldPosition = 0;

    public int encoderError = 5;
    public int max = 3000;
    public int min = 0;
    int highJunction = 2375;
    int midJunction = 1420;

    double latchOpen = 0.1;
    double latchPrime = 0.4;
    double latchClose = 0.65;

    public int automatedMoveTargetPosition;
    public boolean automationWasSet;
    public boolean zeroWasTargetted;

    public static Deposit getInstance() {
        if (instance == null) {
            instance = new Deposit();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot, Telemetry telemetry) {
        double current = 0;
        controlLatch(robot, "Close");

        while(current <= 3) {
            current = (robot.depositLeft.getCurrent(CurrentUnit.AMPS)+robot.depositRight.getCurrent(CurrentUnit.AMPS))/2;
            robot.depositLeft.setPower(-0.5);
            robot.depositRight.setPower(-0.5);
            telemetry.addData("Current Draw: ", current);
            telemetry.update();
        }

        telemetry.addData("Deposit Reset", "");
        telemetry.update();

        robot.depositLeft.setPower(0);
        robot.depositRight.setPower(0);

        robot.depositLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.depositRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runDeposit(RobotHardware robot, int targetPosition, String mode, Telemetry telemetry) {
        if (mode == "Manual") {
            // Stop the automation
            automationWasSet = false;

            robot.depositLeft.setTargetPosition(targetPosition);
            robot.depositRight.setTargetPosition(targetPosition);
            robot.depositLeft.setPower(0.6);
            robot.depositRight.setPower(0.6);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            heldPosition = robot.depositLeft.getCurrentPosition();

        } else if (mode == "High" || mode == "Medium" || mode == "Low" || automationWasSet || zeroWasTargetted) {
            // Change the variable to demonstrate that an automation command was just set
            if (mode == "High" || mode == "Medium" || mode == "Low") {automationWasSet = true;}

            // Set the desired automated values
            if (mode == "High") {
                automatedMoveTargetPosition = highJunction; controlLatch(robot, "Close");} else if (mode == "Medium") {
                automatedMoveTargetPosition = midJunction; controlLatch(robot, "Close");} else if (mode == "Low") {
                automatedMoveTargetPosition = min; controlLatch(robot, "Close");controlLatch(robot, "Close");}

            robot.depositLeft.setTargetPosition(automatedMoveTargetPosition);
            robot.depositRight.setTargetPosition(automatedMoveTargetPosition);
            robot.depositLeft.setPower(1);
            robot.depositRight.setPower(1);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            if (zeroWasTargetted == true && Math.abs(robot.depositRight.getCurrentPosition()) <= 5) {
                zeroWasTargetted = false;

            } else if (automationWasSet == true && Math.abs(robot.depositRight.getCurrentPosition() - automatedMoveTargetPosition) <= 10) {
                //this.runDeposit(robot, min, "Low", telemetry);
                zeroWasTargetted = true;
                automationWasSet = false;
                automatedMoveTargetPosition = 0;
                robot.depositLeft.setTargetPosition(automatedMoveTargetPosition);
                robot.depositRight.setTargetPosition(automatedMoveTargetPosition);
                robot.depositLeft.setPower(1);
                robot.depositRight.setPower(1);
                robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                controlLatch(robot, "Open");
            }

        } else if (!automationWasSet && !zeroWasTargetted){
            robot.depositLeft.setTargetPosition(heldPosition);
            robot.depositRight.setTargetPosition(heldPosition);
            robot.depositLeft.setPower(1);
            robot.depositRight.setPower(1);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
    }

    // Function that returns the average position of both deposits
    public int getDepositPosition(RobotHardware robot) {
        return (robot.depositLeft.getCurrentPosition()+robot.depositRight.getCurrentPosition())/2;
    }

    // Function that controls the latch servo
    public void controlLatch(RobotHardware robot, String mode) {
        if (mode == "Open") {
            robot.latch.setPosition(latchOpen);
        }
        else if (mode == "Prime") {
            robot.latch.setPosition(latchPrime);
        }
        else if (mode == "Close") {
            robot.latch.setPosition(latchClose);
        }
    }

    public String latchMode(RobotHardware robot) {
        if(robot.withinUncertainty(robot.latch.getPosition(), latchOpen, 0.01)) {
            return("Open");
        } else if(robot.withinUncertainty(robot.latch.getPosition(), latchPrime, 0.01)) {
            return("Prime");
        } else if (robot.withinUncertainty(robot.latch.getPosition(), latchClose, 0.01)) {
            return("Close");
        } else {
            return("Unknown");
        }
    }

}