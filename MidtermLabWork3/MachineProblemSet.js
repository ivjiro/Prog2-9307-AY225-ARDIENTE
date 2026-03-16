/**
 * =====================================================================
 *  Programming 2 – Machine Problem Set (node.js ver)
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
 *  Run with: node MachineProblemSet.js
 *
 *  Course  : Programming 2 BSCS - DS
 * Name  : Ardiente, Sofhia Nicole I.
 * =====================================================================
 */

"use strict";

const fs       = require("fs");        // Node.js built-in: reads files from disk
const path     = require("path");      // Node.js built-in: resolves file paths
const readline = require("readline"); // Node.js built-in: prompts user in terminal

// ── Shared dataset – loaded once, shared by all three MPs ────────────────────
let headers = []; // Column names from the real header row
let records = []; // All data rows as string arrays

// ── Single readline interface for the whole program ───────────────────────────
const rl = readline.createInterface({ input: process.stdin, output: process.stdout });

/**
 * prompt(question)
 * Wraps rl.question in a Promise so the program can use async/await cleanly.
 *
 * @param {string} question - Text shown to the user in the terminal
 * @returns {Promise<string>} - Resolves with the user's trimmed input
 */
function prompt(question) {
    return new Promise(resolve => rl.question(question, ans => resolve(ans.trim())));
}

// =============================================================================
//  CSV PARSER
//  The Pearson VUE export has 6 metadata rows before the real "Candidate" header.
//  parseCSV skips everything until that header row, then collects data rows.
// =============================================================================

/**
 * parseCSV(content)
 * Converts raw file text into headers + records arrays.
 * Handles quoted fields (which may contain commas, e.g. candidate names).
 * Skips the metadata rows at the top of the Pearson VUE export.
 *
 * @param {string} content - Full text content of the CSV file
 * @returns {{ headers: string[], records: string[][] }}
 */
function parseCSV(content) {
    // Remove UTF-8 BOM if present, normalise line endings, drop blank lines
    const lines = content
        .replace(/^\uFEFF/, "")          // Strip BOM
        .split(/\r?\n/)
        .filter(l => l.trim() !== "");   // Remove blank lines

    let parsedHeaders = [];
    const parsedRecords = [];
    let headerFound = false; // Flag: true once we locate the "Candidate" row

    for (const line of lines) {
        const fields = splitCSVLine(line); // Parse one line into fields

        if (!headerFound) {
            // Look for the row whose first field is "Candidate"
            if (fields.length > 0 && fields[0].toLowerCase() === "candidate") {
                parsedHeaders = fields;  // This row is our column header
                headerFound = true;
            }
            // All rows before the header are silently skipped
        } else {
            // Skip padding rows (empty first field)
            if (!fields[0] || fields[0].trim() === "") continue;
            parsedRecords.push(fields); // Valid data row
        }
    }

    return { headers: parsedHeaders, records: parsedRecords };
}

/**
 * splitCSVLine(line)
 * Splits one raw CSV line into an array of trimmed strings.
 * Handles fields wrapped in double-quotes (which may contain commas).
 *
 * @param {string} line - One raw line from the CSV file
 * @returns {string[]}  - Array of field values
 */
function splitCSVLine(line) {
    const fields = [];
    let current  = "";
    let inQuotes = false; // Whether the parser is currently inside a quoted field

    for (let i = 0; i < line.length; i++) {
        const ch = line[i];
        if (ch === '"') {
            inQuotes = !inQuotes;          // Toggle quoted-field mode
        } else if (ch === "," && !inQuotes) {
            fields.push(current.trim());   // End of a field
            current = "";
        } else {
            current += ch;                 // Accumulate character into current field
        }
    }
    fields.push(current.trim()); // Push the last field
    return fields;
}

// =============================================================================
//  MP07 – Sort Records Alphabetically by a Column
// =============================================================================

/**
 * mp07_sortRecords()
 *
 * Lists all column names with their indices, asks the user to choose one,
 * then sorts a copy of `records` alphabetically (A→Z, case-insensitive) by
 * that column and prints the sorted result as a padded table.
 *
 * Example: sorting by column [0] (Candidate) lists names A→Z.
 *          Sorting by column [3] (Exam) groups records by exam title.
 */
async function mp07_sortRecords() {
    console.log("\n=======================================================");
    console.log("   MP07 – Sort Exam Records Alphabetically by Column");
    console.log("=======================================================");
    printColumnList();

    const colInput = await prompt("\nEnter column number to sort by: ");
    const colIndex = parseInt(colInput, 10); // Convert input string to integer

    // Validate the chosen index
    if (isNaN(colIndex) || colIndex < 0 || colIndex >= headers.length) {
        console.log("[ERROR] Invalid column number.");
        return;
    }

    // Sort a shallow copy so the original `records` array stays unmodified
    const sorted = [...records].sort((a, b) => {
        const valA = (a[colIndex] || "").toLowerCase(); // Lowercase for case-insensitive sort
        const valB = (b[colIndex] || "").toLowerCase();
        return valA.localeCompare(valB);                // localeCompare = natural A→Z ordering
    });

    console.log(`\nSorted by: ${headers[colIndex]}  (${sorted.length} records)`);
    printTable(sorted);
    console.log(`Total records sorted: ${sorted.length}`);
    console.log("=======================================================");
}

// =============================================================================
//  MP08 – Filter Records Using a Keyword
// =============================================================================

/**
 * mp08_filterRecords()
 *
 * Asks the user for a search keyword and performs a case-insensitive scan
 * across every field of every record.  A row is included in the results
 * if at least one of its fields contains the keyword.
 *
 * Handy keywords for this dataset:
 *   "PASS"       – all passing candidates
 *   "FAIL"       – all failing candidates
 *   "Python"     – all Python exam takers
 *   "Faculty"    – only faculty members
 *   "JavaScript" – all JavaScript exam takers
 */
async function mp08_filterRecords() {
    console.log("\n=======================================================");
    console.log("        MP08 – Filter Exam Records by Keyword");
    console.log("=======================================================");
    console.log("  Tips – try: PASS, FAIL, Python, Faculty, JavaScript");

    const keyword = await prompt("\nEnter keyword to search for: ");

    if (!keyword) {
        console.log("[ERROR] Keyword cannot be empty.");
        return;
    }

    const lowerKeyword = keyword.toLowerCase(); // Lowercase once; reused in comparisons

    // Keep rows where at least one cell contains the keyword
    const filtered = records.filter(row =>
        row.some(cell => cell.toLowerCase().includes(lowerKeyword))
    );

    console.log(`\nKeyword: "${keyword}"  |  Matches: ${filtered.length} of ${records.length} records`);
    console.log("-".repeat(55));

    if (filtered.length === 0) {
        console.log(`No records matched "${keyword}".`);
    } else {
        printTable(filtered);
        console.log(`Showing ${filtered.length} matching record(s) for: "${keyword}"`);
    }
    console.log("=======================================================");
}

// =============================================================================
//  MP09 – Display Dataset Statistics
// =============================================================================

/**
 * mp09_statistics()
 *
 * Scans every column and computes:
 *   • All columns  : non-empty count, empty count
 *   • Numeric cols : min, max, sum, average  (auto-detected; Score qualifies)
 *   • Text cols    : unique value count, most frequent value
 *
 * Then prints a domain-specific "Exam Results Summary" tailored to the
 * Pearson VUE dataset: pass/fail counts, pass rate, most popular exam,
 * unique exam count, and breakdown by candidate role.
 */
function mp09_statistics() {
    console.log("\n=======================================================");
    console.log("         MP09 – Exam Dataset Statistics Report");
    console.log("=======================================================");

    const totalCols = headers.length;

    // Per-column stat containers – one object per column index
    const stats = headers.map(h => ({
        name:       h,          // Column name
        nonEmpty:   0,          // Non-empty cell count
        empty:      0,          // Empty cell count
        allNumeric: true,       // Set to false the moment a non-numeric cell is found
        numVals:    [],         // Collected numeric values (for min/max/sum/avg)
        freq:       {}          // Frequency map: cell value → occurrence count
    }));

    // Scan every record and every column
    for (const row of records) {
        for (let i = 0; i < totalCols; i++) {
            const cell = row[i] !== undefined ? row[i] : ""; // Guard short rows

            if (cell === "") {
                stats[i].empty++;
                stats[i].allNumeric = false; // Empty cell disqualifies the column as numeric
            } else {
                stats[i].nonEmpty++;

                const num = Number(cell); // Attempt numeric conversion
                if (!isNaN(num) && isFinite(num)) {
                    stats[i].numVals.push(num);  // Keep numeric value for calculations
                } else {
                    stats[i].allNumeric = false;  // Non-numeric value found
                }

                // Increment the frequency counter for this cell value
                stats[i].freq[cell] = (stats[i].freq[cell] || 0) + 1;
            }
        }
    }

    // ── General information ───────────────────────────────────────────────────
    console.log("\n[ GENERAL INFORMATION ]");
    console.log("  Institution    : University of Perpetual Help System - Molino");
    console.log(`  Total Records  : ${records.length}`);
    console.log(`  Total Columns  : ${totalCols}`);
    console.log("  Columns:");
    headers.forEach((h, i) => console.log(`    [${i}] ${h}`));

    // ── Per-column statistics ─────────────────────────────────────────────────
    console.log("\n[ PER-COLUMN STATISTICS ]");

    for (let i = 0; i < totalCols; i++) {
        const s = stats[i]; // Alias for the current column's stats
        console.log(`\n  Column [${i}]: ${s.name}`);
        console.log("  " + "-".repeat(42));
        console.log(`    Non-empty values : ${s.nonEmpty}`);
        console.log(`    Empty values     : ${s.empty}`);

        if (s.allNumeric && s.numVals.length > 0) {
            // Numeric column – compute descriptive statistics
            const min = Math.min(...s.numVals);                    // Smallest value
            const max = Math.max(...s.numVals);                    // Largest value
            const sum = s.numVals.reduce((acc, v) => acc + v, 0); // Total sum
            const avg = sum / s.numVals.length;                    // Arithmetic mean

            console.log(`    Type             : Numeric`);
            console.log(`    Minimum          : ${min}`);
            console.log(`    Maximum          : ${max}`);
            console.log(`    Sum              : ${sum}`);
            console.log(`    Average (Mean)   : ${avg.toFixed(2)}`);
        } else {
            // Text column – find the most frequently occurring value
            let topVal = "", topCount = 0;
            for (const [val, count] of Object.entries(s.freq)) {
                if (count > topCount) { topCount = count; topVal = val; }
            }
            console.log(`    Type             : Text`);
            console.log(`    Unique Values    : ${Object.keys(s.freq).length}`);
            console.log(`    Most Frequent    : "${topVal}" (${topCount} times)`);
        }
    }

    // ── Domain-specific Exam Results Summary ─────────────────────────────────
    console.log("\n[ EXAM RESULTS SUMMARY ]");

    let passCount = 0, failCount = 0;
    const examFreq = {}; // Exam name → attempt count
    const roleFreq = {}; // Role (Student/Faculty/NTE) → count

    for (const row of records) {
        // Column 7 = Result (PASS / FAIL)
        if (row[7]) {
            if (row[7].trim().toUpperCase() === "PASS") passCount++;
            else if (row[7].trim().toUpperCase() === "FAIL") failCount++;
        }
        // Column 3 = Exam name
        if (row[3] && row[3].trim()) {
            examFreq[row[3].trim()] = (examFreq[row[3].trim()] || 0) + 1;
        }
        // Column 1 = Role
        if (row[1] && row[1].trim()) {
            roleFreq[row[1].trim()] = (roleFreq[row[1].trim()] || 0) + 1;
        }
    }

    // Find the exam attempted most often
    let topExam = "", topExamCount = 0;
    for (const [exam, count] of Object.entries(examFreq)) {
        if (count > topExamCount) { topExamCount = count; topExam = exam; }
    }

    const passRate = records.length ? (passCount * 100 / records.length).toFixed(1) : "0.0";
    const failRate = records.length ? (failCount * 100 / records.length).toFixed(1) : "0.0";

    console.log(`  Total Candidates : ${records.length}`);
    console.log(`  PASS             : ${passCount}  (${passRate}%)`);
    console.log(`  FAIL             : ${failCount}  (${failRate}%)`);
    console.log(`  Most Taken Exam  : ${topExam} (${topExamCount} candidates)`);
    console.log(`  Unique Exams     : ${Object.keys(examFreq).length}`);
    console.log("\n  Candidates by Role:");
    for (const [role, count] of Object.entries(roleFreq)) {
        console.log(`    ${role.padEnd(12)}: ${count}`);
    }

    console.log("\n=======================================================");
    console.log("               END OF STATISTICS REPORT");
    console.log("=======================================================");
}

// =============================================================================
//  SHARED HELPERS
// =============================================================================

/** Prints the startup banner. */
function printBanner() {
    console.log("=======================================================");
    console.log("    Programming 2 – Machine Problem Set  (Node.js)");
    console.log("    MP07 | MP08 | MP09  –  Pearson VUE Exam Data");
    console.log("    University of Perpetual Help System – Molino");
    console.log("=======================================================");
}

/** Prints the main menu options. */
function printMenu() {
    console.log("\n-------------------------------------------------------");
    console.log("  MAIN MENU");
    console.log("-------------------------------------------------------");
    console.log("  [1] MP07 – Sort Records Alphabetically by Column");
    console.log("  [2] MP08 – Filter Records by Keyword");
    console.log("  [3] MP09 – Display Dataset Statistics");
    console.log("  [0] Exit");
    console.log("-------------------------------------------------------");
}

/** Prints all column indices and names. */
function printColumnList() {
    console.log("Available columns:");
    headers.forEach((h, i) => {
        if (i > 8) return; // Skip trailing empty columns
        console.log(`  [${i}] ${h}`);
    });
}

/**
 * printTable(rows)
 * Calculates the maximum display width per column (header vs. data),
 * then prints every row left-aligned with padding for clean alignment.
 *
 * @param {string[][]} rows - Data rows to display
 */
function printTable(rows) {
    // Calculate column widths
    const widths = headers.map((h, i) => {
        const maxData = rows.reduce((max, row) => {
            const cell = row[i] !== undefined ? row[i] : "";
            return Math.max(max, cell.length);
        }, 0);
        return Math.max(h.length, maxData); // Use larger of header or data width
    });

    const pad       = (str, len) => str + " ".repeat(Math.max(0, len - str.length));
    const separator = widths.map(w => "-".repeat(w + 2)).join("");

    // Header + separator
    console.log(headers.map((h, i) => pad(h, widths[i] + 2)).join(""));
    console.log(separator);

    // Data rows
    rows.forEach(row => {
        console.log(
            headers.map((_, i) => pad(row[i] !== undefined ? row[i] : "", widths[i] + 2)).join("")
        );
    });
    console.log(separator);
}

// =============================================================================
//  MAIN – self-invoking async function (enables top-level await)
// =============================================================================

(async () => {
    printBanner();

    // Step 1 – Ask user for the dataset file path
    const filePath = await prompt("Enter the dataset file path: ");

    // Step 2 – Read the file from disk
    let content; // Raw text content of the CSV file
    try {
        content = fs.readFileSync(path.resolve(filePath), "utf8");
    } catch (err) {
        console.error(`[ERROR] Could not read file: ${err.message}`);
        rl.close();
        return;
    }

    // Step 3 – Parse CSV into headers + records
    const parsed = parseCSV(content);
    headers = parsed.headers;
    records = parsed.records;

    if (headers.length === 0 || records.length === 0) {
        console.error("[ERROR] No valid data found. Check the file format.");
        rl.close();
        return;
    }

    console.log(`\n[OK] Loaded ${records.length} exam records (${headers.length} columns) from:`);
    console.log(`     ${filePath}`);

    // Step 4 – Main menu loop; runs until user enters 0
    let running = true;
    while (running) {
        printMenu();
        const choice = await prompt("Select option: ");

        switch (choice) {
            case "1": await mp07_sortRecords();   break;
            case "2": await mp08_filterRecords(); break;
            case "3":       mp09_statistics();    break;
            case "0":
                console.log("\nGoodbye!");
                running = false;
                break;
            default:
                console.log("[!] Invalid option. Enter 1, 2, 3, or 0.");
        }
    }

    rl.close(); // Close the readline interface cleanly on exit
})();