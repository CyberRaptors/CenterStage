package org.firstinspires.ftc.teamcode.auton.runners.doublepixel.blue;

import static lib8812.common.auton.autopilot.FieldPositions.Autonomous;
import static lib8812.common.auton.autopilot.FieldPositions.BLOCK_LENGTH_IN;
import static lib8812.common.auton.autopilot.FieldPositions.HALF_BLOCK_LENGTH_IN;

import org.firstinspires.ftc.teamcode.auton.detectors.PixelDetectionConstants;
import org.firstinspires.ftc.teamcode.robot.RaptorRobot;

import lib8812.common.auton.IAutonomousRunner;
import lib8812.common.rr.trajectorysequence.TrajectorySequence;
import lib8812.common.robot.IDriveableRobot;


public class BlueLeftDoublePixelRunner extends IAutonomousRunner<PixelDetectionConstants.PixelPosition> {
    RaptorRobot bot = new RaptorRobot();

    protected IDriveableRobot getBot() {
        return bot;
    }

    void armDown()
    {
        bot.arm.setPosition(bot.arm.maxPos-400);
        bot.clawRotate.setPosition(bot.CLAW_ROTATE_OPTIMAL_PICKUP);
    }

    void armUp()
    {
        bot.arm.setPosition(bot.AUTON_FROZEN_ARM_TICKS);
        bot.arm.setPosition(bot.AUTON_FROZEN_ARM_TICKS);
        bot.clawRotate.setPosition(bot.AUTON_FROZEN_CLAW_ROTATE);
    }

    void dropPurple()
    {
        sleep(500);
        bot.arm.setPosition(bot.arm.maxPos-300);
        bot.arm.waitForPosition();
        bot.arm.setPosition(bot.arm.maxPos-300);
        bot.arm.waitForPosition();

        bot.pixelManager.releaseAutonOneFront();
        sleep(300);
    }

    void dropYellow()
    {
        bot.arm.waitForPosition();
        sleep(300);
        bot.pixelManager.releaseAutonTwoFront();
        sleep(100);
        bot.clawRotate.setPosition(bot.CLAW_ROTATE_OVER_PLANE_LAUNCHER_POS);
        bot.arm.setPosition(bot.arm.minPos);
    }

    protected void internalRun(PixelDetectionConstants.PixelPosition pos) {
        sleep(500); // see below comment
        pos = objectDetector.getCurrentFeed(); // see if this changes anything

        drive.setPoseEstimate(Autonomous.BLUE_LEFT_START);
        bot.pixelManager.init(this::sleep);

        TrajectorySequence toTape = null;
        TrajectorySequence toPark = null;
        TrajectorySequence toBackdrop = null;

        TrajectorySequence _toBackdrop;
        TrajectorySequence _toPark;

        switch (pos)
        {
            case RIGHT:
                toTape = drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                        .addTemporalMarker(0, this::armDown)
                        .forward((HALF_BLOCK_LENGTH_IN-7))
                        .turn(Math.toRadians(-11))
                        .build();
                _toBackdrop = drive.trajectorySequenceBuilder(toTape.end())
                        .turn(Math.toRadians(11))
                        .back(HALF_BLOCK_LENGTH_IN-6)
                        .turn(Math.toRadians(90))
                        .forward(BLOCK_LENGTH_IN)
                        .turn(Math.toRadians(-90))
                        .waitSeconds(0.5)
                        .forward(BLOCK_LENGTH_IN+4) // l/r on backdrop
                        .turn(Math.toRadians(90))
                        .waitSeconds(0.5)
                        .forward(HALF_BLOCK_LENGTH_IN-4) // f/b on backdrop
                        .build();
                toBackdrop = drive.trajectorySequenceBuilder(toTape.end())
                        .addTemporalMarker(0, this::armUp)
                        .splineToSplineHeading(_toBackdrop.end(), _toBackdrop.end().getHeading())
                        .build();
                _toPark = drive.trajectorySequenceBuilder(toBackdrop.end())
//                        .back()
                        .forward(5)
                        .turn(Math.toRadians(90))
                        .waitSeconds(0.5)
                        .forward(BLOCK_LENGTH_IN) // *
                        .build();

                toPark = drive.trajectorySequenceBuilder(toBackdrop.end())
                        .splineToSplineHeading(_toPark.end(), _toPark.end().getHeading())
                        .build();
                break;
            case CENTER:
                toTape = drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                        .addTemporalMarker(0, this::armDown)
                        .forward((HALF_BLOCK_LENGTH_IN-1))
                        .turn(Math.toRadians(25))
                        .build();
                _toBackdrop = drive.trajectorySequenceBuilder(toTape.end())
                        .turn(Math.toRadians(-25))
                        .back(HALF_BLOCK_LENGTH_IN-3)
                        .turn(Math.toRadians(90))
                        .forward(BLOCK_LENGTH_IN)
                        .turn(Math.toRadians(-90))
                        .waitSeconds(0.5)
                        .forward(BLOCK_LENGTH_IN-6) // l/r on backdrop
                        .turn(Math.toRadians(90))
                        .waitSeconds(0.5)
                        .forward(HALF_BLOCK_LENGTH_IN-4) // f/b on backdrop
                        .build();
                toBackdrop = drive.trajectorySequenceBuilder(toTape.end())
                        .addTemporalMarker(0, this::armUp)
//                        .back(2)
                        .splineToSplineHeading(_toBackdrop.end(), _toBackdrop.end().getHeading())
                        .build();
                _toPark = drive.trajectorySequenceBuilder(toBackdrop.end())
//                        .back()
                        .forward(5)
                        .turn(Math.toRadians(90))
                        .waitSeconds(0.5)
                        .forward(HALF_BLOCK_LENGTH_IN) // *
                        .build();

                toPark = drive.trajectorySequenceBuilder(toBackdrop.end())
                        .back(3)
                        .splineToSplineHeading(_toPark.end(), _toPark.end().getHeading())
//                        .strafeLeft(HALF_BLOCK_LENGTH_IN)
                        .build();
                break;
            case LEFT:
                toTape = drive.trajectorySequenceBuilder(drive.getPoseEstimate())
                        .addTemporalMarker(0, this::armDown)
                        .forward((HALF_BLOCK_LENGTH_IN-7))
                        .turn(Math.toRadians(40))
                        .build();
                _toBackdrop = drive.trajectorySequenceBuilder(toTape.end())
                        .turn(Math.toRadians(-40))
                        .back(HALF_BLOCK_LENGTH_IN-6)
                        .turn(Math.toRadians(90))
                        .forward(BLOCK_LENGTH_IN)
                        .turn(Math.toRadians(-90))
                        .waitSeconds(0.5)
                        .forward(BLOCK_LENGTH_IN-10 /*10*/) // l/**(r)** on backdrop
                        .turn(Math.toRadians(90))
                        .waitSeconds(0.5)
                        .forward(HALF_BLOCK_LENGTH_IN-4) // f/b on backdrop
                        .build();
                toBackdrop = drive.trajectorySequenceBuilder(toTape.end())
                        .addTemporalMarker(0, this::armUp)
                        .splineToSplineHeading(_toBackdrop.end(), _toBackdrop.end().getHeading())
                        .build();
                _toPark = drive.trajectorySequenceBuilder(toBackdrop.end())
//                        .back()
                        .forward(5)
                        .turn(Math.toRadians(90))
                        .waitSeconds(0.5)
                        .forward(HALF_BLOCK_LENGTH_IN-8) // *
                        .build();

                toPark = drive.trajectorySequenceBuilder(toBackdrop.end())
//                        .splineToSplineHeading(_toPark.end(), _toPark.end().getHeading())
                        .strafeLeft(HALF_BLOCK_LENGTH_IN-8)
                        .build();
                break;
        }

        drive.followTrajectorySequence(toTape);
        dropPurple();
        drive.followTrajectorySequence(toBackdrop);
        sleep(200);
        dropYellow();
        sleep(200);
        drive.followTrajectorySequence(toPark);

    }
}
