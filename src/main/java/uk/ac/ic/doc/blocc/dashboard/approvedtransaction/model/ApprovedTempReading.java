package uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model;

public record ApprovedTempReading(long timestamp, float temperature, int approvals, String txId) {
}
