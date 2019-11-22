package com.bzdnet.demo.df;

import com.bzdnet.demo.df.core.DaoRegistry;
import com.bzdnet.demo.df.core.wrapper.QueryWrapper;
import com.bzdnet.demo.df.core.wrapper.Wrapper;
import com.bzdnet.demo.df.dao.UserDao;
import com.bzdnet.demo.df.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootApplication
public class DfApplication {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DfApplication.class);
        app.addListeners(applicationEvent -> {
            if (applicationEvent instanceof ApplicationReadyEvent) {
                DaoRegistry daoRegistry = ((ApplicationReadyEvent) applicationEvent).getApplicationContext().getBean("daoRegistry", DaoRegistry.class);
                UserDao userDao = daoRegistry.getDao(UserDao.class);

                Wrapper wrapper = new QueryWrapper(UserModel.class).eq("1", 1).notEq("2", 2).and().eq("3", 3).notEq("4", "4").or().eq("5", 5).notEq("6", 6);
                List<UserModel> list = userDao.allList(wrapper);
                for (UserModel user : list) {
                    System.out.println(user);
                }

            }
        });
        app.run(args);
    }

    @Bean
    public DaoRegistry daoRegistry() {
        DaoRegistry daoRegistry = new DaoRegistry(jdbcTemplate);
        daoRegistry.addDaoInPackage("com.bzdnet.demo.df.dao");
        return daoRegistry;
    }


}
