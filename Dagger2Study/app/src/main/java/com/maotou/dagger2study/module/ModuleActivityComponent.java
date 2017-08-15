package com.maotou.dagger2study.module;

import dagger.Component;

/**
 * Created by wuchundu on 17-6-28.
 */
//@Singleton
@Component(modules = {MarkModuleActivityModule.class})
public interface ModuleActivityComponent {

    void Inject(ModuleActivity moduleActivity);
}
