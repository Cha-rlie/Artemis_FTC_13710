package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

// Import the necessary custom-made classes
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.hardware.Deposit;
import org.firstinspires.ftc.teamcode.hardware.RobotHardware;

@TeleOp
public class SpeedComparison extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private RobotHardware robot = RobotHardware.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);
        robot.depositLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.depositRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        runtime.reset();

        while(robot.depositLeft.getCurrentPosition() < 2375) {
            robot.depositLeft.setPower(1);
            robot.depositRight.setPower(1);

            telemetry.addData("Encoder Pos", robot.depositLeft.getCurrentPosition());
            telemetry.update();
        }
        while(robot.depositLeft.getCurrentPosition() > 50) {
            robot.depositLeft.setPower(-1);
            robot.depositRight.setPower(-1);

            telemetry.addData("Encoder Pos", robot.depositLeft.getCurrentPosition());
            telemetry.update();
        }

        robot.depositLeft.setPower(0);
        robot.depositRight.setPower(0);

        double SetPowerTimeUp = runtime.milliseconds();



//        robot.depositLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        robot.depositRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//
//        runtime.reset();
//
//        while(robot.depositLeft.getCurrentPosition() < 2360) {
//            robot.depositLeft.setTargetPosition(2375);
//            robot.depositRight.setTargetPosition(2375);
//            robot.depositLeft.setPower(1);
//            robot.depositRight.setPower(1);
//            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        }
//
//        while(robot.depositLeft.getCurrentPosition() > 50) {
//            robot.depositLeft.setTargetPosition(0);
//            robot.depositRight.setTargetPosition(0);
//            robot.depositLeft.setPower(1);
//            robot.depositRight.setPower(1);
//            robot.depositLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            robot.depositRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//        }
//        double RTPPowerTimeUp = runtime.milliseconds();

        telemetry.addData("Time Taken Up: ", SetPowerTimeUp);
//        telemetry.addData("Time Taken Up: ", RTPPowerTimeUp);
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {}
    }
}
