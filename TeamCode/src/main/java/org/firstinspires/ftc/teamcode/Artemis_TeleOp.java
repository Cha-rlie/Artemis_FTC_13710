package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes

// Import the necessary FTC modules and classes
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp

public class Artemis_TeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {

        // Initialising the motors up for drive base
        //SampleMecanumDrive driveBase = new SampleMecanumDrive(hardwareMap);

        DcMotorEx UpLeft = hardwareMap.get(DcMotorEx.class, "UpLeft");
        DcMotorEx UpRight = hardwareMap.get(DcMotorEx.class, "UpRight");
        DcMotorEx DownLeft = hardwareMap.get(DcMotorEx.class, "DownLeft");
        DcMotorEx DownRight = hardwareMap.get(DcMotorEx.class, "DownRight");

        // Initialising the motors for the dual-intake system
        DcMotorEx IntakeLeft = hardwareMap.get(DcMotorEx.class, "IntakeLeft");
        DcMotorEx IntakeRight = hardwareMap.get(DcMotorEx.class, "IntakeRight");
        DcMotorEx DepositLeft = hardwareMap.get(DcMotorEx.class, "DepositLeft");
        DcMotorEx DepositRight = hardwareMap.get(DcMotorEx.class, "DepositRight");

        // Initialising the servos
        Servo ClawServo = hardwareMap.get(Servo.class, "ClawServo");

        // Communicating with the driver station
        telemetry.addData("Status", "Motors and Servos Initialised");

        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {

            telemetry.addData("Status", "TeleOp Running");

            // Initialise the input values from first controller for the drive base
            float XInput = (float)(this.gamepad1.right_stick_x*1.1); // Account for imperfect strafing
            float YInput = -this.gamepad1.left_stick_y; // One side needs to be reversed
            float RInput = this.gamepad1.right_stick_x;

            // Initialise the input values from the second controller for the dual intake system
            boolean inputLeftStatus = this.gamepad2.a;
            int inputLeftManual = (int)this.gamepad2.left_stick_x;

            // Returns the largest denominator that the power of the motors must be divided by to keep their original ratio
            double ratioScalingDenominator = Math.max(Math.abs(YInput) + Math.abs(XInput) + Math.abs(RInput), 1);

            // Drive the drive base with mecanum code
            UpLeft.setPower((YInput + XInput + RInput) / ratioScalingDenominator);
            UpRight.setPower((YInput - XInput - RInput) / ratioScalingDenominator);
            DownLeft.setPower((YInput - XInput + RInput) / ratioScalingDenominator);
            DownRight.setPower((YInput + XInput - RInput) / ratioScalingDenominator);

            // Code to test the left intake motor
            IntakeLeft.setPower(inputLeftManual);

        }

    }

}
