package com.mmotors.dto;

import com.mmotors.entity.DocumentType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * DTO pour le formulaire de dépôt de dossier
 */
@Data
public class DossierDTO {

    private Long vehicleId;

    private String type;

    private String paymentMode;

    private Boolean tradeIn;

    private Integer duration;

    private Map<DocumentType, MultipartFile> documents;
}
