package ResponseModel;

public class responseModel {
    String attendanceMsg,message;

    public String getAttendanceMsg() {
        return attendanceMsg;
    }

    public void setAttendanceMsg(String attendanceMsg) {
        this.attendanceMsg = attendanceMsg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public responseModel(String attendanceMsg, String message) {
        this.attendanceMsg = attendanceMsg;
        this.message = message;
    }

    public responseModel() {
    }
}
