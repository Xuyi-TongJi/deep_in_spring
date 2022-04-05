package edu.seu.deep_in_spring.aop.cglibProxy;

public class Target {
    public void save() {
        System.out.println("save");
    }

    public void save(int i) {
        System.out.println("save" + i);
    }

    public void save(long l) {
        System.out.println("save" + l + "L");
    }
}
