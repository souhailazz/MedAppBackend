package com.medical.app.service;

import com.medical.app.dao.ConsultationDAO;
import com.medical.app.dao.DocteurDAO;
import com.medical.app.dao.PatientDAO;
import com.medical.app.dto.ConsultationDTO;
import com.medical.app.model.Consultation;
import com.medical.app.model.Docteur;
import com.medical.app.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationService {

    @Autowired
    private ConsultationDAO consultationDAO;

    @Autowired
    private DocteurDAO docteurDAO;

    @Autowired
    private PatientDAO patientDAO;

    public Consultation createConsultation(ConsultationDTO dto) {
        Docteur docteur = docteurDAO.findById(dto.getDocteurId().intValue());
        Patient patient = patientDAO.findById(dto.getPatientId().intValue());

        if (docteur == null || patient == null) {
            throw new IllegalArgumentException("Docteur ou patient introuvable.");
        }

        LocalDateTime dateDemandee = dto.getDateConsultation();
List<Consultation> consultationsExistantes = consultationDAO.findByDocteurId(Long.valueOf(docteur.getId()));

        for (Consultation c : consultationsExistantes) {
            long minutes = Duration.between(c.getDateConsultation(), dateDemandee).abs().toMinutes();
            if (minutes < 15) {
                throw new IllegalArgumentException("Ce médecin a déjà une consultation dans les 15 minutes.");
            }
        }

        Consultation consultation = new Consultation();
        consultation.setDocteur(docteur);
        consultation.setPatient(patient);
        consultation.setDateConsultation(dateDemandee);

        return consultationDAO.save(consultation);
    }

    public List<Consultation> getAllConsultations() {
        return consultationDAO.findAll();
    }

    public void deleteConsultation(Long id) {
        consultationDAO.delete(id);
    }

    public List<Consultation> getConsultationsByDoctorId(Long doctorId) {
        return consultationDAO.findByDocteurId(doctorId);
    }

    public List<Consultation> getConsultationsByPatientId(Long patientId) {
        return consultationDAO.findByPatientId(patientId);
    }
}
