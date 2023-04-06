package org.firstinspires.ftc.teamcode;

// Import the necessary Java modules
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

public class ReadWriteFiles {

    public static void getRobotPosition() throws IOException {

        File robotPositionFile = new File("FIRST/RobotPosition.txt");

        FileReader reader = new FileReader(robotPositionFile);

        char robotLocation[] = new char[20]; // dont forget to change!

        reader.read(robotLocation);

        System.out.print(robotLocation);

        reader.close();
    }
}
