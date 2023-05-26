package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Deposit {
    private static Deposit instance = null;
    public boolean enabled;

    public int encoderError = 5;
    public int Max = 3500;
    public int Min = 0;
    int HighJunction = 2000;
    int MidJunction = 1000;

    public boolean AutomatedMove1 = false;
    public boolean AutomatedMove2 = false;
    public int AutomatedMovePosition;

    public static Deposit getInstance() {
        if (instance == null) {
            instance = new Deposit();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot) {

    }

    public void runDeposit(RobotHardware robot, int targetPosition, Telemetry telemetry) {
        if(!AutomatedMove1) {
            robot.DepositLeft.setTargetPosition(targetPosition);
            robot.DepositRight.setTargetPosition(targetPosition);
            robot.DepositLeft.setPower(1);
            robot.DepositRight.setPower(1);
            robot.DepositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.DepositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            int avg = (robot.DepositLeft.getCurrentPosition()+robot.DepositLeft.getCurrentPosition())/2;
            telemetry.addData("Deposit: ", avg);
        }
    }

    public void runDeposit(RobotHardware robot, int[] targetPosition, Telemetry telemetry) {
        if(!AutomatedMove1) {
            robot.DepositLeft.setTargetPosition(targetPosition[0]);
            robot.DepositRight.setTargetPosition(targetPosition[1]);
            robot.DepositLeft.setPower(1);
            robot.DepositRight.setPower(1);
            robot.DepositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.DepositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            int avg = (robot.DepositLeft.getCurrentPosition()+robot.DepositLeft.getCurrentPosition())/2;
            telemetry.addData("Deposit: ", avg);
        }
    }

    public int[] getDepositPosition(RobotHardware robot) {
        int[] positions = new int[2];
        positions[0] = robot.DepositLeft.getCurrentPosition();
        positions[1] = robot.DepositRight.getCurrentPosition();

        return positions;
    }
    public void depositHigh(RobotHardware robot) {
        AutomatedMove1 = true;
        AutomatedMovePosition = HighJunction;
        robot.DepositLeft.setTargetPosition(AutomatedMovePosition);
        robot.DepositRight.setTargetPosition(AutomatedMovePosition);
        robot.DepositLeft.setPower(1);
        robot.DepositRight.setPower(1);
    }

    public void depositMid(RobotHardware robot) {
        AutomatedMove1 = true;
        AutomatedMovePosition = MidJunction;
        robot.DepositLeft.setTargetPosition(AutomatedMovePosition);
        robot.DepositRight.setTargetPosition(AutomatedMovePosition);
        robot.DepositLeft.setPower(1);
        robot.DepositRight.setPower(1);
    }


}
