package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake {
    private static Intake instance = null;
    public boolean enabled;

    public boolean groundIntake = false; // Is the Claw facing the ground?
    boolean clawBackwards = false; // Is the Claw facing backwards?
    boolean clawState = true; // True = open
    public boolean buttonAReleased = true;

    double V4B_1_HomePos = 0.5994+0.04; // Position where V4B is ready for intaking

    // ----------------- I DONT LIKE THE FACT THAT V4B1 AND V4B2 NEED DIFFERENT VALUES. I THINK WE CAN FIX THIS
    // IN THE ROBOT HARDWARE CLASS IVE PUT SOME CODE THAT IVE COMMENT OUT WHICH REVERSES THE SERVO DIRECTION

    double V4B_2_HomePos = 0.3627-0.04;
    double V4B_1_TransferPos = 0.3077; // Position where V4B is ready for transfer
    double V4B_2_TransferPos = 0.6483;

    double rotateClaw_HomePos = 0.84; // Position where RotateClaw is ready for intaking

    double closedClawPos = 0.14; // Position where claw is closed
    double openClawPos = 0.31; // Position where claw is open

    double clawFowardPos = 0.9;
    double clawBackwardsPos = 0.18;

    double ClosedLatchPos = 0;
    double IntermediatePos = 0;
    double OpenPos = 0;

    public int intakeHome = 0; // Fully contracted position
    public int intakeOut = -3000 ; // Fully extended position -2000
    public int transferPosition = -1100;

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
        robot.rotateClaw.setPosition(rotateClaw_HomePos);
        robot.claw.setPosition(openClawPos);
        robot.spinClaw.setPosition(clawFowardPos);
    }

    public void rotateClaw(RobotHardware robot, double degrees) {
        robot.rotateClaw.setPosition(robot.rotateClaw.getPosition() + degrees);
    }

    public void changeClaw(RobotHardware robot) {
        if (buttonAReleased) {
            buttonAReleased = false;
            if(!clawState) {
                robot.claw.setPosition(openClawPos);
                clawState = true;
            } else if(clawState) {
                robot.claw.setPosition(closedClawPos);
                clawState = false;
            }
        }
    }


    public void runIntake(RobotHardware robot, int targetPosition, Telemetry telemetry) {
        robot.intakeLeft.setTargetPosition(targetPosition);
        robot.intakeRight.setTargetPosition(targetPosition);
        robot.intakeLeft.setPower(1);
        robot.intakeRight.setPower(1);
        robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        int avg = (robot.intakeLeft.getCurrentPosition()+robot.intakeRight.getCurrentPosition())/2;
        telemetry.addData("Intake: ", avg);
    }


    public int[] getIntakePosition(RobotHardware robot) {
        int[] positions = new int[2];
        positions[0] = robot.intakeLeft.getCurrentPosition();
        positions[1] = robot.intakeRight.getCurrentPosition();

        return positions;
    }

    public void coneTransfer(RobotHardware robot) {
        double TransferingRotation = 0.66+0.03;

        boolean SlidePositionReached = false; // Holds whether desired position for intake slides has been reached
        boolean V4BPositionReached = false; // Holds whether desired position for v4b has been reached

        robot.claw.setPosition(closedClawPos);

        while(!SlidePositionReached) { // While the slides aren't in the correct position
            robot.V4B_1.setPosition(V4B_1_TransferPos);
            robot.V4B_2.setPosition(V4B_2_TransferPos);
            robot.spinClaw.setPosition(clawBackwardsPos);
            robot.rotateClaw.setPosition(TransferingRotation);

            robot.intakeLeft.setTargetPosition(-550);
            robot.intakeRight.setTargetPosition(-550);
            robot.intakeLeft.setPower(1);
            robot.intakeRight.setPower(1);

            if (robot.intakeLeft.getCurrentPosition() > -550-10 && robot.intakeLeft.getCurrentPosition() < -550+10) {
                // If the slides have reached the correct position...
                SlidePositionReached = true;
            }
        }

        while(!V4BPositionReached) { // Once the slides are in position, wait to ensure the V4B is in position.
            robot.V4B_1.setPosition(V4B_1_TransferPos);
            robot.V4B_2.setPosition(V4B_2_TransferPos);
            robot.spinClaw.setPosition(clawBackwardsPos);
            robot.rotateClaw.setPosition(TransferingRotation);

            if((robot.V4B_1.getPosition() + robot.V4B_2.getPosition())/2 < V4B_1_TransferPos+0.02 && (robot.V4B_1.getPosition() + robot.V4B_2.getPosition())/2 < V4B_1_TransferPos-0.02) {
                // If the v4b has reached the correct position...
                V4BPositionReached = true;
            }
        }

        while(!(robot.claw.getPosition() > 0.19) || !(robot.claw.getPosition() < 0.21)) { // Is the claw within the range of open?
            robot.claw.setPosition(0.2);
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
