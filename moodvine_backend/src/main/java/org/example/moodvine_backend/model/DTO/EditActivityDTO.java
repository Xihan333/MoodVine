package org.example.moodvine_backend.model.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class EditActivityDTO {
    private Integer id;
    private String name;

    private String description;

    private String picture;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date finishTime;
}
