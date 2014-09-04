package com.atomicleopard.thundr.xml;

import javax.swing.text.AbstractDocument.Content;

import com.atomicleopard.thundr.xml.bind.JaxbBinder;
import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.atomicleopard.thundr.xml.view.JaxbViewResolver;
import com.atomicleopard.thundr.xml.view.XmlNegotiator;
import com.atomicleopard.thundr.xml.view.XmlView;
import com.threewks.thundr.action.method.bind.ActionMethodBinderRegistry;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.injection.InjectionContext;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.view.ViewResolverRegistry;
import com.threewks.thundr.view.negotiating.ViewNegotiatorRegistry;

/**
 * Module class for thundr-contrib-xml. Add it to the {@link DependencyRegistry} in your ApplicationModule
 * like this:
 * 
 * <pre>
 * <code>
 * @Override
 * 	public void requires(DependencyRegistry dependencyRegistry) {
 * 		dependencyRegistry.addDependency(ThundrContribXmlModule.class);
 * 	}
 * 	
 * </code>
 * </pre>
 * 
 * This module provides the following features:
 * <ul>
 * <li>Awesome feature 1</li>
 * <li>Awesome feature 2</li>
 * </ul>
 * 
 */
public class ThundrContribXmlModule implements com.threewks.thundr.injection.Module {
	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
	}

	@Override
	public void initialise(UpdatableInjectionContext injectionContext) {
	}

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		Jaxb jaxb = new Jaxb();
		JaxbBinder jaxbBinder = new JaxbBinder(jaxb);
		JaxbViewResolver viewResolver = new JaxbViewResolver(jaxb);
		XmlNegotiator negotiator = new XmlNegotiator();

		ActionMethodBinderRegistry actionMethodBinderRegistry = injectionContext.get(ActionMethodBinderRegistry.class);
		actionMethodBinderRegistry.registerActionMethodBinder(jaxbBinder);

		ViewResolverRegistry viewResolverRegistry = injectionContext.get(ViewResolverRegistry.class);
		viewResolverRegistry.addResolver(XmlView.class, viewResolver);

		ViewNegotiatorRegistry viewNegotiatorRegistry = injectionContext.get(ViewNegotiatorRegistry.class);
		viewNegotiatorRegistry.addNegotiator(ContentType.ApplicationXml.value(), negotiator);
	}

	@Override
	public void start(UpdatableInjectionContext injectionContext) {
	}

	@Override
	public void stop(InjectionContext injectionContext) {
	}
}
