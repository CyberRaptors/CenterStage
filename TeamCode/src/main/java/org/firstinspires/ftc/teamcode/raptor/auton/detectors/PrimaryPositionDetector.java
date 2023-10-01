/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.raptor.auton.detectors;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import lib8812.common.auton.IObjectDetector;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;

/*
 * This OpMode illustrates the basics of TensorFlow Object Detection,
 * including Java Builder structures for specifying Vision parameters.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 */
public class PrimaryPositionDetector implements IObjectDetector<PixelDetectionConstants.PixelPosition> {
    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera
    TfodProcessor tfod;
    VisionPortal visionPortal;
    LinearOpMode opMode;
    public PrimaryPositionDetector(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public PrimaryPositionDetector create(LinearOpMode opMode) { return new PrimaryPositionDetector(opMode); }

    public void init() {
        initTfod();
        visionPortal.resumeStreaming();
    }

    /**
     * Initialize the TensorFlow Object Detection processor.
     */
    private void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()


            .setModelFileName(PixelDetectionConstants.PRIMARY_TFOD_MODEL_FILE)
            .setModelLabels(
                    PixelDetectionConstants.LABELS
            )
            //.setIsModelTensorFlow2(true)
            //.setIsModelQuantized(true)
            //.setModelInputSize(300)
            //.setModelAspectRatio(16.0 / 9.0)

            .build();

        VisionPortal.Builder builder = new VisionPortal.Builder();

        builder.setCamera(opMode.hardwareMap.get(WebcamName.class, "Webcam 1"));

        builder.addProcessor(tfod);

        visionPortal = builder.build();

        tfod.setMinResultConfidence(0.75f);

        visionPortal.setProcessorEnabled(tfod, true);

    }

    public PixelDetectionConstants.PixelPosition getCurrentFeed() {
        double greatestConfidence = 0;
        Recognition bestRecognition = null;

        List<Recognition> currentRecognitions = tfod.getRecognitions();

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double confidence = recognition.getConfidence();

            if (confidence > greatestConfidence) {
                greatestConfidence = confidence;
                bestRecognition = recognition;
            }
        }

        if (bestRecognition == null) return PixelDetectionConstants.PixelPosition.CENTER;

        switch (bestRecognition.getLabel())
        {
            case "left": return PixelDetectionConstants.PixelPosition.LEFT;
            case "center": return PixelDetectionConstants.PixelPosition.CENTER;
            case "right": return PixelDetectionConstants.PixelPosition.RIGHT;
            default: return PixelDetectionConstants.PixelPosition.NONE;
        }
    }

    public void destroy() {
        visionPortal.close();
    };

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    public void logInputToTelemetry() {
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        opMode.telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            opMode.telemetry.addData(""," ");
            opMode.telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            opMode.telemetry.addData("- Position", "%.0f / %.0f", x, y);
            opMode.telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method telemetryTfod()

}   // end class