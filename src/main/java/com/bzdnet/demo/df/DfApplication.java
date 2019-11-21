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

                QueryWrapper queryWrapper = Wrapper.query().eq("1", 1).notEq("2",2).and().lt("3", 3).or().in("4", "2","3");
                List<UserModel> list = userDao.allList(queryWrapper);
                for (UserModel user:list){
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
