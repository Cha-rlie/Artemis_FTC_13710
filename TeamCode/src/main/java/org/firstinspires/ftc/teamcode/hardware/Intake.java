package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {
    private static Intake instance = null;
    public boolean enabled;

    boolean GroundIntake = false; // Is the Claw facing the ground?
    boolean ClawBackwards = false; // Is the Claw facing backwards?
    boolean ClawState = true; // True = open
    public boolean AbuttonIsReleased = true;

    double V4B_1_HomePos = 0.5994+0.04; // Position where V4B is ready for intaking
    double V4B_2_HomePos = 0.3627-0.04;
    double V4B_1_TransferPos = 0.3077; // Position where V4B is ready for transfer
    double V4B_2_TransferPos = 0.6483;

    double RotateClaw_HomePos = 0.84; // Position where RotateClaw is ready for intaking

    double ClosedClawPos = 0.14; // Position where claw is closed
    double OpenClawPos = 0.31; // Position where claw is open

    double ClawFowardPos = 0.9;
    double ClawBackwardsPos = 0.18;

    double ClosedLatchPos = 0;
    double IntermediatePos = 0;
    double OpenPos = 0;

    public int IntakeHome = 0; // Fully contracted position
    public int IntakeOut = -2000; // Fully extended position -2000
    public int TransferPosition = -1100;

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot) {
        robot.V4B_1.setPosition(V4B_1_HomePos);
        robot.V4B_2.setPosition(V4B_2_HomePos);
        robot.RotateClaw.setPosition(RotateClaw_HomePos);
        robot.Claw.setPosition(ClosedClawPos);
        robot.SpinClaw.setPosition(ClawFowardPos);
    }

    public void rotateClaw(RobotHardware robot, double degrees) {
        robot.RotateClaw.setPosition(robot.RotateClaw.getPosition() + degrees);
    }

    public void changeClaw(RobotHardware robot) {
        if (AbuttonIsReleased) {
            AbuttonIsReleased = false;
            if(!ClawState) {
                robot.Claw.setPosition(OpenClawPos);
                ClawState = true;
            } else if(ClawState) {
                robot.Claw.setPosition(ClosedClawPos);
                ClawState = false;
            }
        }
    }


    public void runIntake(RobotHardware robot, int targetPosition) {
        robot.IntakeLeft.setTargetPosition(targetPosition);
        robot.IntakeRight.setTargetPosition(targetPosition);
        robot.IntakeLeft.setPower(1);
        robot.IntakeRight.setPower(1);
        robot.IntakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.IntakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    // Method Overloading
    public void runIntake(RobotHardware robot, int[] targetPosition) {
        robot.IntakeLeft.setTargetPosition(targetPosition[0]);
        robot.IntakeRight.setTargetPosition(targetPosition[1]);
        robot.IntakeLeft.setPower(1);
        robot.IntakeRight.setPower(1);
        robot.IntakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.IntakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public int[] getIntakePosition(RobotHardware robot) {
        int[] positions = new int[1];
        positions[0] = robot.IntakeLeft.getCurrentPosition();
        positions[1] = robot.IntakeRight.getCurrentPosition();

        return positions;
    }

    public void coneTransfer(DcMotorEx IntakeLeft, DcMotorEx IntakeRight, Servo V4B_1, Servo V4B_2, Servo Claw, Servo SpinClaw, Servo RotateClaw) {
        boolean GroundIntake = false; // Is the Claw facing the ground?
        boolean ClawBackwards = false; // Is the Claw facing backwards?
        boolean buttonIsReleased = true; // Handling debounce issues
        boolean AbuttonIsReleased = true;
        boolean ClawState = true; // True = open

        double V4B_1_HomePos = 0.5994+0.04; // Position where V4B is ready for intaking
        double V4B_2_HomePos = 0.3627-0.04;
        double V4B_1_TransferPos = 0.3077; // Position where V4B is ready for transfer
        double V4B_2_TransferPos = 0.6483;

        double RotateClaw_HomePos = 0.84; // Position where RotateClaw is ready for intaking

        double ClosedClawPos = 0.14; // Position where claw is closed
        double OpenClawPos = 0.31; // Position where claw is open

        double ClawFowardPos = 0.9;
        double ClawBackwardsPos = 0.18;

        double ClosedLatchPos = 0;
        double IntermediatePos = 0;
        double OpenPos = 0;

        int IntakeHome = 0; // Fully contracted position
        int IntakeOut = -2000; // Fully extended position -2000
        int TransferPosition = -1100;
        double TransferingRotation = 0.66+0.03;

        boolean SlidePositionReached = false; // Holds whether desired position for intake slides has been reached
        boolean V4BPositionReached = false; // Holds whether desired position for v4b has been reached

        Claw.setPosition(ClosedClawPos);

        while(!SlidePositionReached) { // While the slides aren't in the correct position
            V4B_1.setPosition(V4B_1_TransferPos);
            V4B_2.setPosition(V4B_2_TransferPos);
            SpinClaw.setPosition(ClawBackwardsPos);
            RotateClaw.setPosition(TransferingRotation);

            IntakeLeft.setTargetPosition(-700);
            IntakeRight.setTargetPosition(-700);
            IntakeLeft.setPower(1);
            IntakeRight.setPower(1);

            if ((IntakeLeft.getCurrentPosition()+IntakeRight.getCurrentPosition())/2 < TransferPosition+10 && (IntakeLeft.getCurrentPosition()+IntakeRight.getCurrentPosition())/2 > TransferPosition-10) {
                // If the slides have reached the correct position...
                SlidePositionReached = true;
            }
        }

        while(!V4BPositionReached) { // Once the slides are in position, wait to ensure the V4B is in position.
            V4B_1.setPosition(V4B_1_TransferPos);
            V4B_2.setPosition(V4B_2_TransferPos);
            SpinClaw.setPosition(ClawBackwardsPos);
            RotateClaw.setPosition(TransferingRotation);

            if((V4B_1.getPosition() + V4B_2.getPosition())/2 < V4B_1_TransferPos+0.02 && (V4B_1.getPosition() + V4B_2.getPosition())/2 < V4B_1_TransferPos-0.02) {
                // If the v4b has reached the correct position...
                V4BPositionReached = true;
            }
        }

        while(!(Claw.getPosition() > 0.19) || !(Claw.getPosition() < 0.21)) { // Is the claw within the range of open?
            Claw.setPosition(0.2);
        }

        // The claw has now dropped the cone, and successfully transferred i hope :')
    }

    public void cycle(DcMotorEx IntakeLeft, DcMotorEx IntakeRight, int IntakeOut) {
        IntakeLeft.setTargetPosition(IntakeOut);
        IntakeRight.setTargetPosition(IntakeOut);
        IntakeLeft.setPower(1);
        IntakeRight.setPower(1);
    }

}
