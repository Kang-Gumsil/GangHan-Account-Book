package dankook.kanghyeyoung.capstone_2;

import java.util.ArrayList;
import java.util.Date;

public class TestClass {

    public static ArrayList<Spec> getTestSpec(int year, int month, int day) {
        ArrayList<Spec> specs=new ArrayList<>();
        if (year==2020 && month==11) {
            if (day==11) {
                specs.add(new Spec(1, 3000, "삼공티 단국대점", 1, 0, new Date(2020, 11, 11)));
                specs.add(new Spec(1, 4000, "사공티 단국대점", 1, 1, new Date(2020, 11, 11)));
                specs.add(new Spec(1, 5000, "오공티 단국대점", 1, 2, new Date(2020, 11, 11)));
                specs.add(new Spec(1, 6000, "육공티 단국대점", 1, 0, new Date(2020, 11, 11)));

            } else if (day==12) {
                specs.add(new Spec(1, 7000, "칠공티 단국대점", 1, 1, new Date(2020, 11, 12)));

            } else if (day==13) {
                specs.add(new Spec(1, 8000, "팔공티 단국대점", 1, 2, new Date(2020, 11, 13)));
                specs.add(new Spec(1, 9000, "구공티 단국대점", 1, 0, new Date(2020, 11, 13)));
            }
        }

        return specs;
    }
}
