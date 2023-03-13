package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class Automated_Movement_Functions extends LinearOpMode{

    @Override
    public void runOpMode() {

    }

    public void driveBaseMovementX() {}

    public void manualViperTest(int viperTickChange, DcMotorEx viperMotor) {

        int newViperTickTarget = viperMotor.getCurrentPosition() + viperTickChange;

        viperMotor.setTargetPosition(newViperTickTarget);

        viperMotor.setVelocity(viperTickChange*100);

    }

}
