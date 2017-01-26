package web.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import utils.luceneService.SingletonFuzzy;

public class ServletContextClass implements ServletContextListener{

@Override
public void contextInitialized(ServletContextEvent arg0) 
{        
	SingletonFuzzy.getInstance();
}


@Override
public void contextDestroyed(ServletContextEvent arg0) 
{
	SingletonFuzzy.destroyFuzzy();
      
}
}
