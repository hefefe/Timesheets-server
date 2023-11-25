package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.model.SearchPersonDTO;
import com.mw.timesheets.domain.person.type.Experience;
import com.mw.timesheets.domain.person.type.Position;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/person")
public class PersonController {

    private final PersonService personService;
    private final PersonDataProvider personDataProvider;

    @GetMapping
    public ResponseEntity<PersonDTO> getLoggedInUser() {
        return ResponseEntity.ok(personService.getLoggedInUSer());
    }

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getAllUsers() {
        return ResponseEntity.ok(personService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<List<PersonDTO>> saveUsers(@RequestBody List<PersonDTO> persons) {
        return new ResponseEntity<>(personService.saveUsers(persons), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUsers(@RequestParam List<Long> ids) {
        personService.deleteUsers(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("experience")
    public ResponseEntity<List<Experience>> getExperience() {
        return ResponseEntity.ok(personService.getExperience());
    }

    @GetMapping("position")
    public ResponseEntity<List<Position>> getJobPosition() {
        return ResponseEntity.ok(personService.getJobPosition());
    }

    @GetMapping("genders")
    public ResponseEntity<List<String>> getGenders() {
        return ResponseEntity.ok(personService.getGenders());
    }

    @PostMapping("search")
    public ResponseEntity<List<PersonDTO>> searchPerson(@RequestBody SearchPersonDTO searchPersonDTO) {
        return ResponseEntity.ok(personDataProvider.getPersonByCriteria(searchPersonDTO));
    }

    @PostMapping("photo/{personId}")
    public ResponseEntity<List<PersonDTO>> savePersonPhoto(@PathVariable Long personId, @RequestParam MultipartFile photo) {
        return ResponseEntity.ok(personService.savePersonPhoto(personId, photo));
    }
}
