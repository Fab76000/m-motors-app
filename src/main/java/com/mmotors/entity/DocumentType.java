package com.mmotors.entity;


/**
 * Types de documents justificatifs (KYC)
 */
public enum DocumentType {
    /**
     * Pièces d'identité (CNI, passeport)
     */
    PIECE_ID,

    /**
     * Justificatif de domicile
     */
    JUSTIFICATIF_DOMICILE,
    /**
    * Avis d'imposition
    */

    AVIS_IMPOT,

    /**
    * Relevé d'identité bancaire
    */

    RIB,

    /**
     * Bulletin de salaire
     */
    BULLETIN_SALAIRE
}