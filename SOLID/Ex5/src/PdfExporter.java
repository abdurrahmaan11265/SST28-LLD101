import java.nio.charset.StandardCharsets;

public class PdfExporter extends Exporter {
    @Override
    public ExportResult export(ExportRequest req) {
        // LSP violation: tightens precondition arbitrarily
        if (req.body != null && req.body.length() > 20) {
            return new ExportResult("PDF cannot handle content > 20 chars", true);
        }
        String fakePdf = "PDF(" + req.title + "):" + req.body;
        return new ExportResult("application/pdf", fakePdf.getBytes(StandardCharsets.UTF_8));
    }
}
