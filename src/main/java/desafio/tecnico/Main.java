package desafio.tecnico;

import java.util.List;

import javax.transaction.Transactional;

import desafio.tecnico.entity.PiorFilme;
import desafio.tecnico.service.PiorFilmeService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
    }

    public static class MyApp implements QuarkusApplication {

        @Override
        public int run(String... args) throws Exception {
            PiorFilmeService.insertMoviesStarting();
            Quarkus.waitForExit();
            return 0;
        }
    }
}
