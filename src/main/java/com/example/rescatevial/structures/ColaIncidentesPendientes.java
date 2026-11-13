package com.example.rescatevial.structures;

import com.example.rescatevial.models.Incidente;
import java.util.ArrayList;
import java.util.List;

public class ColaIncidentesPendientes {

    private Nodo<Incidente> frente;
    private Nodo<Incidente> fin;

    public ColaIncidentesPendientes() {
        frente = null;
        fin = null;
    }

    public void encolar(Incidente incidente) {
        Nodo<Incidente> nuevo = new Nodo<>(incidente);

        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }

        fin = nuevo;
    }

    public Incidente desencolar() {
        if (estaVacia()) {
            return null;
        }

        Incidente incidente = frente.dato;
        frente = frente.siguiente;

        if (frente == null) {
            fin = null;
        }

        return incidente;
    }

    public Incidente verPrimero() {
        if (estaVacia()) {
            return null;
        }

        return frente.dato;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public List<Incidente> aLista() {
        List<Incidente> resultado = new ArrayList<>();
        Nodo<Incidente> actual = frente;

        while (actual != null) {
            resultado.add(actual.dato);
            actual = actual.siguiente;
        }

        return resultado;
    }
}