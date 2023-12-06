

function setupValidation(inputId, feedbackId) {
    const inputElem = document.getElementById(inputId);
    const feedbackElem = document.getElementById(feedbackId);

    inputElem.addEventListener('input', function () {
        if (inputElem.validity.patternMismatch) {
            feedbackElem.textContent = inputElem.title;
        } else {
            const trimmedValue = inputElem.value.trim();
            if (trimmedValue === "") {
                feedbackElem.textContent = "This field cannot be empty or contain only spaces.";
            } else if (trimmedValue[0] === " ") {
                feedbackElem.textContent = "First character cannot be a space.";
            } else if (inputId === 'phoneNumber' && trimmedValue.length != 10) {
                feedbackElem.textContent = "Phone number must be  10 digits.";
            } else {
                feedbackElem.textContent = '';
            }
        }
    });
}


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
            formError.style.display = 'none';
            return true;
        } else {
            formError.style.display = 'block';
            return false;
        }
    } else {
        formError.style.display = 'block';
        return false;
    }
}

