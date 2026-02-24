public class ClassroomController {
    private final DeviceRegistry reg;

    public ClassroomController(DeviceRegistry reg) { this.reg = reg; }

    public void startClass() {
        Powerable pj = reg.getFirstOfType("Projector");
        pj.powerOn();
        ((InputConnectable) pj).connectInput("HDMI-1");

        Powerable lights = reg.getFirstOfType("LightsPanel");
        ((BrightnessControllable) lights).setBrightness(60);

        Powerable ac = reg.getFirstOfType("AirConditioner");
        ((TemperatureControllable) ac).setTemperatureC(24);

        Powerable scan = reg.getFirstOfType("AttendanceScanner");
        System.out.println("Attendance scanned: present=" + ((AttendanceScannable) scan).scanAttendance());
    }

    public void endClass() {
        System.out.println("Shutdown sequence:");
        reg.getFirstOfType("Projector").powerOff();
        reg.getFirstOfType("LightsPanel").powerOff();
        reg.getFirstOfType("AirConditioner").powerOff();
    }
}
