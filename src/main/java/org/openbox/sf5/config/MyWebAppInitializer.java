package org.openbox.sf5.config;

// Failed to use this class because it gives duplicate root context error
public class MyWebAppInitializer {

	// implements WebApplicationInitializer {

	// http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/WebApplicationInitializer.html
	// @Override
	// public void onStartup(ServletContext container) throws ServletException {
	// AnnotationConfigWebApplicationContext rootContext = new
	// AnnotationConfigWebApplicationContext();
	// rootContext.register(AppConfiguration.class);
	//
	// // Manage the lifecycle of the root application context
	// container.addListener(new ContextLoaderListener(rootContext));
	//
	// // Create the dispatcher servlet's Spring application context
	// AnnotationConfigWebApplicationContext dispatcherContext = new
	// AnnotationConfigWebApplicationContext();
	// dispatcherContext.register(ManualWebMvcConfiguration.class);
	//
	// // Register and map the dispatcher servlet
	// ServletRegistration.Dynamic dispatcher = container.addServlet("sf5", new
	// DispatcherServlet(dispatcherContext));
	// dispatcher.setLoadOnStartup(1);
	// dispatcher.addMapping("/");
	//
	// }

}
