// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.drive;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.subsystems.Drivetrain;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class TurnToAngle extends PIDCommand {

  private static Drivetrain drivetrain;

  public enum DriveAngle {
    FRONT(0),
    LEFT(-90),
    RIGHT(90),
    BACK(180);

    public final double angle;

    private DriveAngle(double angle) {
      this.angle = angle;
    }
  }

  /** Creates a new TurnToAngle. */
  public TurnToAngle(DriveAngle driveAngle) {
    super(
        // The controller that the command will use
        new PIDController(0.005, 0, 0),
        // This should return the measurement
        () -> drivetrain.getHeading().getDegrees(),
        // This should return the setpoint (can also be a constant)
        () -> driveAngle.angle,
        // This uses the output
        output -> {
          // Use the output here
          drivetrain.drive(0, 0, output, true);
        });
    // Use addRequirements() here to declare subsystem dependencies.
    getController().setTolerance(3);
    drivetrain = Drivetrain.getInstance();
    addRequirements(drivetrain);
    // Configure additional PID options by calling `getController` here.
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}