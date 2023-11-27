function checkUserStatus() {
    fetch('/user/status')
        .then(response => {
            if (response.ok) {
                return response.text(); // Get text response
            } else {
                throw new Error('Failed to fetch user status');
            }
        })
        .then(data => {
            if (data === "BLOCKED") {
                Swal.fire({
                    icon: 'error',
                    title: 'You have been blocked!',
                    text: 'Please contact support for further assistance.'
                }).then(() => {
                    fetch('/logout') // Perform logout action
                        .then(() => {
                            window.location.href = '/main/login';
                        })
                        .catch(error => {
                            console.error('Logout error:', error);
                        });
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

window.addEventListener('load', checkUserStatus);

