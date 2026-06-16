package pt.isep.desofs.vendnet.api.dto;

import java.time.LocalDateTime;
import pt.isep.desofs.vendnet.domain.model.slot.Slot;

public record SlotResponse(
		Long id,
		String position,
		int capacity,
		int currentStock,
		Long machineId,
		String machineCode,
		Long productId,
		String productSku,
		LocalDateTime updatedAt) {

	public static SlotResponse from(Slot slot) {
		return new SlotResponse(
				slot.getId(),
				slot.getPosition(),
				slot.getCapacity(),
				slot.getCurrentStock(),
				slot.getMachine().getId(),
				slot.getMachine().getCode(),
				slot.getProduct().getId(),
				slot.getProduct().getSku(),
				slot.getUpdatedAt());
	}
}
