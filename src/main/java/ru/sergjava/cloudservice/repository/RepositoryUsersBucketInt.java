package ru.sergjava.cloudservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sergjava.cloudservice.model.Authorities;
import ru.sergjava.cloudservice.model.User;

@Repository
public interface RepositoryUsersBucketInt extends JpaRepository<User, Authorities> {
    @Query(value = "select u from User u where u.username=:username")
    User login(@Param("username") String username);
//
//    @Query(value = "select p from Person p where p.personID.age<:age")
//    List<Person> findByPersonIDAgeLessThan(@Param("age") Integer age, Sort sort);
//
//    @Query(value = "select p from Person p where p.personID.name=:name and p.personID.surname=:surname")
//    List<Optional<Person>> findByPersonIDNameAndPersonIDSurname(@Param("name") String name, @Param("surname") String surname);


}
