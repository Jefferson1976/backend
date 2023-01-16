package desafio.tecnico.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.ConfigProvider;

import desafio.tecnico.entity.PiorFilme;
import desafio.tecnico.entity.Producer;
import desafio.tecnico.entity.Studio;
import desafio.tecnico.vo.PiorFilmeMinMaxVO;
import desafio.tecnico.vo.ProducerVO;
import io.quarkus.panache.common.Sort;

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

        List<PiorFilme> listaPriorFilme = PiorFilme.listAll(Sort.by("yearMovie").ascending());
        HashMap<String, List<Integer>> hashProducers = new HashMap<String, List<Integer>>();

        for (PiorFilme pf : listaPriorFilme) {
            for (Producer producer : pf.getProducers()) {
                List<Integer> yearsWinner = new ArrayList<>();

                if (hashProducers.get(producer.getNameProducer()) != null) {
                    hashProducers.get(producer.getNameProducer()).add(pf.getYearMovie());
                } else {
                    yearsWinner.add(pf.getYearMovie());

                    hashProducers.put(producer.getNameProducer(), yearsWinner);
                }
            }
        }
        // obtem os produtores que ganharam mais de uma vez
        HashMap<String, List<Integer>> hashProducersWinners = new HashMap<String, List<Integer>>();
        for (Map.Entry m : hashProducers.entrySet()) {
            if (((List<Integer>)m.getValue()).size() > 1) {
                hashProducersWinners.put((String)m.getKey(), (List<Integer>)m.getValue());
            }
        }

        // Obtem o produtor com menor intervalo de prêmio obtido
        List<ProducerVO> minListProducer = new ArrayList<>();
        minListProducer.add(this.getMinWinner(hashProducersWinners));
        resp.setMin(minListProducer);

        // Obtem o produtor com maior intervalo de prêmio obtido
        List<ProducerVO> maxListProducer = new ArrayList<>();
        maxListProducer.add(this.getMaxWinner(hashProducersWinners));
        resp.setMax(maxListProducer);

        return resp;
    }

    public ProducerVO getMinWinner(HashMap<String, List<Integer>> hashProducersWinners) { 
        // Obtem o menor intervalo de prêmio obtido
        HashMap<String, List<Integer>> hashProducersWinnersMin = new HashMap<String, List<Integer>>();

        int intervaloMinimo = 1; // ganhou 2 anos seguidos
        boolean localizouIntervaloMinimo = false;

        do {
            // verifica todos os produtores para localizar o produtor que ganhou dentro do intervalo mínimo
            for (Map.Entry m : hashProducersWinners.entrySet()) {
                List<Integer> yeasrWinner = (List<Integer>)m.getValue();

                for (int i = 1; i <= yeasrWinner.size()-1; i++) {
                    int aux = yeasrWinner.get(i) - yeasrWinner.get(i-1);

                    if (aux == intervaloMinimo) {
                        localizouIntervaloMinimo = true;
                        hashProducersWinnersMin.put((String)m.getKey(), (List<Integer>)m.getValue());
                        break;
                    }
                }
            }

            if (!localizouIntervaloMinimo) {
                intervaloMinimo++;
            }
        } while (!localizouIntervaloMinimo);
        
        System.out.println("\n\nLista dos Produtores que receberam o prêmio no menor intervalo localizado: \n");
        for (Map.Entry m : hashProducersWinnersMin.entrySet()) {
            System.out.println(m.getKey()+" "+m.getValue());
        }

        // Obtem o primeiro que obteve dois prêmios mais rápido
        int anoInicialMaisRapido = 0;
        String nomeProdutorMaisRapido = "";
        int previous = 0;
        int followin = 0;

        for (Map.Entry m : hashProducersWinnersMin.entrySet()) {
            List<Integer> yeasrWinner = (List<Integer>)m.getValue();

            for (int i = 1; i <= yeasrWinner.size()-1; i++) {
                int aux = yeasrWinner.get(i) - yeasrWinner.get(i-1);

                if ((aux == intervaloMinimo) && 
                    ((anoInicialMaisRapido == 0) || (yeasrWinner.get(i-1) < anoInicialMaisRapido))) {
                        anoInicialMaisRapido = yeasrWinner.get(i-1);
                        nomeProdutorMaisRapido = (String)m.getKey();
                        previous = yeasrWinner.get(i-1);
                        followin = yeasrWinner.get(i);
                }
            }
        }

        System.out.println("\n\nMenor Ano inicial localizado dentro do intervalo mínimo: " + anoInicialMaisRapido);
        System.out.println("\nNome do Produtor: " + nomeProdutorMaisRapido);

        ProducerVO minProducerVO = new ProducerVO();
        minProducerVO.setProducer(nomeProdutorMaisRapido);
        minProducerVO.setPreviousWin(previous);
        minProducerVO.setFollowingWin(followin);
        minProducerVO.setInterval(intervaloMinimo);

        return minProducerVO;
    }

    public ProducerVO getMaxWinner(HashMap<String, List<Integer>> hashProducersWinners) { 
        int intervaloMaximo = 2; // ganhou 2 anos seguidos

        // verifica todos os produtores para localizar o maior intervalo
        for (Map.Entry m : hashProducersWinners.entrySet()) {
            List<Integer> yeasrWinner = (List<Integer>)m.getValue();

            for (int i = 1; i <= yeasrWinner.size()-1; i++) {
                int aux = yeasrWinner.get(i) - yeasrWinner.get(i-1);

                if (aux > intervaloMaximo) {
                    intervaloMaximo = aux;
                }
            }
        }

        System.out.println("\n\nMaior intervalo localizado foi: " + intervaloMaximo);

        // Obtem o menor intervalo de prêmio obtido
        HashMap<String, List<Integer>> hashProducersWinnersMax = new HashMap<String, List<Integer>>();

        for (Map.Entry m : hashProducersWinners.entrySet()) {
            List<Integer> yeasrWinner = (List<Integer>)m.getValue();

            for (int i = 1; i <= yeasrWinner.size()-1; i++) {
                int aux = yeasrWinner.get(i) - yeasrWinner.get(i-1);

                if (aux == intervaloMaximo) {
                    hashProducersWinnersMax.put((String)m.getKey(), (List<Integer>)m.getValue());
                    break;
                }
            }
        }

        System.out.println("\n\nLista dos Produtores que receberam o prêmio no maior intervalo localizado: \n");
        for (Map.Entry m : hashProducersWinnersMax.entrySet()) {
            System.out.println(m.getKey()+" "+m.getValue());
        }

        // Obtem o primeiro que obteve dois prêmios com maior intervalo
        int anoInicialMaisRapido = 0;
        String nomeProdutorMaisRapido = "";
        int previous = 0;
        int followin = 0;

        for (Map.Entry m : hashProducersWinnersMax.entrySet()) {
            List<Integer> yeasrWinner = (List<Integer>)m.getValue();

            for (int i = 1; i <= yeasrWinner.size()-1; i++) {
                int aux = yeasrWinner.get(i) - yeasrWinner.get(i-1);

                if ((aux == intervaloMaximo) && 
                    ((anoInicialMaisRapido == 0) || (yeasrWinner.get(i-1) < anoInicialMaisRapido))) {
                        anoInicialMaisRapido = yeasrWinner.get(i-1);
                        nomeProdutorMaisRapido = (String)m.getKey();
                        previous = yeasrWinner.get(i-1);
                        followin = yeasrWinner.get(i);
                }
            }
        }

        System.out.println("\n\nMenor Ano inicial localizado dentro do intervalo máximo: " + anoInicialMaisRapido);
        System.out.println("\nNome do Produtor: " + nomeProdutorMaisRapido);

        ProducerVO minProducerVO = new ProducerVO();
        minProducerVO.setProducer(nomeProdutorMaisRapido);
        minProducerVO.setPreviousWin(previous);
        minProducerVO.setFollowingWin(followin);
        minProducerVO.setInterval(intervaloMaximo);
        
        return minProducerVO;
    }
}