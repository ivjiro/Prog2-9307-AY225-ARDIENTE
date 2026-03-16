/**
 * =====================================================
 * Student Name    : Ardiente, Sofhia Nicole I.
 * Course          : Math 101/ Linear Algebra / Programming 2 
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : March 15, 2025
 * GitHub Repo     :https://github.com/ivjiro/Prog2-9307-AY225-ARDIENTE
 *
 * Description:
 *   This program computes the determinant of a hardcoded 3x3 matrix assigned
 *   to Sofhia Nicole I. Ardiente for Math 101. The solution is computed using
 *   cofactor expansion along the first row. Each intermediate step (2x2 minor,
 *   cofactor term, running sum) is printed to the console in a readable format.
 * =====================================================
 */
public class DeterminantSolver {

    // ── SECTION 1: Matrix Declaration ───────────────────────────────────
    // Declare the 3x3 matrix assigned to this student.
    // Values are hardcoded as a 2D integer array in row-major order.
    static int[][] matrix = {
        { 1, 2, 3 },   // Row 1 of assigned matrix
        { 4, 5, 6 },   // Row 2 of assigned matrix
        { 7, 8, 9 }    // Row 3 of assigned matrix
    };

    // ── SECTION 2: 2×2 Determinant Helper ───────────────────────────────
    // Computes the determinant of a 2x2 matrix given its four elements.
    // Formula: det = (a * d) - (b * c)
    static int computeMinor(int a, int b, int c, int d) {
        return (a * d) - (b * c);
    }

    // ── SECTION 3: Matrix Printer ────────────────────────────────────────
    // Prints the 3x3 matrix to the console in a formatted, readable layout.
    static void printMatrix(int[][] m) {
        System.out.println("┌               ┐");
        for (int[] row : m) {
            System.out.printf("│  %2d  %2d  %2d  │%n", row[0], row[1], row[2]);
        }
        System.out.println("└               ┘");
    }

    // ── SECTION 4: Step-by-Step Determinant Solver ──────────────────────
    // Computes the determinant of a 3x3 matrix using cofactor expansion
    // along the first row. Prints each step clearly.
    static void solveDeterminant(int[][] m) {

        System.out.println("=".repeat(52));
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: Ardiente, Sofhia Nicole I.");
        System.out.println("  Assigned Matrix:");
        System.out.println("=".repeat(52));
        printMatrix(m);
        System.out.println("=".repeat(52));

        // ── Step 1: Compute minor M₁₁ ──
        int minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
        System.out.printf("  Step 1 - Minor M11: det([%d,%d],[%d,%d]) = (%dx%d)-(%dx%d) = %d%n",
            m[1][1], m[1][2], m[2][1], m[2][2],
            m[1][1], m[2][2], m[1][2], m[2][1], minor11);

        // ── Step 2: Compute minor M₁₂ ──
        int minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
        System.out.printf("  Step 2 - Minor M12: det([%d,%d],[%d,%d]) = (%dx%d)-(%dx%d) = %d%n",
            m[1][0], m[1][2], m[2][0], m[2][2],
            m[1][0], m[2][2], m[1][2], m[2][0], minor12);

        // ── Step 3: Compute minor M₁₃ ──
        int minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
        System.out.printf("  Step 3 - Minor M13: det([%d,%d],[%d,%d]) = (%dx%d)-(%dx%d) = %d%n",
            m[1][0], m[1][1], m[2][0], m[2][1],
            m[1][0], m[2][1], m[1][1], m[2][0], minor13);

        // ── Cofactor Terms ──
        int c11 =  m[0][0] * minor11;
        int c12 = -m[0][1] * minor12;
        int c13 =  m[0][2] * minor13;

        System.out.println();
        System.out.printf("  Cofactor C11 = (+1) x %d x %d = %d%n", m[0][0], minor11, c11);
        System.out.printf("  Cofactor C12 = (-1) x %d x %d = %d%n", m[0][1], minor12, c12);
        System.out.printf("  Cofactor C13 = (+1) x %d x %d = %d%n", m[0][2], minor13, c13);

        // ── Final Determinant ──
        int det = c11 + c12 + c13;
        System.out.printf("%n  det(M) = %d + (%d) + %d%n", c11, c12, c13);
        System.out.println("=".repeat(52));
        System.out.printf("    DETERMINANT = %d%n", det);

        // ── Singular Matrix Check ──
        if (det == 0) {
            System.out.println("  Note: The matrix is SINGULAR - it has no inverse.");
        }
        System.out.println("=".repeat(52));
    }

    // ── SECTION 5: Entry Point ───────────────────────────────────────────
    public static void main(String[] args) {
        solveDeterminant(matrix);
    }

}