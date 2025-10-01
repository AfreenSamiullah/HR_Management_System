import java.sql.Timestamp;

public class AuditLog {
    private int logId;
    private int empId;
    private String changedBy;
    private String columnName;
    private String oldValue;
    private String newValue;
    private Timestamp changedAt;

    public AuditLog(int logId, int empId, String changedBy, String columnName,
                    String oldValue, String newValue, Timestamp changedAt) {
        this.logId = logId;
        this.empId = empId;
        this.changedBy = changedBy;
        this.columnName = columnName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = changedAt;
    }

    public int getLogId() { return logId; }
    public int getEmpId() { return empId; }
    public String getChangedBy() { return changedBy; }
    public String getColumnName() { return columnName; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public Timestamp getChangedAt() { return changedAt; }
}
