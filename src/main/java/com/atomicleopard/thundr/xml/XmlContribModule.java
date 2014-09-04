/*
 * This file is a community contributed library for use with thundr.
 * Read more: http://3wks.github.io/thundr/
 *
 * Copyright (C) 2014 Atomic Leopard, <nick@atomicleopard.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomicleopard.thundr.xml;

import com.atomicleopard.thundr.xml.bind.JaxbBinder;
import com.atomicleopard.thundr.xml.jaxb.Jaxb;
import com.atomicleopard.thundr.xml.view.JaxbViewResolver;
import com.atomicleopard.thundr.xml.view.XmlNegotiator;
import com.atomicleopard.thundr.xml.view.XmlView;
import com.threewks.thundr.bind.BinderModule;
import com.threewks.thundr.bind.BinderRegistry;
import com.threewks.thundr.http.ContentType;
import com.threewks.thundr.injection.BaseModule;
import com.threewks.thundr.injection.UpdatableInjectionContext;
import com.threewks.thundr.module.DependencyRegistry;
import com.threewks.thundr.view.ViewModule;
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
public class XmlContribModule extends BaseModule {

	@Override
	public void requires(DependencyRegistry dependencyRegistry) {
		super.requires(dependencyRegistry);
		dependencyRegistry.addDependency(BinderModule.class);
		dependencyRegistry.addDependency(ViewModule.class);
	}

	@Override
	public void configure(UpdatableInjectionContext injectionContext) {
		Jaxb jaxb = new Jaxb();
		JaxbBinder jaxbBinder = new JaxbBinder(jaxb);
		JaxbViewResolver viewResolver = new JaxbViewResolver(jaxb);
		XmlNegotiator negotiator = new XmlNegotiator();

		BinderRegistry binderRegistry = injectionContext.get(BinderRegistry.class);
		binderRegistry.registerBinder(jaxbBinder);

		ViewResolverRegistry viewResolverRegistry = injectionContext.get(ViewResolverRegistry.class);
		viewResolverRegistry.addResolver(XmlView.class, viewResolver);

		ViewNegotiatorRegistry viewNegotiatorRegistry = injectionContext.get(ViewNegotiatorRegistry.class);
		viewNegotiatorRegistry.addNegotiator(ContentType.ApplicationXml.value(), negotiator);
	}

}
