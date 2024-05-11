package ru.sergjava.cloudservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ListDto {
    private String filename;
    private Integer size;

    public ListDto getListDto() {
        return this;
    }

}
