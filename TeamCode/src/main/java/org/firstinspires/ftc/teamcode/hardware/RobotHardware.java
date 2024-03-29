package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary custom-made classes

// Import the necessary FTC modules and classes
//import com.google.android.libraries.play.games.inputmapping.Input;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;


public class RobotHardware {
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx rearLeft;
    public DcMotorEx rearRight;

    public DcMotorEx intakeLeft;
    public DcMotorEx intakeRight;
    public DcMotorEx depositLeft;
    public DcMotorEx depositRight;

    public Servo claw;
    public Servo rotateClaw;
    public Servo spinClaw;
    public Servo V4B_1;
    public Servo V4B_2;
    public Servo latch;

    public IMU imu;

    public DistanceSensor distanceSensor;

    private static RobotHardware instance = null;
    public boolean enabled;

    public static RobotHardware getInstance() {
        if (instance == null) {
            instance = new RobotHardware();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(HardwareMap hardwareMap) {
        // Initialising the motors up for drive base
        frontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        rearLeft = hardwareMap.get(DcMotorEx.class, "RearLeft");
        rearRight = hardwareMap.get(DcMotorEx.class, "RearRight");
        //rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeft.setDirection(DcMotor.Direction.REVERSE);

        // Initialising the motors for the dual-intake system
        intakeLeft = hardwareMap.get(DcMotorEx.class, "IntakeLeft");
        intakeRight = hardwareMap.get(DcMotorEx.class, "IntakeRight");
        intakeRight.setDirection(DcMotorEx.Direction.REVERSE);
        intakeLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        depositLeft = hardwareMap.get(DcMotorEx.class, "DepositLeft");
        depositRight = hardwareMap.get(DcMotorEx.class, "DepositRight");
        depositRight.setDirection(DcMotorEx.Direction.REVERSE);
        depositLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        depositRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        depositLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        depositRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Initialising the servos
        claw = hardwareMap.get(Servo.class, "Claw");
        spinClaw = hardwareMap.get(Servo.class,"SpinClaw");
        rotateClaw = hardwareMap.get(Servo.class, "RotateClaw");
        V4B_1 = hardwareMap.get(Servo.class, "V4B_1");
        V4B_2 = hardwareMap.get(Servo.class, "V4B_2");
        V4B_2.setDirection(Servo.Direction.REVERSE);
        latch = hardwareMap.get(Servo.class, "Latch");

        imu = hardwareMap.get(IMU.class, "imu");

        this.enabled = true;
    }

    public boolean withinUncertainty(double currentPos, double wantedPos, double range) {
        if((currentPos < wantedPos + range) && currentPos > wantedPos - range) {
            return true;
        } else {
            return false;
        }
    }
// the code works now :sparkles:
    public double valueOff(double currentPos, double wantedPos) {
        return(Math.abs(wantedPos-currentPos));
    }
}