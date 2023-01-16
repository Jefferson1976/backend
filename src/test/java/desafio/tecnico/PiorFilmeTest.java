package desafio.tecnico;

import io.quarkus.test.junit.QuarkusTest;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import desafio.tecnico.entity.PiorFilme;
import desafio.tecnico.service.PiorFilmeService;

import static io.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;

@QuarkusTest
public class PiorFilmeTest {

    @Test
    public void testMinMaxIntervalProducersWinEndpoint() {
        given()
          .when().get("/movies?projection=max-min-win-interval-for-producers")
          .then()
             .statusCode(200)
             .body("size()", is(2),
              "min[0].producer", is("Yoram Globus and Menahem Golan"),
              "max[0].producer", is("Renny Harlin")
              );
    }

    @Test
    public void testImportProducers() {
      String pathFile = ConfigProvider.getConfig().getValue("path.file.import",String.class);
      int totalLinhas = 0;

      try {
          File fileMovie = new File(pathFile);
          BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileMovie), StandardCharsets.ISO_8859_1));
  
          
          while (reader.readLine() != null) {
            totalLinhas++;
          }
          
          reader.close();
      } catch (Exception e) {
          e.printStackTrace();
      }

      PiorFilmeService.insertMoviesStarting();
    
      List<PiorFilme> listaPriorFilme = PiorFilme.listAll();

      //Verifica se importou todos os registros (exceto o cabeçalho)
      Assertions.assertEquals(listaPriorFilme.size(), totalLinhas-1);
    }
}