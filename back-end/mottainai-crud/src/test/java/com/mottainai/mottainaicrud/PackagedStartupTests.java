package com.mottainai.mottainaicrud;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import static org.junit.jupiter.api.Assertions.*;

/** Executa o artefato real em outra pasta, sem o classpath nem a configuração do IntelliJ. */
class PackagedStartupTests {
    @TempDir Path root;

    @Test void warStartsFromExternalConfigurationAndIncludesAllPublicJsps() throws Exception {
        try(var postgres=EmbeddedPostgres.builder().setPort(0).start()) {
            new ResourceDatabasePopulator(new ClassPathResource("db/schema.sql"))
                    .execute(postgres.getPostgresDatabase());
            Files.createDirectories(root.resolve("config"));
            Files.writeString(root.resolve("config/local.properties"),
                    "spring.datasource.url="+postgres.getJdbcUrl("postgres","postgres")+"\n"+
                    "spring.datasource.username=postgres\nspring.datasource.password=postgres\n"+
                    "mottainai.crypto.key="+Base64.getEncoder().encodeToString(new byte[32])+"\n");
            int port;
            try(var socket=new java.net.ServerSocket(0)) { port=socket.getLocalPort(); }
            Path jar=Path.of("build/libs/mottainai-crud-0.0.1-SNAPSHOT.war").toAbsolutePath();
            Path log=Path.of("build/war-smoke.log").toAbsolutePath();
            String java=Path.of(System.getProperty("java.home"),"bin",System.getProperty("os.name").startsWith("Windows")?"java.exe":"java").toString();
            var process=new ProcessBuilder(java,"-jar",jar.toString(),"--server.port="+port)
                    .directory(root.toFile()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
            try {
                var http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1)).build();
                boolean ready=false;
                for(int attempt=0;attempt<180 && process.isAlive();attempt++) {
                    try {
                        var response=http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/home"))
                                .timeout(Duration.ofSeconds(2)).GET().build(),HttpResponse.BodyHandlers.ofString());
                        if(response.statusCode()==200) { ready=true;break; }
                    } catch(java.io.IOException ignored) { }
                    Thread.sleep(250);
                }
                assertTrue(ready,"WAR não iniciou com config/local.properties; consulte build/war-smoke.log");
                for(String page:new String[]{"/home","/login","/sobre-nos","/planos","/faq","/assets/css/modais.css"}) {
                    var response=http.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+page)).GET().build(),HttpResponse.BodyHandlers.ofString());
                    assertEquals(200,response.statusCode(),page);
                }
            } finally {
                process.destroy();
                if(!process.waitFor(10,TimeUnit.SECONDS)) { process.destroyForcibly();process.waitFor(10,TimeUnit.SECONDS); }
            }
        }
    }
}
