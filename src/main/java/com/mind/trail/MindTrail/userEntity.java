package com.mind.trail.MindTrail;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class userEntity {
    
    @Id
    private ObjectId id;
    private String username;
    private String email;
    private String password;
    private List<String> Roles = new ArrayList<>();
    
}
