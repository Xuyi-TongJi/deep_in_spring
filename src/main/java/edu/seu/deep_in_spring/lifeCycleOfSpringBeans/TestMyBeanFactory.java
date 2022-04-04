package edu.seu.deep_in_spring.lifeCycleOfSpringBeans;

import java.util.ArrayList;
import java.util.List;

public class TestMyBeanFactory {
    public static void main(String[] args) {
        // 实例化一个自定义BeanFactory实现类
        MyBeanFactoryImpl factory = new MyBeanFactoryImpl();
        // 在该factory中加入一个自定义的后处理器
        factory.addPostProcessor(
                (bean) -> {
                    System.out.println("解析@Autowired注解");
                }
        );
        // 调用该factory的模版方法
        System.out.println(factory.getBean());
    }

    /**
     * 模版模式自定义一个BeanFactory
     */
    static abstract class MyBeanFactory {

        /**
         * 获得bean：模板方法，不可被继承
         */
        public final Object getBean() {
            Object bean = new Object();
            construct(bean);
            inject(bean);
            init(bean);
            return bean;
        }

        public abstract void construct(Object bean);

        public abstract void inject(Object bean);

        public abstract void init(Object bean);

    }

    interface BeanPostProcessor {
        /**
         * 对依赖注入阶段的扩展
         *
         * @param bean spring bean
         */
        void inject(Object bean);
    }

    static class MyBeanFactoryImpl extends MyBeanFactory {

        /**
         * 后处理器集合
         */
        private final List<BeanPostProcessor> processors = new ArrayList<>();

        @Override
        public void construct(Object bean) {
            System.out.println("bean构造方法");
        }

        @Override
        public void inject(Object bean) {
            processors.forEach(
                    postProcessor ->
                            postProcessor.inject(bean)
            );
            System.out.println("bean依赖注入");
        }

        @Override
        public void init(Object bean) {
            System.out.println("bean初始化");
        }

        public void addPostProcessor(BeanPostProcessor postProcessor) {
            processors.add(postProcessor);
        }
    }
}
