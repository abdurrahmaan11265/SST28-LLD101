public abstract class Exporter {
    /**
        Contract:
        - req must not be null, title and body must not be null
        - must always return a non-null ExportResult
        - must never throw for any valid ExportRequest
        - subclasses must not add stricter preconditions
    */
    public abstract ExportResult export(ExportRequest req);
}
