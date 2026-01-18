package com.mmotors.entity;


/**
 * Statut de disponibilité d'un véhicule
 */
public enum VehicleStatus {

    /**
     * Véhicule disponible
     */
    DISPONIBLE,

    /**
     * Véhicule réservé (dossier en cours)
     */
    RESERVE,

    /**
     * Véhicule vendu ou loué
     */
    VENDU
}
