package com.example.adminService.Dto.quiz;

import lombok.*;

/**
 * Question DTO (Simplified)
 * Innehåller de viktigaste fälten från Question entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO {

    private Long id;
    private Integer excelId;
    private String question;
    private String sfi;
    private String correctAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;
    private String explanationForStudent;

    // Körkortsbehörigheter
    private int a;
    private int am;
    private int b;
    private int be;
    private int c;
    private int ce;
    private int d;
    private int de;
    private int ykbC;
    private int ykbD;
    private int adr;
    private int vtl;
    private int ta1i1;
    private int ta1i2;
    private int ta1i3;
    private int ta1i4;
    private int ta1i5;
    private int apv;
    private int yrs;
    private int tra1;

    private String image;
    private int subject;
    private String lang;
}
