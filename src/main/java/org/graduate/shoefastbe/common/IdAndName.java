package org.graduate.shoefastbe.common;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdAndName {
    private Long id;
    private String name;
    private Long size;
    private Long number;
}
