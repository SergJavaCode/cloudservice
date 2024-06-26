package ru.sergjava.cloudservice.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sergjava.cloudservice.model.Authorities;
import ru.sergjava.cloudservice.model.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Authorities> {
    @Query(value = "select u from User u where u.username=:username")
    User getUserByName(@Param("username") String username);

    @Query(value = "select u.bucket from User u where u.username=:username")
    String getBucketUser(@Param("username") String username);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update users set bucket=:bucket where username=:username")
    void updateUserBucket(@Param("username") String username, @Param("bucket") String bucket);

}
