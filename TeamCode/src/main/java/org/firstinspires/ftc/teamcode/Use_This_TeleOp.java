package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp

public class Use_This_TeleOp extends LinearOpMode {

    // Creating the control hub motors.
    private DcMotor UpLeft;
    private DcMotor UpRight;
    private DcMotor DownLeft;
    private DcMotor DownRight;

    // Creating the expansion hub motors.
    private DcMotorEx LinearLeft;
    private DcMotorEx LinearRight;
    private DcMotorEx SpoolMotor;

    // Creating the servos.
    private Servo ClawServo;

    // Creating the BLINKEN LED Driver
    private RevBlinkinLedDriver LightDriver;

    @Override
    public void runOpMode() {

        LynxModule ControlHub = hardwareMap.get(LynxModule.class, "Control Hub");

        // Setting the motors up for control hub.
        UpLeft = hardwareMap.get(DcMotor.class, "UpLeft");
        UpRight = hardwareMap.get(DcMotor.class, "UpRight");
        DownLeft = hardwareMap.get(DcMotor.class, "DownLeft");
        DownRight = hardwareMap.get(DcMotor.class, "DownRight");

        // Setting up the expansion hub's motors.
        LinearLeft = hardwareMap.get(DcMotorEx.class, "LinearLeft");
        LinearRight = hardwareMap.get(DcMotorEx.class,"LinearRight");
        SpoolMotor = hardwareMap.get(DcMotorEx.class, "SpoolMotor");

        // Setting the servos up.
        ClawServo = hardwareMap.get(Servo.class, "ClawServo");

        // Setting up the BLINKIN LED Driver
        LightDriver = hardwareMap.get(RevBlinkinLedDriver.class, "LightDriver");

        LinearLeft.setTargetPosition(0);
        LinearRight.setTargetPosition(0);
        LinearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        LinearRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Set the encoder tick values of the linear lift motors to 0
        LinearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LinearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        LinearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LinearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        LinearLeft.setVelocity(400);
        LinearRight.setVelocity(400);

        LinearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        LinearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        //Creating Power and Direction Variables.
        double xDirection = 0;
        double yDirection = 0;
        double spinDirection = 0;

        //Creating Servo Variables.
        boolean ClawPositionA = false;
        boolean ClawPositionB = false;

        // Creating expansion hub motor variables
        double linearSpeed = 0;
        double spoolSpeed = 0;
        boolean autoUp = false;
        boolean autoDown = false;

        float RoboArmNum = 0;

        // Communicating with Control Hub.
        telemetry.addData("Status", "Initialized");

        //Wait for "Play" button to be pressed.
        waitForStart();

        //Run until "STOP" button pressed.
        while (opModeIsActive()) {

            telemetry.addData("Starting Code!","");


            //Assign driving values based off what is being pressed.
            xDirection = -this.gamepad1.left_stick_x;
            yDirection = -this.gamepad1.left_stick_y;
            spinDirection = this.gamepad1.right_stick_x;

            //Assign servo values based off what is being pressed.
            ClawPositionA = this.gamepad2.a;
            ClawPositionB = this.gamepad2.b;

            //Assign expansion hub motors based off what is being pressed.
            linearSpeed = this.gamepad2.left_stick_y;
            spoolSpeed = this.gamepad2.right_stick_y;
            autoUp = this.gamepad2.x;
            autoDown = this.gamepad2.y;

            //Create instance of the Artemis_Motors class.
            Artemis_Movement movement = new Artemis_Movement();

            //Nested if for gamepad1:
            movement.controlHubMotorMovement(this.gamepad1.left_bumper,this.gamepad1.right_bumper,xDirection, yDirection,spinDirection,UpLeft,UpRight,DownLeft,DownRight);

            //If for servo:
            movement.clawMovement(ClawPositionA,ClawPositionB,ClawServo);

            //If for linear lift motor.
            RoboArmNum = movement.linearLiftMovement(this.gamepad2.dpad_up,this.gamepad2.dpad_down,this.gamepad2.right_trigger,this.gamepad2.left_trigger,RoboArmNum,linearSpeed,LinearLeft,LinearRight);

            telemetry.addData("A Button",ClawPositionA);
            telemetry.addData("B Button",ClawPositionB);
            telemetry.addData("LinearSpeed",linearSpeed);
            telemetry.addData("Left Encoder",LinearLeft.getCurrentPosition());
            telemetry.addData("Right Encoder",LinearRight.getCurrentPosition());


            //If-loop for spool motor.
            movement.spoolMotorMovement(spoolSpeed,SpoolMotor);

            //If-loop for the lights
            int modeNum = movement.lightChange(this.gamepad1.dpad_left, this.gamepad1.dpad_right, LightDriver, 10);

            telemetry.addData("Mode Number Lights:",modeNum);

            //Communicating with Control Hub.
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }

}
