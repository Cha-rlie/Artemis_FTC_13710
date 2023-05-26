package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary custom-made classes

// Import the necessary FTC modules and classes
//import com.google.android.libraries.play.games.inputmapping.Input;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;


public class RobotHardware {
    public DcMotorEx FrontLeft;
    public DcMotorEx FrontRight;
    public DcMotorEx RearLeft;
    public DcMotorEx RearRight;

    public DcMotorEx IntakeLeft;
    public DcMotorEx IntakeRight;
    public DcMotorEx DepositLeft;
    public DcMotorEx DepositRight;

    public Servo Claw;
    public Servo RotateClaw;
    public Servo SpinClaw;
    public Servo V4B_1;
    public Servo V4B_2;
    public Servo Latch;

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
        FrontLeft = hardwareMap.get(DcMotorEx.class, "FrontLeft");
        FrontRight = hardwareMap.get(DcMotorEx.class, "FrontRight");
        RearLeft = hardwareMap.get(DcMotorEx.class, "RearLeft");
        RearRight = hardwareMap.get(DcMotorEx.class, "RearRight");

        RearLeft.setDirection(DcMotor.Direction.REVERSE);

        // Initialising the motors for the dual-intake system
        IntakeLeft = hardwareMap.get(DcMotorEx.class, "IntakeLeft");
        IntakeRight = hardwareMap.get(DcMotorEx.class, "IntakeRight");
        IntakeRight.setDirection(DcMotorEx.Direction.REVERSE);
        IntakeLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        IntakeRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        DepositLeft = hardwareMap.get(DcMotorEx.class, "DepositLeft");
        DepositRight = hardwareMap.get(DcMotorEx.class, "DepositRight");
        DepositRight.setDirection(DcMotorEx.Direction.REVERSE);
        DepositLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DepositRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Initialising the servos
        Claw = hardwareMap.get(Servo.class, "Claw");
        SpinClaw = hardwareMap.get(Servo.class,"SpinClaw");
        RotateClaw = hardwareMap.get(Servo.class, "RotateClaw");
        V4B_1 = hardwareMap.get(Servo.class, "V4B_1");
        V4B_2 = hardwareMap.get(Servo.class, "V4B_2");
        Latch = hardwareMap.get(Servo.class, "Latch");

        this.enabled = true;
    }
}
