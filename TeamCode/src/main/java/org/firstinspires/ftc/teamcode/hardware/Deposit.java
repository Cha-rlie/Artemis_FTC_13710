package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import java.lang.Math;

public class Deposit {
    private static Deposit instance = null;
    public boolean enabled;
    public int heldPosition = 0;
    public boolean buttonReleased = true;
    boolean latchState = false; // True = open
    double current;

    public int encoderError = 5;
    public int max = 3000;
    public int min = 0;
    int highJunction = 2375;
    int midJunction = 1600;

    double latchOpen = 0.1;
    double latchPrime = 0.5;
    double latchClose = 0.68;

    public int automatedMoveTargetPosition;
    public boolean automationWasSet;
    public boolean zeroWasTargetted;
    public String automationPoleTarget;

    public static Deposit getInstance() {
        if (instance == null) {
            instance = new Deposit();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot, Telemetry telemetry) {
        // Vairables Reset ------------------------
        boolean buttonReleased = true;
        boolean latchState = false; // True = open

        encoderError = 5;
        max = 3000;
        min = 0;
        int highJunction = 2375;
        int midJunction = 1600;

        double latchOpen = 0.1;
        double latchPrime = 0.5;
        double latchClose = 0.7;

        int automatedMoveTargetPosition;
        boolean automationWasSet;
        boolean zeroWasTargetted;



        double current = 0;
        controlLatch(robot, "Close");


        while(current <= 3) {
            current = (robot.depositLeft.getCurrent(CurrentUnit.AMPS)+robot.depositRight.getCurrent(CurrentUnit.AMPS))/2;
            robot.depositLeft.setPower(-0.5);
            robot.depositRight.setPower(-0.5);
            telemetry.addData("Current Draw: ", current);
            telemetry.update();
        }

        robot.depositLeft.setPower(0);
        robot.depositRight.setPower(0);

        robot.depositLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.depositRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void runDeposit(RobotHardware robot, int targetPosition, String mode, Telemetry telemetry) {

        // Safety check to halt the automatic process if the current charge is too high (indicating the deposit is stuck on something)
        current = (robot.depositLeft.getCurrent(CurrentUnit.AMPS)+robot.depositRight.getCurrent(CurrentUnit.AMPS))/2;
        if(current > 2 && zeroWasTargetted && robot.depositLeft.getCurrentPosition() < highJunction-200 && automationPoleTarget == "High") {
            zeroWasTargetted = false;
            mode = "Manual";
        }

        if (mode == "Manual") {
            // Stop the automation
            automationWasSet = false;
            zeroWasTargetted = false;

            robot.depositLeft.setTargetPosition(targetPosition);
            robot.depositRight.setTargetPosition(targetPosition);
            robot.depositLeft.setPower(0.6);
            robot.depositRight.setPower(0.6);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            heldPosition = robot.depositLeft.getCurrentPosition();

        } else if (mode == "High" || mode == "Medium" || automationWasSet || zeroWasTargetted) {
            heldPosition = 0;

            // Change the variable to demonstrate that an automation command was just set
            if (mode == "High" || mode == "Medium") {automationWasSet = true; controlLatch(robot, "Close");}
            // Set the desired automated values
            if (mode == "High") {
                automatedMoveTargetPosition = highJunction; automationPoleTarget = "High";}
            else if (mode == "Medium") {
                automatedMoveTargetPosition = midJunction; automationPoleTarget = "Medium";}

            robot.depositLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            if(robot.depositLeft.getCurrentPosition() < automatedMoveTargetPosition) {
                robot.depositLeft.setPower(1);
                robot.depositRight.setPower(1);
            } else if (robot.depositLeft.getCurrentPosition() > automatedMoveTargetPosition+50) {
                robot.depositLeft.setPower(-1);
                robot.depositRight.setPower(-1);
            } else {
                robot.depositLeft.setPower(-0.6);
                robot.depositRight.setPower(-0.6);
            }

            if (zeroWasTargetted == true && robot.withinUncertainty(robot.depositLeft.getCurrentPosition(), min, 10)) {
                zeroWasTargetted = false;

            } else if (automationWasSet == true && robot.withinUncertainty(robot.depositLeft.getCurrentPosition(), automatedMoveTargetPosition, 10)) {
                //this.runDeposit(robot, min, "Low", telemetry);
                zeroWasTargetted = true;
                automationWasSet = false;
                automatedMoveTargetPosition = 0;

                controlLatch(robot, "Prime");

                robot.depositLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.depositRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                if(robot.depositLeft.getCurrentPosition() > automatedMoveTargetPosition + 100) {
                    robot.depositLeft.setPower(-1);
                    robot.depositRight.setPower(-1);

                } if(robot.depositLeft.getCurrentPosition() < automatedMoveTargetPosition + 100) {
                    robot.depositLeft.setPower(-0.2);
                    robot.depositRight.setPower(-0.2);
                }
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

    public void changeLatch(RobotHardware robot) {
        if (buttonReleased) {
            buttonReleased = false;
            if(!latchState) {
                robot.latch.setPosition(latchPrime);
                latchState = true;
            } else if(latchState) {
                robot.latch.setPosition(latchClose);
                latchState = false;
            }
        }
    }
}