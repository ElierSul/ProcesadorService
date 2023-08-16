package com.example.ProcessadorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CSVController {

    private final RestTemplate restTemplate;

    @Autowired
    public CSVController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/validatecsv")
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile csvFile) {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error al procesar el archivo CSV.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        for (String[] row : rows) {
            restTemplate.postForEntity(
                    "http://localhost:8081/validarlinea", row, Boolean.class);
        }

        return new ResponseEntity<>("Archivo CSV procesado correctamente.", HttpStatus.OK);
    }
}
