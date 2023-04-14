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

        DcMotorEx FrontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        DcMotorEx FrontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        DcMotorEx RearLeft = hardwareMap.get(DcMotorEx.class, "RearLeft");
        DcMotorEx RearRight = hardwareMap.get(DcMotorEx.class, "RearRight");

        // Initialising the motors for the dual-intake system
        DcMotorEx IntakeLeft = hardwareMap.get(DcMotorEx.class, "IntakeLeft");
        DcMotorEx IntakeRight = hardwareMap.get(DcMotorEx.class, "IntakeRight");
        DcMotorEx DepositLeft = hardwareMap.get(DcMotorEx.class, "DepositLeft");
        DcMotorEx DepositRight = hardwareMap.get(DcMotorEx.class, "DepositRight");

        // Initialising the servos
        //Servo Claw = hardwareMap.get(Servo.class, "Claw");
        //Servo SpinClaw = hardwareMap.get(Servo.class,"SpinClaw");
        Servo RotateClaw = hardwareMap.get(Servo.class, "RotateClaw");
        Servo V4B_1 = hardwareMap.get(Servo.class, "V4B_1");
        Servo V4B_2 = hardwareMap.get(Servo.class, "V4B_2");
        //Servo Latch = hardwareMap.get(Servo.class, "Latch");

        // Communicating with the driver station
        telemetry.addData("Status", "Motors and Servos Initialised");

        // Wait for the driver to click the "Play" button before proceeding
        waitForStart();

        // Run until the "Stop" button is pressed
        while (opModeIsActive()) {

            telemetry.addData("Status", "TeleOp Running");

            // Initialise the input values from first controller for the drive base
            float xInput = (float)(this.gamepad1.right_stick_x*1.1); // Account for imperfect strafing
            float yInput = -this.gamepad1.right_stick_y; // One side needs to be reversed
            float rInput = this.gamepad1.left_stick_x;

            // Initialise the input values from the second controller for the dual intake system
            float intakeInput = this.gamepad2.left_stick_y;
            float V4BInput = this.gamepad2.right_stick_y;
            boolean depositInputUp = this.gamepad2.dpad_up;
            boolean depositInputDown = this.gamepad2.dpad_down;
            boolean mediumJunctionScore = this.gamepad2.dpad_left;
            boolean highJunctionScore = this.gamepad2.dpad_right;
            boolean clawInput = this.gamepad2.a;
            boolean transportCone = this.gamepad2.b;
            boolean rotateClaw = this.gamepad2.x;
            boolean spinClaw = this.gamepad2.y;


            // Returns the largest denominator that the power of the motors must be divided by to keep their original ratio
            double ratioScalingDenominator = Math.max(Math.abs(yInput) + Math.abs(xInput) + Math.abs(rInput), 1);

<<<<<<< HEAD
            // Drive the drive base with mecanum code\
            UpLeft.setPower((yInput + xInput + rInput) / ratioScalingDenominator);
            UpRight.setPower((yInput - xInput - rInput) / ratioScalingDenominator);
            DownLeft.setPower((yInput - xInput + rInput) / ratioScalingDenominator);
            DownRight.setPower((yInput + xInput - rInput) / ratioScalingDenominator);
=======
            // Drive the drive base with mecanum code
            FrontLeft.setPower((yInput + xInput + rInput) / ratioScalingDenominator);
            FrontRight.setPower((yInput - xInput - rInput) / ratioScalingDenominator);
            RearLeft.setPower((yInput - xInput + rInput) / ratioScalingDenominator);
            RearRight.setPower((yInput + xInput - rInput) / ratioScalingDenominator);
>>>>>>> 4946d791514605b27cdcd58186ade45e5cd37cb2

            // MANUALLY move the intake and deposit systems
            IntakeLeft.setPower(intakeInput / 2);
            IntakeRight.setPower(intakeInput / 2);

            if (depositInputUp && depositInputDown) {}
            else if (depositInputUp) {
                DepositLeft.setPower(0.5);
                DepositRight.setPower(0.5);
            } else if (depositInputDown) {
                DepositLeft.setPower(-0.5);
                DepositRight.setPower(-0.5);
            }

            // Print out Encoder Values to the driver station
            telemetry.addData("Intake",(int)((IntakeLeft.getCurrentPosition()+IntakeRight.getCurrentPosition())/2));
            telemetry.addData("Deposit",(int)((DepositLeft.getCurrentPosition()+DepositRight.getCurrentPosition())/2));

            // MANUALLY test servos
            if (clawInput) {
                //Claw.setPosition(Claw.getPosition()+0.2);
            }
            /*if (spinClaw) {
                SpinClaw.setPosition(1);
            }*/
            if (rotateClaw) {
                RotateClaw.setPosition(0.5);
            }

            V4B_1.setPosition(V4B_1.getPosition()+(V4BInput/10));
            V4B_2.setPosition(V4B_2.getPosition()-(V4BInput/10));

        }

    }

}
