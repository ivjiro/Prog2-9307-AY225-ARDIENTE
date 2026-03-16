import java.io.*;
import java.util.*;

/**
 * =====================================================================
 *  Programming 2 – Machine Problem Set (Java version)
 *  MP07 | MP08 | MP09  –  Pearson VUE Exam Results CSV Processing
 * =====================================================================
 *
 *  Dataset columns (real header row starts with "Candidate"):
 *    [0] Candidate       – Full name  (e.g. "Nanete, Ennor")
 *    [1] Student/Faculty – Role       (Student / Faculty / NTE)
 *    [2] Column1         – Unused column in source file
 *    [3] Exam            – Exam title (e.g. Python, Cybersecurity)
 *    [4] Language        – Exam language
 *    [5] Exam Date       – Date taken (MM/DD/YYYY)
 *    [6] Score           – Numeric score
 *    [7] Result          – PASS or FAIL
 *    [8] Time Used       – Duration string (e.g. "36 min 38 sec")
 *
 *  MP07 – Sort records alphabetically by a chosen column
 *  MP08 – Filter records using a keyword (all columns searched)
 *  MP09 – Display dataset statistics (general + per-column + summary)
 *
 *  Compile : javac MachineProblemSet.java
 *  Run     : java  MachineProblemSet
 *
 *  
 *  Course  : Programming 2 BSCS - DS
 *  Name: Ardiente, Sofhia Nicole I.
 * =====================================================================
 */
public class MachineProblemSet {

    // ── Shared dataset – loaded once at startup, used by all MPs ─────────────
    static String[]       headers = null;              // Column header names
    static List<String[]> records = new ArrayList<>(); // All data rows

    // ── Program entry point ───────────────────────────────────────────────────
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Single scanner for all user input

        printBanner();

        // Step 1 - Ask the user to enter the dataset file path
        System.out.print("Enter the dataset file path: ");
        String filePath = scanner.nextLine().trim();

        // Step 2 - Load and parse the CSV file
        if (!loadCSV(filePath)) return; // loadCSV prints its own error; just exit

        System.out.printf("%n[OK] Loaded %d records from: %s%n",
                          records.size(), new File(filePath).getName());

        // Step 3 - Main menu loop; keep running until user enters 0
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": mp07_sortRecords(scanner);   break;
                case "2": mp08_filterRecords(scanner); break;
                case "3": mp09_statistics();           break;
                case "0":
                    System.out.println("\nGoodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[!] Invalid option. Enter 1, 2, 3, or 0.");
            }
        }
    }


    // =========================================================================
    //  CSV LOADER
    //  The Pearson VUE export has 6 non-data rows before the real header.
    //  We skip everything until we find the line whose first field is "Candidate".
    // =========================================================================

    /**
     * loadCSV(filePath)
     * Opens the file, skips metadata rows, finds the "Candidate" header row,
     * and stores all subsequent non-blank lines as data records.
     *
     * @param filePath  Path supplied by the user
     * @return true on success, false on any error
     */
    static boolean loadCSV(String filePath) {
        records.clear();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {

            String line;
            boolean headerFound = false; // Becomes true once we locate the header row

            while ((line = br.readLine()) != null) {
                // Strip the UTF-8 Byte Order Mark if present at the start of the file
                if (line.startsWith("\uFEFF")) line = line.substring(1);

                String[] fields = splitCSVLine(line); // Parse line into fields

                if (!headerFound) {
                    // Search for the row that begins with "Candidate"
                    if (fields.length > 0 && fields[0].equalsIgnoreCase("Candidate")) {
                        headers = fields;    // This row contains our column names
                        headerFound = true;
                    }
                    // All rows before the header are skipped silently
                } else {
                    // Skip blank or padding rows that appear after the data
                    if (line.trim().isEmpty() || fields[0].trim().isEmpty()) continue;
                    records.add(fields); // Valid data row – add to list
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File not found: " + filePath);
            return false;
        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + e.getMessage());
            return false;
        }

        if (headers == null || records.isEmpty()) {
            System.out.println("[ERROR] No valid data found. Check the file format.");
            return false;
        }
        return true;
    }

    /**
     * splitCSVLine(line)
     * Splits one raw CSV line into trimmed field strings.
     * Correctly handles fields enclosed in double-quotes (which may contain commas).
     *
     * @param line  One raw line from the file
     * @return      Array of field strings with surrounding quotes removed
     */
    static String[] splitCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false; // Whether the parser is inside a quoted field

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                inQuotes = !inQuotes;           // Toggle quote mode
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString().trim()); // Field boundary reached
                current.setLength(0);                  // Reset the buffer
            } else {
                current.append(ch);                    // Accumulate character
            }
        }
        fields.add(current.toString().trim()); // Push the last field
        return fields.toArray(new String[0]);
    }

    // =========================================================================
    //  MP07 – Sort Records Alphabetically by a Column
    // =========================================================================

    /**
     * mp07_sortRecords(scanner)
     *
     * Lists all columns, prompts the user to choose one by index, then sorts
     * a copy of the loaded records alphabetically (A→Z, case-insensitive) by
     * that column and prints the sorted results as a padded table.
     *
     * Example: sorting by column [0] (Candidate) lists names A→Z.
     *          Sorting by column [3] (Exam) groups records by exam title.
     *
     * @param scanner  Shared Scanner for reading terminal input
     */
    static void mp07_sortRecords(Scanner scanner) {
        System.out.println("\n=======================================================");
        System.out.println("   MP07 – Sort Exam Records Alphabetically by Column");
        System.out.println("=======================================================");
        printColumnList();

        System.out.print("\nEnter column number to sort by: ");
        int col = readColumnIndex(scanner);
        if (col < 0) return; // readColumnIndex already printed the error

        final int ci = col; // Effectively-final for lambda

        // Sort a copy so the original `records` list stays unmodified
        List<String[]> sorted = new ArrayList<>(records);
        sorted.sort(Comparator.comparing(
            row -> ci < row.length ? row[ci].toLowerCase() : ""
        ));

        System.out.printf("%nSorted by: %s  (%d records)%n", headers[ci], sorted.size());
        printTable(sorted);
        System.out.printf("Total records sorted: %d%n", sorted.size());
        System.out.println("=======================================================");
    }

    // =========================================================================
    //  MP08 – Filter Records Using a Keyword
    // =========================================================================

    /**
     * mp08_filterRecords(scanner)
     *
     * Prompts for a search keyword and performs a case-insensitive scan
     * across every field of every record.  A row is included in results
     * if any of its fields contain the keyword.
     *
     * Handy keywords for this dataset:
     *   "PASS"       – all passing candidates
     *   "FAIL"       – all failing candidates
     *   "Python"     – all Python exam takers
     *   "Faculty"    – only faculty members
     *   "JavaScript" – all JavaScript exam takers
     *
     * @param scanner  Shared Scanner for reading terminal input
     */
    static void mp08_filterRecords(Scanner scanner) {
        System.out.println("\n=======================================================");
        System.out.println("        MP08 – Filter Exam Records by Keyword");
        System.out.println("=======================================================");
        System.out.println("  Tips – try: PASS, FAIL, Python, Faculty, JavaScript");

        System.out.print("\nEnter keyword to search for: ");
        String keyword = scanner.nextLine().trim().toLowerCase(); // Lowercase for comparison

        if (keyword.isEmpty()) {
            System.out.println("[ERROR] Keyword cannot be empty.");
            return;
        }

        // Filter: keep rows that have at least one cell matching the keyword
        List<String[]> filtered = new ArrayList<>();
        for (String[] row : records) {
            for (String cell : row) {
                if (cell.toLowerCase().contains(keyword)) {
                    filtered.add(row);
                    break; // Match found – no need to check remaining cells
                }
            }
        }

        System.out.printf("%nKeyword: \"%s\"  |  Matches: %d of %d records%n",
                          keyword, filtered.size(), records.size());
        System.out.println("-".repeat(55));

        if (filtered.isEmpty()) {
            System.out.printf("No records matched \"%s\".%n", keyword);
        } else {
            printTable(filtered);
            System.out.printf("Showing %d matching record(s) for: \"%s\"%n",
                              filtered.size(), keyword);
        }
        System.out.println("=======================================================");
    }

    // =========================================================================
    //  MP09 – Display Dataset Statistics
    // =========================================================================

    /**
     * mp09_statistics()
     *
     * Scans every column and computes:
     *   • All columns  : non-empty count, empty count
     *   • Numeric cols : min, max, sum, average  (auto-detected; Score qualifies)
     *   • Text cols    : unique value count, most frequent value
     *
     * Then prints a domain-specific "Exam Results Summary" tailored to the
     * Pearson VUE dataset: overall pass rate, most popular exam, unique exam count.
     */
    static void mp09_statistics() {
        System.out.println("\n=======================================================");
        System.out.println("         MP09 – Exam Dataset Statistics Report");
        System.out.println("=======================================================");

        int totalCols = headers.length;

        // Per-column accumulators
        int[]     nonEmpty  = new int[totalCols];     // Non-empty cell count
        int[]     empty     = new int[totalCols];     // Empty cell count
        boolean[] isNumeric = new boolean[totalCols]; // All non-empty values numeric?
        double[]  numMin    = new double[totalCols];
        double[]  numMax    = new double[totalCols];
        double[]  numSum    = new double[totalCols];
        int[]     numCount  = new int[totalCols];

        // Frequency maps for text columns: value → occurrence count
        @SuppressWarnings("unchecked")
        Map<String, Integer>[] freq = new LinkedHashMap[totalCols];

        // Initialise before scanning
        Arrays.fill(isNumeric, true);
        Arrays.fill(numMin, Double.MAX_VALUE);
        Arrays.fill(numMax, -Double.MAX_VALUE);
        for (int i = 0; i < totalCols; i++) freq[i] = new LinkedHashMap<>();

        // Scan every record cell-by-cell
        for (String[] row : records) {
            for (int i = 0; i < totalCols; i++) {
                String cell = (i < row.length) ? row[i].trim() : "";

                if (cell.isEmpty()) {
                    empty[i]++;
                    isNumeric[i] = false; // Empty cell disqualifies column as numeric
                } else {
                    nonEmpty[i]++;
                    try {
                        double val = Double.parseDouble(cell); // Attempt numeric parse
                        numMin[i]  = Math.min(numMin[i], val);
                        numMax[i]  = Math.max(numMax[i], val);
                        numSum[i] += val;
                        numCount[i]++;
                    } catch (NumberFormatException e) {
                        isNumeric[i] = false; // Non-numeric value found in this column
                    }
                    freq[i].merge(cell, 1, Integer::sum); // Increment frequency for value
                }
            }
        }

        // ── General information ───────────────────────────────────────────────
        System.out.println("\n[ GENERAL INFORMATION ]");
        System.out.printf("  Institution    : University of Perpetual Help System - Molino%n");
        System.out.printf("  Total Records  : %d%n", records.size());
        System.out.printf("  Total Columns  : %d%n", totalCols);
        System.out.println("  Columns:");
        for (int i = 0; i < totalCols; i++) {
            System.out.printf("    [%d] %s%n", i, headers[i]);
        }

        // ── Per-column statistics ─────────────────────────────────────────────
        System.out.println("\n[ PER-COLUMN STATISTICS ]");
        for (int i = 0; i < totalCols; i++) {
            System.out.printf("%n  Column [%d]: %s%n", i, headers[i]);
            System.out.println("  " + "-".repeat(42));
            System.out.printf("    Non-empty values : %d%n", nonEmpty[i]);
            System.out.printf("    Empty values     : %d%n", empty[i]);

            if (isNumeric[i] && numCount[i] > 0) {
                // Numeric column – print descriptive statistics
                double avg = numSum[i] / numCount[i]; // Mean = total sum / count
                System.out.printf("    Type             : Numeric%n");
                System.out.printf("    Minimum          : %.0f%n", numMin[i]);
                System.out.printf("    Maximum          : %.0f%n", numMax[i]);
                System.out.printf("    Sum              : %.0f%n", numSum[i]);
                System.out.printf("    Average (Mean)   : %.2f%n", avg);
            } else {
                // Text column – find most frequent value
                String topVal   = "";
                int    topCount = 0;
                for (Map.Entry<String, Integer> e : freq[i].entrySet()) {
                    if (e.getValue() > topCount) {
                        topCount = e.getValue();
                        topVal   = e.getKey();
                    }
                }
                System.out.printf("    Type             : Text%n");
                System.out.printf("    Unique Values    : %d%n", freq[i].size());
                System.out.printf("    Most Frequent    : \"%s\" (%d times)%n",
                                  topVal, topCount);
            }
        }

        // ── Domain-specific Exam Results Summary ──────────────────────────────
        System.out.println("\n[ EXAM RESULTS SUMMARY ]");

        int passCount = 0, failCount = 0;
        Map<String, Integer> examFreq = new LinkedHashMap<>(); // Exam → attempt count
        Map<String, Integer> roleFreq = new LinkedHashMap<>(); // Role → count

        for (String[] row : records) {
            // Column 7 = Result (PASS/FAIL)
            if (row.length > 7 && !row[7].trim().isEmpty()) {
                if ("PASS".equalsIgnoreCase(row[7].trim())) passCount++;
                else if ("FAIL".equalsIgnoreCase(row[7].trim())) failCount++;
            }
            // Column 3 = Exam name
            if (row.length > 3 && !row[3].trim().isEmpty()) {
                examFreq.merge(row[3].trim(), 1, Integer::sum);
            }
            // Column 1 = Role (Student / Faculty / NTE)
            if (row.length > 1 && !row[1].trim().isEmpty()) {
                roleFreq.merge(row[1].trim(), 1, Integer::sum);
            }
        }

        // Find the exam with the most attempts
        String topExam = ""; int topExamCount = 0;
        for (Map.Entry<String, Integer> e : examFreq.entrySet()) {
            if (e.getValue() > topExamCount) {
                topExamCount = e.getValue();
                topExam      = e.getKey();
            }
        }

        double passRate = records.isEmpty() ? 0 : (passCount * 100.0 / records.size());

        System.out.printf("  Total Candidates : %d%n", records.size());
        System.out.printf("  PASS             : %d  (%.1f%%)%n", passCount, passRate);
        System.out.printf("  FAIL             : %d  (%.1f%%)%n", failCount, 100.0 - passRate);
        System.out.printf("  Most Taken Exam  : %s (%d candidates)%n", topExam, topExamCount);
        System.out.printf("  Unique Exams     : %d%n", examFreq.size());

        System.out.println("\n  Candidates by Role:");
        for (Map.Entry<String, Integer> e : roleFreq.entrySet()) {
            System.out.printf("    %-10s : %d%n", e.getKey(), e.getValue());
        }

        System.out.println("\n=======================================================");
        System.out.println("               END OF STATISTICS REPORT");
        System.out.println("=======================================================");
    }

    // =========================================================================
    //  SHARED HELPERS
    // =========================================================================

    /** Prints the startup banner. */
    static void printBanner() {
        System.out.println("=======================================================");
        System.out.println("    Programming 2 – Machine Problem Set  (Java)");
        System.out.println("    MP07 | MP08 | MP09  –  Pearson VUE Exam Data");
        System.out.println("    University of Perpetual Help System – Molino");
        System.out.println("=======================================================");
    }

    /** Prints the main menu options. */
    static void printMenu() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("  MAIN MENU");
        System.out.println("-------------------------------------------------------");
        System.out.println("  [1] MP07 – Sort Records Alphabetically by Column");
        System.out.println("  [2] MP08 – Filter Records by Keyword");
        System.out.println("  [3] MP09 – Display Dataset Statistics");
        System.out.println("  [0] Exit");
        System.out.println("-------------------------------------------------------");
    }

    /** Prints all column indices and names. */
    static void printColumnList() {
        System.out.println("Available columns:");
        for (int i = 0; i < headers.length && i <= 8; i++) {
            System.out.printf("  [%d] %s%n", i, headers[i]);
        }
    }

    /**
     * readColumnIndex(scanner)
     * Reads and validates a column index from the user.
     *
     * @return Valid index (0 to headers.length-1), or -1 on invalid input
     */
    static int readColumnIndex(Scanner scanner) {
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 0 || idx >= headers.length) {
                System.out.println("[ERROR] Column index out of range.");
                return -1;
            }
            return idx;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Please enter a valid number.");
            return -1;
        }
    }

    /**
     * printTable(rows)
     * Computes the max width of each column (comparing header and data lengths),
     * then prints every row left-aligned with padding for clean column alignment.
     *
     * @param rows  List of data rows to display
     */
    static void printTable(List<String[]> rows) {
        // Determine display width per column
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) widths[i] = headers[i].length();
        for (String[] row : rows) {
            for (int i = 0; i < headers.length && i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i].length());
            }
        }

        // Build the printf format string from column widths
        StringBuilder fmt = new StringBuilder();
        int totalWidth = 0;
        for (int w : widths) {
            fmt.append("%-").append(w + 2).append("s");
            totalWidth += w + 2;
        }
        String rowFmt = fmt.toString();

        // Header row + separator
        System.out.printf((rowFmt + "%n"), (Object[]) headers);
        System.out.println("-".repeat(totalWidth));

        // Data rows
        for (String[] row : rows) {
            String[] padded = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                padded[i] = (i < row.length) ? row[i] : "";
            }
            System.out.printf((rowFmt + "%n"), (Object[]) padded);
        }
        System.out.println("-".repeat(totalWidth));
    }
}