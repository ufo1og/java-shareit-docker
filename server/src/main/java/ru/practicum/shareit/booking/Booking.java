package ru.practicum.shareit.booking;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "BOOKINGS")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    @Column(name = "booker_id", nullable = false)
    private Long bookerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) && Objects.equals(startDate, booking.startDate) && Objects.equals(endDate, booking.endDate) && Objects.equals(itemId, booking.itemId) && Objects.equals(bookerId, booking.bookerId) && status == booking.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, itemId, bookerId, status);
    }
}
