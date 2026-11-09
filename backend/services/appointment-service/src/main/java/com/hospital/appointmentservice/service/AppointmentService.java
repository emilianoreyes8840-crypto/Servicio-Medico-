package com.hospital.appointmentservice.service;

import com.hospital.appointmentservice.client.DoctorClient;
import com.hospital.appointmentservice.client.PatientClient;
import com.hospital.appointmentservice.entity.Appointment;
import com.hospital.appointmentservice.repository.AppointmentRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private DoctorClient doctorClient;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public Appointment createAppointment(Appointment appointment) {
        try {
            patientClient.getPatientById(appointment.getPatientId());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("No existe el paciente con ID: " + appointment.getPatientId());
        }

        try {
            doctorClient.getDoctorById(appointment.getDoctorId());
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("No existe el doctor con ID: " + appointment.getDoctorId());
        }

        if (appointment.getStatus() == null || appointment.getStatus().isBlank()) {
            appointment.setStatus("PENDING");
        }

        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment updatedAppointment) {
        return appointmentRepository.findById(id).map(appointment -> {
            try {
                patientClient.getPatientById(updatedAppointment.getPatientId());
            } catch (FeignException.NotFound e) {
                throw new IllegalArgumentException("No existe el paciente con ID: " + updatedAppointment.getPatientId());
            }

            try {
                doctorClient.getDoctorById(updatedAppointment.getDoctorId());
            } catch (FeignException.NotFound e) {
                throw new IllegalArgumentException("No existe el doctor con ID: " + updatedAppointment.getDoctorId());
            }

            appointment.setPatientId(updatedAppointment.getPatientId());
            appointment.setDoctorId(updatedAppointment.getDoctorId());
            appointment.setAppointmentDateTime(updatedAppointment.getAppointmentDateTime());
            appointment.setReason(updatedAppointment.getReason());
            appointment.setStatus(updatedAppointment.getStatus());
            return appointmentRepository.save(appointment);
        }).orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}