package com.foogaro.trc.repository;

import com.foogaro.trc.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<User, Long> {

    //@Query(value = "SELECT SLEEP(3); SELECT u.id, u.name, u.email FROM user u WHERE u.id = ?1", nativeQuery = true)
    @Query(value = "SELECT SLEEP(?1), u.id, u.name, u.email FROM user u WHERE u.id = ?2", nativeQuery = true)
    public User findOneSlowly(Integer sleepy, Long id);

}
