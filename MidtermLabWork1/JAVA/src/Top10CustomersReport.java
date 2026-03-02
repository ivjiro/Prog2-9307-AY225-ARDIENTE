// ============================================================
//  PROGRAMMING 2 – MACHINE PROBLEM
//  University of Perpetual Help System DALTA – Molino Campus
//  BS Computer Science – Data Science
//
//  Top10CustomersReport.java
//  Scenario : Marketing needs top revenue contributors.
//  Dataset  : vgchartz-2024.csv
//  "Customer" is mapped to -> publisher
//  "Amount"   is mapped to -> total_sales (in millions)
// ============================================================

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Top10CustomersReport {

    // Column names mapped to vgchartz-2024.csv
    private static final String CUSTOMER_COL = "publisher";
    private static final String AMOUNT_COL   = "total_sales";

    // ─────────────────────────────────────────────────────────
    // METHOD: validateFile
    // Checks if file exists, is readable, and ends with .csv
    // Returns null if valid, or an error message string if not.
    // ─────────────────────────────────────────────────────────
    public static String validateFile(String filePath) {
        File file = new File(filePath.trim());

        if (!file.exists()) {
            return "File not found: \"" + file.getAbsolutePath() + "\"";
        }

        if (!file.canRead()) {
            return "File is not readable: \"" + file.getAbsolutePath() + "\"";
        }

        if (!filePath.trim().toLowerCase().endsWith(".csv")) {
            return "File must have a .csv extension.";
        }

        // Check if first line has commas (basic CSV validation)
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine == null || !firstLine.contains(",")) {
                return "File does not appear to be a valid CSV (no commas found in header).";
            }
        } catch (IOException e) {
            return "Could not read file: " + e.getMessage();
        }

        return null; // null means valid
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: parseAndAggregate
    // Reads the CSV using BufferedReader, aggregates total_sales
    // per publisher, and returns a map of publisher -> total.
    // ─────────────────────────────────────────────────────────
    public static Map<String, Double> parseAndAggregate(String filePath) throws IOException {
        Map<String, Double> salesMap = new HashMap<>();
        int totalRecords = 0;
        int skippedRows  = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath.trim()))) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty.");
            }

            // Parse headers
            String[] headers = headerLine.split(",");
            int customerIndex = -1;
            int amountIndex   = -1;

            for (int i = 0; i < headers.length; i++) {
                String h = headers[i].trim().toLowerCase();
                if (h.equals(CUSTOMER_COL)) customerIndex = i;
                if (h.equals(AMOUNT_COL))   amountIndex   = i;
            }

            if (customerIndex == -1) {
                throw new IOException("Column \"" + CUSTOMER_COL + "\" not found in CSV headers.");
            }
            if (amountIndex == -1) {
                throw new IOException("Column \"" + AMOUNT_COL + "\" not found in CSV headers.");
            }

            // Read data rows
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] values = line.split(",");

                try {
                    if (values.length <= Math.max(customerIndex, amountIndex)) {
                        skippedRows++;
                        continue;
                    }

                    String customer  = values[customerIndex].trim();
                    String amountStr = values[amountIndex].trim();

                    if (customer.isEmpty() || amountStr.isEmpty()) {
                        skippedRows++;
                        continue;
                    }

                    double amount = Double.parseDouble(amountStr);
                    salesMap.merge(customer, amount, Double::sum);
                    totalRecords++;

                } catch (NumberFormatException e) {
                    skippedRows++;
                }
            }
        }

        System.out.println("  Records processed : " + String.format("%,d", totalRecords));
        if (skippedRows > 0) {
            System.out.println("  Rows skipped      : " + String.format("%,d", skippedRows) + " (missing/invalid data)");
        }

        return salesMap;
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: getTop10
    // Converts the sales map into a sorted list of CustomerRecord
    // objects and returns the top 10.
    // ─────────────────────────────────────────────────────────
    public static List<CustomerRecord> getTop10(Map<String, Double> salesMap) {
        List<CustomerRecord> records = new ArrayList<>();

        for (Map.Entry<String, Double> entry : salesMap.entrySet()) {
            records.add(new CustomerRecord(entry.getKey(), entry.getValue()));
        }

        Collections.sort(records); // Uses compareTo → descending by totalSales
        return records.subList(0, Math.min(10, records.size()));
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: printReport
    // Displays the formatted Top 10 Customers Report.
    // ─────────────────────────────────────────────────────────
    public static void printReport(List<CustomerRecord> top10) {
        String BORDER  = "=".repeat(62);
        String DIVIDER = "-".repeat(62);

        System.out.println("\n" + BORDER);
        System.out.println("       TOP 10 CUSTOMERS REPORT");
        System.out.println("  University of Perpetual Help System DALTA - Molino Campus");
        System.out.println("  By: Ardiente, Sofhia Nicole I. BSCS-DS");
        System.out.println(BORDER);
        System.out.println("  Dataset  : vgchartz-2024.csv");
        System.out.println("  Customer : Publisher");
        System.out.println("  Metric   : Total Sales (in millions of units)");
        System.out.println(BORDER);
        System.out.printf("  %-6s %-32s %18s%n", "Rank", "Publisher (Customer)", "Total Sales (M)");
        System.out.println(DIVIDER);

        double grandTotal = 0;

        for (int i = 0; i < top10.size(); i++) {
            CustomerRecord record = top10.get(i);
            String rank = "#" + (i + 1);
            String name = record.getCustomerName();

            // Truncate long names
            if (name.length() > 30) {
                name = name.substring(0, 27) + "...";
            }

            System.out.printf("  %-6s %-32s %18s%n",
                rank,
                name,
                record.getFormattedSales()
            );

            grandTotal += record.getTotalSales();
        }

        System.out.println(DIVIDER);
        System.out.printf("  %-40s %18s%n",
            "GRAND TOTAL (Top 10 Publishers):",
            String.format("%,.2f", grandTotal)
        );
        System.out.println(BORDER + "\n");
    }

    // ─────────────────────────────────────────────────────────
    // METHOD: listCsvFiles
    // Scans the current directory for .csv files and lets the
    // user pick one by number instead of typing the full path.
    // Returns the selected file path, or null if none found.
    // ─────────────────────────────────────────────────────────
    // Recursively find all .csv files under a directory
    public static void findCsvFiles(File dir, List<File> result) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                findCsvFiles(f, result);
            } else if (f.getName().toLowerCase().endsWith(".csv")) {
                result.add(f);
            }
        }
    }

    public static String listCsvFiles(Scanner scanner) {
        File currentDir = new File(System.getProperty("user.dir"));
        List<File> csvFiles = new ArrayList<>();
        findCsvFiles(currentDir, csvFiles);

        if (csvFiles.isEmpty()) {
            System.out.println("  [INFO] No CSV files found under:");
            System.out.println("         " + currentDir.getAbsolutePath());
            System.out.println("  You can still type the full file path manually.\n");
            return null;
        }

        System.out.println("  [INFO] CSV files found: (Please select one or enter path manually)");
        System.out.println();
        for (int i = 0; i < csvFiles.size(); i++) {
            System.out.printf("  [%d] %s%n", i + 1, csvFiles.get(i).getName());
            System.out.printf("       %s%n", csvFiles.get(i).getAbsolutePath());
        }
        System.out.println("  [0] Enter file path manually");
        System.out.println();
        System.out.print("  Select a file (enter number): ");

        String input = scanner.nextLine().trim();

        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) return null;
            if (choice >= 1 && choice <= csvFiles.size()) {
                return csvFiles.get(choice - 1).getAbsolutePath();
            } else {
                System.out.println("\n  [WARNING] Invalid selection. Please type the path manually.\n");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("\n  [WARNING] Invalid input. Please type the path manually.\n");
            return null;
        }
    }

    // ─────────────────────────────────────────────────────────
    // MAIN METHOD
    // Entry point - handles user input loop and orchestrates flow.
    // ─────────────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ── Header Banner ──1
        System.out.println("\n+============================================================+");
        System.out.println("|         PROGRAMMING 2 - MIDTERM LAB WORK                   |");
        System.out.println("|  University of Perpetual Help System DALTA                 |");
        System.out.println("|  By: Ardiente, Sofhia Nicole I. BSCS-DS                    |");
        System.out.println("|             Top 10 Customers Report                        |");
        System.out.println("+============================================================+\n");

        String filePath = null;

        // ── File Input Loop (repeats until a valid CSV is provided) ──
        while (true) {

            // Show CSV picker first
            String picked = listCsvFiles(scanner);

            if (picked != null) {
                filePath = picked;
                System.out.println("\n  [OK] Selected: " + new File(filePath).getName());
            } else {
                System.out.print("  Enter dataset file path: ");
                filePath = scanner.nextLine();
            }

            String error = validateFile(filePath);

            if (error != null) {
                System.out.println("\n  [ERROR] " + error + "\n");
                continue;
            }

            // File is valid — try parsing
            try {
                System.out.println("\n  [OK] File found and validated. Loading data...\n");

                Map<String, Double> salesMap = parseAndAggregate(filePath);

                if (salesMap.isEmpty()) {
                    System.out.println("\n  [WARNING] No valid sales data found. Please check your CSV.\n");
                    continue;
                }

                List<CustomerRecord> top10 = getTop10(salesMap);

                System.out.println("\n  [OK] Data processed successfully!\n");

                printReport(top10);
                break; // Done!

            } catch (IOException e) {
                System.out.println("\n  [ERROR] File reading failed: " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.out.println("\n  [ERROR] Unexpected error: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }
}