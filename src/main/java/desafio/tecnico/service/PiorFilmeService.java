package desafio.tecnico.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.ConfigProvider;

import desafio.tecnico.entity.PiorFilme;
import desafio.tecnico.entity.Producer;
import desafio.tecnico.entity.Studio;
import desafio.tecnico.vo.PiorFilmeMinMaxVO;
import desafio.tecnico.vo.ProducerVO;

@RequestScoped
public class PiorFilmeService {

    @Transactional
    public static void insertMoviesStarting() {
        String pathFile = ConfigProvider.getConfig().getValue("path.file.import",String.class);

        try {
            File fileMovie = new File(pathFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileMovie), StandardCharsets.ISO_8859_1));
    
            String linha = "";
            
            reader.readLine(); // para ignorar a primeira linha
            
            while ((linha = reader.readLine()) != null) {
                String[] reg = linha.split(";");

                PiorFilme movie = new PiorFilme();

                movie.setTitle(reg[1]);
                movie.setYear(Integer.valueOf(reg[0]));
                movie.setWinner(reg.length > 4 && "yes".equalsIgnoreCase(reg[4]));
        
                String[] studiosImport = reg[2].split(", ");
                List<Studio> studios = new ArrayList<>();

                for (String nameStudio : studiosImport) {
                    Studio studio = new Studio(nameStudio);
                    studios.add(studio);
                }
                movie.setStudios(studios);

                // no arquivo enviado os nomes estão separados por vírgula e um espaço em branco
                String[] producersImport = reg[3].split(", ");
                List<Producer> producers = new ArrayList<>();

                for (String nameProducer : producersImport) {
                    Producer producer = new Producer(nameProducer);
                    producers.add(producer);
                }
                movie.setProducers(producers);

                movie.persist();
            }
            
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PiorFilmeMinMaxVO getMinMaxWinner() { 
        PiorFilmeMinMaxVO resp = new PiorFilmeMinMaxVO();

        List<PiorFilme> listaPriorFilme = PiorFilme.listAll();
        List<ProducerVO> listProducerVO = new ArrayList<>();

        for (PiorFilme pf : listaPriorFilme) {
            for (Producer producer : pf.getProducers()) {
                ProducerVO newProducer = null;

                boolean encontrou = false;
                for (ProducerVO producerVO : listProducerVO) {
                    if (producerVO.getProducer().equals(producer.getNameProducer())) {
                        encontrou = true;

                        if (producerVO.getPreviousWin() > pf.getYearMovie()) {
                            producerVO.setPreviousWin(pf.getYearMovie());
                        }

                        if (producerVO.getFollowingWin() < pf.getYearMovie()) {
                            producerVO.setFollowingWin(pf.getYearMovie());
                        }

                        producerVO.setInterval(producerVO.getFollowingWin()-producerVO.getPreviousWin());

                        break;
                    }
                }

                if (!encontrou) {
                    newProducer = new ProducerVO();
                    newProducer.setProducer(producer.getNameProducer());
                    newProducer.setPreviousWin(pf.getYearMovie());
                    newProducer.setFollowingWin(pf.getYearMovie());
                    newProducer.setInterval(0);

                    listProducerVO.add(newProducer);
                }
            }
        }

        // retira os produtores que ganharam apenas uma vez
        listProducerVO = listProducerVO.stream()
            .filter(p -> p.getInterval() > 0)
            .sorted(Comparator.comparingInt(ProducerVO::getInterval))
            .collect(Collectors.toList());

        resp.setMin(listProducerVO.subList(0, 2));
        resp.setMax(listProducerVO.subList(listProducerVO.size() - 2, listProducerVO.size()));

        return resp;
    }
}