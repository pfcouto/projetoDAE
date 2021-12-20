package pt.ipleiria.estg.dei.ei.dae.projetodae.ejbs;

import pt.ipleiria.estg.dei.ei.dae.projetodae.entities.Doctor;
import pt.ipleiria.estg.dei.ei.dae.projetodae.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.projetodae.exceptions.MyEntityExistsException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.print.Doc;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;

@Stateless
public class DoctorBean {
    @PersistenceContext
    EntityManager em;

    public void create(String username, String password, String name, String email, String phoneNumber, String office) throws MyEntityExistsException, MyConstraintViolationException {

        Doctor newDoctor = findDoctor(username);

        if (newDoctor != null) {
            throw new MyEntityExistsException("Doctor with username: " + username + " already exists");
        }
        try {
            newDoctor = new Doctor(username, password, name, email, phoneNumber, office);
            em.persist(newDoctor);
        } catch (ConstraintViolationException e) {
            throw new MyConstraintViolationException(e);
        }

    }

    public List<Doctor> getAllDoctors() {
        // remember, maps to: “SELECT s FROM Student s ORDER BY s.name”
        return (List<Doctor>) em.createNamedQuery("getAllDoctors").getResultList();
    }

    public Doctor findDoctor(String username) {
        return em.find(Doctor.class, username);
    }

    public void deleteDoctor(String username) {
        Doctor doctor = findDoctor(username);
        if (doctor != null) {
            em.remove(doctor);
        }
    }

    public void updateDoctor(String username, String password, String name, String email, String phoneNumber, String office) {
        Doctor doctor = em.find(Doctor.class, username);
        doctor.setPassword(password);
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPhoneNumber(phoneNumber);
        doctor.setOffice(office);
    }
}
