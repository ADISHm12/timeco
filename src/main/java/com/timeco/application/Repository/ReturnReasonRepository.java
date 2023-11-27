package com.timeco.application.Repository;

import com.timeco.application.model.returnReason.ReturnReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnReasonRepository extends JpaRepository<ReturnReason ,Integer> {
}
