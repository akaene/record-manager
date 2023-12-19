package cz.cvut.kbss.study.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of importing records to this instance.
 */
public class RecordImportResult {

    /**
     * Total number of processed records.
     */
    private int totalCount;

    /**
     * Number of successfully imported records.
     */
    private int importedCount;

    /**
     * Errors that occurred during import.
     */
    private List<String> errors;

    public RecordImportResult() {
    }

    public RecordImportResult(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(int importedCount) {
        this.importedCount = importedCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        errors.add(error);
    }

    @Override
    public String toString() {
        return "RecordImportResult{" +
                "totalCount=" + totalCount +
                ", importedCount=" + importedCount +
                (errors != null ? ", errors=" + errors : "") +
                '}';
    }
}
