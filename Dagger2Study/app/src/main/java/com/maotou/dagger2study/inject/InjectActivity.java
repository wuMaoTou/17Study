package com.maotou.dagger2study.inject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by wuchundu on 17-6-28.
 */

public class InjectActivity extends Activity {

    @Inject
    User user;

    @Inject
    PresentBean presentBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //方式一:创建InjectActivityComponent,使用下面的方法注入
//        @Component
//        public interface ScopeActivityComponent {
//
//            void Inject(ScopeActivity injectACtivity);
//        }

//        创建上面的类后编译时会生成DaggerInjectActivityComponent,
//         相当于@Component标示就是让编译器帮我们生成了模板代码
//        以上正常使用方式,可以直接注入多个类
        DaggerInjectActivityComponent.builder().build().Inject(this);

        //方式二:就是编译器生成的模板代码
//        其中StudentBean_Factory, AreaBean_Factory, ScoreBean_Factory, PresentBean_Factory, InjectActivity_MembersInjector
//        都是使用@Inject注解编译时生成的类
//        Factory<User> studentBeanFactory = StudentBean_Factory.create();
//        MembersInjector<PresentBean> presentBeanMembersInjector = PresentBean_MembersInjector.create(AreaBean_Factory.create(), ScoreBean_Factory.create());
//        Factory<PresentBean> presentBeanFactory = PresentBean_Factory.create(presentBeanMembersInjector);
//        MembersInjector<ScopeActivity> injectActivityMembersInjector = InjectActivity_MembersInjector.create(studentBeanFactory, presentBeanFactory);
//        injectActivityMembersInjector.injectMembers(this);

        TextView textView = new TextView(this);
        String s = user.toString() + "\n" + presentBean.toString();
        textView.setText(s);
        setContentView(textView);
    }
}
