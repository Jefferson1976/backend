package desafio.tecnico.resource;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import desafio.tecnico.dto.ErroDTO;
import desafio.tecnico.entity.PiorFilme;
import desafio.tecnico.service.PiorFilmeService;
import desafio.tecnico.vo.PiorFilmeMinMaxVO;

@RestController
@RequestMapping("movies")
public class PiorFilmeResource {
    @Inject
    PiorFilmeService piorFilmeService;

    @GetMapping
    @GET
    @Transactional
    public Response getMovies(@QueryParam("projection") String projection) {
        PiorFilmeMinMaxVO retorno = null;

        if ("max-min-win-interval-for-producers".equalsIgnoreCase(projection)) {
            try {
                retorno = piorFilmeService.getMinMaxWinner();
            }  catch (Exception e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(new ErroDTO(e.getMessage())).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErroDTO("Projeção não localizada!")).build();  
        }

        return Response.ok().entity(retorno).build();
    }

    @PostMapping("add")
    @Transactional
    public void addMovie(PiorFilme movie) {
        PiorFilme.persist(movie);
    }

    @GetMapping("list")
    public List<PiorFilme> listMovies() {
        // PiorFilme.listAll(Sort.by("campo1").and("campo2").ascending());
        return PiorFilme.listAll();
    }

    @GetMapping("{id}")
    public PiorFilme getMovie(@PathVariable("id") long id) {
        return PiorFilme.findById(id);
    }

    @DeleteMapping("{id}")
    @Transactional
    public void deleteMovie(@PathVariable("id") long id) {
        PiorFilme.delete("id", id);
    }
}
