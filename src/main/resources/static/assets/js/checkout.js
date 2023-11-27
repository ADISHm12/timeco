



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
                window.location.href = "/orderPlaced";
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
                rzp1.on('payment.failed', function (response) {
                    // Handle payment failure
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
    alert("function called..");
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

