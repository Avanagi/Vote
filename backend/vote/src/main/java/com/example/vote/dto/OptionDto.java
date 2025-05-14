package com.example.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "OptionText is required")
    private String optionText;

}
