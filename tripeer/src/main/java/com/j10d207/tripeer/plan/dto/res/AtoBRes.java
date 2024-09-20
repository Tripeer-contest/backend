package com.j10d207.tripeer.plan.dto.res;

import com.j10d207.tripeer.tmap.db.dto.PublicRootDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtoBRes {

    private int option;
    private List<String> time;
    private List<PublicRootDTO> rootList;

}
