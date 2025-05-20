package org.pehlivan.mert.awsfileservicedemo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.pehlivan.mert.awsfileservicedemo.dto.UserCreateRequest;

@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String profilePhotoKey;

    public static User create(UserCreateRequest request, String profilePhotoKey) {
        User user = new User();
        user.email = request.getEmail();
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.profilePhotoKey = profilePhotoKey;
        return user;
    }

}