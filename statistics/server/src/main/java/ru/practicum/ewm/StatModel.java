package ru.practicum.ewm;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Getter
@Setter
public class StatModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
