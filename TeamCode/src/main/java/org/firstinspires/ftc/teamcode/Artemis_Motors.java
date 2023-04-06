package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Artemis_Motors extends LinearOpMode {

    // Creating the IMU
    private BNO055IMU imu;

    // Creating the important variables
    Orientation lastAngles = new Orientation();
    double globalAngle, power = 0.30, correction;

    @Override
    public void runOpMode() {

        // Setting up the IMU
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        // Setting up the IMU's parameters
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled = false;

        // Resetting the IMU
        imu.initialize(parameters);

        // Calibrating the IMU's gyro
        while (!isStopRequested() && !imu.isGyroCalibrated()) {
            sleep(50);
            idle();
        }

    }

    private void resetAngle() {

        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        globalAngle = 0;
    }

    private double getAngle() {
        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;
        if (deltaAngle < -180) {
            deltaAngle += 360;
        }
        else if (deltaAngle > 180) {
            deltaAngle -= 360;
        }

        globalAngle += deltaAngle;

        lastAngles = angles;

        return globalAngle;

    }

    private void rotate(int degrees, double power, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {
        double leftPower, rightPower;

        // Restart IMU movement tracking.
        resetAngle();

        if (degrees < 0)
        {   // turn right.
            leftPower = power;
            rightPower = -power;
        }
        else if (degrees > 0)
        {   // turn left.
            leftPower = -power;
            rightPower = power;
        }
        else return;

        // set power to rotate.
        UpLeft.setPower(leftPower);
        UpRight.setPower(rightPower);
        DownLeft.setPower(leftPower);
        DownRight.setPower(rightPower);

        // rotate until turn is completed.
        if (degrees < 0)
        {
            // On right turn we have to get off zero first.
            while (opModeIsActive() && getAngle() == 0) {}

            while (opModeIsActive() && getAngle() > degrees) {}
        }
        else    // left turn.
            while (opModeIsActive() && getAngle() < degrees) {}

        // turn the motors off.
        UpLeft.setPower(0);
        UpRight.setPower(0);
        DownLeft.setPower(0);
        DownRight.setPower(0);

        // wait for rotation to stop.
        sleep(1000);

        // reset angle tracking on new heading.
        resetAngle();

    }

    public void controlHubMotorMovement (double xDirection, double yDirection, double spinDirection, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        // Creating movement variables
        double UL_movement = 0;
        double UR_movement = 0;
        double DL_movement = 0;
        double DR_movement = 0;

        // Code to account for gamepad values asking the robot to move in the Y axis
        if (yDirection > 0.1 || yDirection < -0.1) {
            UL_movement = UR_movement = DL_movement = DR_movement = yDirection;
        }

        // Code to account for gamepad values asking the robot to move in the X axis
        if (xDirection < -0.1) { // The robot has been asked to strafe left
            UL_movement += -xDirection;
            UR_movement += xDirection;
            DL_movement += xDirection;
            DR_movement += -xDirection;
        } else if (xDirection > 0.1) { // The robot has been asked to strafe right
            UL_movement += -xDirection;
            UR_movement += xDirection;
            DL_movement += xDirection;
            DR_movement += -xDirection;
        }

        // Code to account for gamepad values asking the robot to rotate
        if (spinDirection < -0.1) { // // Code to account for gamepad values asking the robot to rotate left
            UL_movement += spinDirection;
            UR_movement += -spinDirection;
            DL_movement += spinDirection;
            DR_movement += -spinDirection;
        }
        else if (spinDirection > 0.1) { // Code to account for gamepad values asking the robot to rotate right
            UL_movement += spinDirection;
            UR_movement += -spinDirection;
            DL_movement += spinDirection;
            DR_movement += -spinDirection;
        }

        // Code to normalise all values to have a maximum of one
        double greatest_value = (double) Collections.max(Arrays.asList(Math.abs(UL_movement),Math.abs(UR_movement),Math.abs(DL_movement),Math.abs(DR_movement)));

        UL_movement = UL_movement / greatest_value;
        UR_movement = UR_movement / greatest_value;
        DL_movement = DL_movement / greatest_value;
        DR_movement = DR_movement / greatest_value;

        telemetry.addData("UL_movement",UL_movement);
        telemetry.addData("UR_movement",UR_movement);
        telemetry.addData("DL_movement",DL_movement);
        telemetry.addData("DR_movement",DR_movement);
        telemetry.update();

        // Run the motors
        UpLeft.setPower(-UL_movement);
        DownLeft.setPower(-DL_movement);
        UpRight.setPower(UR_movement);
        DownRight.setPower(DR_movement);

    }

    /*public void controlHubMotorMovement (double xDirection, double yDirection, boolean leftSpin, boolean rightSpin, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        //Nested if-loop for gamepad1:
        //If left joystick pushed to LEFT:
        if (xDirection == -1) {
            UpLeft.setPower(-1);
            UpRight.setPower(-1);
            DownLeft.setPower(1);
            DownRight.setPower(1);
            // If left joystick pushed to RIGHT:
        } else if (xDirection == 1) {
            UpLeft.setPower(1);
            UpRight.setPower(1);
            DownLeft.setPower(-1);
            DownRight.setPower(-1);
            // If right joystick pushed UP or DOWN:
        } else if (yDirection != 0) {
            UpLeft.setPower(-yDirection);
            UpRight.setPower(yDirection);
            DownLeft.setPower(-yDirection);
            DownRight.setPower(yDirection);
            // If left trigger pushed:
        } else if (leftSpin) {
            UpLeft.setPower(1);
            DownLeft.setPower(1);
            UpRight.setPower(1);
            DownRight.setPower(1);
            telemetry.addData("LeftSpinnnnnn", "");
        } else if (rightSpin) {
            UpLeft.setPower(-1);
            DownLeft.setPower(-1);
            UpRight.setPower(-1);
            DownRight.setPower(-1);
            telemetry.addData("Rightspinnn","");
        } else {
            UpLeft.setPower(0);
            UpRight.setPower(0);
            DownLeft.setPower(0);
            DownRight.setPower(0);
        }

    }*/

    public void clawMovement (boolean ClawPositionA, boolean ClawPositionB, CRServo ClawServo) {

        if (ClawPositionB) {
            ClawServo.setPower(0.7);
            sleep(250);
        } else if (ClawPositionA) {
            ClawServo.setPower(-0.7);
            sleep(250);
        }

    }

    public int[] linearLiftMovement (double linearSpeed,int leftEncoderTargetPosition,int rightEncoderTargetPosition,DcMotor LinearLeft,DcMotor LinearRight) {

        //If-loop for linear lift motor.
        if(linearSpeed > 0) {
            leftEncoderTargetPosition += linearSpeed*10;
            rightEncoderTargetPosition -= linearSpeed*10;
            LinearLeft.setPower(-linearSpeed);
            LinearRight.setPower(linearSpeed);
        }
        if(linearSpeed < 0) {
            leftEncoderTargetPosition += linearSpeed*10;
            rightEncoderTargetPosition -= linearSpeed*10;
            LinearLeft.setPower(-linearSpeed);
            LinearRight.setPower(linearSpeed);
        }

        LinearLeft.setTargetPosition(leftEncoderTargetPosition);
        LinearRight.setTargetPosition(rightEncoderTargetPosition);

        LinearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LinearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        LinearLeft.setPower(0);
        LinearRight.setPower(0);

        int[] encoderTargetPositions = new int[2];
        encoderTargetPositions[0] = leftEncoderTargetPosition;
        encoderTargetPositions[1] = rightEncoderTargetPosition;

        return encoderTargetPositions;
    }

    public void spoolMotorMovement (double spoolSpeed,DcMotor SpoolMotor) {

        if (spoolSpeed >= 0.2) {
            SpoolMotor.setPower(spoolSpeed);
        }
        else if (spoolSpeed <= -0.2) {
            SpoolMotor.setPower(spoolSpeed);
        }
        else {
            SpoolMotor.setPower(0);
        }

    }

    public void auto_move (String type, float amount, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        // Reset tick counts on motors
        UpLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        UpRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DownLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DownRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Defining constants
        int TICKS_PER_REV = 1120;
        float WHEEL_CIRCUMFERENCE = 20; // To be revised
        float TICKS_PER_CM = TICKS_PER_REV / WHEEL_CIRCUMFERENCE;
        int TICKS_TO_TRAVEL = Math.round(TICKS_PER_CM*amount);

        if (type == "forwards") {
            UpLeft.setTargetPosition(TICKS_TO_TRAVEL);
            UpRight.setTargetPosition(TICKS_TO_TRAVEL);
            DownLeft.setTargetPosition(TICKS_TO_TRAVEL);
            DownRight.setTargetPosition(TICKS_TO_TRAVEL);
        }
        else if (type == "backwards") {
            UpLeft.setTargetPosition(-TICKS_TO_TRAVEL);
            UpRight.setTargetPosition(-TICKS_TO_TRAVEL);
            DownLeft.setTargetPosition(-TICKS_TO_TRAVEL);
            DownRight.setTargetPosition(-TICKS_TO_TRAVEL);
        }
        else if (type == "slide_left") {
            UpLeft.setTargetPosition(TICKS_TO_TRAVEL);
            UpRight.setTargetPosition(-TICKS_TO_TRAVEL);
            DownLeft.setTargetPosition(-TICKS_TO_TRAVEL);
            DownRight.setTargetPosition(TICKS_TO_TRAVEL);
        }
        else if (type == "slide_right") {
            UpLeft.setTargetPosition(-TICKS_TO_TRAVEL);
            UpRight.setTargetPosition(TICKS_TO_TRAVEL);
            DownLeft.setTargetPosition(TICKS_TO_TRAVEL);
            DownRight.setTargetPosition(-TICKS_TO_TRAVEL);
        }
        else if (type == "rotate_left") {
            rotate(Math.round(amount),0.5,UpLeft, UpRight, DownLeft, DownRight);
            return;
        }
        else if (type == "rotate_right") {
            rotate(Math.round(-amount),0.5,UpLeft, UpRight, DownLeft, DownRight);
            return;
        }

        UpLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        UpRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        DownLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        DownRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        UpLeft.setPower(0.5);
        UpRight.setPower(0.5);
        DownLeft.setPower(0.5);
        DownRight.setPower(0.5);

    }

    public void auto_slide(String direction, int wanted_time, double power, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        if (direction == "right") {
            UpLeft.setPower(-power);
            UpRight.setPower(-power);
            DownLeft.setPower(power);
            DownRight.setPower(power);
            sleep(wanted_time*1000);
        }
        else if (direction == "left") {
            UpLeft.setPower(power);
            UpRight.setPower(power);
            DownLeft.setPower(-power);
            DownRight.setPower(-power);
            sleep(wanted_time*1000);
        }
    }

    public void auto_move_y (int wanted_time, double power, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        UpLeft.setPower(-power);
        UpRight.setPower(power);
        DownLeft.setPower(-power);
        DownRight.setPower(power);
        sleep(wanted_time*1000);

    }

    public void auto_movement (String type, double power, long amount, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        if (type == "y_move") {
            UpLeft.setPower(-power);
            UpRight.setPower(power);
            DownLeft.setPower(-power);
            DownRight.setPower(power);
            sleep(amount*1000);
        }
        else if (type == "x_move_left") {
            UpLeft.setPower(power);
            UpRight.setPower(power);
            DownLeft.setPower(-power);
            DownRight.setPower(-power);
            sleep(amount*1000);
        }
        else if (type == "x_move_right") {
            UpLeft.setPower(-power);
            UpRight.setPower(-power);
            DownLeft.setPower(power);
            DownRight.setPower(power);
            sleep(amount * 1000);
        }
        else if (type == "rotate_left") {
            rotate(Math.round(amount),0.5,UpLeft, UpRight, DownLeft, DownRight);
        }
        else if (type == "rotate_right") {
            rotate(Math.round(-amount),0.5,UpLeft, UpRight, DownLeft, DownRight);
        }

    }

}
