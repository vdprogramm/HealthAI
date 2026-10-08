package com.vinh.healthcare.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FastApiTriageRequest(

        @JsonProperty("trieu_chung")
        String trieuChung

) {
}
