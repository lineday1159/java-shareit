package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Date;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Entity
@Table(name = "requests", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    @ToString.Exclude
    private User requester;
    @Column
    private Date created;
}
