package com.kanjiServer.kanji;

public enum ListTypes {
    JOYO("joyo"),
    JINMEIYO("jinmeiyo"),
    KYOIKU("kyoiku"),
    GRADE_1("grade-1"),
    GRADE_2("grade-2"),
    GRADE_3("grade-3"),
    GRADE_4("grade-4"),
    GRADE_5("grade-5"),
    GRADE_6("grade-6");

    private final String value;

    ListTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
