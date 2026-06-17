package com.golmok.golmok_daejang.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BusinessTypeTransitionRow {
    String getCurrentType();
    String getNextType();
    LocalDate getDate();
    LocalTime getTime();
}
