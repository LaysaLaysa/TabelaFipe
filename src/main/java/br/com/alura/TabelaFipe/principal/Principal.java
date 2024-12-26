package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConSumoAPI;
import br.com.alura.TabelaFipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitor = new Scanner(System.in);
    private ConSumoAPI consumo = new ConSumoAPI();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1";
    private ConverteDados conversor = new ConverteDados();

    public void exibeMenu() {

        String menu = """
                ****** OPÇÕES ******
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);
        String opcao = leitor.nextLine();
        String endereco;

        if (opcao.toLowerCase().contains("arr")) {
            endereco = URL_BASE + "/carros/marcas";
        } else if (opcao.toLowerCase().contains("oto")) {
            endereco = URL_BASE + "/motos/marcas";
        } else {
            endereco = URL_BASE + "/caminhoes/marcas";
        }

        String json = consumo.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterlista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))        //ordenando pelo numero do código
                .forEach(System.out::println);

        System.out.println("Informe o código da marca para pesquisa: ");
        var codigoMarca = leitor.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var modelosLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modelosLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("\nDigite um trechoo do modelo a ser buscado: ");
        String nomeVeiculo = leitor.nextLine();

        List<Dados> modelosFiltrados = modelosLista.modelos().stream()
                .filter(n -> n.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);

        System.out.println("Digite o código do modelo para mais informações: ");
        var codigoModelo = leitor.nextLine();
        endereco = endereco + "/" + codigoModelo + "/anos";

        json = consumo.obterDados(endereco);
        List<Dados> anos = conversor.obterlista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var veiculoAnos = endereco + anos.get(i).codigo();
            json = consumo.obterDados(veiculoAnos);
            Veiculo veiculo =  conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nVeículos filtrados por ano: ");
        veiculos.forEach(System.out::println);

    }
}
