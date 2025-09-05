package com.ecomptaia.service;

import com.ecomptaia.dto.UserRegistrationRequest;
import com.ecomptaia.dto.UserUpdateRequest;
import com.ecomptaia.entity.Utilisateur;
import com.ecomptaia.repository.UtilisateurRepository;
import com.ecomptaia.exception.ResourceNotFoundException;
import com.ecomptaia.exception.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Utilisateur createUser(UserRegistrationRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Un utilisateur avec cet email existe déjà");
        }

        Utilisateur user = new Utilisateur();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setActif(true);

        return utilisateurRepository.save(user);
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", email));
    }

    public Utilisateur updateUserProfile(String email, UserUpdateRequest request) {
        Utilisateur user = findByEmail(email);
        
        if (request.getNom() != null) user.setNom(request.getNom());
        if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
        if (request.getEmail() != null && !request.getEmail().equals(email)) {
            if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BusinessException("Cet email est déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }

        return utilisateurRepository.save(user);
    }

    public List<Utilisateur> findAllUsers() {
        return utilisateurRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur", id);
        }
        utilisateurRepository.deleteById(id);
    }
}
