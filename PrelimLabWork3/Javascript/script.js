function calculateGrade() {
            // Get input values
            const attendance = parseFloat(document.getElementById('attendance').value);
            const labWork1 = parseFloat(document.getElementById('labWork1').value);
            const labWork2 = parseFloat(document.getElementById('labWork2').value);
            const labWork3 = parseFloat(document.getElementById('labWork3').value);
            
            // Validate inputs
            if (isNaN(attendance) || isNaN(labWork1) || isNaN(labWork2) || isNaN(labWork3)) {
                alert('Please fill in all fields with valid numbers.');
                return;
            }
            
            if (attendance < 0 || attendance > 100 || labWork1 < 0 || labWork1 > 100 || 
                labWork2 < 0 || labWork2 > 100 || labWork3 < 0 || labWork3 > 100) {
                alert('All values must be between 0 and 100.');
                return;
            }
            
            // Computations
            // Lab Work Average = (LW1 + LW2 + LW3) / 3
            const labWorkAverage = (labWork1 + labWork2 + labWork3) / 3;
            
            // Class Standing = (40% * Attendance) + (60% * Lab Work Average)
            const classStanding = (0.40 * attendance) + (0.60 * labWorkAverage);
            
            // Prelim Grade = (70% * Prelim Exam) + (30% * Class Standing)
            // To find required Prelim Exam score:
            // Target Grade = (0.70 * Prelim Exam) + (0.30 * Class Standing)
            // Prelim Exam = (Target Grade - 0.30 * Class Standing) / 0.70
            
            const requiredPrelimForPassing = (75 - (0.30 * classStanding)) / 0.70;
            const requiredPrelimForExcellent = (100 - (0.30 * classStanding)) / 0.70;
            
            // Display results
            let resultsHTML = `
                <div class="result-section">
                    <h3>📋 Input Summary</h3>
                    <div class="result-item">
                        <span class="result-label">Attendance Score:</span>
                        <span class="result-value">${attendance.toFixed(2)}%</span>
                    </div>
                    <div class="result-item">
                        <span class="result-label">Lab Work 1:</span>
                        <span class="result-value">${labWork1.toFixed(2)}%</span>
                    </div>
                    <div class="result-item">
                        <span class="result-label">Lab Work 2:</span>
                        <span class="result-value">${labWork2.toFixed(2)}%</span>
                    </div>
                    <div class="result-item">
                        <span class="result-label">Lab Work 3:</span>
                        <span class="result-value">${labWork3.toFixed(2)}%</span>
                    </div>
                </div>
                
                <div class="result-section">
                    <h3> Computed Values</h3>
                    <div class="result-item">
                        <span class="result-label">Lab Work Average:</span>
                        <span class="result-value">${labWorkAverage.toFixed(2)}%</span>
                    </div>
                    <div class="result-item">
                        <span class="result-label">Class Standing (30%):</span>
                        <span class="result-value">${classStanding.toFixed(2)}%</span>
                    </div>
                </div>
                
                <div class="result-section">
                    <h3> ‼️Required Prelim Exam Scores‼️</h3>
                    <div class="result-item">
                        <span class="result-label">To PASS (75%):</span>
                        <span class="result-value">${requiredPrelimForPassing.toFixed(2)}%</span>
                    </div>
                    <div class="result-item">
                        <span class="result-label">To achieve EXCELLENT (100%):</span>
                        <span class="result-value">${requiredPrelimForExcellent.toFixed(2)}%</span>
                    </div>
                </div>
            `;
            
            // Generate remarks
            let remarksHTML = '<div class="result-section"><h3>💡 Remarks</h3>';
            
            if (requiredPrelimForPassing > 100) {
                remarksHTML += `
                    <div class="remarks danger">
                        <strong>⚠️ Passing Status:</strong><br>
                        Unfortunately, it is mathematically impossible to pass the Prelim period 
                        based on your current Class Standing. The required Prelim Exam score 
                        exceeds 100%.
                    </div>
                `;
            } else if (requiredPrelimForPassing < 0) {
                remarksHTML += `
                    <div class="remarks success">
                        <strong>🎉 Passing Status:</strong><br>
                        Congratulations! Your Class Standing is high enough that you will pass 
                        the Prelim period regardless of your Prelim Exam score!
                    </div>
                `;
            } else {
                remarksHTML += `
                    <div class="remarks">
                        <strong>📝 Passing Status:</strong><br>
                        You need to score at least <strong>${requiredPrelimForPassing.toFixed(2)}%</strong> 
                        on the Prelim Exam to pass the Prelim period.
                    </div>
                `;
            }
            
            if (requiredPrelimForExcellent > 100) {
                remarksHTML += `
                    <div class="remarks danger" style="margin-top: 10px;">
                        <strong>⭐ Excellent Status:</strong><br>
                        Achieving an Excellent grade (100%) is not possible based on your 
                        current Class Standing.
                    </div>
                `;
            } else if (requiredPrelimForExcellent < 0) {
                remarksHTML += `
                    <div class="remarks success" style="margin-top: 10px;">
                        <strong>🌟 Excellent Status:</strong><br>
                        Your Class Standing guarantees an Excellent grade regardless of 
                        Prelim Exam performance!
                    </div>
                `;
            } else {
                remarksHTML += `
                    <div class="remarks" style="margin-top: 10px;">
                        <strong>🌟 Excellent Status:</strong><br>
                        To achieve an Excellent grade, you need 
                        <strong>${requiredPrelimForExcellent.toFixed(2)}%</strong> on the Prelim Exam.
                    </div>
                `;
            }
            
            remarksHTML += '</div>';
            resultsHTML += remarksHTML;
            
            // Display results
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = resultsHTML;
            resultsDiv.style.display = 'block';
            
            // Scroll to results
            resultsDiv.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }
        
        // Allow Enter key to submit
        document.addEventListener('keypress', function(event) {
            if (event.key === 'Enter') {
                calculateGrade();
            }
        });