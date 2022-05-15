package classificador;

import java.util.Arrays;

/**
 *
 * @author Eduardo Vieira e Sousa
 */
public class Objeto {

    private float[] atributos; // Vetor de atributos
    private int classe;
    private float distancia; // Distância em relação ao objeto testado

    public Objeto() {
    }

    public Objeto(float[] atributos, int c) {
        this.atributos = atributos;
        this.classe = c;
    }

    public float[] getAtributos() {
        return atributos;
    }

    public void setAtributos(float[] atributos) {
        this.atributos = atributos;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    @Override
    public String toString() {
        return "Objeto{" + "atributos=" + Arrays.toString(atributos) + ", classe=" + classe + ", distancia=" + distancia
                + '}';
    }
}
