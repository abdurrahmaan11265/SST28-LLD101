public class SendResult {
    public final boolean isError;
    public final String errorMessage;

    public SendResult() {
        this.isError = false;
        this.errorMessage = null;
    }

    public SendResult(String errorMessage) {
        this.isError = true;
        this.errorMessage = errorMessage;
    }
}
