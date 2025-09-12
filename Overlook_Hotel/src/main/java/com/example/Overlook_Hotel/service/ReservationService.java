package com.example.Overlook_Hotel.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Overlook_Hotel.model.Chambre;
import com.example.Overlook_Hotel.model.Client;
import com.example.Overlook_Hotel.model.Reservation;
import com.example.Overlook_Hotel.repository.ReservationRepository;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ChambreService chambreService;
    private final ClientService clientService;

    public ReservationService(ReservationRepository reservationRepository, 
                             ChambreService chambreService,
                             ClientService clientService) {
        this.reservationRepository = reservationRepository;
        this.chambreService = chambreService;
        this.clientService = clientService;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Integer id) {
        return reservationRepository.findById(id);
    }

    public Reservation save(Reservation reservation) {
        // Validation des dates
        validateReservationDates(reservation.getDateDebut(), reservation.getDateFin());
        
        // Vérification de la disponibilité de la chambre
        validateChambreAvailability(reservation.getChambre(), reservation.getDateDebut(), reservation.getDateFin());
        
        // Calcul du prix total si non fourni
        if (reservation.getPrixTotal() == null) {
            BigDecimal prixTotal = calculatePrixTotal(reservation.getChambre(), reservation.getDateDebut(), reservation.getDateFin());
            reservation.setPrixTotal(prixTotal);
        }
        
        return reservationRepository.save(reservation);
    }

    public void deleteById(Integer id) {
        reservationRepository.deleteById(id);
    }

    public List<Reservation> findByClient(Client client) {
        return reservationRepository.findByClient(client);
    }

    public Reservation updateReservation(Integer id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));

        // Validation des dates si elles sont modifiées
        if (reservationDetails.getDateDebut() != null || reservationDetails.getDateFin() != null) {
            LocalDate newDateDebut = reservationDetails.getDateDebut() != null ? 
                reservationDetails.getDateDebut() : reservation.getDateDebut();
            LocalDate newDateFin = reservationDetails.getDateFin() != null ? 
                reservationDetails.getDateFin() : reservation.getDateFin();
            
            validateReservationDates(newDateDebut, newDateFin);
            
            // Vérification de la disponibilité si la chambre ou les dates changent
            Chambre chambre = reservationDetails.getChambre() != null ? 
                reservationDetails.getChambre() : reservation.getChambre();
            
            validateChambreAvailability(chambre, newDateDebut, newDateFin, id);
        }

        // Mise à jour des champs
        if (reservationDetails.getDateDebut() != null) {
            reservation.setDateDebut(reservationDetails.getDateDebut());
        }
        
        if (reservationDetails.getDateFin() != null) {
            reservation.setDateFin(reservationDetails.getDateFin());
        }
        
        if (reservationDetails.getPrixTotal() != null) {
            reservation.setPrixTotal(reservationDetails.getPrixTotal());
        }
        
        if (reservationDetails.getStatut() != null) {
            reservation.setStatut(reservationDetails.getStatut());
        }
        
        if (reservationDetails.getClient() != null) {
            reservation.setClient(reservationDetails.getClient());
        }
        
        if (reservationDetails.getChambre() != null) {
            reservation.setChambre(reservationDetails.getChambre());
        }

        return reservationRepository.save(reservation);
    }

    public List<Reservation> findByStatut(String statut) {
        return reservationRepository.findAll().stream()
                .filter(r -> r.getStatut().equalsIgnoreCase(statut))
                .toList();
    }

    public List<Reservation> findByDateDebutBetween(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getDateDebut().isBefore(startDate) && !r.getDateDebut().isAfter(endDate))
                .toList();
    }

    public List<Reservation> findByDateFinBetween(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getDateFin().isBefore(startDate) && !r.getDateFin().isAfter(endDate))
                .toList();
    }

    public Reservation annulerReservation(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));
        
        reservation.setStatut("annulee");
        return reservationRepository.save(reservation);
    }

    public Reservation confirmerReservation(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));
        
        reservation.setStatut("confirmee");
        return reservationRepository.save(reservation);
    }

    public Reservation terminerReservation(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'ID: " + id));
        
        reservation.setStatut("terminee");
        
        // Ajouter des points de fidélité au client
        int points = calculatePointsFidelite(reservation.getPrixTotal());
        clientService.addPoints(reservation.getClient().getIdClient(), points);
        
        return reservationRepository.save(reservation);
    }

    private void validateReservationDates(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut.isBefore(LocalDate.now())) {
            throw new RuntimeException("La date de début ne peut pas être dans le passé");
        }
        
        if (dateFin.isBefore(dateDebut)) {
            throw new RuntimeException("La date de fin doit être après la date de début");
        }
        
        if (ChronoUnit.DAYS.between(dateDebut, dateFin) < 1) {
            throw new RuntimeException("La réservation doit être d'au moins une nuit");
        }
    }

    private void validateChambreAvailability(Chambre chambre, LocalDate dateDebut, LocalDate dateFin) {
        validateChambreAvailability(chambre, dateDebut, dateFin, null);
    }

    private void validateChambreAvailability(Chambre chambre, LocalDate dateDebut, LocalDate dateFin, Integer reservationIdToExclude) {
        // Vérifier si la chambre est disponible
        if (!chambre.getDisponible()) {
            throw new RuntimeException("La chambre n'est pas disponible");
        }

        // Vérifier les conflits de réservation
        List<Reservation> conflictingReservations = reservationRepository.findAll().stream()
                .filter(r -> r.getChambre().getIdChambre().equals(chambre.getIdChambre()))
                .filter(r -> !r.getStatut().equalsIgnoreCase("annulee"))
                .filter(r -> reservationIdToExclude == null || !r.getIdReservation().equals(reservationIdToExclude))
                .filter(r -> datesOverlap(r.getDateDebut(), r.getDateFin(), dateDebut, dateFin))
                .toList();

        if (!conflictingReservations.isEmpty()) {
            throw new RuntimeException("La chambre n'est pas disponible pour les dates sélectionnées");
        }
    }

    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }

    private BigDecimal calculatePrixTotal(Chambre chambre, LocalDate dateDebut, LocalDate dateFin) {
        long numberOfNights = ChronoUnit.DAYS.between(dateDebut, dateFin);
        return chambre.getPrixNuit().multiply(BigDecimal.valueOf(numberOfNights));
    }

    private int calculatePointsFidelite(BigDecimal prixTotal) {
        // 1 point pour chaque 10€ dépensés
        return prixTotal.divide(BigDecimal.TEN, 0, java.math.RoundingMode.DOWN).intValue();
    }

    public List<Reservation> findReservationsActives() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getStatut().equalsIgnoreCase("annulee"))
                .filter(r -> !r.getDateFin().isBefore(today))
                .toList();
    }

    public List<Reservation> findReservationsFutures() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> !r.getStatut().equalsIgnoreCase("annulee"))
                .filter(r -> r.getDateDebut().isAfter(today))
                .toList();
    }

    public List<Reservation> findReservationsPassees() {
        LocalDate today = LocalDate.now();
        return reservationRepository.findAll().stream()
                .filter(r -> r.getDateFin().isBefore(today))
                .toList();
    }
}