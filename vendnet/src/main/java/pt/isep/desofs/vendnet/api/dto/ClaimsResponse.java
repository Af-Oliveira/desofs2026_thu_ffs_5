package pt.isep.desofs.vendnet.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimsResponse {

    private String subject;
    private String role;
    private Date issuedAt;
    private Date expiration;
}
