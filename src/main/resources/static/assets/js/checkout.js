


let rzpWindowOpen = false;

    // Add a click event listener to the "Proceed" button
    document.getElementById("proceedButton").addEventListener("click", function (e) {
    e.preventDefault(); // Prevent the default form submission

    Swal.fire({
    title: 'Are you sure?',
    text: "You won't be able to revert this!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#3085d6',
    cancelButtonColor: '#d33',
    confirmButtonText: 'Yes, proceed!'
}).then((result) => {
    if (result.isConfirmed) {
    handleSubmitForm();
}
});
});

    function handleSubmitForm() {
    $.ajax({
        url: '/placeOrder',
        method: 'post',
        data: $("#addressForm").serialize(),
        success: (response) => {
            var responseData = JSON.parse(response);
            console.log(responseData.isValid);
            if (responseData.isValid === true) {
                if (responseData.error && responseData.error === "Insufficient funds in the wallet") {
                    Swal.fire({
                        icon: 'error',
                        title: 'Insufficient Funds',
                        text: 'Your wallet does not have enough balance.'
                    });
                    return;
                }
                let timerInterval;
                Swal.fire({
                    title: "Please wait!",
                    html: "Finish in <b></b> milliseconds.",
                    timer: 2000,
                    timerProgressBar: true,
                    didOpen: () => {
                        Swal.showLoading();
                        const timer = Swal.getPopup().querySelector("b");
                        timerInterval = setInterval(() => {
                            timer.textContent = `${Swal.getTimerLeft()}`;
                        }, 100);
                    },
                    willClose: () => {
                        clearInterval(timerInterval);
                    }
                }).then((result) => {
                    /* Read more about handling dismissals below */
                    if (result.dismiss === Swal.DismissReason.timer) {
                        console.log("I was closed by the timer");
                        // Redirect to the orderPlaced page
                        window.location.href = "/orderPlaced";
                    }
                });
                return;
            }
            if (responseData.orderId) {
                console.log(responseData.orderId);
                var options = {
                    "key": "rzp_test_CiR2CrX4sA44LS",
                    "amount": responseData.amount,
                    "currency": "INR",
                    "name": "TIMECO",
                    "description": "Test Transaction",
                    "image": "/img/logo/logo.png",
                    "order_id": responseData.orderId,
                    "handler": function (response) {
                        verifyPayment(response, responseData.purchaseId);
                    },
                    "prefill": {
                        "name": responseData.username,
                        "email": responseData.email,
                        "contact": responseData.contact
                    },
                    "notes": {
                        "address": "Razorpay Corporate Office"
                    },
                    "theme": {
                        "color": "#fc1808"
                    }
                };

                var rzp1 = new Razorpay(options);

                rzp1.on('payment.window.open', function () {
                    rzpWindowOpen = true;
                });


                rzp1.on('payment.failed', function (response) {
                });

                rzp1.open();
            }
        }
    });
    }

    function verifyPayment(data, purchaseId) {
    let orderId = data.razorpay_order_id;
    let signature = data.razorpay_signature;
    let paymentId = data.razorpay_payment_id;
    $.ajax({
    url: "/verifyPayment",
    method: "post",
    data: { orderId, signature, paymentId, purchaseId },
    success: (response) => {
    if (response) {
    window.location.href = "/orderPlaced";
}
    alert("something went wrong");
}
});
}

setInterval(function () {
    if (rzpWindowOpen) {
        return;
    }
    $.ajax({
        url: "/deletePayment",
        method: "post",
        data: {
            purchaseId: responseData.purchaseId
        },
        success: function (deletionResponse) {
            console.log('Payment deleted:', deletionResponse);
        },
        error: function (error) {
            console.error('Error deleting payment:', error);
        }
    });
}, 5000);

