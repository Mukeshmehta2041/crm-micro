package com.programmingmukesh.users.service.users_service.event;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEvent;

import com.programmingmukesh.users.service.users_service.entity.User;

/**
 * Event published when a user is updated.
 * 
 * @author Programming Mukesh
 * @version 1.0
 * @since 2024
 */
public class UserUpdatedEvent extends ApplicationEvent {

  private final User user;
  private final LocalDateTime timestamp;

  public UserUpdatedEvent(User user) {
    super(user);
    this.user = user;
    this.timestamp = LocalDateTime.now();
  }

  public User getUser() {
    return user;
  }

  public LocalDateTime getEventTimestamp() {
    return timestamp;
  }
}