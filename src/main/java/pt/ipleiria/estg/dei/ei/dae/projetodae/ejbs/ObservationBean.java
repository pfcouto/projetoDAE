package pt.ipleiria.estg.dei.ei.dae.projetodae.ejbs;

import pt.ipleiria.estg.dei.ei.dae.projetodae.dtos.ObservationDTO;
import pt.ipleiria.estg.dei.ei.dae.projetodae.entities.BiometricsType;
import pt.ipleiria.estg.dei.ei.dae.projetodae.entities.Observation;
import pt.ipleiria.estg.dei.ei.dae.projetodae.entities.Patient;
import pt.ipleiria.estg.dei.ei.dae.projetodae.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.projetodae.exceptions.MyIllegalArgumentException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Stateless
public class ObservationBean {
    @PersistenceContext
    EntityManager em;
    public Observation create(String date, String patientUsername, int biometricName, int quantitativeValue, String what, String local) throws MyEntityNotFoundException,MyIllegalArgumentException{
        BiometricsType biometricsType=em.find(BiometricsType.class,biometricName);
        if(biometricsType!=null){
            if(quantitativeValue>biometricsType.getValueMax() || quantitativeValue<biometricsType.getValueMin()){
                throw new MyIllegalArgumentException("The quantitative value should should be between "+biometricsType.getValueMax()+" and "+biometricsType.getValueMin() );
            }
            Patient patient=em.find(Patient.class,patientUsername);
            if(patient!=null){
                try {
                    Observation observation = new Observation(date, patient, biometricsType, quantitativeValue, what, local);
                    observation.setQualitativeValue("Mario faz aqui");
                    em.persist(observation);
                    biometricsType.getObservations().add(observation);
                    em.merge(biometricsType);
                    return observation;
                }catch (PersistenceException e) {
                    throw new PersistenceException(e);
                }
            }else{
                throw new MyEntityNotFoundException("The Patient has not found");
            }
        }else{
            throw new MyEntityNotFoundException("The Biometric Type has not found");
        }
    }

    public Observation find(int code) throws MyEntityNotFoundException {
        Observation observation= em.find(Observation.class,code);
        if(observation!=null){
            return observation;
        }
        else {
            throw new MyEntityNotFoundException("The Biometric Type has not found");
        }
    }

    public List<Observation> getAllObservations() {
        return (List<Observation>) em.createNamedQuery("getAllObservations").getResultList();
    }

    public boolean update(int code, ObservationDTO observationDTO) throws MyEntityNotFoundException,MyIllegalArgumentException {
        Observation observation=em.find(Observation.class,code);
        if(observation!=null){
            em.lock(observation, LockModeType.OPTIMISTIC);
            if(observationDTO.getLocal()!=null){
                observation.setLocal(observationDTO.getLocal());
            }else{
                throw new MyIllegalArgumentException("The Local should not be empty");
            }
            if(observationDTO.getQuantitativeValue()!=-1){
                if(observationDTO.getQuantitativeValue()>observation.getBiometricsType().getValueMax() && observationDTO.getQuantitativeValue()<observation.getBiometricsType().getValueMin()){
                    throw new MyIllegalArgumentException("The quantitative value should should be between "+observation.getBiometricsType().getValueMax()+" and "+observation.getBiometricsType().getValueMin() );
                }
                    observation.setQuantitativeValue(observationDTO.getQuantitativeValue());
                    observation.setQualitativeValue("Aqui também mario");
            }
            else{
                throw new MyIllegalArgumentException("The Quantitative Value should not be empty");
            }
            if(observationDTO.getWhat()!=null){
                observation.setWhat(observationDTO.getWhat());
            }
            else{
                throw new MyIllegalArgumentException("What should not be empty");
            }
        }else{
            throw new MyEntityNotFoundException("The Observation Type has not found");
        }
        em.merge(observation);
        return true;
    }
}
