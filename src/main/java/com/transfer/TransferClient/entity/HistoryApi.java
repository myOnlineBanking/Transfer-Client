package com.transfer.TransferClient.entity;
import lombok.*;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryApi implements Serializable {

    private String userId;

    private String compteId;

    private String message ;


}
