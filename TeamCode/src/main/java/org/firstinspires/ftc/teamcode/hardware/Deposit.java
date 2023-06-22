package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.Math;

public class Deposit {
    private static Deposit instance = null;
    public boolean enabled;

    public int encoderError = 5;
    public int max = 3000;
    public int min = 0;
    int highJunction = 2200;
    int midJunction = 1600;

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

    public void init(RobotHardware robot) {

    }

    public void runDeposit(RobotHardware robot, int targetPosition, String mode, Telemetry telemetry) {
        telemetry.addData("mode:", mode);
        telemetry.addData("targetPosition:", targetPosition);
        telemetry.addData("automatedmovetargetwas set", automatedMoveTargetPosition);
        telemetry.addData("automaiton", automationWasSet);
        telemetry.addData("zero", zeroWasTargetted);
        if (mode == "Manual") {
            // Stop the automation
            automationWasSet = false;

            robot.depositLeft.setTargetPosition(targetPosition);
            robot.depositRight.setTargetPosition(targetPosition);
            robot.depositLeft.setPower(0.6);
            robot.depositRight.setPower(0.6);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            int avg = (robot.depositLeft.getCurrentPosition() + robot.depositLeft.getCurrentPosition()) / 2;
            telemetry.addData("Deposit: ", avg);
        } else if (mode == "High" || mode == "Medium" || mode == "Low" || automationWasSet || zeroWasTargetted) {
            // Change the variable to demonstrate that an automation command was just set
            if (mode == "High" || mode == "Medium" || mode == "Low") {automationWasSet = true;}

            // Set the desired automated values
            if (mode == "High") {
                automatedMoveTargetPosition = highJunction;} else if (mode == "Medium") {
                automatedMoveTargetPosition = midJunction;} else if (mode == "Low") {
                automatedMoveTargetPosition = min;}

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
            }

        } else if (!automationWasSet && !zeroWasTargetted){

            /*robot.depositLeft.setTargetPosition((this.getDepositPosition(robot)));
            robot.depositRight.setTargetPosition((this.getDepositPosition(robot)));
            robot.depositLeft.setPower(1);
            robot.depositRight.setPower(1);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);*/

            robot.depositLeft.setPower(0);
            robot.depositRight.setPower(0);

        }
    }

    // Function that returns the average position of both deposits
    public int getDepositPosition(RobotHardware robot) {
        return (robot.depositLeft.getCurrentPosition()+robot.depositRight.getCurrentPosition())/2;
    }

    // Function that controls the latch servo
    public void controlLatch(RobotHardware robot, String mode, Telemetry telemetry) {
        if (mode == "Open") {
            robot.latch.setPosition(0);
        }
        else if (mode == "Prime") {
            robot.latch.setPosition(1);
        }
        else if (mode == "Close") {
            robot.latch.setPosition(1);
        }
    }

    /*public void depositHigh(RobotHardware robot) {
        AutomatedMove1 = true;
        AutomatedMoveTargetPosition = HighJunction;
        robot.DepositLeft.setTargetPosition(AutomatedMoveTargetPosition);
        robot.DepositRight.setTargetPosition(AutomatedMoveTargetPosition);
        robot.DepositLeft.setPower(1);
        robot.DepositRight.setPower(1);

        while(!(Math.abs(robot.DepositRight.getCurrentPosition() - HighJunction) <= 10)) {

            robot.DepositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.DepositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        }

        AutomatedMoveTargetPosition = Min;
        robot.DepositLeft.setTargetPosition(AutomatedMoveTargetPosition);
        robot.DepositRight.setTargetPosition(AutomatedMoveTargetPosition);
        robot.DepositLeft.setPower(1);
        robot.DepositRight.setPower(1);

    }

    public void depositMid(RobotHardware robot) {
        AutomatedMove1 = true;
        AutomatedMoveTargetPosition = MidJunction;
        robot.DepositLeft.setTargetPosition(AutomatedMoveTargetPosition);
        robot.DepositRight.setTargetPosition(AutomatedMoveTargetPosition);
        robot.DepositLeft.setPower(1);
        robot.DepositRight.setPower(1);
    }*/


}
