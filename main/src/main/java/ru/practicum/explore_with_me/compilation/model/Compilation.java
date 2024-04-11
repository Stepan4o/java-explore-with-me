package ru.practicum.explore_with_me.compilation.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean pinned;

    private String title;

    @ElementCollection
    private List<Long> events;

    public Compilation(Boolean pinned, String title) {
        this.pinned = pinned;
        this.title = title;
    }
}
