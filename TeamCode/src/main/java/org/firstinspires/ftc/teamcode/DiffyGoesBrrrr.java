package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp

public class DiffyGoesBrrrr extends LinearOpMode {
    public DcMotorEx motor1;
    public DcMotorEx motor2;

    @Override
    public void runOpMode() throws InterruptedException {
        motor1 = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        motor2 = hardwareMap.get(DcMotorEx.class, "FrontRight");

        waitForStart();

        motor1.setPower(0.1);
        motor2.setPower(0.1);
    }
}
