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
    public int max = 3500;
    public int min = 0;
    int highJunction = 2000;
    int midJunction = 1000;

    public int automatedMoveTargetPosition;

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
        if (mode == "Manual") {
            robot.depositLeft.setTargetPosition(targetPosition);
            robot.depositRight.setTargetPosition(targetPosition);
            robot.depositLeft.setPower(1);
            robot.depositRight.setPower(1);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            int avg = (robot.depositLeft.getCurrentPosition() + robot.depositLeft.getCurrentPosition()) / 2;
            telemetry.addData("Deposit: ", avg);
        } else {
            if (mode == "High") {
                automatedMoveTargetPosition = highJunction;} else if (mode == "Low") {
                automatedMoveTargetPosition = midJunction;}

            robot.depositLeft.setPower(1);
            robot.depositRight.setPower(1);
            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            if (Math.abs(robot.depositRight.getCurrentPosition() - automatedMoveTargetPosition) <= 10) {
                automatedMoveTargetPosition = min;
                robot.depositLeft.setTargetPosition(automatedMoveTargetPosition);
                robot.depositRight.setTargetPosition(automatedMoveTargetPosition);
                robot.depositLeft.setPower(1);
                robot.depositRight.setPower(1);
            }

        }
    }

    public int[] getDepositPosition(RobotHardware robot) {
        int[] positions = new int[2];
        positions[0] = robot.depositLeft.getCurrentPosition();
        positions[1] = robot.depositRight.getCurrentPosition();

        return positions;
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
