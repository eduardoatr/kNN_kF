package classificador;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Eduardo Vieira e Sousa
 */
public class Dados {

    private int nObjetos;
    private int nAtributos;
    private ArrayList<Objeto> dados;

    public Dados() {
        this.dados = new ArrayList<Objeto>();
    }

    public int getnObjetos() {
        return nObjetos;
    }

    public void setnObjetos(int nObjetos) {
        this.nObjetos = nObjetos;
    }

    public int getnAtributos() {
        return nAtributos;
    }

    public void setnAtributos(int nAtributos) {
        this.nAtributos = nAtributos;
    }

    public ArrayList<Objeto> getDados() {
        return dados;
    }

    public void setDados(ArrayList<Objeto> dados) {
        this.dados = dados;
    }

    @Override
    public String toString() {
        return "Dados{" + "nObjetos=" + nObjetos + ", nAtributos=" + nAtributos + ", dados=" + dados + '}';
    }

    public void lerArquivo(String caminho) {
        FileReader arquivo;
        BufferedReader lerArquivo;

        String linha;
        String[] linhaSeparada;

        float[] buffer;
        Objeto obj;

        try {
            arquivo = new FileReader(caminho);
            lerArquivo = new BufferedReader(arquivo);

            linha = lerArquivo.readLine();

            if (linha != null) {

                // Trata a primeira linha do arquivo de dados
                linhaSeparada = linha.split(" ");
                this.nObjetos = Integer.parseInt(linhaSeparada[0]);
                this.nAtributos = Integer.parseInt(linhaSeparada[1]);

                // Buffer para leitura de cada linha
                buffer = new float[this.nAtributos];

                // Lê o resto do arquivo
                linha = lerArquivo.readLine();

                while (linha != null) {
                    linhaSeparada = linha.split(" ");

                    for (int i = 0; i < this.nAtributos; i++) {
                        buffer[i] = Float.parseFloat(linhaSeparada[i]);
                    }

                    obj = new Objeto(buffer.clone(), Integer.parseInt(linhaSeparada[this.nAtributos]));

                    this.dados.add(obj);

                    linha = lerArquivo.readLine();
                }

                arquivo.close();
            }
        } catch (IOException e) {
            System.out.println(">=================================================<");
            System.out.println("\tERRO: PROBLEMA NA LEITURA DO ARQUIVO");
            System.out.println(">=================================================<");
        }
    }

    public void zScore() {
        float media = 0;
        float desvioP = 0;

        // Para cada atributo
        for (int j = 0; j < this.nAtributos; j++) {

            // Calcula a soma
            for (int i = 0; i < this.nObjetos; i++) {
                media += this.dados.get(i).getAtributos()[j];
            }

            // Calcula a média
            media = (media / this.nObjetos);

            // Calcula a variância
            for (int i = 0; i < this.nObjetos; i++) {
                desvioP += Math.pow((this.dados.get(i).getAtributos()[j] - media), 2);
            }

            // Calcula o desvio padrão
            desvioP = (float) Math.sqrt((desvioP / (nObjetos - 1)));

            // Calcula o z-score e substitui os valores
            for (int i = 0; i < this.nObjetos; i++) {
                this.dados.get(i).getAtributos()[j] = ((this.dados.get(i).getAtributos()[j] - media) / desvioP);
            }
        }
    }

    public void imprimeDados() {
        DecimalFormat df = new DecimalFormat("0.00");

        System.out.println(">=================================================<");
        System.out.println("\t\t   BASE DE DADOS:");
        System.out.println(">=================================================<");

        for (int i = 0; i < this.nAtributos; i++) {
            System.out.print("\tATR " + i);
        }

        System.out.println("\t  CLASSE");
        System.out.println(">=================================================<");

        for (int i = 0; i < this.nObjetos; i++) {
            for (int j = 0; j < this.nAtributos; j++) {
                System.out.print("\t" + df.format(this.dados.get(i).getAtributos()[j]));
            }
            System.out.println("\t     " + this.dados.get(i).getClasse());
        }

        System.out.println(">=================================================<");
        System.out.println("\tTOTAL: " + this.nObjetos + "\t|\tCLASSES: " + this.contaClasses());
        System.out.println(">=================================================<");
    }

    // Imprime média e desvio padrão
    public void imprimeStats() {
        DecimalFormat df = new DecimalFormat("0.000");

        float media = 0;
        float desvioP = 0;

        System.out.println(">=================================================<");
        System.out.println("\t\t MÉDIA\t   DESVIO PADRÃO");
        System.out.println(">=================================================<");

        for (int j = 0; j < this.nAtributos; j++) {
            // Calcula a soma
            for (int i = 0; i < this.nObjetos; i++) {
                media += this.dados.get(i).getAtributos()[j];
            }

            // Calcula a média
            media = (media / this.nObjetos);

            // Calcula a variância
            for (int i = 0; i < this.nObjetos; i++) {
                desvioP += Math.pow((this.dados.get(i).getAtributos()[j] - media), 2);
            }

            // Calcula o desvio padrão
            desvioP = (float) Math.sqrt((desvioP / (nObjetos - 1)));

            System.out.print("\tATR " + j + " ");
            System.out.print("\t" + df.format(media));
            System.out.println("\t       " + df.format(desvioP));
        }

        System.out.println(">=================================================<");
    }

    // Retorna hashmap com contagem de objetos para cada classe
    public int contaClasses() {
        HashMap<Integer, Integer> classes = new HashMap<Integer, Integer>();
        int classe;
        int count = 0;

        for (int i = 0; i < this.nObjetos; i++) {
            classe = this.dados.get(i).getClasse();
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

        return count;
    }
}
