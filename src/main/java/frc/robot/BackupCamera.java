package frc.robot;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;

import static frc.robot.Constants.BackupCameraConstants.*;

public class BackupCamera {

    private Thread visionThread;

    public BackupCamera() {
        visionThread =
        new Thread(
            () -> {
            try (UsbCamera camera = new UsbCamera("Backup Camera Raw", 0);) {
                camera.setResolution(640, 480); //any other values for resolution will crash the code for some reason
                
                CvSink cvSink = CameraServer.getVideo(camera);
                CvSource outputStream = CameraServer.putVideo("Backup Camera", kResolutionWidth, kResolutionHeight);

                Mat mat = new Mat();

                Point upperLeftCorner = new Point(kResolutionWidth - (kResolutionWidth * (0.8/2 + 0.5)), kResolutionHeight - (kResolutionHeight * (0.8/2 + 0.5)));
                Point lowerRightCorner = new Point(kResolutionWidth * (0.8/2 + 0.5), kResolutionHeight * (0.8/2 + 0.5));

                //Thread.interrupted() allows for the thread to stop when code redeploys/resets
                while (!Thread.interrupted()) {
                    if (cvSink.grabFrame(mat) == 0) {
                    outputStream.notifyError(cvSink.getError());
                    continue;
                    }
                    Imgproc.rectangle(mat, upperLeftCorner, lowerRightCorner, new Scalar(0, 0, 0), 7);
                    Imgproc.rectangle(mat, upperLeftCorner, lowerRightCorner, new Scalar(70, 255, 70), 4);
                    outputStream.putFrame(mat);
                }
            }
        });
        if (Robot.isReal()) {
            visionThread.setDaemon(true);
            visionThread.start();
        }
    }
}
