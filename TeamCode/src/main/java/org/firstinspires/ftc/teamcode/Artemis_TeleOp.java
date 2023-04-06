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
            float xInput = (float)(this.gamepad1.right_stick_x*1.1); // Account for imperfect strafing
            float yInput = -this.gamepad1.left_stick_y; // One side needs to be reversed
            float rInput = this.gamepad1.right_stick_x;

            // Initialise the input values from the second controller for the dual intake system
            //boolean inputLeftStatus = this.gamepad2.a;
            boolean intakeDepositStatus = true;
            float leftIntakeInput = this.gamepad2.left_stick_y;
            float rightIntakeInput = this.gamepad2.right_stick_y;
            float leftDepositInput = this.gamepad2.left_stick_y;
            float rightDepositInput = this.gamepad2.right_stick_y;


            // Returns the largest denominator that the power of the motors must be divided by to keep their original ratio
            double ratioScalingDenominator = Math.max(Math.abs(yInput) + Math.abs(xInput) + Math.abs(rInput), 1);

            // Drive the drive base with mecanum code
            UpLeft.setPower((yInput + xInput + rInput) / ratioScalingDenominator);
            UpRight.setPower((yInput - xInput - rInput) / ratioScalingDenominator);
            DownLeft.setPower((yInput - xInput + rInput) / ratioScalingDenominator);
            DownRight.setPower((yInput + xInput - rInput) / ratioScalingDenominator);

            // Code to test the MANUALLY test the intake and deposit systems
            if (this.gamepad2.a) {
                intakeDepositStatus = true;
            }
            if (this.gamepad2.b) {
                intakeDepositStatus = false;
            }

            if (intakeDepositStatus) {
                IntakeLeft.setPower(leftIntakeInput);
                IntakeRight.setPower(rightIntakeInput);
            }
            else {
                DepositLeft.setPower(leftDepositInput);
                DepositRight.setPower(rightDepositInput);
            }

        }

    }

}
