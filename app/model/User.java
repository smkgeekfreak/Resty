package model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
  public Integer id;
  public String name;

    public String validate() {
        if (name.length() < 2 )
            return ("Name provided be longer than 2 charaters");
        return null;
    }
  public User(final Integer id, final String name) {
    this.id = id;
    this.name = name;
    }
    public User(){}
}

