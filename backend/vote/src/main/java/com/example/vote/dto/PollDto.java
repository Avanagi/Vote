package com.example.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollDto {

    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Question is required")
    private String question;

    private List<String> visibleFor;

    private Long teacherId;

    @NotNull(message = "Options are required")
    private List<OptionDto> options;

}
