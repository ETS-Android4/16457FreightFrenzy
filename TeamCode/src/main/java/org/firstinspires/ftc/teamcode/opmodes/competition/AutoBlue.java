package org.firstinspires.ftc.teamcode.opmodes.competition;


import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.ftc11392.sequoia.SequoiaOpMode;
import com.ftc11392.sequoia.task.InstantTask;
import com.ftc11392.sequoia.task.SequentialTaskBundle;
import com.ftc11392.sequoia.task.SwitchTask;
import com.ftc11392.sequoia.task.Task;
import com.ftc11392.sequoia.task.WaitTask;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.subsystems.Arm2;
import org.firstinspires.ftc.teamcode.subsystems.DuckDetector;
import org.firstinspires.ftc.teamcode.subsystems.Gripper;
import org.firstinspires.ftc.teamcode.subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.subsystems.Rotator;
import org.firstinspires.ftc.teamcode.tasks.FollowTrajectoryTask;

import java.util.HashMap;
import java.util.Map;

@Autonomous(name = "Auto Blue", group = "Working Title")
public class AutoBlue extends SequoiaOpMode {

    DuckDetector duckDetector = new DuckDetector(0, 105, 185);
    Mecanum mecanum = new Mecanum();
    Rotator rotator = new Rotator();
    Arm2 arm = new Arm2();
    Gripper gripper = new Gripper();

    Map<Object, Task> positionMap = new HashMap<Object, Task>(){{
        put(DuckDetector.DuckPipeline.DuckPosition.LEFT, new SequentialTaskBundle(
                new InstantTask(() -> {
                    arm.setMode(Arm2.ArmMode.HORIZONTAL);
                    arm.modifySetpoint(6);
                }),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-12,60,-Math.PI/2))
                        .build()),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-12,53,-Math.PI/2))
                        .build())

        ));
        put(DuckDetector.DuckPipeline.DuckPosition.CENTER, new SequentialTaskBundle(
                new InstantTask(() -> {
                    arm.setMode(Arm2.ArmMode.HORIZONTAL);
                    arm.modifySetpoint(10);
                }),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-12,60,-Math.PI/2))
                        .build()),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-12,51,-Math.PI/2))
                        .build())
        ));
        put(DuckDetector.DuckPipeline.DuckPosition.RIGHT, new SequentialTaskBundle(
                new InstantTask(() -> {
                    arm.setMode(Arm2.ArmMode.HORIZONTAL);
                    arm.modifySetpoint(18); // 11 18
                }),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-12,58,-Math.PI/2))
                        .build()),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-12,51,-Math.PI/2))
                        .build())
        ));
    }};

    @Override
    public void initTriggers() {
        mecanum.mecanum().setPoseEstimate(new Pose2d(-33,63.5, Math.PI));
        gripper.setState(Gripper.GripperState.CLOSED);
    }

    @Override
    public void runTriggers() {
        DuckDetector.DuckPipeline.DuckPosition position = duckDetector.getAnalysis();
        scheduler.schedule(new SequentialTaskBundle(
                //git new WaitTask(5, TimeUnit.SECONDS),
                new SwitchTask(positionMap, () -> position),
                new InstantTask(() -> gripper.setState(Gripper.GripperState.OPEN)),
                new WaitTask(1),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(mecanum.mecanum().getPoseEstimate()
                                .plus(new Pose2d(0,5)))
                        .build()),
                new InstantTask(() -> arm.setMode(Arm2.ArmMode.HOME)),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(-66.5,59.5, Math.PI))
                        .build()),
                new InstantTask(() -> rotator.setSetpoint(10)),
                new WaitTask(3),
                new InstantTask(() -> rotator.setSetpoint(0)),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(0,72.5,0))
                        .build()),
                new FollowTrajectoryTask(mecanum, () -> mecanum.mecanum()
                        .trajectoryBuilder(mecanum.mecanum().getPoseEstimate())
                        .lineToLinearHeading(new Pose2d(48,72.5,0))
                        .build()),
                new InstantTask(this::requestOpModeStop)
        ));
    }
}
