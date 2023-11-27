package com.timeco.application.Repository;

import com.timeco.application.model.user.User;
import com.timeco.application.model.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {


    Wallet findByUser(User user);
}
