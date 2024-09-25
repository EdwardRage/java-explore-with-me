package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.events.EventMapper;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto newCompilation) {
        List<Event> events = eventRepository.findAllEventsById(newCompilation.getEvents());
        Set<Event> eventSet = new HashSet<>(events);
        Compilation compilation = compilationMapper.toCompilation(newCompilation, eventSet /*events*/);

        compilationRepository.save(compilation);

        Set<EventShortDto> eventShortDtoSet = compilation.getEvents().stream().map(eventMapper::toEventShortDto).collect(Collectors.toSet());
        return compilationMapper.toCompilationDto(compilation, eventShortDtoSet);
    }

    @Override
    public void delete(long compId) {
        Compilation compilation = compilationRepository.findByCompId(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + " не найдена"));

        compilationRepository.delete(compilation);
    }

    @Transactional
    @Override
    public CompilationDto update(long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation oldCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + " не найдена"));

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllEventsById(updateCompilationRequest.getEvents());
            Set<Event> eventSet = new HashSet<>(events);
            oldCompilation.setEvents(eventSet);
        }
        if (updateCompilationRequest.getPinned() != null) {
            oldCompilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            oldCompilation.setTitle(updateCompilationRequest.getTitle());
        }
        compilationRepository.save(oldCompilation);
        Set<EventShortDto> eventShortDtoSet = oldCompilation.getEvents().stream()
                                                                        .map(eventMapper::toEventShortDto)
                                                                        .collect(Collectors.toSet());
        return compilationMapper.toCompilationDto(oldCompilation, eventShortDtoSet);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned).stream()
                    .map(comp -> compilationMapper.toCompilationDto(comp, comp.getEvents().stream()
                            .map(eventMapper::toEventShortDto).collect(Collectors.toSet())))
                    .skip(from)
                    .limit(size)
                    .toList();
        }

        return compilationRepository.findAllCompilations(/*PageRequest.of(from, size)*/).stream()
                .map(comp -> compilationMapper.toCompilationDto(comp, comp.getEvents().stream()
                        .map(eventMapper::toEventShortDto).collect(Collectors.toSet())))
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public CompilationDto getById(long compId) {
        Compilation compilation = compilationRepository.findByCompId(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + " не найдена"));
        Set<EventShortDto> eventShortDtoSet = compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toSet());
        return compilationMapper.toCompilationDto(compilation, eventShortDtoSet);
    }
}
