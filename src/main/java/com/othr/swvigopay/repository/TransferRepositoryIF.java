package com.othr.swvigopay.repository;

import com.othr.swvigopay.entity.State;
import com.othr.swvigopay.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface TransferRepositoryIF extends PagingAndSortingRepository<Transfer, Long> {
    Transfer findById(long id);
    Page<Transfer> findByPayerAccountIdAndStateIsNotOrReceiverAccountIdAndStateIsNot(long accountId, State state, long accountId2,  State state2, Pageable pageable);
    Page<Transfer> findByPayerAccountIdAndStateOrReceiverAccountIdAndState(long accountId, State state, long accountId2,  State state2, Pageable pageable);
}
