package com.example.kvizko.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;*/

import java.util.Collection;

@Entity
@Data
@Table(name = "user_table")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long userid;
  private String fullname;
  private String username;
  private String passwordAttr;

  public User(String fullname, String username, String passwordAttr) {
    this.fullname = fullname;
    this.username = username;
    this.passwordAttr = passwordAttr;
  }

  public User() {}
}
