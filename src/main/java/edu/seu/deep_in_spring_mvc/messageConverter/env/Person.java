package edu.seu.deep_in_spring_mvc.messageConverter.env;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Person {
    private Integer age;
    private String name;

    public Person(@JsonProperty("age") Integer age, @JsonProperty("name") String name) {
        this.age = age;
        this.name = name;
    }
}
