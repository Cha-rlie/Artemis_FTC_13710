package org.firstinspires.ftc.teamcode;

// Import the necessary custom-made classes

// Import the necessary FTC modules and classes
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

public class Artemis_Functions {

    private void coneTransfer(DcMotorEx IntakeLeft, DcMotorEx IntakeRight, Servo V4B_Left, Servo V4B_Right, Servo SpinClaw) {
    int intakeTransferPos = 400;
    int spinClawForwardValue = 0;
    int spinClawBackwardValue = 0;
    int V4BFlatPos = 0;
    int V4BInBetweenPos = 0;
    int V4BTransferPos = 0;

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

        sleep(5000);

        while(!(Claw.getPosition() > 0.19) || !(Claw.getPosition() < 0.21)) { // Is the claw within the range of open?
            Claw.setPosition(0.2);
        }

        // The claw has now dropped the cone, and successfully transferred i hope :')
        // We can revert all the components to their intaking position

        V4B_1.setPosition(V4B_1_HomePos);
        V4B_2.setPosition(V4B_1_HomePos);

//      SpinClaw.setPosition(ClawFowardPos);
//      RotateClaw.setPosition(RotateClaw_HomePos);

    }

}
