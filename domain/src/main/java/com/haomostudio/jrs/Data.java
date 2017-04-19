package com.haomostudio.jrs;

/**
 * Created by shidaizhoukan on 2017/4/17.
 */
public class Data {

    private String name;
    private String name1;
    private String name2;
    private String name3;
    private String name4;
    private String name5;
    private String name6;

    public Data(String name, String name1, String name2, String name3, String name4,String name5, String name6) {
        this.name = name;
        this.name1 = name1;
        this.name2 = name2;
        this.name3 = name3;
        this.name4 = name4;
        this.name5 = name5;
        this.name6 = name6;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public String getName5() {
        return name5;
    }

    public void setName5(String name5) {
        this.name5 = name5;
    }

    public String getName6() {
        return name6;
    }

    public void setName6(String name6) {
        this.name6 = name6;
    }

    public  static void main(String[] args){
        int num = 0;
        System.out.println(num==1?"值为1":(num==2?"值为2":"值为0"));
    }
}
