package model;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

//@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("User")
public class User {
    @ApiModelProperty(
            required = false,
            notes = "Unique id of the user"
    )
    public Integer id;
    @ApiModelProperty(
            required = true,
            notes = "Name of the user"
    )
    public String name;

    public User(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
    }
}

