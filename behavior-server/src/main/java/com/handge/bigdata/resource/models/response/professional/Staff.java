package com.handge.bigdata.resource.models.response.professional;

import com.handge.bigdata.annotation.Hiding;

/**
 * 优秀员工或者差劲员工
 * Created by MaJianfu on 2018/6/12.
 */
public class Staff {
    /**
     * 排名
     */
    private String num;
    /**
     * 工号
     */
    private String number;
    /**
     * 姓名
     */
    private String name;
    /**
     * 综合分数
     */
    private String score;
    /**
     * 职业素养  遵规守纪,忠诚度等
     */
    private String professionalism;
    /**
     * 优、良、中、差、劣
     */
    private String grade;


    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    @Hiding
    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getProfessionalism() {
        return professionalism;
    }

    public void setProfessionalism(String professionalism) {
        this.professionalism = professionalism;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Staff{" +
                "num='" + num + '\'' +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", score='" + score + '\'' +
                ", professionalism='" + professionalism + '\'' +
                ", grade='" + grade + '\'' +
                '}';
    }
}
