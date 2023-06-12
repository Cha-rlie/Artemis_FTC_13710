## Description
This is the team code for Artemis (Team 13710) for the 2022-2023 "PowerPlay" FTC season, specifically the Asia Pacific Open Competition in July 2023.

## About Us
Check out our [YouTube Channel](https://www.youtube.com/@artemis13710)
Check out our [website](https://sites.google.com/view/artemis13710/)
Contact us at artemis13710@gmail.com

## Naming Conventions
* Everything mostly uses camel case
* Motors, servos and devices in general start with a capital (e.g. ) 

## Code Files
### Artemis_TeleOp
* Takes input from two gamepads
* Allows for movement in all directions
* Uses mecanum drive code to combine movement commands
* Manual control for intake and deposit systems

### Gamepad Command Map

#### Controller A
| Control       | Function                    |
|---------------|-----------------------------|
| Left Stick X  | Turns robot                 |
| Right Stick X | Strafes left or right       |
| Right Stick Y | Drives forward or backwards |
| Left Bumper   | Halves speed of robot       |
| Right Bumper  | Doubles speed of robot      |

#### Controller B
| Control          | Function                                                                                                 |
|------------------|----------------------------------------------------------------------------------------------------------|
| Left Stick Y     | Extend or Retracts Intake System                                                                         |
| Right Stick Y    | Rotates V4B                                                                                              |
| Dpad Up and Down | Extends or Retracts Deposit System                                                                       |
| Dpad Left        | Automatically scores on medium junction position (Using encoder values)                                  |
| Dpad Right       | Automatically scores on high junction position (Using encoder values)                                    | 
| A                | Opens and Closes Claw                                                                                    |
| B                | Transfer cone onto deposit platform (Requires movement of both SpinClaw and V4B, and perhaps RotateClaw) | 
| X                | Spin claw 90 degrees (facing downwards), or reverts to vertical position for normal intaking             |
| Y                | Return V4B to the home position (intaking)                                                               |
| Left Bumper      | Halves speed of robot                                                                                    |
| Right Bumper     | Doubles speed of robot                                                                                   |


### Devices

**_Intake:_**
| Variable Name       | Function                  | How is it controlled? |
|---------------|--------------------------------------| --------- |
| Claw  | Grab and Drop Cone    | Button press |
| SpinClaw | Spins the claw around to face the back of the robot, used for the cone transfer | Automated button press | 
| RotateClaw* | Spins the claw down to face the ground, to intake fallen over cones + aid with intaking from the stack in auto | Button press + controlled independently in auto | 
| V4B* | Two servos, driving the same mechanism. Powers the arm like mechanism mounted on the dlies, moving the claw in an arc. Powered by two servos, will need to spin in opposite directions cause they are mirrored | Mostly automated, but manual control is needed (joystick) | 


**_Deposit:_**
| Variable Name       | Function                  | How is it controlled? |
|---------------|--------------------------------------| --------- |
| Latch  | Hold cone while on deposit platform to prevent it from flying off   | Automatically controlled, linked to the movement of the deposit |
=======
| Pin | Device Name       | Function                  | How is it controlled? |
|---|-------------|--------------------------------------| --------- |
| E0 | Claw  | Grab and Drop Cone    | Button press |
| E1 | SpinClaw | Spins the claw around to face the back of the robot, used for the cone transfer | Automated button press |
| E2 | RotateClaw | Rotates the claw down to face the ground, to intake fallen over cones + aid with intaking from the stack in auto | Button press + controlled independently in auto |
| E3 & E4 | V4B | Two servos, driving the same mechanism. Powers the arm-like mechanism mounted on the intake, moving the claw in an arc. Powered by two servos, will need to spin in opposite directions cause they are mirrored | Mostly automated, but manual control is needed (joystick) |


**_Deposit:_**
| Pin | Device Name       | Function                  | How is it controlled? |
|---|-------------|--------------------------------------| --------- |
| E5 |Latch  | Hold cone while on deposit platform to prevent it from flying off   | Automatically controlled, linked to the movement of the deposit |


**_Odo-pods_**
| Pin | Device Name | Location |
| --- | ----------- | -------- |
| C0 | BackOdoPod | At the back of the robot in the centre |
| C1 | Something | Somewhere |
| C2 | Something else | Somewhere else |

## Disclaimer
This entire repo is under the MIT License meaning you can use this code as you please. Look at it, learn from it and use it all you want. :)

## Thank You
Thank you for reading this far!
