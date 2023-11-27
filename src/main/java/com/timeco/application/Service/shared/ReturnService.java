package com.timeco.application.Service.shared;

import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.timeco.application.Repository.*;
import com.timeco.application.model.order.OrderItem;
import com.timeco.application.model.order.PaymentMethod;
import com.timeco.application.model.order.PurchaseOrder;
import com.timeco.application.model.product.Product;
import com.timeco.application.model.returnReason.ReturnReason;
import com.timeco.application.model.user.User;
import com.timeco.application.model.wallet.Wallet;
import com.timeco.application.model.wallet.WalletTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReturnService {

    @Autowired
    private ReturnReasonRepository returnReasonRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public void processReturnAndRefund(OrderItem orderItem, String returnReason, String method) {
        // Create ReturnReason

        ReturnReason returnReasonEntity = new ReturnReason();
        returnReasonEntity.setReturnReason(returnReason);
        returnReasonEntity.setOrder(orderItem.getOrder());
        returnReasonRepository.save(returnReasonEntity);
        double refundedAmount=0;
        if(orderItem.getOrderedPrice()!=null) {

             refundedAmount = orderItem.getOrderedPrice() * orderItem.getOrderItemCount();
           }else {
            refundedAmount = orderItem.getProduct().getPrice() * orderItem.getOrderItemCount();
        }
            // Get or create the user's wallet (you need to implement this method)
            if (refundedAmount > 0) {
                // Get or create the user's wallet
                Wallet wallet = walletRepository.findByUser(orderItem.getOrder().getUser());

                if (wallet == null) {
                    wallet = new Wallet();
                    wallet.setUser(orderItem.getOrder().getUser());
                }
                wallet.deposit(refundedAmount);
                walletRepository.save(wallet);

                WalletTransaction transaction = new WalletTransaction();
                transaction.setWallet(wallet);
                transaction.setAmount(refundedAmount);
                transaction.setTransactionType(method); // You may customize this based on your transaction types
                transaction.setTransactionTime(LocalDateTime.now());
                walletTransactionRepository.save(transaction);
            }

        Product product = orderItem.getProduct();
        int orderedQuantity = orderItem.getOrderItemCount();
        product.setQuantity(product.getQuantity() + orderedQuantity);


        // Update OrderItem status
        orderItem.setOrderStatus(method); // Adjust the enum based on your OrderStatus values
        productRepository.save(product);
        orderItemRepository.save(orderItem);
        // Other logic as needed
    }

    public void processCancelAndRefund(OrderItem orderItem, String returnReason, String method) {


        ReturnReason returnReasonEntity = new ReturnReason();
        returnReasonEntity.setReturnReason(returnReason);
        returnReasonEntity.setOrder(orderItem.getOrder());
        returnReasonRepository.save(returnReasonEntity);

        Product product = orderItem.getProduct();
        if (product != null) {
            int orderedQuantity = orderItem.getOrderItemCount();
            int currentQuantity = product.getQuantity();

            // Increase the available quantity of the product
            product.setQuantity(currentQuantity + orderedQuantity);

            // Set the order status to "cancelled"
            orderItem.setOrderStatus("cancelled");

            // Refund the payment to the user's wallet if it's an online payment
            PaymentMethod paymentMethod = orderItem.getOrder().getPaymentMethod();
            if (paymentMethod != null && paymentMethod.getPaymentMethodName().equals("ONLINE PAYMENT")) {
                User user = orderItem.getOrder().getUser();

                // Check if the user already has a wallet
                Wallet userWallet = user.getWallet();
                if (userWallet == null) {
                    // Create a new wallet for the user
                    userWallet = new Wallet();
                    userWallet.setUser(user);
                    user.setWallets(userWallet);
                }
                double refundedAmount = 0;
                if(orderItem.getOrderedPrice()!=null) {

                    refundedAmount = orderItem.getOrderedPrice() * orderItem.getOrderItemCount();
                }else {
                    refundedAmount = orderItem.getProduct().getPrice() * orderItem.getOrderItemCount();
                }
//                userWallet.deposit(refundedAmount);
//                walletRepository.save(userWallet);

                WalletTransaction transaction = new WalletTransaction();
                transaction.setWallet(userWallet);
                transaction.setAmount(refundedAmount);
                transaction.setTransactionType(method); // You may customize this based on your transaction types
                transaction.setTransactionTime(LocalDateTime.now());
                walletTransactionRepository.save(transaction);



                // Perform the wallet refund
                userWallet.deposit(refundedAmount);
//                userWallet.recordTransaction(refundedAmount, "Refund");
                // Save the user and wallet entities

                walletRepository.save(userWallet);
                userRepository.save(user);
            }

            // Update the product and order item
            productRepository.save(product);


        }

    }

    private final String razorpayApiKey = "rzp_test_CiR2CrX4sA44LS";
    private final String razorpayApiSecret = "z5RbPNpHnx9le0sSSLCu4DIk";

    public boolean verifyRazorpayPayment(String razorpayPaymentId, String razorpayOrderId, double amount) throws RazorpayException {
        try {
            RazorpayClient  razorpayClient = new RazorpayClient(razorpayApiKey, razorpayApiSecret);

            Payment  payment = razorpayClient.payments .fetch(razorpayPaymentId);

            if (payment != null
                    && payment.get("order_id").equals(razorpayOrderId)
                    && Double.parseDouble(payment.get("amount").toString()) == amount * 100 // Parse amount to double
                    && payment.get("status").equals("captured")) {
                return true;
            }
            return false;
        } catch (RazorpayException e) {
            throw e;
        }
    }

    @Transactional
    public boolean rollbackDeposit(User user, double amount) {
        try {
            Wallet userWallet = user.getWallet();

            if (userWallet != null && userWallet.getBalance() >= amount) {
                userWallet.withdraw(amount);
                walletRepository.save(userWallet);
                List <WalletTransaction> allTransactions = walletTransactionRepository.findAllByWalletOrderByTransactionTimeDesc(userWallet);
                if (!allTransactions.isEmpty()) {
                    WalletTransaction lastTransaction = allTransactions.get(0);

                    lastTransaction.setTransactionType("Failed");
                    walletTransactionRepository.save(lastTransaction);
                    return true;
                } else {

                    return false;
                }
            } else {

                return false;
            }
        } catch (Exception e) {

            return false;
        }
    }

}
