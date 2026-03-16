# Programming 2 – Machine Problem Set
**MP07 | MP08 | MP09 – CSV Dataset Processing**
University of Perpetual Help System – Molino | Pearson VUE Exam Results

---

## Files Included

| File | Description |
|------|-------------|
| `MachineProblemSet.java` | Java implementation of all three MPs |
| `MachineProblemSet.js` | JavaScript (Node.js) implementation of all three MPs |
| `Sample_Data-Prog-2-csv.csv` | Dataset used by the program |

---

## How to Run

### JavaScript
```
node MachineProblemSet.js
```

### Java
```
javac MachineProblemSet.java
java MachineProblemSet
```

When prompted, enter the full path to the CSV file:
```
Enter the dataset file path: C:\Users\yourname\Desktop\Sample_Data-Prog-2-csv.csv
```

---

## Machine Problems

### MP07 – Sort Records Alphabetically by Column
The program displays all available columns and asks the user to choose one by number. It then rearranges all 168 exam records in A to Z order based on the chosen column and prints the sorted table.

### MP08 – Filter Records by Keyword
The program asks the user to enter a keyword and searches every field of every record for a case-insensitive match. All rows containing the keyword in any column are displayed in a table along with a count of how many records matched.

### MP09 – Display Dataset Statistics
The program scans every column and automatically computes statistics. Numeric columns (such as Score) show minimum, maximum, sum, and average. Text columns show the number of unique values and the most frequently occurring value. A summary section also shows the overall pass/fail count and rate.

---

## Program Logic

The program first asks the user to enter the CSV file path, then reads and parses the file using `BufferedReader` and `FileReader` in Java, or the `fs` module in JavaScript, automatically skipping the metadata rows at the top of the Pearson VUE export until the real header row is found. All 168 records are loaded into memory and shared across three machine problems accessible from a single main menu that loops until the user exits. MP07 sorts a copy of the records alphabetically using the column the user selects, MP08 filters records by performing a case-insensitive keyword search across all columns, and MP09 computes per-column statistics by scanning every cell and detecting whether each column contains numeric or text data. Error handling is included throughout for invalid file paths, empty keywords, and out-of-range column selections.

---

## Dataset Columns

| # | Column | Description |
|---|--------|-------------|
| 0 | Candidate | Full name of the exam taker |
| 1 | Student/ Faculty/ NTE | Role type |
| 2 | Column1 | Unused column |
| 3 | Exam | Name of the exam taken |
| 4 | Language | Exam language |
| 5 | Exam Date | Date the exam was taken |
| 6 | Score | Numeric exam score |
| 7 | Result | PASS or FAIL |
| 8 | Time Used | Time spent on the exam |

---

## Sample Keywords for MP08

| Keyword | What it returns |
|---------|----------------|
| `PASS` | All passing candidates |
| `FAIL` | All failing candidates |
| `Python` | All Python exam takers |
| `Faculty` | Faculty members only |
| `JavaScript` | All JavaScript exam takers |
| `HTML and CSS` | All HTML and CSS exam takers |