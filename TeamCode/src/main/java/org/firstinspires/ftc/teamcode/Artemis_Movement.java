package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;
import java.util.Collections;

public class Artemis_Movement extends LinearOpMode {

    @Override
    public void runOpMode() {

    }

    public void controlHubMotorMovement (boolean decreaseSpeed, boolean increaseSpeed, double xDirection, double yDirection, double spinDirection, DcMotor UpLeft, DcMotor UpRight, DcMotor DownLeft, DcMotor DownRight) {

        // Declaring the speed modifier
        double speedModifier = 1.8;

        // Creating movement variables
        double UL_movement = 0;
        double UR_movement = 0;
        double DL_movement = 0;
        double DR_movement = 0;

        // Changing the speed modifier
        if (decreaseSpeed) {
            speedModifier = 3;
        } else if (increaseSpeed) {
            speedModifier = 1;
        }

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
        UpLeft.setPower(-UL_movement/speedModifier);
        DownLeft.setPower(-DL_movement/speedModifier);
        UpRight.setPower(UR_movement/speedModifier);
        DownRight.setPower(DR_movement/speedModifier);

    }

    int maxArmPos;
    int minArmPos;
    boolean runtoMax = false;
    boolean runToMin = false;

    public float linearLiftMovement (boolean setMax, boolean setMin, double goToMax, double goToMin, float RoboArmNum, double linearSpeed, DcMotorEx LinearLeft, DcMotorEx LinearRight) {

        RoboArmNum -= linearSpeed * 4;

        LinearLeft.setTargetPosition(Math.round(RoboArmNum));
        LinearRight.setTargetPosition(Math.round(RoboArmNum));

        if(setMax) {
            maxArmPos = (LinearLeft.getCurrentPosition() + LinearRight.getCurrentPosition())/2;
            runtoMax = true;
        } else if (setMin) {
            minArmPos = (LinearLeft.getCurrentPosition() + LinearRight.getCurrentPosition())/2;
            runToMin = true;
        }

        // If the max and min values have been set
        if(runToMin && runtoMax) {
            if(goToMax > 0.5) {
                RoboArmNum = maxArmPos;

                // Make zoom zoom
                LinearLeft.setVelocity(1400);
                LinearRight.setVelocity(1400);

            } else if (goToMin > 0.5) {
                RoboArmNum = minArmPos;

                // Make not so zoom zoom so claw doesn't die
                LinearLeft.setVelocity(1000);
                LinearRight.setVelocity(1000);
            }
        }

        telemetry.addData("Max Position", maxArmPos);
        telemetry.addData("Min Position", minArmPos);
        telemetry.update();

        return RoboArmNum;

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

    public void clawMovement (boolean ClawPositionA, boolean ClawPositionB, Servo ClawServo) {

        // Claw Code
        if(ClawPositionA) {
            // open
            ClawServo.setPosition(0.2);
        } else if (ClawPositionB) {
            // closed
            ClawServo.setPosition(0.1);
        }

    }

    int modeNumber = 0;

    public int lightChange (boolean decreaseMode, boolean increaseMode, RevBlinkinLedDriver LightDriver, int current) {
        if (decreaseMode) {
            modeNumber = (modeNumber-1)%5;
        } else if (increaseMode) {
            modeNumber += (modeNumber+1)%5;
        }

        switch (modeNumber) {
            case 0:
                LightDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE);
            case 1:
                LightDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.LARSON_SCANNER_GRAY);
            case 2:
                LightDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.GOLD);
            case 3:
                LightDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_RED);
            case 4:
                LightDriver.setPattern(RevBlinkinLedDriver.BlinkinPattern.STROBE_WHITE);
        }

//        telemetry.addData("Mode:", modeNumber);
//        telemetry.addData("TriggerLeft:",triggerLeft);
//        telemetry.addData("TriggerRight:",triggerRight);
//        telemetry.addData("Current:", current);
//        telemetry.update();

        return modeNumber;

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

}
