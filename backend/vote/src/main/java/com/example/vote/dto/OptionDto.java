package com.example.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    @NotNull(message = "Id is required")
    private Long Id;

    @NotNull(message = "PollId is required")
    private Long pollId;

    @NotNull(message = "OptionText is required")
    private String optionText;

}
