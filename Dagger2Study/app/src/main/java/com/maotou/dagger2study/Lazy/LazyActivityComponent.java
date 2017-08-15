package com.maotou.dagger2study.Lazy;

import dagger.Component;

/**
 * Created by wuchundu on 17-6-28.
 */
//@Singleton
@Component
public interface LazyActivityComponent {

    void Inject(LazyActivity lazyActivity);
}
