package frc.robot.subsystems;


import com.revrobotics.AbsoluteEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkBase.SoftLimitDirection;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Ports;
import frc.robot.subsystems.LEDStrip.SubsystemsPriority;
import frc.robot.Constants.MechConstants;
import frc.robot.Constants.VisionConstants;



public class Arm extends SubsystemBase{

        private CANSparkMax leftMotor;
        private CANSparkMax rightMotor;

        private RelativeEncoder leftEncoder;
        private RelativeEncoder rightEncoder;

        private double angleAverage;
        private static Arm instance;
        private double aprilAngle;

        private Arm(){
          this.leftMotor = new CANSparkMax(Ports.ARM_LEFT, MotorType.kBrushless);
          this.rightMotor = new CANSparkMax(Ports.ARM_RIGHT, MotorType.kBrushless);

          leftMotor.setInverted(true);
          rightMotor.setInverted(false);

          SoftLimitDirection direction = SoftLimitDirection.kReverse;
          leftMotor.enableSoftLimit(direction, true);
          leftMotor.enableSoftLimit(direction, true);

          leftMotor.setSoftLimit(direction, 0.0f);
          rightMotor.setSoftLimit(direction, 0.0f);
          
          leftEncoder = leftMotor.getEncoder();
          rightEncoder = rightMotor.getEncoder();

          leftEncoder.setPositionConversionFactor(360 / (80 * 40 / 18));
          rightEncoder.setPositionConversionFactor(360 / (80 * 40 / 18));

          leftMotor.burnFlash();

        }

        public static Arm getInstance() {
          if (instance == null) {
            instance = new Arm();
          }
          
          return instance;
          }

          public double getAngle(){
            angleAverage = (leftEncoder.getPosition() + rightEncoder.getPosition()) / 2;
            return angleAverage;
          }

          public void resetValue() {
            leftEncoder.setPosition(0);
            rightEncoder.setPosition(0);
          }

          public void pivot(double speed) {

            leftMotor.set(speed);
            rightMotor.set(speed);
          }
        
          public void stop() {
            leftMotor.set(0);
            rightMotor.set(0);
          }

  public double getArmAprilAngle() {
    aprilAngle = VisionConstants.kC + VisionConstants.kB * Camera.getInstance().getX() + VisionConstants.kA * Math.pow(Camera.getInstance().getX(), 2);
    return aprilAngle;
  }

  public boolean isGoodLaunchAngle(){
    double diff = getAngle() - aprilAngle;
    if (Math.abs(diff) < VisionConstants.LAUNCH_ANGLE_TOLERANCE){
      return true;
    }
    return false;
  }

  public boolean isGoodAmpAngle(){
    double diff = getAngle() - aprilAngle;
    if (Math.abs(diff) < VisionConstants.AMP_ANGLE_TOLERANCE){
      return true;
    }
    return false;
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Pivot Value", getAngle());
    SmartDashboard.putNumber("April Arm Angle", getArmAprilAngle());
    SmartDashboard.putBoolean("isGoodAmpAngle", isGoodAmpAngle());
    SmartDashboard.putBoolean("isGoodLaunchAngle", isGoodLaunchAngle());

    
    if (isGoodAmpAngle()) {
      LEDStrip.request(SubsystemsPriority.ARM, LEDStrip.AMP_ANGLE);
    } else if (isGoodLaunchAngle()) {
      LEDStrip.request(SubsystemsPriority.ARM, LEDStrip.SPEAKER_ANGLE);
    }
    

  }   
}
