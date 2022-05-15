package classificador;

/**
 *
 * @author Eduardo Vieira e Sousa
 */
public class Main {
    public static void main(String[] args) {

        // Trata erro nas entradas
        if (args.length != 3) {
            System.out.println(">=================================================<");
            System.out.println("\tERRO: NÚMERO INCORRETO DE PARÂMETROS");
            System.out.println(">=================================================<");
        }

        // Lê parâmetros
        String caminho = args[0];
        int knn = Integer.parseInt(args[1]);
        int kfolds = Integer.parseInt(args[2]);

        // Instancia e lê dados
        Knn teste = new Knn(kfolds, knn);
        teste.lerDados(caminho);
        teste.imprimeDados();
        teste.imprimeStats();

        // Faz as partições e as classificações
        teste.kfold();

        // Normaliza
        teste.zScore();
        teste.imprimeDados();
        teste.imprimeStats();
        teste.kfold();
    }
}
