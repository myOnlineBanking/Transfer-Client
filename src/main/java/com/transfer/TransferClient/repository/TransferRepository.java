package com.transfer.TransferClient.repository;

import com.transfer.TransferClient.entity.Transfer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


@RepositoryRestResource
public interface TransferRepository extends MongoRepository<Transfer, String> {
    List<Transfer> getTransferByAccountFrom(@Param(value = "accountId") String accountId);
    Transfer findByAccountTo(@Param(value = "accountTo") String accountTo);

}

