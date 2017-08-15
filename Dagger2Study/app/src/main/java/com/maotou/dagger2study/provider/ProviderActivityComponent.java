package com.maotou.dagger2study.provider;

import dagger.Component;

/**
 * Created by wuchundu on 17-6-28.
 */
//@Singleton
@Component
public interface ProviderActivityComponent {

    void Inject(ProviderActivity providerActivity);
}
