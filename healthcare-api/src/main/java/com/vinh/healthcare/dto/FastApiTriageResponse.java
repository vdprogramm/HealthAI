package com.vinh.healthcare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FastApiTriageResponse(

        @JsonProperty("chuyen_khoa")
        String chuyenKhoa,

        @JsonProperty("muc_do_khan_cap")
        String mucDoKhanCap,

        @JsonProperty("giai_thich_ngan")
        String giaiThichNgan,

        @JsonProperty("canh_bao")
        String canhBao

) {
}
