package com.mw.timesheets.domain.person;

import com.mw.timesheets.domain.person.model.PersonDTO;
import com.mw.timesheets.domain.person.model.SearchPersonDTO;
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
    public ResponseEntity<PersonDTO> saveUsers(@RequestBody PersonDTO person) {
        return new ResponseEntity<>(personService.saveUser(person), HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUsers(@RequestParam List<Long> ids) {
        personService.deleteUsers(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("search")
    public ResponseEntity<List<PersonDTO>> searchPerson(@RequestBody SearchPersonDTO searchPersonDTO) {
        return ResponseEntity.ok(personDataProvider.getPersonByCriteria(searchPersonDTO));
    }

    @PostMapping("photo")
    public ResponseEntity<PersonDTO> savePersonPhoto(@RequestParam Long personId, @RequestParam MultipartFile photo) {
        return ResponseEntity.ok(personService.savePersonPhoto(personId, photo));
    }

    @PostMapping("reset-password")
    public ResponseEntity<List<PersonDTO>> resetUsersPassword(@RequestParam List<Long> ids) {
        return new ResponseEntity<>(personService.resetPassword(ids), HttpStatus.OK);
    }

    @GetMapping("/projectMakers")
    public ResponseEntity<List<PersonDTO>> getTeamLeadersAndManagers() {
        return ResponseEntity.ok(personService.getTeamLeadersAndManagers());
    }

    @GetMapping("/in-project")
    public ResponseEntity<List<PersonDTO>> getEmployeesInProject(@RequestParam Long projectId) {
        return ResponseEntity.ok(personService.getEmployeesInProject(projectId));
    }
}
