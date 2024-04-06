package ru.practicum.explore_with_me.location;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float lat;

    @Column(nullable = false)
    private Float lon;

    public Location(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
