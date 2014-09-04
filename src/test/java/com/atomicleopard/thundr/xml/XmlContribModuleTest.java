package com.atomicleopard.thundr.xml;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.atomicleopard.thundr.xml.bind.JaxbBinder;
import com.atomicleopard.thundr.xml.view.JaxbViewResolver;
import com.atomicleopard.thundr.xml.view.XmlNegotiator;
import com.atomicleopard.thundr.xml.view.XmlView;
import com.threewks.thundr.bind.BinderModule;
import com.threewks.thundr.bind.BinderRegistry;
import com.threewks.thundr.injection.InjectionContextImpl;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.view.ViewModule;
import com.threewks.thundr.view.ViewResolverRegistry;
import com.threewks.thundr.view.negotiating.ViewNegotiatorRegistry;
import com.threewks.thundr.view.negotiating.ViewNegotiatorRegistryImpl;

public class XmlContribModuleTest {
	private UpdatableInjectionContext injectionContext = new InjectionContextImpl();
	private ViewResolverRegistry viewResolverRegistry = new ViewResolverRegistry();
	private ViewNegotiatorRegistry viewNegotiatorRegistry = new ViewNegotiatorRegistryImpl();
	private BinderRegistry binderRegistry = new BinderRegistry();
	private XmlContribModule module = new XmlContribModule();

	@Before
	public void before() {
		injectionContext.inject(viewResolverRegistry).as(ViewResolverRegistry.class);
		injectionContext.inject(viewNegotiatorRegistry).as(ViewNegotiatorRegistry.class);
		injectionContext.inject(binderRegistry).as(BinderRegistry.class);
	}

	@Test
	public void shouldDependOnViewAndBinderModules() {
		DependencyRegistry dependencyRegistry = new DependencyRegistry();
		module.requires(dependencyRegistry);
		assertThat(dependencyRegistry.hasDependency(BinderModule.class), is(true));
		assertThat(dependencyRegistry.hasDependency(ViewModule.class), is(true));
	}

	@Test
	public void shouldAddBinderAndViewResolvers() {
		module.configure(injectionContext);

		assertThat(viewResolverRegistry.findViewResolver(new XmlView(null)), instanceOf(JaxbViewResolver.class));
		assertThat(viewNegotiatorRegistry.getNegotiator("application/xml"), instanceOf(XmlNegotiator.class));
		assertThat(binderRegistry.hasBinder(JaxbBinder.class), is(true));
	}
}
