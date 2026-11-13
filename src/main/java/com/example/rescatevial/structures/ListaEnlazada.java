package com.example.rescatevial.structures;
import java.util.ArrayList;
import java.util.List;
public class ListaEnlazada<T> {

    private Nodo<T> primero;

    public ListaEnlazada() {
        this.primero = null;
    }

    public void agregar (T dato){
        Nodo <T> nuevo = new Nodo<>(dato);

        if (primero == null){
            primero = nuevo;
        }else {
            Nodo<T> actual = primero;
            while (actual.siguiente != null){
                actual= actual.siguiente;
            }
            actual.siguiente = nuevo;

        }

    }
    public List <T> aLista () {
        List<T> resultado = new ArrayList<>();
        Nodo <T> actual = primero;

        while (actual != null){
            resultado.add(actual.dato);
            actual= actual.siguiente;
        }
        return resultado;
    }
}