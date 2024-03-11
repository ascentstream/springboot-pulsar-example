package com.ascentstream.example.bean;

import java.io.Serializable;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor(staticName = "of")
@Accessors(chain = true)
@Generated
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private String city;
}
