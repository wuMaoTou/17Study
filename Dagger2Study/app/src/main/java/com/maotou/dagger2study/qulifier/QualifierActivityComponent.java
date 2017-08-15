package com.maotou.dagger2study.qulifier;

import dagger.Component;

/**
 * Created by wuchundu on 17-6-28.
 */
//@Singleton
@Component(modules = {MarkQualifierActivityModule.class})
public interface QualifierActivityComponent {

    void Inject(QualifierActivity qualifierActivity);
}
