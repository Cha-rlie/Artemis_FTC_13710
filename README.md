## Description
This is the team code for Artemis (Team 13710) for the 2022-2023 "PowerPlay" FTC season, specifically the Asia Pacific Open Competition in July 2023.

## About Us
Check out our YouTube page at: https://www.youtube.com/@artemis13710

## Features
### Artemis_TeleOp
It is the TeleOp code.
It:
- Takes an input from the gamepads from the drivers
- Gives this input to select functions from Artemis_Movement (see Bellow)
- Allows robot to move all motors and servos upon command

### Artemis_Movement
A file filled with many different functions to be called in other files.
Each function:
- Takes in parameters that will dictate, speed, power, time, distance and/or direction
  - These values could come from the user through the gamepads (TeleOp)
  - Or from the code (autonomous)
- Also takes in the servos or motors
- Then runs the servos and/or motors according to the user input

### Artemis_Motors
NOTE: NOT TO BE USED
The old version of Artemis_Movement (see above).
It had many different versions of the various functions and was not clean or fully functional.

### ConeDetectorPipeLine
A file that will take in the view of a camera and run it through a pipeline of several different processes to eventually identify a colour from the custom sleeve.
It is used by Artemis_Auto (see bellow).

### Artemis_Auto
NOTE: CURRENTLY NOT FUNCTIONING
The autonomous file that was supposed to be run at regionals.
- There is reason to believe this is because of the Scalars in the ConeDetectorPipeline (see above) having 3 values instead of 4

### AprilTagDetectionPipeline
A file that takes in camera input to run it through many different functions and processes to identify which AprilTag the custom sleeve is showing.
It came from [OpenFTC's EOCV-AprilTag-Plugin](https://github.com/OpenFTC/EOCV-AprilTag-Plugin/tree/master/examples/src/main/java/org/firstinspires/ftc/teamcode) and was implemented with the help of [Team 6547's tutorial on AprilTags](https://www.youtube.com/watch?v=gjiLNWnPPN0).

### Artemis_AprilTag_Autonomous
The new autonomous code that is supposed to be run in Nationals.
It also came from [OpenFTC's EOCV-AprilTag-Plugin](https://github.com/OpenFTC/EOCV-AprilTag-Plugin/tree/master/examples/src/main/java/org/firstinspires/ftc/teamcode) and was implemented with the help of [Team 6547's tutorial on AprilTags](https://www.youtube.com/watch?v=gjiLNWnPPN0).

### Artmis_Forwards/Left/Right
A variety of autonomous files to be used when proper autonomous code does not function. Left and Right at for the robot to park in the spaces and Forwards is for a one in three chance of getting 20 points.
They:
- Initialise the wheel motors
- Call the respective function from Artemis_Motors (see above) with the right parameters

## Thank You
Thank you for reading this far!
