package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {

    private final PersonService personService;

    @GetMapping
    private ResponseEntity<List<PersonDTO>> getAllUsers() {
        return ResponseEntity.ok(personService.getAllUsers());
    }

    @PostMapping
    private ResponseEntity<List<PersonDTO>> saveUsers(@RequestBody List<PersonDTO> persons){
        return new ResponseEntity<>(personService.saveUsers(persons), HttpStatus.CREATED);
    }

    @DeleteMapping
    private ResponseEntity<Void> deleteUsers(@RequestParam List<Long> ids){
        personService.deleteUsers(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("experience")
    private ResponseEntity<List<Experience>> getExperience(){
        return ResponseEntity.ok(personService.getExperience());
    }

    @GetMapping("position")
    private ResponseEntity<List<Position>> getJobPosition(){
        return ResponseEntity.ok(personService.getJobPosition());
    }

    @GetMapping("genders")
    private ResponseEntity<List<String>> getGenders(){
        return ResponseEntity.ok(personService.getGenders());
    }
}
