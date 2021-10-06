package com.example.SAGSystems.dao;
import com.example.SAGSystems.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("fakeDao")
public class FakePersonDataAccesService implements PersonDao {

    private static List<Person> DB = new ArrayList<>();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FakePersonDataAccesService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertPerson(Person person) {
        //DB.add(new Person(id,person.getName()));
        //return 1;
        final String sql = "Insert into persona(name)  values (?)";
        jdbcTemplate.update(sql,person.getName());
        return 1;
    }

    @Override
    public List<Person> selectAllPeople() {
        //return DB;
        final String sql = "Select id,name from persona";
        List<Person> people = jdbcTemplate.query(sql,(resultSet, i) -> {
            return new Person(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
            );
        });
        return people;
    }

    @Override
    public Optional<Person> selectPersonById(int id) {
        return DB.stream().filter(person -> person.getId()==id).findFirst();
    }

    @Override
    public int deletePersonById(int id) {
        Optional<Person> personMaybe = selectPersonById(id);
        if (personMaybe.isPresent()){
            DB.remove(personMaybe.get());
            return 1;
        }
        else return 0;
    }

    @Override
    public int updatePersonById(int id, Person person) {
        return selectPersonById(id).map(p -> {
            int indexOfPersonToDelete = DB.indexOf(p);
            if (indexOfPersonToDelete >= 0) {
                DB.set(indexOfPersonToDelete,new Person(id,person.getName()));
                return 1;
            }
            return 0;
        }).orElse(0);
    }
}
