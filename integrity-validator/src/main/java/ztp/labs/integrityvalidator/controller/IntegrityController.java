package ztp.labs.integrityvalidator.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ztp.labs.integrityvalidator.dto.FileIntegrityRequestDto;
import ztp.labs.integrityvalidator.service.FileIntegrityService;

@RestController
@AllArgsConstructor
@Log4j2
public class IntegrityController {

    private FileIntegrityService fileIntegrityService;

    @PostMapping("/validate")
    public Mono<Boolean> validateChecksum(@RequestBody FileIntegrityRequestDto fileIntegrityRequest) {
        log.info(fileIntegrityRequest);
        return fileIntegrityService.isChecksumValid(fileIntegrityRequest);
    }

}
