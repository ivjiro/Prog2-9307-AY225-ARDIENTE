const validCredentials = {
            'admin': {
                password: 'password123',
                fullname: 'Sir. Val',
                course: 'Computer Science'
            },
            'student1': {
                password: 'pass123',
                fullname: 'Sofhia Nicole Ardiente',
                course: 'Computer Science'
            },
            'student': {
                password: 'pass456',
                fullname: 'Jeon Jungkook',
                course: 'Business Administration'
            }
        };

let attendanceRecords = [];

        // Load attendance records from storage on page load
        async function loadAttendanceRecords() {
            try {
                const result = await window.storage.get('attendance_records');
                if (result && result.value) {
                    attendanceRecords = JSON.parse(result.value);
                    updateAttendanceList();
                }
            } catch (error) {
                console.log('No existing records found or error loading:', error);
                attendanceRecords = [];
            }
        }

        // Save attendance records to storage
        async function saveAttendanceRecords() {
            try {
                await window.storage.set('attendance_records', JSON.stringify(attendanceRecords));
            } catch (error) {
                console.error('Error saving attendance records:', error);
            }
        }

        // Load records when page loads
        loadAttendanceRecords();

        // Handle form submission
        document.getElementById('loginForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const errorMsg = document.getElementById('errorMessage');

            // Validate credentials
            if (validCredentials[username] && validCredentials[username].password === password) {
                // Successful login
                errorMsg.style.display = 'none';
                
                // Get user details
                const userDetails = validCredentials[username];
                
                // Get current timestamp
                const loginTime = new Date();
                const formattedTime = formatDateTime(loginTime);
                
                // Add to attendance records
                attendanceRecords.push({
                    username: username,
                    fullname: userDetails.fullname,
                    course: userDetails.course,
                    timestamp: formattedTime,
                    rawTime: loginTime
                });

                // Save to persistent storage
                saveAttendanceRecords();

                // Display welcome message
                document.getElementById('welcomeMessage').textContent = `Welcome, ${userDetails.fullname}!`;
                document.getElementById('timestamp').innerHTML = `<strong>Login Time:</strong> ${formattedTime}`;
                
                // Update attendance list
                updateAttendanceList();

                // Show admin controls if user is admin
                if (username === 'admin') {
                    document.getElementById('adminControls').style.display = 'block';
                } else {
                    document.getElementById('adminControls').style.display = 'none';
                }

                // Switch views
                document.getElementById('loginSection').style.display = 'none';
                document.getElementById('welcomeSection').style.display = 'block';
            } else {
                // Failed login - show error and play beep
                errorMsg.style.display = 'block';
                playErrorBeep();
            }
        });

        // Format date and time
        function formatDateTime(date) {
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const year = String(date.getFullYear()).slice(-2);
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const seconds = String(date.getSeconds()).padStart(2, '0');
            
            return `${month}/${day}/${year} ${hours}:${minutes}:${seconds}`;
        }

        // Play error beep sound
        function playErrorBeep() {
            const beep = document.getElementById('errorBeep');
            beep.currentTime = 0;
            beep.play().catch(e => console.log('Audio playback failed:', e));
        }

        // Update attendance list display
        function updateAttendanceList() {
            const listContainer = document.getElementById('attendanceList');
            listContainer.innerHTML = '';

            attendanceRecords.forEach((record, index) => {
                const item = document.createElement('div');
                item.className = 'attendance-item';
                item.innerHTML = `<strong>#${index + 1}</strong> - ${record.fullname} (${record.course}) | Username: ${record.username} | ${record.timestamp}`;
                listContainer.appendChild(item);
            });
        }

        // Download attendance as text file
        function downloadAttendance() {
            let content = 'ATTENDANCE REPORT\n';
            content += '='.repeat(50) + '\n\n';
            content += `Generated on: ${formatDateTime(new Date())}\n`;
            content += `Total logins: ${attendanceRecords.length}\n\n`;
            content += '-'.repeat(50) + '\n\n';

            attendanceRecords.forEach((record, index) => {
                content += `Record #${index + 1}\n`;
                content += `Full Name: ${record.fullname}\n`;
                content += `Course: ${record.course}\n`;
                content += `Username: ${record.username}\n`;
                content += `Login Time: ${record.timestamp}\n\n`;
            });

            // Create blob and download
            const blob = new Blob([content], { type: 'text/plain' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `attendance_report_${Date.now()}.txt`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        }

        // Logout function
        function logout() {
            document.getElementById('loginSection').style.display = 'block';
            document.getElementById('welcomeSection').style.display = 'none';
            document.getElementById('loginForm').reset();
            document.getElementById('errorMessage').style.display = 'none';
        }

        // Clear all attendance records
        async function clearAttendance() {
            if (confirm('Are you sure you want to clear all attendance records? This cannot be undone.')) {
                attendanceRecords = [];
                await saveAttendanceRecords();
                updateAttendanceList();
                alert('All attendance records have been cleared.');
            }
        }