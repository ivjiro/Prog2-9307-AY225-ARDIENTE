// ============================================================
//  PROGRAMMING 2 – MACHINE PROBLEM
//  University of Perpetual Help System DALTA – Molino Campus
//  BS Computer Science – Data Science
//
//  CustomerRecord.java
//  Represents one aggregated publisher/customer sales record.
// ============================================================

public class CustomerRecord implements Comparable<CustomerRecord> {

    private String customerName;
    private double totalSales;

    // ── Constructor ──────────────────────────────────────────
    public CustomerRecord(String customerName, double totalSales) {
        this.customerName = customerName;
        this.totalSales   = totalSales;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getCustomerName() {
        return customerName;
    }

    public double getTotalSales() {
        return totalSales;
    }

    // ── Add to running total ─────────────────────────────────
    public void addSales(double amount) {
        this.totalSales += amount;
    }

    // ── Formatted sales string ───────────────────────────────
    public String getFormattedSales() {
        return String.format("%,.2f", totalSales);
    }

    // ── Sort descending by totalSales ────────────────────────
    @Override
    public int compareTo(CustomerRecord other) {
        return Double.compare(other.totalSales, this.totalSales);
    }

    // ── toString for debugging ───────────────────────────────
    @Override
    public String toString() {
        return "CustomerRecord{name='" + customerName + "', totalSales=" + totalSales + "}";
    }
}
