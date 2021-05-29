/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignment.thesociopath;

import java.util.Random;

/**
 *
 * @author XuanEr
 */
public class Edge<T extends Comparable<T>, N extends Comparable<N>> {

	private int rep;
	private Vertex<T, N> destination;
	private Edge<T, N> nextEdge;
	private Random random = new Random();

	public Edge() {
		this.rep = 0;
		this.destination = null;
		this.nextEdge = null;
	}

	public Edge(Vertex<T, N> dest, Edge<T, N> next) {
		this.rep = random.nextInt(10) + 1;
		this.destination = dest;
		this.nextEdge = next;

	}

	public Edge(Vertex<T, N> dest, int r, Edge<T, N> next) {
		this.rep = r;
		this.destination = dest;
		this.nextEdge = next;
	}

	public int getRep() {
		return rep;
	}

	public void setRep(int rep) {
		this.rep = rep;
	}

	public Vertex<T, N> getDestination() {
		return destination;
	}

	public void setDestination(Vertex<T, N> destination) {
		this.destination = destination;
	}

	public Edge<T, N> getNextEdge() {
		return nextEdge;
	}

	public void setNextEdge(Edge<T, N> nextEdge) {
		this.nextEdge = nextEdge;
	}
}
