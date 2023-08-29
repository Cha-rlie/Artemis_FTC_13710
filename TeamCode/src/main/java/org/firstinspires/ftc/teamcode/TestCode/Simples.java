package org.firstinspires.ftc.teamcode.TestCode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class Simples extends LinearOpMode {

    // Define the motors
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        rearLeft = hardwareMap.get(DcMotorEx.class, "RearLeft");
        rearRight = hardwareMap.get(DcMotorEx.class, "RearRight");

        waitForStart();

        while (opModeIsActive()) {
            rearLeft.setPower(-1);
            rearRight.setPower(1);

        }

//        try {
//            frontLeft = hardwareMap.get(DcMotor.class, "FrontLeft");
//        } catch(Exception e) {
//            try {
//                frontRight = hardwareMap.get(DcMotor.class, "FrontRight");
//            } catch(Exception r) {
//                try {
//                    rearLeft = hardwareMap.get(DcMotor.class, "RearLeft");
//                } catch(Exception a) {
//                    try {
//                        rearRight = hardwareMap.get(DcMotor.class, "RearRight");
//                    } catch(Exception b) {
//                        waitForStart();
//                    }
//                }
//            }
//        } finally {
//            waitForStart();
//        }
//
//        while (opModeIsActive()) {
//            try {
//                frontLeft.setPower(1);
//            } catch(Exception w) {
//
//            }
//            try {
//                frontRight.setPower(1);
//            } catch(Exception g) {
//
//            }
//            try {
//                rearLeft.setPower(1);
//            } catch(Exception h) {
//
//            }
//            try {
//                rearRight.setPower(1);
//            } catch(Exception k) {
//
//            }
//        }


    }

}
