package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes

// Import the necessary FTC modules and classes
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp

public class Artemis_TeleOp extends LinearOpMode {

    @Override
    public void runOpMode() {
        boolean GroundIntake = false; // Is the CLaw facing the ground?
        boolean buttonIsReleased = true; // Handling debounce issues
        boolean AbuttonIsReleased = true;
        boolean ClawState = true; // True = open

        double V4B_1_HomePos = 0.83; // Position where V4B is ready for intaking
        double V4B_2_HomePos = 0.14;

        double RotateClaw_HomePos = 0.631; // Position where RotateClaw is ready for intaking

        double ClosedClawPos = 0.116; // Position where claw is closed
        double OpenClawPos = 0.2994; // Position where claw is open

        double ClawFowardPos = 0;
        double ClawBackwardsPos = 0.7;

        int IntakeHome = 955; // Fully contracted position
        int IntakeOut = -1050; // Fully extended position


        // Initialising the motors up for drive base

        DcMotorEx FrontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        DcMotorEx FrontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        DcMotorEx RearLeft = hardwareMap.get(DcMotorEx.class, "RearLeft");
        RearLeft.setDirection(DcMotor.Direction.REVERSE);
        DcMotorEx RearRight = hardwareMap.get(DcMotorEx.class, "RearRight");


        // Initialising the motors for the dual-intake system

        DcMotorEx IntakeLeft = hardwareMap.get(DcMotorEx.class, "IntakeLeft");
        IntakeLeft.setDirection(DcMotorEx.Direction.REVERSE);
        DcMotorEx IntakeRight = hardwareMap.get(DcMotorEx.class, "IntakeRight");

        IntakeLeft.setTargetPosition(IntakeHome);
        IntakeRight.setTargetPosition(IntakeHome);
        IntakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        IntakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);


         /*
        DcMotorEx DepositLeft = hardwareMap.get(DcMotorEx.class, "DepositLeft");
        DcMotorEx DepositRight = hardwareMap.get(DcMotorEx.class, "DepositRight");
*/

        // Initialising the servos
        Servo Claw = hardwareMap.get(Servo.class, "Claw");
        Servo SpinClaw = hardwareMap.get(Servo.class,"SpinClaw");
        Servo RotateClaw = hardwareMap.get(Servo.class, "RotateClaw");
        Servo V4B_1 = hardwareMap.get(Servo.class, "V4B_1");
        Servo V4B_2 = hardwareMap.get(Servo.class, "V4B_2");
        //Servo Latch = hardwareMap.get(Servo.class, "Latch");

        // Default Positions
        V4B_1.setPosition(V4B_1_HomePos);
        V4B_2.setPosition(V4B_2_HomePos);
        RotateClaw.setPosition(RotateClaw_HomePos);
        Claw.setPosition(ClosedClawPos);
        // SpinClaw.setPosition(ClawFowardPos);


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


            // Returns the largest denominator that the power of the motors must be divided by to keep their original ratio
            double ratioScalingDenominator = Math.max(Math.abs(yInput) + Math.abs(xInput) + Math.abs(rInput), 1);


            // Drive the drive base with mecanum code
            FrontLeft.setPower((yInput + xInput + rInput) / ratioScalingDenominator);
            FrontRight.setPower((yInput - xInput - rInput) / ratioScalingDenominator);
            RearLeft.setPower((yInput - xInput + rInput) / ratioScalingDenominator);
            RearRight.setPower((yInput + xInput - rInput) / ratioScalingDenominator);

            // MANUALLY move the intake and deposit systems

            IntakeLeft.setPower(intakeInput/2);
            IntakeRight.setPower(intakeInput/2);
 /*
            if (depositInputUp && depositInputDown) {}
            else if (depositInputUp) {
                DepositLeft.setPower(0.5);
                DepositRight.setPower(0.5);
            } else if (depositInputDown) {
                DepositLeft.setPower(-0.5);
                DepositRight.setPower(-0.5);
            }
*/

            // Print out Encoder Values to the driver station
            telemetry.addData("IntakeL", IntakeLeft.getCurrentPosition());
            telemetry.addData("IntakeR", IntakeRight.getCurrentPosition());
           // telemetry.addData("Deposit",(int)((DepositLeft.getCurrentPosition()+DepositRight.getCurrentPosition())/2));

//            if(gamepad2.left_stick_y > 0.5) {
//                IntakeLeft.setTargetPosition(IntakeHome);
//                IntakeRight.setTargetPosition(IntakeHome);
//            } else if(gamepad2.left_stick_y < -0.5) {
//                IntakeLeft.setTargetPosition(IntakeOut);
//                IntakeRight.setTargetPosition(IntakeOut);
//            } else {
//                IntakeLeft.setTargetPosition(IntakeLeft.getCurrentPosition());
//                IntakeRight.setTargetPosition(IntakeRight.getCurrentPosition());
//            }
//


            // Handling debounce issues
            if(this.gamepad2.x) {
                if (buttonIsReleased) {
                    buttonIsReleased = false;
                    if(!GroundIntake) {
                        RotateClaw.setPosition(0.860);
                        GroundIntake = true;
                    } else if(GroundIntake) {
                        RotateClaw.setPosition(0.631);
                        GroundIntake = false;
                    }
                }
            } else {
                buttonIsReleased = true;
            }

            if(this.gamepad2.a) {
                if (AbuttonIsReleased) {
                    AbuttonIsReleased = false;
                    if(!ClawState) {
                        Claw.setPosition(OpenClawPos);
                        ClawState = true;
                    } else if(ClawState) {
                        Claw.setPosition(ClosedClawPos);
                        ClawState = false;
                    }
                }
            } else {
                AbuttonIsReleased = true;
            }


            if(this.gamepad2.y) {
                V4B_1.setPosition(V4B_1_HomePos);
                V4B_2.setPosition(V4B_2_HomePos);
            }

            V4B_1.setPosition(V4B_1.getPosition()+(V4BInput*2/200));
            V4B_2.setPosition(V4B_2.getPosition()-(V4BInput*2/200));
            telemetry.addData("V4B_1", V4B_1.getPosition());
            telemetry.addData("V4B_2", V4B_2.getPosition());
            telemetry.addData("RotateClaw", RotateClaw.getPosition());
            telemetry.addData("Claw", Claw.getPosition());
            telemetry.addData("SpinClaw", SpinClaw.getPosition());

            // Update the telemetry's information screen
            telemetry.update();

        }

    }

}
