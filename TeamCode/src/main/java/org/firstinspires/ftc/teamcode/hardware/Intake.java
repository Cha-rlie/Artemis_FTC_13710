package org.firstinspires.ftc.teamcode.hardware;

// Import the necessary FTC modules and classes
//import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import android.transition.Slide;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Artemis_TeleOp;

public class Intake {
    private static Intake instance = null;
    public boolean enabled;
    ElapsedTime mStateTime = new ElapsedTime();
    ElapsedTime dStateTime = new ElapsedTime();

    // Variable to keep track of whether or not a cone is being transferred
    public boolean isConeBeingTransferred = false;
    public boolean cycleTransferCompete = false;
    public boolean transferRunning = false;

    public boolean lowJunctionPosition = false; // Is the Claw facing the ground?
    boolean clawBackwards = false; // Is the Claw facing backwards?
    public boolean clawState = true; // True = open
    public boolean buttonAReleased = true;
    public boolean buttonXReleased = true;
    public boolean movingToGround = false; // Has the reset home function been called
    public boolean movingToIdle = false;
    public boolean delayedBool;

    // Transfer stoof
    public double V4B_HomePos = 0.55; // Position where V4B is ready for intaking
    public double V4B_GentleHomePos = 0.53; // Position where V4B is ready for intaking
    public double V4B_TransferPos = 0.31; // Position where V4B is ready for transfer
    public double V4B_IdlePos = 0.377; // Position where the claw is in dimension and out of the way (for general driving)
    public int intakeTransferPos = -150;
    public int intakeCyclePos = -1420;
    public int depositTransferPos = 380;
    public int depositMidTransferPos = 200;

    double rotateClaw_HomePos = 0.7379; // Position where RotateClaw is ready for intaking
    double rotateClaw_Ground = 0.5; // Position where RotateClaw is ready for ground intaking
    public double increment = 0.1;

    public double closedClawPos = 0.32; // Position where claw is closed
    public double openClawPos = 0.55; // Position where claw is open
    public double midOpenClawPos = 0.45; // Position where claw is open enough to release cone during transfer
    public boolean restrictClawMovement = false;

    public double clawFowardPos = 0.712;
    public double clawBackwardsPos = 0.090;

    public int intakeHome = 0; // Fully contracted position
    public int intakeOut = -3000 ; // Fully extended position -2000
    double powerValue;
    boolean dStateTimeReset;

    public boolean SlidePositionReached;
    boolean V4BPositionReached;
    boolean DepositReached;
    boolean DepositReached2;
    public boolean cycleRunning = false;
    public boolean cycleSlidesHaveReachedPos = false;
    double targetChange;
    public boolean isRotatedBack = false;

    public static Intake getInstance() {
        if (instance == null) {
            instance = new Intake();
        }
        instance.enabled = true;
        return instance;
    }

    public void init(RobotHardware robot, Telemetry telemetry) {
        // ----------------------------------- INIT VARIABLES
        ElapsedTime mStateTime = new ElapsedTime();
        ElapsedTime dStateTime = new ElapsedTime();

        // Variable to keep track of whether or not a cone is being transferred
        boolean isConeBeingTransferred = false;
        boolean cycleTransferCompete = false;
        boolean transferRunning = false;

        boolean lowJunctionPosition = false; // Is the Claw facing the ground?
        boolean clawBackwards = false; // Is the Claw facing backwards?
        boolean clawState = true; // True = open
        boolean buttonAReleased = true;
        boolean buttonXReleased = true;
        boolean movingToGround = false; // Has the reset home function been called
        boolean movingToIdle = false;
        boolean delayedBool;

        // Transfer stoof
        double V4B_HomePos = 0.55; // Position where V4B is ready for intaking
        double V4B_GentleHomePos = 0.53; // Position where V4B is ready for intaking
        double V4B_TransferPos = 0.31; // Position where V4B is ready for transfer
        double V4B_IdlePos = 0.377; // Position where the claw is in dimension and out of the way (for general driving)
        int intakeTransferPos = -150;
        int intakeCyclePos = -1420;
        int depositTransferPos = 380;
        int depositMidTransferPos = 200;

        double rotateClaw_HomePos = 0.7379; // Position where RotateClaw is ready for intaking
        double rotateClaw_Ground = 0.5; // Position where RotateClaw is ready for ground intaking
        double increment = 0.1;

        double closedClawPos = 0.32; // Position where claw is closed
        double openClawPos = 0.55; // Position where claw is open
        double midOpenClawPos = 0.45; // Position where claw is open enough to release cone during transfer
        boolean restrictClawMovement = false;

        double clawFowardPos = 0.712;
        double clawBackwardsPos = 0.090;

        int intakeHome = 0; // Fully contracted position
        int intakeOut = -3000 ; // Fully extended position -2000
        double powerValue;
        boolean dStateTimeReset;
        boolean SlidePositionReached;
        boolean V4BPositionReached;
        boolean DepositReached;
        boolean DepositReached2;
        boolean cycleRunning = false;
        boolean cycleSlidesHaveReachedPos = false;
        double targetChange;
        boolean isRotatedBack = false;



        robot.claw.setPosition(closedClawPos);

        mStateTime.reset();
        while(!(mStateTime.time() >= 1.0)) {

        }

        robot.V4B_1.setPosition(V4B_TransferPos);
        robot.V4B_2.setPosition(V4B_TransferPos);


        robot.spinClaw.setPosition(clawFowardPos);

        updateRotateClaw(robot, 0);

        double current = 0;
        int exceededCount = 0;

        while(current <= 1) {
            current = (robot.intakeLeft.getCurrent(CurrentUnit.AMPS)+robot.intakeRight.getCurrent(CurrentUnit.AMPS))/2;
            robot.intakeLeft.setPower(0.2);
            robot.intakeRight.setPower(0.2);
        }

        robot.intakeLeft.setPower(0);
        robot.intakeRight.setPower(0);

        robot.intakeLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.intakeRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        cycleRunning = false;

    }

    public void changeClaw(RobotHardware robot) {
        if (buttonAReleased) {
            buttonAReleased = false;
            if(!clawState) {
                if(restrictClawMovement) {
                    robot.claw.setPosition(midOpenClawPos);
                } else {
                    robot.claw.setPosition(openClawPos);
                }
                clawState = true;
            } else if(clawState) {
                robot.claw.setPosition(closedClawPos);
                clawState = false;
            }
        }
    }

    public void scoreLow(RobotHardware robot) {
        if (buttonXReleased) {
            buttonXReleased = false;
            if(!lowJunctionPosition) {
                increment = increment - 0.25;
                lowJunctionPosition = true;
            } else if(lowJunctionPosition) {
                increment = increment + 0.25;
                lowJunctionPosition = false;
            }
        }
    }

    public double resetToHome(RobotHardware robot, Deposit deposit) {
        movingToGround = true;
        return(increment);
    }

    public void resetToIdle(RobotHardware robot) {
        movingToIdle = true;
    }

    public double intakeMotionProfile(RobotHardware robot, double distanceFromTarget) {
//        powerValue = Math.pow(1.115 , (1.84 * distanceFromTarget)) * 0.00003;
        double a = 0.0297803;
        double b = 3.09586;
        double k = 0.0121099;
        double c = 0.0823633;

        powerValue = Math.pow(b , (k * distanceFromTarget)) * a + c;

        // Cap values
        if(powerValue > 1) {powerValue = 1;}
        if(powerValue < 0) {powerValue = 0;}

        return(powerValue);
    }

    public void runIntake(RobotHardware robot, int targetPosition, Telemetry telemetry, double power) {
        power = Math.abs(power);
        double multiplier = intakeMotionProfile(robot, robot.valueOff(robot.intakeLeft.getCurrentPosition(), targetPosition));

        robot.intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if(robot.intakeLeft.getCurrentPosition() < targetPosition - 5) {
            robot.intakeLeft.setPower(power * multiplier);
            robot.intakeRight.setPower(power * multiplier);
        } else if (robot.intakeLeft.getCurrentPosition() > targetPosition + 5) {
            robot.intakeLeft.setPower(-power * multiplier);
            robot.intakeRight.setPower(-power * multiplier);
        }
    }

    public int updateRotateClaw(RobotHardware robot, double increment) {
        int rotationEncoder = robot.rearRight.getCurrentPosition();
        double gradient = -0.722063;
        double cintercept =0.957285 - 0.05 + increment;
        robot.rotateClaw.setPosition(gradient * robot.V4B_1.getPosition() + cintercept);
        return(rotationEncoder);
    }

    public boolean coneTransfer (RobotHardware robot, String mode, Telemetry telemetry, Deposit deposit) {
        // Run the checks
        if (robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeTransferPos, 10)) {SlidePositionReached = true;}
        if (robot.withinUncertainty(robot.V4B_1.getPosition(), V4B_TransferPos, 0.01)) {V4BPositionReached = true;}
        if (robot.withinUncertainty(robot.depositLeft.getCurrentPosition(), depositTransferPos, 10)) {DepositReached = true;}
        if (robot.withinUncertainty(robot.depositLeft.getCurrentPosition(), depositMidTransferPos, 10)) {DepositReached2 = true;}

        // Do things just once at the beginning
        if (mode == "Start" && !transferRunning) {
            transferRunning = true;
            isConeBeingTransferred = true;
            robot.claw.setPosition(closedClawPos);
            SlidePositionReached = false; // Holds whether desired position for intake slides has been reached
            V4BPositionReached = false; // Holds whether desired position for v4b has been reached
            DepositReached = false;
            DepositReached2 = false;
            isRotatedBack = false;
            delayedBool = false;
            dStateTimeReset = false;
            targetChange = 0;

            deposit.controlLatch(robot, "Prime");
            increment += 0.15;
        }

        if (!SlidePositionReached) {
            runIntake(robot, intakeTransferPos, telemetry, 1);
        }

        if(robot.V4B_1.getPosition() < V4B_HomePos - 0.1) {
            robot.spinClaw.setPosition(clawBackwardsPos);
        }

        if(SlidePositionReached) { // Put this into cycle function
            robot.intakeLeft.setTargetPosition(intakeTransferPos);
            robot.intakeRight.setTargetPosition(intakeTransferPos);
            robot.intakeLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.intakeRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.intakeLeft.setPower(1);
            robot.intakeRight.setPower(1);
        }


        if(!V4BPositionReached) {
            if(robot.V4B_1.getPosition() > V4B_TransferPos) {
                targetChange -= 0.0005;
            } else if(robot.V4B_1.getPosition() < V4B_TransferPos) {
                targetChange += 0.0005;
            }

            robot.V4B_1.setPosition(robot.V4B_1.getPosition() + targetChange);
            robot.V4B_2.setPosition(robot.V4B_1.getPosition() + targetChange);
        }


        // There is a certain point where the claw needs to be spun back so it latches into the transfer platform
//        if(robot.V4B_1.getPosition() < V4B_TransferPos+0.01 && SlidePositionReached && DepositReached) {
//            if(!isRotatedBack) {
//                isRotatedBack = true;
//                increment -= 0.2;
//            }
//            }
//        }

        if(SlidePositionReached && V4BPositionReached && DepositReached && !delayedBool) {
            dStateTime.reset();
            dStateTimeReset = true;
            delayedBool = true;
        }

        telemetry.addData("dStateTime: ", dStateTime.seconds());
        telemetry.addData("delayedBool: ", delayedBool);

        if(dStateTime.seconds() > 0.2 && delayedBool) {
            deposit.controlLatch(robot, "Close");
        }

        if (dStateTime.seconds() > 0.5 && delayedBool) {
            deposit.controlLatch(robot, "Close");
            robot.claw.setPosition(midOpenClawPos);
        }

        if(!DepositReached2) {
            deposit.runDeposit(robot, depositMidTransferPos, "Manual", telemetry);
            deposit.heldPosition = depositMidTransferPos;
        }

        if (DepositReached2 && SlidePositionReached && V4BPositionReached) {
            deposit.runDeposit(robot, depositTransferPos, "Manual", telemetry);
            deposit.heldPosition = depositTransferPos;
        }

        if(dStateTime.seconds() > 0.5 && SlidePositionReached && V4BPositionReached && DepositReached && delayedBool) {
            robot.claw.setPosition(midOpenClawPos);
            delayedBool = false;
            if(cycleRunning) {
                resetToHome(robot, deposit);
            } else {
                resetToIdle(robot);
            }

            cycleTransferCompete = true;

            return false;
        } else {
            return true;
        }
    }

    public void newCycle (RobotHardware robot, Deposit deposit, Intake intake, Telemetry telemetry, Gamepad gamepad2) {

        int cyclesLeft = 4;
        boolean slidesAreExtended = false;
        boolean V4B_AtConeGrabbingPos = false;
        double V4B_GoneGrabbingPos;
        boolean V4B_SafeToTransfer = false;
        double V4B_ReadyToTransferPos;
        boolean V4B_AtHomePos = false;

        while (cyclesLeft > -1 && !gamepad2.guide) {

            // Open the claw
            robot.claw.setPosition(openClawPos);

            // Position the V4B at the right angle to grab the current cone on the stack
            V4B_GoneGrabbingPos = V4B_TransferPos-(0.5*cyclesLeft);
            robot.V4B_1.setPosition(V4B_GoneGrabbingPos);
            robot.V4B_2.setPosition(V4B_GoneGrabbingPos);

            // Slightly change the V4B's target position until the desired position is reached
            if(!V4B_AtConeGrabbingPos) {
                if(robot.V4B_1.getPosition() > V4B_GoneGrabbingPos) {
                    targetChange -= 0.0005;
                } else if(robot.V4B_1.getPosition() < V4B_GoneGrabbingPos) {
                    targetChange += 0.0005;
                }

                robot.V4B_1.setPosition(robot.V4B_1.getPosition() + targetChange);
                robot.V4B_2.setPosition(robot.V4B_1.getPosition() + targetChange);

                // Check if the V4B is now in the correct position
                if (robot.withinUncertainty(robot.V4B_1.getPosition(), V4B_GoneGrabbingPos, 0.01)) {
                    V4B_AtConeGrabbingPos = true;
                }

            }
            /*
            // Extend the intake outwards
            if(!slidesAreExtended) {
                intake.runIntake(robot, intake.intakeCyclePos, telemetry, 1);

                // Check if the slides have reach extension
                if(robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeCyclePos, 10)) {
                    slidesAreExtended = true;
                }
            }

            // Close the claw and grab the cone
            robot.claw.setPosition(closedClawPos);

            // Lift the V4B up so that the cone transfer program does not tip over the stack
            V4B_ReadyToTransferPos = robot.V4B_1.getPosition() - 0.10;
            robot.V4B_1.setPosition(V4B_ReadyToTransferPos);
            robot.V4B_2.setPosition(V4B_ReadyToTransferPos);

            // Slightly change the V4B's target position until the desired position is reached
            if(!V4B_SafeToTransfer) {
                if(robot.V4B_1.getPosition() > V4B_ReadyToTransferPos) {
                    targetChange -= 0.0005;
                } else if(robot.V4B_1.getPosition() < V4B_ReadyToTransferPos) {
                    targetChange += 0.0005;
                }

                robot.V4B_1.setPosition(robot.V4B_1.getPosition() + targetChange);
                robot.V4B_2.setPosition(robot.V4B_1.getPosition() + targetChange);

                // Check if the V4B is now in the correct position
                if (robot.withinUncertainty(robot.V4B_1.getPosition(), V4B_ReadyToTransferPos, 0.01)) {
                    V4B_SafeToTransfer = true;
                }

            }

            // Start the cone transfer process
            coneTransfer(robot, "Start", telemetry, deposit);

            // Keep the cone transfer process running until it is complete
            while (isConeBeingTransferred) {
                coneTransfer(robot, "Update", telemetry, deposit);
            }

            // Move the claw out of the way of the deposit and ready to grab the next cone
            robot.V4B_1.setPosition(V4B_HomePos);
            robot.V4B_2.setPosition(V4B_HomePos);

            // Slightly change the V4B's target position until the desired position is reached
            if(!V4B_AtHomePos) {
                if(robot.V4B_1.getPosition() > V4B_HomePos) {
                    targetChange -= 0.0005;
                } else if(robot.V4B_1.getPosition() < V4B_HomePos) {
                    targetChange += 0.0005;
                }

                robot.V4B_1.setPosition(robot.V4B_1.getPosition() + targetChange);
                robot.V4B_2.setPosition(robot.V4B_1.getPosition() + targetChange);

                // Check if the V4B is now in the correct position
                if (robot.withinUncertainty(robot.V4B_1.getPosition(), V4B_HomePos, 0.01)) {
                    V4B_AtHomePos = true;
                }

            }

            // Start the deposit "High" scoring program
            deposit.runDeposit(robot, 0, "High", telemetry);

            // Continue the deposit "High" scoring program until it finishes
            while (deposit.automationWasSet || deposit.zeroWasTargetted) {
                deposit.runDeposit(robot, 0, "Update", telemetry);
            }

            // Decrease the amount of cycles left to run by one
            cyclesLeft--;
            */
        }

    }

    public void cycle(RobotHardware robot, Deposit deposit, Intake intake, Telemetry telemetry, Gamepad gamepad2) {
        if(!cycleRunning) {
            cycleRunning = true;
            resetToHome(robot, deposit);
            cycleSlidesHaveReachedPos = false;
        }

        if(!cycleSlidesHaveReachedPos) {
            intake.runIntake(robot, intake.intakeCyclePos, telemetry, 1);
        }

        if(robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeCyclePos, 10)) {
            cycleSlidesHaveReachedPos = true;
        }

        if(cycleSlidesHaveReachedPos && gamepad2.a) {
            intake.runIntake(robot, intake.intakeCyclePos - 400, telemetry, 1);
        }

        if(robot.withinUncertainty(robot.intakeLeft.getCurrentPosition(), intakeCyclePos - 400, 10)) {
            robot.claw.setPosition(closedClawPos);

            mStateTime.reset();
            while (mStateTime.seconds() <= 0.5) {}

            isConeBeingTransferred = true;
            intake.coneTransfer(robot,"Start", telemetry, deposit);
        }

    }

    public double toV4Bposition(int degrees) {
        return(((degrees * 4.0f / 7.0f) / 300.0f) + V4B_HomePos);
    }

    public double getRotationDegrees(int ticks, Telemetry telemetry) {
        telemetry.addData("Ticks Count: ", ticks);
        return(ticks * 0.0439453125);
    }


}