package com.dividend.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private String ticker;
    private String name;
}
