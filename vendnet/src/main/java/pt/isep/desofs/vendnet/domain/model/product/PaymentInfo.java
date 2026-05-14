package pt.isep.desofs.vendnet.domain.model.product;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfo {

    private String method;

    private String transactionRef;

    private String status;
}
