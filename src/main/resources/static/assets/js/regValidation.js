

function setupValidation(inputId, feedbackId) {
    const inputElem = document.getElementById(inputId);
    const feedbackElem = document.getElementById(feedbackId);

    inputElem.addEventListener('input', function () {
        if (inputElem.validity.patternMismatch) {
            feedbackElem.textContent = inputElem.title;
        } else {
            const trimmedValue = inputElem.value.trim(); // Trim leading/trailing spaces
            if (trimmedValue === "") {
                feedbackElem.textContent = "This field cannot be empty or contain only spaces.";
            } else if (trimmedValue[0] === " ") {
                feedbackElem.textContent = "First character cannot be a space.";
            } else if (inputId === 'phoneNumber' && trimmedValue.length < 10) {
                feedbackElem.textContent = "Phone number must be at least 10 digits.";
            } else {
                feedbackElem.textContent = ''; // Clear the error message if input is valid
            }
        }
    });
}

// Set up validation for each input
setupValidation('firstName', 'usernameFeedback');
setupValidation('lastName', 'lastnameFeedback');
setupValidation('email', 'emailFeedback');
setupValidation('phoneNumber', 'numberFeedback');
setupValidation('password', 'passwordFeedback');

function validateForm() {
    const formError = document.getElementById('formError');
    formError.style.display = 'none'; // Hide the global error message initially

    const firstName = document.getElementById('firstName');
    const lastName = document.getElementById('lastName');
    const email = document.getElementById('email');
    const phoneNumber = document.getElementById('phoneNumber');
    const password = document.getElementById('password');

    if (
        firstName.validity.valid &&
        lastName.validity.valid &&
        email.validity.valid &&
        phoneNumber.validity.valid &&
        password.validity.valid
    ) {
        if (
            firstName.value.trim() !== "" &&
            lastName.value.trim() !== "" &&
            email.value.trim() !== "" &&
            phoneNumber.value.trim() !== "" &&
            password.value.trim() !== ""
        ) {
            return true; // Allow form submission if all fields are valid and not empty
        } else {
            formError.style.display = 'block'; // Show the global error message if any field is empty
            return false; // Prevent form submission
        }
    } else {
        formError.style.display = 'block'; // Show the global error message if any field is invalid
        return false; // Prevent form submission
    }
}
