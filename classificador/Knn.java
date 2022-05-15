package classificador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Eduardo Vieira e Sousa
 */
public class Knn {

    private Dados dados;

    private int kfold; // Tamanho da partição
    private int knn; // Número de vizinhos mais próximos

    private int nObjetos;
    private int nAtributos;
    private int nClasses;

    private ArrayList<Objeto> teste; // Lista de objetos para teste
    private ArrayList<Objeto> treinamento; // Lista de objetos para treinamento
    private ArrayList<Objeto> vizinhos; // Lista de objetos vizinhos

    public Knn(int kfold, int knn) {
        this.kfold = kfold;
        this.knn = knn;

        this.dados = new Dados();
        this.teste = new ArrayList<Objeto>();
        this.treinamento = new ArrayList<Objeto>();
        this.vizinhos = new ArrayList<Objeto>();
    }

    public void kfold() {
        int acertos = 0; // Contador para taxa de acerto
        int contador = 0; // Contador para o número de iterações

        // Verifica se o conjunto de dados está vazio
        if (this.dados.getDados().isEmpty()) {
            System.out.println(">=================================================<");
            System.out.println("\tERRO: CONJUNTO DE DADOS VAZIO");
            System.out.println(">=================================================<");
        } else {
            // Colhe as características do conjunto de dados
            this.nObjetos = this.dados.getnObjetos();
            this.nAtributos = this.dados.getnAtributos();
            this.nClasses = this.dados.contaClasses();

            // Verifica se o tamanho das partições é maior que 1 e menor que total de
            // objetos
            if ((this.kfold <= 1) || (this.kfold > this.nObjetos)) {
                System.out.println(">=================================================<");
                System.out.println("\tERRO: NÚMERO DE PARTIÇÕES INVÁLIDO");
                System.out.println(">=================================================<");
            } else {
                // Verifica se o número de vizinhos é válido
                // Calcula número de objetos em cada grupo e o resto
                if (((nObjetos % kfold) == 0) && (this.knn > (nObjetos - (nObjetos / kfold))) ||
                        ((nObjetos % kfold) != 0)
                                && (this.knn >= ((nObjetos - (nObjetos / kfold)) - ((nObjetos % kfold) - 1)))
                        || this.knn <= 0) {
                    System.out.println(">=================================================<");
                    System.out.println("\tERRO: NÚMERO DE VIZINHOS INVÁLIDO");
                    System.out.println(">=================================================<");

                } else {

                    // Caso exista uma classe que possui apenas 1 objeto, entra em loop
                    this.distribuiDados();
                    // Collections.shuffle(this.dados.getDados());

                    for (int i = 0; i < kfold; i++) {
                        // Reinicia as listas de teste e treinamento
                        this.teste.clear();
                        this.treinamento.clear();

                        // Para cada objeto do total ...
                        // ... separa nas partições de treinamento e teste
                        // ... usando o resto da divisão por kfold
                        for (int j = 0; j < this.nObjetos; j++) {
                            if ((j % this.kfold) == i) {
                                this.teste.add(dados.getDados().get(j));
                            } else {
                                treinamento.add(dados.getDados().get(j));
                            }
                        }

                        contador++;
                        System.out.println(">=================================================<");
                        System.out.println("\t\t    ITERAÇÃO " + contador);
                        System.out.println(">=================================================<");

                        // Após dividir os grupos, classifica
                        acertos += this.classifica();
                    }

                    System.out.println(">=================================================<");
                    System.out.println("\t\tTAXA DE ACERTO: " + (acertos / (float) nObjetos));
                    System.out.println(">=================================================<");
                }
            }
        }
    }

    public int classifica() {

        float atb1;
        float atb2;

        float distancia;

        boolean prosseguir = false;

        HashMap<Integer, Integer> classesCount = new HashMap<Integer, Integer>();

        int acertos = 0;
        int classefinal;

        int indice = 0; // Para guardar o índice do vizinho mais próximo
        int classe; // Para contar classes no hashmap

        int valor; // Para contar os vizinhos

        ArrayList<Objeto> treinamentoTest;

        // Para cada objeto na lista de teste
        for (int i = 0; i < this.teste.size(); i++) {

            // Calcula a distância de cada objeto em relação ao testado
            for (int j = 0; j < this.treinamento.size(); j++) {
                // Reseta distancias
                distancia = 0;
                this.treinamento.get(j).setDistancia(distancia);

                // Para cada atributo faz a soma
                for (int k = 0; k < dados.getnAtributos(); k++) {
                    atb1 = this.teste.get(i).getAtributos()[k];
                    atb2 = this.treinamento.get(j).getAtributos()[k];
                    distancia += Math.pow((atb1 - atb2), 2);
                }

                // Atribui distancia
                distancia = (float) Math.sqrt(distancia);
                this.treinamento.get(j).setDistancia(distancia);
            }

            // Faz uma cópia para calcular as distâncias e limpa os vizinhos
            treinamentoTest = (ArrayList<Objeto>) this.treinamento.clone();
            this.vizinhos.clear();

            // Separa os knn vizinhos mais próximos...
            // ... do mais próximo para o mais distante
            for (int z = 0; z < knn; z++) {
                distancia = treinamentoTest.get(0).getDistancia();
                indice = 0;

                // Acha a menor distância
                for (int j = 0; j < treinamentoTest.size(); j++) {
                    if ((treinamentoTest.get(j).getDistancia() < distancia)) {
                        distancia = treinamentoTest.get(j).getDistancia();
                        indice = j;
                    }
                }

                // Insere nos vizinhos
                this.vizinhos.add(treinamentoTest.get(indice));
                treinamentoTest.remove(indice);
            }

            do {
                // Reseta os valores para recalcular no caso de empate
                classefinal = 0;
                classesCount.clear();
                valor = 0;

                // Insere os knn vizinhos mais próximos no hashmap
                for (int z = 0; z < knn; z++) {
                    classe = this.vizinhos.get(z).getClasse();
                    if (classesCount.containsKey(classe)) {
                        classesCount.put(classe, classesCount.get(classe) + 1);
                    } else {
                        classesCount.put(classe, 1);
                    }
                }

                // Procura a classe com maior valor e verifica empates
                Set<Integer> chaves = classesCount.keySet();
                for (Integer chave : chaves) {
                    if (chave != null) {
                        if (classesCount.get(chave) > valor) {
                            classefinal = chave;
                            valor = classesCount.get(chave);
                            prosseguir = true;
                        } else {
                            if (classesCount.get(chave) == valor) {
                                prosseguir = false;
                            }
                        }
                    }
                }

                // Reduz o número de vizinhos e refaz o processo
                if (prosseguir == false) {
                    knn--;
                }
            } while (prosseguir == false);

            System.out.println(
                    "    CLASSE ORIGINAL: " + this.teste.get(i).getClasse() + "\t  |    CLASSIFICADOR: " + classefinal);

            if (this.teste.get(i).getClasse() == classefinal) {
                acertos++;
            }
        }

        return acertos;
    }

    public void zScore() {
        this.dados.zScore();
    }

    public void lerDados(String caminho) {
        this.dados.lerArquivo(caminho);
    }

    public void imprimeDados() {
        if (this.dados.getDados().isEmpty()) {
            System.out.println(">=================================================<");
            System.out.println("\tERRO: CONJUNTO DE DADOS VAZIO");
            System.out.println(">=================================================<");
        } else {
            this.dados.imprimeDados();
        }
    }

    // Imprime média e desvio padrão
    public void imprimeStats() {
        if (this.dados.getDados().isEmpty()) {
            System.out.println(">=================================================<");
            System.out.println("\tERRO: CONJUNTO DE DADOS VAZIO");
            System.out.println(">=================================================<");
        } else {
            this.dados.imprimeStats();
        }
    }

    // Conta o número de classes na partição de treinamento
    // Retorna verdadeiro se a partição contém ...
    // ... pelo menos 1 elemento de cada classe
    public boolean checaTreino() {
        HashMap<Integer, Integer> classes = new HashMap<Integer, Integer>();
        int classe;
        int count = 0;

        for (int i = 0; i < this.treinamento.size(); i++) {
            classe = this.treinamento.get(i).getClasse();
            if (classes.containsKey(classe)) {
                classes.put(classe, classes.get(classe) + 1);
            } else {
                classes.put(classe, 1);
            }
        }

        Set<Integer> chaves = classes.keySet();

        for (Integer chave : chaves) {
            if (chave != null) {
                count++;
            }
        }

        if (count == this.nClasses) {
            return true;
        }

        return false;
    }

    // Reembaralha os dados até atingir uma distribuição de partições válida
    public void distribuiDados() {
        boolean ok;

        // Embaralha os dados ...
        // ... faz o teste dividindo nas partições e verifica ...
        // ... se alguma das partições de treino ...
        // ... vai faltar alguma das classes ...
        // ... repete
        do {
            // Embaralha e reseta ok
            Collections.shuffle(this.dados.getDados());
            ok = true;

            System.out.println(">=================================================<");
            System.out.println("\t\tDISTRIBUINDO DADOS...");
            System.out.println(">=================================================<");

            for (int i = 0; i < kfold; i++) {
                // Se ok for verdadeiro continua verificando
                if (ok == true) {
                    // Reinicia as listas de teste e treinamento
                    this.teste.clear();
                    this.treinamento.clear();

                    // Para cada objeto do total ...
                    // ... separa nas partições de treinamento e teste
                    // ... usando o resto da divisão por kfold
                    for (int j = 0; j < this.nObjetos; j++) {
                        if ((j % this.kfold) == i) {
                            this.teste.add(dados.getDados().get(j));
                        } else {
                            treinamento.add(dados.getDados().get(j));
                        }
                    }

                    // Se em algum momento alguma partição for inválida...
                    // ok == false
                    ok = this.checaTreino();
                }
            }

        } while (ok == false);
    }
}