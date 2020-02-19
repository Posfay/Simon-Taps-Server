package com.simon.taps.server;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ServerApplication {

  private static ConfigurableApplicationContext context;

  public static void main(final String[] args) {

    ServerApplication.context = SpringApplication.run(ServerApplication.class, args);
  }

  public static void restart() {

    ApplicationArguments args = ServerApplication.context.getBean(ApplicationArguments.class);

    Thread thread = new Thread(() -> {
      ServerApplication.context.close();
      ServerApplication.context =
          SpringApplication.run(ServerApplication.class, args.getSourceArgs());
    });

    thread.setDaemon(false);
    thread.start();
  }

}
