package com.mw.timesheets.domain.team;

import com.mw.timesheets.domain.team.model.TeamDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/team")
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getTeams() {
        return ResponseEntity.ok(teamService.getTeams());
    }

    @GetMapping("search")
    public ResponseEntity<List<TeamDTO>> getTeamsLike(@RequestParam String name) {
        return ResponseEntity.ok(teamService.getTeamsLike(name));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTeam(@RequestParam Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<List<TeamDTO>> saveTeam(@RequestBody List<TeamDTO> teamDTOs) {
        return ResponseEntity.ok(teamService.saveTeam(teamDTOs));
    }
}
