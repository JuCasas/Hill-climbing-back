package com.example.SAGSystems.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.SAGSystems.model.Person;

public interface PersonDao {
    //insertar persona con id

    //Insertar persona sin id
    int insertPerson(Person person);

    List<Person> selectAllPeople();

    Optional<Person> selectPersonById(int id);
    int deletePersonById(int id);
    int updatePersonById(int id, Person person);


}
