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

    while (((IntakeLeft.getCurrentPosition()+IntakeRight.getCurrentPosition())/2) != intakeTransferPos && (V4B_Left.getPosition()+V4B_Right.getPosition()) != V4BInBetweenPos && SpinClaw.getPosition() != spinClawBackwardValue) {

        IntakeLeft.setTargetPosition(IntakeLeft.getCurrentPosition()+5);
        IntakeRight.setTargetPosition(IntakeRight.getCurrentPosition()+5);
        // ETC....

        }

    }

}
