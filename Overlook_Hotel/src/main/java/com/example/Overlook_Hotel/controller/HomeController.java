package com.example.Overlook_Hotel.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.Overlook_Hotel.repository.ClientRepository;
import com.example.Overlook_Hotel.repository.EmployeRepository;
import com.example.Overlook_Hotel.model.Client;
import com.example.Overlook_Hotel.model.Employe;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
public class HomeController {

    private final ClientRepository clientRepository;
    private final EmployeRepository employeRepository;

    public HomeController(ClientRepository clientRepository, EmployeRepository employeRepository) {
        this.clientRepository = clientRepository;
        this.employeRepository = employeRepository;
    }

@GetMapping("/home")
public Map<String, Object> getAllClientsAndEmployes() {
    Map<String, Object> response = new HashMap<>();

    List<Map<String, Object>> clients = clientRepository.findAll().stream().map(c -> {
        Map<String, Object> map = new HashMap<>();
        map.put("prenom", c.getPrenom());
        map.put("nom", c.getNom());
        return map;
    }).toList();

    List<Map<String, Object>> employes = employeRepository.findAll().stream().map(e -> {
        Map<String, Object> map = new HashMap<>();
        map.put("prenom", e.getPrenom());
        map.put("nom", e.getNom());
        return map;
    }).toList();

    response.put("clients", clients);
    response.put("employes", employes);

    return response;
}

}
