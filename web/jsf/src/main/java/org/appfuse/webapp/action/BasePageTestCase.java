package org.appfuse.webapp.action;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.webapp.StartupServletContextListener;
import org.appfuse.webapp.util.FacesUtils;
import org.appfuse.Constants;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class BasePageTestCase extends TestCase {
    protected final Log log = LogFactory.getLog(getClass());
    protected static final String MESSAGES = Constants.BUNDLE_KEY;
    protected static FacesContext facesContext;
    protected static MockServletConfig config;
    protected static MockServletContext servletContext;
    protected static WebApplicationContext applicationContext;

    // This static block ensures that Spring's BeanFactory and JSF's 
    // FacesContext is only loaded once for all tests. If there's something
    // wrong with this approach - please let me know!
    static {
        servletContext = new MockServletContext("");
        servletContext.addInitParameter(BasePage.jstlBundleParam, MESSAGES);
        servletContext.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM,
                "classpath*:/applicationContext-resources.xml, " +
                "classpath*:/applicationContext-dao.xml, " +
                "classpath*:/applicationContext-service.xml," +
                "classpath*:/applicationContext.xml," + // for modular archetypes
                "/WEB-INF/applicationContext*.xml");

        ServletContextListener contextListener = new ContextLoaderListener();
        ServletContextEvent event = new ServletContextEvent(servletContext);
        contextListener.contextInitialized(event);

        applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        config = new MockServletConfig(servletContext);
        performFacesContextConfig();
    }

    @Override
    protected void setUp() throws Exception {
        // change the port on the mailSender so it doesn't conflict with an 
        // existing SMTP server on localhost
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(2525);
        mailSender.setHost("localhost");
    }

    protected static void performFacesContextConfig() {
        StartupServletContextListener facesListener =
            new StartupServletContextListener();
        ServletContextEvent event = new ServletContextEvent(servletContext);
        facesListener.contextInitialized(event);

        LifecycleFactory lifecycleFactory =
            (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = lifecycleFactory.getLifecycle(getLifecycleId());
        FacesContextFactory facesCtxFactory =
            (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
        facesContext =
            facesCtxFactory.getFacesContext(servletContext, new MockHttpServletRequest(), new MockHttpServletResponse(),
                                            lifecycle);
    }

    protected static String getLifecycleId() {
        String lifecycleId =
            servletContext.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);

        return (lifecycleId != null) ? lifecycleId
                                     : LifecycleFactory.DEFAULT_LIFECYCLE;
    }

    protected Object getManagedBean(String name) {
        return FacesUtils.getManagedBean(name);
    }
}