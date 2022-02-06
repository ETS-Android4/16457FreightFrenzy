package org.firstinspires.ftc.teamcode.subsystems;

import com.ftc11392.sequoia.subsystem.Subsystem;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Arm extends Subsystem {
    DcMotorEx arm;
    DcMotorEx rotator;
    AngularServo wrist;
    AngularServo gripper;
    int armTarget;
    int rotatorTarget;
    double wristTarget;
    double gripperTarget;

    double WRIST_UPRIGHT_POSITION = 0.0;
    double WRIST_HORIZONTAL_POSITION = 0.75;

    double GRIPPER_OPEN_POSITION = 0.3;
    double GRIPPER_CLOSE_POSITION = 0.0;
    double GRIPPER_INTAKE_POSITION = 0.6;
    double GRIPPER_ELEMENT_POSITION = 0.25;

    double armTargetPower = 0.8;
    double rotatorTargetPower = 0.8;
    public final static double TICKS_PER_RADIAN = 85.5776129005;

    ArmState armState;

    WristState wristState;
    GripperState gripperState;

    // Untracked value, updated externally
    ArmWaypointGraph.ArmWaypointName lastWaypoint;

    @Override
    public void initialize(HardwareMap hardwareMap) {
        arm = hardwareMap.get(DcMotorEx.class, "arm");
        rotator = hardwareMap.get(DcMotorEx.class, "rotator");
        arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rotator.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        arm.setPower(armTargetPower);
        rotator.setPower(rotatorTargetPower);
        arm.setTargetPosition(0);
        rotator.setTargetPosition(0);
        arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rotator.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armTarget = 0;
        rotatorTarget = 0;

        wrist = new AngularServo(
                hardwareMap.get(Servo.class, "wrist"), 3 * Math.PI / 4
        );
        wrist.setPosition(0);
        wristState = WristState.UPRIGHT;

        gripper = new AngularServo(
                hardwareMap.get(Servo.class, "gripper"), 3 * Math.PI / 4);
        gripper.setPosition(0);
        gripperState = GripperState.CLOSE;

        wristTarget = 0;
        gripperTarget = 0;

        armState = ArmState.TARGET;

        lastWaypoint = ArmWaypointGraph.ArmWaypointName.INTAKE_DOWN_UPRIGHT;

        arm.setTargetPositionTolerance(10);
        rotator.setTargetPositionTolerance(10);

        arm.setPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDCoefficients(7.5, 0, 3.625));
        rotator.setPIDCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDCoefficients(2, 0.1, 0.5));

        telemetry.log().add("arm P%.2f I%.2f D%.2f F%.2f",
                arm.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).p,
                arm.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).i,
                arm.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).d,
                arm.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).f
        );
        telemetry.log().add("rotator P%.2f I%.2f D%.2f F%.2f",
                rotator.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).p,
                rotator.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).i,
                rotator.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).d,
                rotator.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION).f
        );
    }

    @Override
    public void initPeriodic() {

    }

    @Override
    public void start() {

    }

    @Override
    public void runPeriodic() {
        // Arm and rotator state handling
        switch (armState) {
            case IDLE:
                arm.setPower(0);
                rotator.setPower(0);
                break;
            case TARGET:
                arm.setPower(armTargetPower);
                rotator.setPower(rotatorTargetPower);
                arm.setTargetPosition(armTarget);
                rotator.setTargetPosition(rotatorTarget);
                break;
        }
        // Wrist state handling
        switch (wristState) {
            case UPRIGHT:
                wrist.setPosition(WRIST_UPRIGHT_POSITION);
                break;
            case HORIZONTAL:
                wrist.setPosition(WRIST_HORIZONTAL_POSITION);
                break;
            case TARGET:
                wrist.setPosition(wristTarget);
                break;
        }
        // Gripper state handling
        switch (gripperState) {
            case OPEN:
                gripper.setPosition(GRIPPER_OPEN_POSITION);
                break;
            case CLOSE:
                gripper.setPosition(GRIPPER_CLOSE_POSITION);
                break;
            case INTAKE:
                gripper.setPosition(GRIPPER_INTAKE_POSITION);
                break;
            case ELEMENT:
                gripper.setPosition(GRIPPER_ELEMENT_POSITION);
                break;
            case TARGET:
                gripper.setPosition(gripperTarget);
                break;
        }

        telemetry.addLine()
                .addData("armRad", ticksToRadians(arm.getCurrentPosition()))
                .addData("rotRad", ticksToRadians(rotator.getCurrentPosition()))
                .addData("armCTET", "%d %d %d %d", arm.getCurrentPosition(), arm.getTargetPosition(), Math.abs(arm.getCurrentPosition() - arm.getTargetPosition()), arm.getTargetPositionTolerance())
                .addData("rotCTET", "%d %d %d %d", rotator.getCurrentPosition(), rotator.getTargetPosition(), Math.abs(rotator.getCurrentPosition() - rotator.getTargetPosition()), rotator.getTargetPositionTolerance());
    }

    @Override
    public void stop() {

    }

    public ArmState getArmState() {
        return armState;
    }

    public void setArmState(ArmState armState) {
        this.armState = armState;
    }

    public WristState getWristState() {
        return wristState;
    }

    public void setWristState(WristState wristState) {
        this.wristState = wristState;
    }

    public GripperState getGripperState() {
        return gripperState;
    }

    public void setGripperState(GripperState gripperState) {
        this.gripperState = gripperState;
    }


    public double radiansToTicks(double radians) {
        return radians * TICKS_PER_RADIAN;
    }

    public double radiansToTicks(double angle, AngleUnit angleUnit) {
        return radiansToTicks(angleUnit.toRadians(angle));
    }

    public double ticksToRadians(int ticks) {
        return ticks / TICKS_PER_RADIAN;
    }

    public void moveArmToRadians(double radians) {
        armTarget = (int) Math.round(radiansToTicks(radians));

    }

    public void moveRotatorToRadians(double radians) {
        rotatorTarget = (int) Math.round(radiansToTicks(radians));
    }

    public boolean isWithinTarget() {
        return (
                (Math.abs(arm.getCurrentPosition() - arm.getTargetPosition()) <= arm.getTargetPositionTolerance()) &&
                        (Math.abs(rotator.getCurrentPosition() - rotator.getTargetPosition()) < rotator.getTargetPositionTolerance())
        );
    }

    public ArmWaypointGraph.ArmWaypointName getLastWaypoint() {
        return lastWaypoint;
    }

    public void setLastWaypoint(ArmWaypointGraph.ArmWaypointName lastWaypoint) {
        this.lastWaypoint = lastWaypoint;
    }

    public enum ArmState {
        IDLE, TARGET
    }

    public enum WristState {
        UPRIGHT, HORIZONTAL, TARGET
    }

    public enum GripperState {
        OPEN, CLOSE, INTAKE, ELEMENT, TARGET
    }
}
