package pt.isep.desofs.vendnet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {

	private String saleId;
	private String status;
	private String transactionRef;
	private String message;
}
