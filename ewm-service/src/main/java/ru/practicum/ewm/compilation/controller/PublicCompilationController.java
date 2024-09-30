package ru.practicum.ewm.compilation.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.CompilationService;
import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@AllArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> get(@RequestParam(required = false) Boolean pinned,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "10") int size) {
        List<CompilationDto> compilationDtoList = compilationService.getCompilations(pinned, from, size);
        log.info("Получение подборок событий");
        return compilationDtoList;
    }

    @GetMapping("/{comId}")
    public CompilationDto getById(@PathVariable long comId) {
        CompilationDto compilationDto = compilationService.getById(comId);
        log.info("Получение подборки событий по его id {} ", comId);
        return compilationDto;
    }
}
