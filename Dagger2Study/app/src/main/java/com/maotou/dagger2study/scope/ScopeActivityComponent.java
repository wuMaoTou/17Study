package com.maotou.dagger2study.scope;

import dagger.Component;

/**
 * Created by wuchundu on 17-6-28.
 */
//@Singleton
@UserScope
@Component(modules = {MarkScopeActivityModule.class})
public interface ScopeActivityComponent {

    void Inject(ScopeActivity scopeActivity);
}
