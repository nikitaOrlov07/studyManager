// Function to hide the alert after a delay
function hideAlertAfterDelay(elementId) {
    var alertMessage = document.getElementById(elementId);
    if (alertMessage) {
        setTimeout(function() {
            alertMessage.style.display = 'none';
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}
