/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignment.thesociopath;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

/**
 *
 * @author XuanEr
 */

public class Vertex<T extends Comparable<T>, N extends Comparable<N>> {
	
	private T vertexInfo;
	private int inDeg;
	private int outDeg;
	private Vertex<T, N> nextVertex;
	private Edge<T, N> firstEdge;
	private int dive;
	private String lunchStart;
	private int lunchPeriod;
	private List<Vertex> friends = new ArrayList<>();

	private Random random = new Random();

	public Vertex() {
		this.vertexInfo = null;
		this.inDeg = 0;
		this.outDeg = 0;
		this.nextVertex = null;
		this.firstEdge = null;
		this.dive = 0;
		this.lunchStart = null;
		this.lunchPeriod = 0;
		this.friends = null;
	}

	public Vertex(T vInfo, Vertex<T, N> next) {
		this.vertexInfo = vInfo;
		this.inDeg = 0;
		this.outDeg = 0;
		this.nextVertex = next;
		this.firstEdge = null;
		this.dive = random.nextInt(99) + 1;
		this.lunchPeriod = random.nextInt(54) + 6;
		this.lunchStart = generateLunchStart();
		this.friends = null;
	}

	public T getVertexInfo() {
		return vertexInfo;
	}

	public int getInDeg() {
		return inDeg;
	}

	public void setInDeg(int inDeg) {
		this.inDeg = inDeg;
	}

	public int getOutDeg() {
		return outDeg;
	}

	public void setOutDeg(int outDeg) {
		this.outDeg = outDeg;
	}

	public Vertex<T, N> getNextVertex() {
		return nextVertex;
	}

	public void setNextVertex(Vertex<T, N> nextVertex) {
		this.nextVertex = nextVertex;
	}

	public Edge<T, N> getFirstEdge() {
		return firstEdge;
	}

	public void setFirstEdge(Edge<T, N> firstEdge) {
		this.firstEdge = firstEdge;
	}

	public int getDive() {
		return dive;
	}

	public String getLunchStart() {
		return lunchStart;
	}

	public int getLunchPeriod() {
		return lunchPeriod;
	}

	public String generateLunchStart() {
		int hour, minute;
		hour = random.nextInt(3) + 11;
		String time;
		if (hour < 13) {
			minute = random.nextInt(60);
		} else {
			minute = random.nextInt(60 - lunchPeriod);
		}
		if (String.valueOf(minute).length() == 1) {
			time = String.valueOf(hour) + "0" + String.valueOf(minute);
		} else {
			time = String.valueOf(hour) + String.valueOf(minute);
		}

		return time;
	}
}
