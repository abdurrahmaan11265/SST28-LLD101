public class ExportResult {
    public final String contentType;
    public final byte[] bytes;
    public final boolean isError;
    public final String errorMessage;

    public ExportResult(String contentType, byte[] bytes) {
        this.contentType = contentType;
        this.bytes = bytes;
        this.isError = false;
        this.errorMessage = null;
    }

    public ExportResult(String errorMessage, boolean isError) {
        this.contentType = "";
        this.bytes = new byte[0];
        this.isError = isError;
        this.errorMessage = errorMessage;
    }
}
