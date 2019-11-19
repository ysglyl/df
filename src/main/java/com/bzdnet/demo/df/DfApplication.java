package com.bzdnet.demo.df;

import com.bzdnet.demo.df.core.DaoRegistry;
import com.bzdnet.demo.df.dao.UserDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DfApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DfApplication.class);
        app.addListeners(applicationEvent -> {
            if (applicationEvent instanceof ApplicationReadyEvent) {
                DaoRegistry daoRegistry = ((ApplicationReadyEvent) applicationEvent).getApplicationContext().getBean("daoRegistry", DaoRegistry.class);
                UserDao userDao = daoRegistry.getDao(UserDao.class);
                userDao.allList();
                userDao.test();
            }
        });
        app.run(args);
    }

    @Bean
    public DaoRegistry daoRegistry() {
        DaoRegistry daoRegistry = new DaoRegistry();
        daoRegistry.addDaoInPackage("com.bzdnet.demo.df.dao");
        return daoRegistry;
    }


}
