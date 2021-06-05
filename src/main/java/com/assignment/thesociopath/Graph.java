/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignment.thesociopath;

import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author XuanEr
 * @param <T>
 * @param <N>
 */
@RestController
public class Graph<T extends Comparable<T>, N extends Comparable<N>> {

	private final Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123"));
	private Session session;
	private Vertex<T, N> head;
	private int size;
	private int relationship = 0;
	private String result = "";
	// Adjacency list for vertex
	private static LinkedList<Integer> adj[];
	private int[][] teacherRep = new int[11][11];

	public Graph() {
		head = null;
		size = 0;
	}

	public Graph(T[] node, int[][] edges) {
		clearDatabase();
		head = null;
		size = 0;
		session = driver.session();
		try (Transaction tx = session.beginTransaction()) {
			for (T temp : node) {
				// Create node in database
				String query = "CREATE (a:Person {name: \'" + temp + "\'})";
				tx.run(query);
				addVertex(temp);
			}
			for (int i = 0; i < edges.length; i++) {
				for (int j = 0; j < edges[i].length; j++) {
					if (edges[i][j] != 0) {
						// Create relationship between the two vertex with the specific reputation value in database 
						String query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + node[i] + "\' AND b.name = \'" + node[j] + "\' CREATE (a)-[r:FRIEND {reputation: " + edges[i][j] + "}]-> (b)";
						tx.run(query);
						addEdge(node[i], edges[i][j], node[j]);
					}
				}
			}
			tx.commit();
		}
		session.close();
	}

	// Constructor to build adjacency lists for vertex
	public Graph(int v) {
		adj = new LinkedList[v];
		for (int i = 0; i < v; i++) {
			adj[i] = new LinkedList();
		}
	}

	/**
	 * Method to add undirected edge within 2 vertex
	 *
	 * @param v1 vertex1
	 * @param v2 vertex2
	 */
	public void addUndirectedEdge(int v1, int v2) {
		adj[v1].add(v2); // Add v1 to v2's list.
		adj[v2].add(v1); // Add v2 to v1's list.
	}

	/**
	 * This method is used to clear the graph.
	 */
	public void clear() {
		head = null;
	}

	/**
	 * This method is used to get the size of the graph.
	 *
	 * @return The size of graph
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * This method is used to obtain the in degree of a vertex.
	 *
	 * @param v The vertex
	 * @return int in degree of the vertex
	 */
	public int getInDeg(T v) {
		if (hasVertex(v)) {
			Vertex<T, N> temp = head;
			while (temp != null) {
				if (temp.getVertexInfo().compareTo(v) == 0) {
					return temp.getInDeg();
				}
				temp = temp.getNextVertex();
			}
		}
		//System.out.println("Vertex does not exist!");
		return -1;
	}

	/**
	 * This method is used to obtain the out degree of a vertex.
	 *
	 * @param v The vertex
	 * @return int out degree of the vertex
	 */
	public int getOutDeg(T v) {
		if (hasVertex(v)) {
			Vertex<T, N> temp = head;
			while (temp != null) {
				if (temp.getVertexInfo().compareTo(v) == 0) {
					return temp.getOutDeg();
				}
				temp = temp.getNextVertex();
			}
		}
		//System.out.println("Vertex does not exist!");
		return -1;
	}

	/**
	 * This method is used to check if a vertex is present in the graph.
	 *
	 * @param v The vertex
	 * @return Boolean vertex exist or not
	 */
	public boolean hasVertex(T v) {
		if (head == null) {
			return false;
		}
		Vertex<T, N> temp = head;
		while (temp != null) {
			if (temp.getVertexInfo().compareTo(v) == 0) {
				return true;
			}
			temp = temp.getNextVertex();
		}
		//System.out.println("Vertex does not exist!");
		return false;
	}

	/**
	 * This method is used to add a vertex into the graph.
	 *
	 * @param v The vertex info
	 * @return Boolean successfully added or not
	 */
	public boolean addVertex(T v) {
		if (!hasVertex(v)) {
			Vertex<T, N> newVertex = new Vertex<>(v, null);
			Vertex<T, N> temp = head;
			if (head == null) {
				head = newVertex;
			} else {
				Vertex<T, N> previous = head;
				while (temp != null) {
					previous = temp;
					temp = temp.getNextVertex();
				}
				previous.setNextVertex(newVertex);
			}
			size++;
			return true;
		}
		System.out.println("Vertex already exists!");
		return false;
	}

	/**
	 * This method is used to get the position of a vertex in the graph.
	 *
	 * @param v The vertex
	 * @return int position index of the vertex
	 */
	public int getIndex(T v) {
		if (head == null) {
			System.out.println("Graph is empty!");
			return -1;
		}
		if (!hasVertex(v)) {
			System.out.println("Vertex does not exist!");
			return -1;
		}
		int pos = 0;
		Vertex<T, N> temp = head;
		while (temp != null) {
			if (temp.getVertexInfo().compareTo(v) == 0) {
				return pos;
			}
			pos++;
			temp = temp.getNextVertex();
		}
		System.out.println("Vertex not found!");
		return -1;
	}

	/**
	 * This method is used to get all vertex info of vertex present in the
	 * graph.
	 *
	 * @return ArrayList with vertex info of all vertex in the graph.
	 */
	public ArrayList<T> getAllVertexObjects() {
		if (head == null) {
			System.out.println("Graph is empty!");
			return null;
		}
		Vertex<T, N> temp = head;
		ArrayList<T> list = new ArrayList<>();
		while (temp != null) {
			list.add(temp.getVertexInfo());
			temp = temp.getNextVertex();
		}
		return list;
	}

	/**
	 * This method is used to get all vertex object in the graph.
	 *
	 * @return ArrayList with all vertex object in the graph
	 */
	public ArrayList<Vertex<T, N>> getAllVertices() {
		if (head == null) {
			System.out.println("Graph is empty!");
			return null;
		}
		ArrayList<Vertex<T, N>> list = new ArrayList<>();
		Vertex<T, N> temp = head;
		while (temp != null) {
			list.add(temp);
			temp = temp.getNextVertex();
		}
		return list;
	}

	/**
	 * This method is used to obtain the vertex info of vertex at index value
	 * given.
	 *
	 * @param pos Position of vertex
	 * @return The vertex info
	 */
	public T getVertexInfo(int pos) {
		if (pos < 0 || pos >= size) {
			System.out.println("Index out of bounds!");
			return null;
		}
		Vertex<T, N> temp = head;
		for (int i = 0; i < pos; i++) {
			temp = temp.getNextVertex();
		}
		return temp.getVertexInfo();
	}

	/**
	 * This method is used to obtain the vertex object.
	 *
	 * @param v The vertexInfo of vertex to obtain
	 * @return The vertex object
	 */
	public Vertex<T, N> getVertex(T v) {
		if (hasVertex(v)) {
			Vertex<T, N> temp = head;
			while (temp != null) {
				if (temp.getVertexInfo() == v) {
					return temp;
				}
				temp = temp.getNextVertex();
			}
		}
		return null;
	}

	/**
	 * This method is used to add edge between two vertex with respective
	 * reputation value.
	 *
	 * @param src The source vertex
	 * @param rep The reputation value
	 * @param dest The destination vertex
	 * @return Boolean successfully added or not
	 */
	public boolean addEdge(T src, int rep, T dest) {
		if (head == null) {
			System.out.println("Graph is empty!");
			return false;
		}
		if (!hasVertex(src) || !hasVertex(dest)) {
			System.out.println("One/Both of the vertex does not exist!");
			return false;
		}
		if (hasEdge(src, dest)) {
			System.out.println("Edge already exist!");
			return false;
		}
		Vertex<T, N> srcVertex = head;
		while (srcVertex != null) {
			if (srcVertex.getVertexInfo().compareTo(src) == 0) {
				Vertex<T, N> destVertex = head;
				while (destVertex != null) {
					if (destVertex.getVertexInfo().compareTo(dest) == 0) {
						Edge<T, N> currentEdge = srcVertex.getFirstEdge();
						Edge<T, N> newEdge = new Edge(destVertex, rep, currentEdge);
						srcVertex.setFirstEdge(newEdge);
						int outDeg = srcVertex.getOutDeg() + 1;
						int inDeg = destVertex.getInDeg() + 1;
						srcVertex.setOutDeg(outDeg);
						destVertex.setInDeg(inDeg);
						return true;
					}
					destVertex = destVertex.getNextVertex();
				}
			}
			srcVertex = srcVertex.getNextVertex();
		}
		System.out.println("Addition of edge failed!");
		return false;
	}

	/**
	 * This method is used to remove edge between two vertex.
	 *
	 * @param src The source vertex
	 * @param dest The destination vertex
	 * @return Boolean removed successfully or not
	 */
	public boolean removeEdge(T src, T dest) {
		if (head == null) {
			System.out.println("Graph is empty!");
			return false;
		}
		if (!hasVertex(src) || !hasVertex(dest)) {
			System.out.println("One/Both of the vertex does not exist!");
			return false;
		}
		if (!hasEdge(src, dest)) {
			System.out.println("Edge does not exist!");
			return false;
		}
		Vertex<T, N> srcVertex = head;
		while (srcVertex != null) {
			if (srcVertex.getVertexInfo().compareTo(src) == 0) {
				Edge<T, N> currentEdge = srcVertex.getFirstEdge();
				Edge<T, N> previous = srcVertex.getFirstEdge();
				while (currentEdge != null) {
					if (currentEdge.getDestination().getVertexInfo().compareTo(dest) == 0) {
						if (currentEdge.getDestination().getVertexInfo().compareTo(srcVertex.getFirstEdge().getDestination().getVertexInfo()) == 0) {
							srcVertex.setFirstEdge(currentEdge.getNextEdge());
							currentEdge.setNextEdge(null);
							return true;
						} else {
							previous.setNextEdge(currentEdge.getNextEdge());
							currentEdge.setNextEdge(null);
							return true;
						}
					}
					previous = currentEdge;
					currentEdge = currentEdge.getNextEdge();
				}
			}
			srcVertex = srcVertex.getNextVertex();
		}
		System.out.println("Removal of edge failed!");
		return false;
	}

	/**
	 * This method is used to check if there is edge between two vertex.
	 *
	 * @param src The source vertex
	 * @param dest The destination vertex
	 * @return Boolean if there is edge between the two vertex
	 */
	public boolean hasEdge(T src, T dest) {
		if (head == null) {
			System.out.println("Graph is empty!");
			return false;
		}
		if (!hasVertex(src) || !hasVertex(dest)) {
			System.out.println("One/Both of the vertex does not exist!");
			return false;
		}
		Vertex<T, N> srcVertex = head;
		while (srcVertex != null) {
			if (srcVertex.getVertexInfo().compareTo(src) == 0) {
				Edge<T, N> currentEdge = srcVertex.getFirstEdge();
				while (currentEdge != null) {
					if (currentEdge.getDestination().getVertexInfo().compareTo(dest) == 0) {
						return true;
					}
					currentEdge = currentEdge.getNextEdge();
				}
			}
			srcVertex = srcVertex.getNextVertex();
		}
		// System.out.println("Edge not found!");
		return false;
	}

	/**
	 * This method is used to get the edge weight between two vertex.
	 *
	 * @param src The source vertex
	 * @param dest The destination vertex
	 * @return The edge weight
	 */
	public int getEdgeWeight(T src, T dest) {
		if (head == null) {
			System.out.println("Graph is empty!");
			return Integer.MIN_VALUE;
		}
		if (!hasVertex(src) || !hasVertex(dest)) {
			System.out.println("One/Both of the vertex does not exist!");
			return Integer.MIN_VALUE;
		}
		Vertex<T, N> srcVertex = head;
		while (srcVertex != null) {
			if (srcVertex.getVertexInfo().compareTo(src) == 0) {
				Edge<T, N> currentEdge = srcVertex.getFirstEdge();
				while (currentEdge != null) {
					if (currentEdge.getDestination().getVertexInfo().compareTo(dest) == 0) {
						return currentEdge.getRep();
					}
					currentEdge = currentEdge.getNextEdge();
				}
			}
			srcVertex = srcVertex.getNextVertex();
		}
		System.out.println("Edge not found!");
		return Integer.MIN_VALUE;
	}

	/**
	 * This method is used to obtain the neighbors of an individual.
	 *
	 * @param v The individual
	 * @return ArrayList containing the neighbors
	 */
	public ArrayList<T> getNeighbours(T v) {
		if (head == null) {
			System.out.println("Graph is empty!");
			return null;
		}
		if (!hasVertex(v)) {
			System.out.println("Vertex does not exist!");
			return null;
		}
		Vertex<T, N> temp = head;
		ArrayList<T> list = new ArrayList<>();
		while (temp != null) {
			if (temp.getVertexInfo().compareTo(v) == 0) {
				Edge<T, N> currentEdge = temp.getFirstEdge();
				while (currentEdge != null) {
					list.add(currentEdge.getDestination().getVertexInfo());
					currentEdge = currentEdge.getNextEdge();
				}
			}
			temp = temp.getNextVertex();
		}
		return list;
	}

	/**
	 * This method is used to print the friends of an individual and the
	 * reputation of their relationship.
	 */
	public void printEdges() {
		Vertex<T, N> temp = head;
		int count1 = 1;
		while (temp != null) {
			int count2 = 1;
			System.out.println(count1 + ". " + temp.getVertexInfo() + "'s Edge(s)");
			Edge<T, N> currentEdge = temp.getFirstEdge();
			while (currentEdge != null) {
				System.out.println("\t" + count2 + ". " + temp.getVertexInfo() + " to " + currentEdge.getDestination().getVertexInfo() + " Reputation: " + getEdgeWeight(temp.getVertexInfo(), currentEdge.getDestination().getVertexInfo()));
				currentEdge = currentEdge.getNextEdge();
				count2++;
			}
			count1++;
			if (count2 == 1) {
				System.out.println("\tNo edge for this vertex.\n");
			} else {
				System.out.println();
			}
			temp = temp.getNextVertex();
		}
	}

	/**
	 * This method is used to display the details of all individual in the
	 * graph. Printed values include lunch start time, period of lunch and
	 * reputation.
	 */
	public void displayDetails() {
		Vertex<T, N> temp = head;
		while (temp != null) {
			System.out.println("Vertex: " + temp.getVertexInfo() + "\tLunch start: " + temp.getLunchStart() + "\tPeriod: " + temp.getLunchPeriod() + "\tReputation: " + repOf(temp.getVertexInfo()));
			temp = temp.getNextVertex();
		}
	}

	/**
	 * This method is used to find the average reputation of a specific
	 * individual.
	 *
	 * @param src The person we want to find his reputation
	 * @return The reputation value of the individual
	 */
	private int repOf(T src) {
		int reputation = 0, count = 0;
		if (head == null) {
			System.out.println("Graph is empty!");
		}
		Vertex<T, N> temp = head;
		while (temp != null) {
			if (temp.getVertexInfo().compareTo(src) != 0) {
				Edge<T, N> currentEdge = temp.getFirstEdge();
				while (currentEdge != null) {
					if (currentEdge.getDestination().getVertexInfo().compareTo(src) == 0) {
						reputation += currentEdge.getRep();
						count++;
					}
					currentEdge = currentEdge.getNextEdge();
				}
			}
			temp = temp.getNextVertex();
		}
		return reputation / count;
	}

	/**
	 * This method is used to form all timeline that a person can eat lunch with
	 * other persons in 11-14.
	 *
	 * @param gloryPerson The person which want to eat lunch with others to
	 * increase reputation.
	 * @return 2D ArrayList which have all the person that gloryPerson can eat
	 * lunch with.
	 */
	private ArrayList<ArrayList<T>> formAllPath(T gloryPerson) {
		ArrayList<ArrayList<T>> allPath = new ArrayList<>();
		if (head == null) {
			System.out.println("Graph is empty!");
		}
		Vertex<T, N> temp = head;
		while (temp != null) {
			// If the vertex is not gloryPerson
			if (temp.getVertexInfo().compareTo(gloryPerson) != 0) {
				// If one of the rep of the vertex is higher than 5(which means high reputation) 
				if (repOf(temp.getVertexInfo()) > 5) {
					// Create a new arraylist (which represents each timeline) for each possible vertex and add their vertexinfo into the arraylist
					ArrayList<T> possibleHead = new ArrayList<>();
					possibleHead.add(temp.getVertexInfo());
					// Add it to the 2d arraylist
					allPath.add(possibleHead);
				}
			}
			temp = temp.getNextVertex();
		}
		// Create passed to use it as a parameter in findNextPossibleNode
		ArrayList<T> passed = new ArrayList<>();
		passed.add(gloryPerson);
		// For loop to complete all the path 
		for (int i = 0; i < allPath.size(); i++) {
			passed.add(allPath.get(i).get(0));
			ArrayList<T> possiblePaths = new ArrayList<>();
			// Use the findNextPossibleNode recursive methods to complete the path
			ArrayList<T> answer = findNextPossibleNode(passed, Integer.MAX_VALUE, possiblePaths);
			for (T loop : answer) {
				allPath.get(i).add(loop);
			}
			// Reset the passed
			passed = new ArrayList<>();
			passed.add(gloryPerson);
		}
		return allPath;
	}

	/**
	 * This recursive method is used to add the next possible nodes to each path
	 * in the formAllPath method.
	 *
	 * @param passed The ArrayList to check which vertex has been visited.
	 * @param tempEndTime the value to check which EndTime is smaller between
	 * other nodes.
	 * @param possiblePaths the ArrayList to keep all the element that can form
	 * a path.
	 * @return ArrayList which represents the possible paths
	 */
	private ArrayList<T> findNextPossibleNode(ArrayList<T> passed, int tempEndTime, ArrayList<T> possiblePaths) {
		// Create a boolean to check whether it need to recurse
		boolean toRecurse = false;
		Vertex<T, N> temp = head;
		int currentEndTime;
		while (temp != null) {
			// Check whether the vertex has been visited
			if (!passed.contains(temp.getVertexInfo())) {
				// If one of the rep of the vertex is higher than 5(which means high reputation)
				if (repOf(temp.getVertexInfo()) > 5) {
					int currentStartTime = Integer.parseInt(temp.getLunchStart());
					int previousEndTime = Integer.parseInt(findEndTime(passed.get(passed.size() - 1)));
					currentEndTime = Integer.parseInt(findEndTime(temp.getVertexInfo()));
					// Check if the StartTime of current vertex in the path is greater than the End Time of previous vertex in the path
					if (currentStartTime > previousEndTime) {
						// If the path have more than 1 vertex
						if (passed.size() > 1) {
							// If the current vertex EndTime is shorter than the other vertex EndTime.
							if (currentEndTime < tempEndTime) {
								// Set to Recurse to true(let it recurse)
								toRecurse = true;
								// If shorter, set currentEndTime equals tempEndTime
								tempEndTime = currentEndTime;
							}
						}
					}
				}
			}
			temp = temp.getNextVertex();
		}

		temp = head;
		// For loop to add the possible vertex to the arraylist 
		while (temp != null) {
			if (findEndTime(temp.getVertexInfo()).equals(String.valueOf(tempEndTime))) {
				passed.add(temp.getVertexInfo());
				possiblePaths.add(temp.getVertexInfo());
				break;
			}
			temp = temp.getNextVertex();
		}
		// Recurse if toRecurse is true
		if (toRecurse) {
			tempEndTime = Integer.MAX_VALUE;
			findNextPossibleNode(passed, tempEndTime, possiblePaths);
		}
		return possiblePaths;
	}

	/**
	 * Method to print all paths from 's'(source) to 'd'(destination)
	 *
	 * @param s source
	 * @param d destination
	 */
	private void printAllPaths(int s, int d) {
		ArrayList<Integer> visitedList = new ArrayList<>();
		ArrayList<Integer> list = new ArrayList<>();

		// Add source to list
		list.add(s);

		// Recursion function
		printAllPathsRecursion(s, d, visitedList, list);
	}

	/**
	 * A recursive function to print all paths from 'a' to 'b'.
	 *
	 * @param a source
	 * @param b next vertex
	 * @param visitedList store the vertex that visited
	 * @param list store the path
	 */
	private void printAllPathsRecursion(Integer a, Integer b, List<Integer> visitedList, List<Integer> list) {
		// If the vertex equals to destination,means arrived destination. No need to traverse more
		if (a.equals(b)) {
			System.out.println(list);
			result += list.toString() + "%";
			// Increment of the 'total number of friendship way can be formed' by 1
			relationship++;
			return;
		}

		// Mark the current vertex as visited
		visitedList.add(a);
		// Recur for all the vertices adjacent to current vertex
		for (Integer i : adj[a]) {
			// If the vertex is not in visited list
			if (!visitedList.contains(i)) {
				// Store current vertex in list
				list.add(i);
				// Recur for next vertex
				printAllPathsRecursion(i, b, visitedList, list);
				// Remove current vertex in list to look for other possibilities
				list.remove(i);
			}
		}
	}

	/**
	 * This method is used to find the end time of lunch for an individual.
	 *
	 * @param v The vertex we want to find endTime for
	 * @return The endTime
	 */
	private String findEndTime(T v) {
		if (!hasVertex(v)) {
			return "";
		}
		String startTime = "";
		int period = 0;
		Vertex<T, N> temp = head;
		while (temp != null) {
			if (temp.getVertexInfo().compareTo(v) == 0) {
				startTime = temp.getLunchStart();
				period = temp.getLunchPeriod();
				break;
			}
			temp = temp.getNextVertex();
		}
		int minute = Integer.parseInt(startTime.substring(2));
		if (minute + period >= 60) {
			if ((minute + period - 60) >= 10) {
				startTime = String.valueOf(Integer.parseInt(startTime.substring(0, 2)) + 1) + String.valueOf(minute + period - 60);
			} else {
				startTime = String.valueOf(Integer.parseInt(startTime.substring(0, 2)) + 1) + "0" + String.valueOf(minute + period - 60);
			}
		} else {
			if (minute + period >= 10) {
				startTime = startTime.substring(0, 2) + String.valueOf(minute + period);
			} else {
				startTime = startTime.substring(0, 2) + "0" + String.valueOf(minute + period);
			}
		}
		return startTime;
	}

	/**
	 * This is a recursive method to find friend's friends and update/create new
	 * relationship between that individual with the teacher in event1.
	 *
	 * @param src The student
	 * @param dest The teacher
	 * @param rep The reputation value to increase/decrease
	 * @param passed ArrayList to check which vertex has been visited
	 */
	private void findFriendsOfFriends(T src, T dest, int rep, ArrayList<T> passed) {
		ArrayList<T> neighbours = getNeighbours(src);
		for (T v : neighbours) {
			// Get neighbours of current vertex
			// If current vertex haven't been gone through, run the rest code
			// This is the base case for recursion
			// Base case is when passed contains all possible vertexes
			// When it contains all possible vertex, recursion will stop, else continue recursion
			if (!passed.contains(v)) {
				// Add current vertex into the arraylist, to know that it has been gone through now
				passed.add(v);
				findFriendsOfFriends(v, dest, rep, passed);
				// If the edge from current vertex to destination does not exist, then create edge with rep
				String query;
				session = driver.session();
				try (Transaction tx = session.beginTransaction()) {
					if (!hasEdge(v, dest) && (v != dest) && (v != src)) {
						System.out.println(v + " now knows " + dest + ". Reputation of " + dest + " relative to " + v + ": " + rep);
						// Update the database with new relationship
						query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + v + "\' AND b.name = \'" + dest + "\' CREATE (a)-[r:FRIEND {reputation: " + rep + "}]-> (b)";
						tx.run(query);
						addEdge(v, rep, dest);
					} // The edge from current vertex to destination already exist, so add the original edge weight with rep
					else if (hasEdge(v, dest) && (v != dest) && (v != src)) {
						Vertex<T, N> currentVertex = head;
						while (currentVertex != null) {
							if (currentVertex.getVertexInfo() == v) {
								Edge<T, N> currentEdge = currentVertex.getFirstEdge();
								while (currentEdge != null) {
									if (currentEdge.getDestination().getVertexInfo() == dest) {
										currentEdge.setRep(currentEdge.getRep() + rep);
										// Update the database with new reputation value
										// between the friend's friend and teacher
										query = "MATCH (:Person {name: \'" + v + "\'})-[rel:FRIEND]-(:Person {name: \'" + dest + "\'}) SET rel.reputation = " + currentEdge.getRep();
										tx.run(query);
										System.out.println(v + " already knows " + dest + ". Reputation of " + dest + " relative to " + v + ": " + currentEdge.getRep());
									}
									currentEdge = currentEdge.getNextEdge();
								}
							}
							currentVertex = currentVertex.getNextVertex();
						}
					}
					tx.commit();
					tx.close();
				}
				session.close();
			}
		}
	}

	/**
	 * This method is used to check if rumor and crush is in the same group.
	 *
	 * @param src The rumor
	 * @param dest The crush
	 * @param passed ArrayList to check which vertex has been visited
	 * @return Boolean of is crush in the same group with rumor
	 */
	private boolean isGroup(T src, T dest, ArrayList<T> passed) {
		ArrayList<T> neighbours = getNeighbours(src);
		for (T v : neighbours) {
			if (!passed.contains(v)) {
				passed.add(v);
				isGroup(v, dest, passed);
			}
		}
		return passed.contains(dest);
	}

	/**
	 * Method to start the recursion to find all path
	 *
	 * @param source (rumors) The source vertex
	 * @param dest (crush) The destination vertex
	 */
	ArrayList<ArrayList<T>> path2d = new ArrayList<>();

	private void findAllPaths(T source, T dest) {
		// To store visited nodes
		ArrayList<T> passed = new ArrayList<>();
		// To store vertices of current path 
		ArrayList<T> currentPath = new ArrayList<>();
		// Add the source to the path
		currentPath.add(source);
		// Call recursive method
		findAllPathRec(source, dest, passed, currentPath);
		// System.out.println("Path2d " + path2d);
	}

	/**
	 * A recursive method to gets the whole path from source to destination
	 *
	 *
	 * @param source Vertex of the starting of rumors
	 * @param source (rumors) The source vertex
	 * @param dest (crush) The destination vertex
	 * @param currentPath Vertices in current path
	 */
	private void findAllPathRec(T source, T dest, ArrayList<T> passed, ArrayList<T> currentPath) {
		if (source.equals(dest)) {
			//  If the destination point is found, do not need to continue traverse
			ArrayList<T> temp = new ArrayList<>();
			temp.addAll(currentPath);
			path2d.add(temp);

		}
		// Add the current node as visited node
		passed.add(source);

		// Recur all the neighbours (adjacent vertices) of current vertex
		ArrayList<T> temp = getNeighbours(source);
		for (int i = 0; i < temp.size(); i++) {
			if (!passed.contains(temp.get(i))) {
				currentPath.add(temp.get(i));
				findAllPathRec(temp.get(i), dest, passed, currentPath);
				currentPath.remove(temp.get(i));
			}
		}
		// Remove the current vertex from visited vertex
		passed.remove(source);
	}

	/**
	 *
	 * @param teacher The teacher
	 * @param student The student
	 * @return The teachingRep value between this teacher and student
	 */
	public int getTeachingRep(T teacher, T student) {
		return teacherRep[(int) (Object) teacher][(int) (Object) student];
	}

	/**
	 * Increase/Decrease the value of teachingRep further to indicate event 2
	 * between this teacher and student has been run through before.
	 *
	 * @param teacher The teacher
	 * @param student The student
	 */
	public void changeTeachingRep(T teacher, T student) {
		if (teacherRep[(int) (Object) teacher][(int) (Object) student] > 0) {
			teacherRep[(int) (Object) teacher][(int) (Object) student]++;
		} else if (teacherRep[(int) (Object) teacher][(int) (Object) student] < 0) {
			teacherRep[(int) (Object) teacher][(int) (Object) student]--;
		}
	}

	/**
	 * This method is to clear the database when the application first run.
	 */
	private void clearDatabase() {
		session = driver.session();
		try (Transaction tx = session.beginTransaction()) {
			String query = "MATCH (a) -[r] -> () DELETE a, r";
			tx.run(query);
			tx.commit();
			tx.close();
		}
		session.close();
	}

	/**
	 * This method is used to determine if a teacher will provide student with
	 * proper teaching, or not.
	 *
	 * @param teacher The teacher
	 * @param student The student
	 * @return Response message to pass back for HTTP request
	 */
	public String event1(T teacher, T student) {
		if (!hasVertex(teacher) || !hasVertex(student)) {
			return "NV";
		}
		// If edge does not exist, then proceed, because have to make sure is stranger
		if (!hasEdge(teacher, student)) {
			Vertex<T, N> temp1 = head;
			// Traverse to teacher vertex
			while (temp1 != null) {
				if (temp1.getVertexInfo() == teacher) {
					// Depending on dive rate of teacher, decides if teacher can teach like an expert or noob
					// Dive > 80, chance of teaching good = 1/5
					// Dive > 60, chance of teaching good = 2/5
					// Dive > 40, chance of teaching good = 3/5
					// Dive > 20, chance of teaching good = 4/5
					// Dive >  0, chance of teaching good = 5/5
					boolean goodTeaching;
					if (temp1.getDive() > 80) {
						goodTeaching = new Random().nextInt(100) >= 80;
					} else if (temp1.getDive() > 60) {
						goodTeaching = new Random().nextInt(100) >= 60;
					} else if (temp1.getDive() > 40) {
						goodTeaching = new Random().nextInt(100) >= 40;
					} else if (temp1.getDive() > 20) {
						goodTeaching = new Random().nextInt(100) >= 20;
					} else {
						goodTeaching = true;
					}
					// Traverse to student vertex
					Vertex<T, N> temp2 = head;
					while (temp2 != null) {
						if (temp2.getVertexInfo() == student) {
							break;
						}
						temp2 = temp2.getNextVertex();
					}
					// Taught well, then reputation of student relative to teacher is 2
					//					 reputation of teacher relative to student is 10
					session = driver.session();
					System.out.println("Event 1");
					System.out.println("Teacher dive: " + temp1.getDive() + "\t Teach good: " + goodTeaching);
					try (Transaction tx = session.beginTransaction()) {
						if (goodTeaching) {
							System.out.println(teacher + " taught " + student + " very well!");
							addEdge(teacher, 2, student);
							addEdge(student, 10, teacher);
							// Update graph database with new relationship
							String query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + teacher + "\' AND b.name = \'" + student + "\' CREATE (a)-[r:FRIEND {reputation: " + 2 + "}]-> (b)";
							tx.run(query);
							query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + student + "\' AND b.name = \'" + teacher + "\' CREATE (a)-[r:FRIEND {reputation: " + 10 + "}]-> (b)";
							tx.run(query);
							tx.commit();
							tx.close();
							session.close();
							// Update teacherRep to 1 because teacher taught student well
							teacherRep[(int) (Object) teacher][(int) (Object) student] = 1;
							return "good";
						} // Taught bad, then reputation of student relative to teacher is 2
						//					  reputation of teacher relative to student is 2
						else {
							System.out.println(teacher + " taught " + student + " badly.");
							addEdge(teacher, 2, student);
							addEdge(student, 2, teacher);
							// Update graph database with new relationship
							String query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + teacher + "\' AND b.name = \'" + student + "\' CREATE (a)-[r:FRIEND {reputation: " + 2 + "}]-> (b)";
							tx.run(query);
							query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + student + "\' AND b.name = \'" + teacher + "\' CREATE (a)-[r:FRIEND {reputation: " + 2 + "}]-> (b)";
							tx.run(query);
							tx.commit();
							tx.close();
							session.close();
							teacherRep[(int) (Object) teacher][(int) (Object) student] = -1;
							return "bad";
						}
					}
				}
				temp1 = temp1.getNextVertex();
			}
		} else {
			System.out.println("Not stranger!");
			return "NS";
		}
		System.out.println("Something went wrong while teaching!");
		return "Error";
	}

	/**
	 * This method is used to demonstrate situation when student in event 1
	 * chit-chat with his friends about the teacher, the news will further
	 * propagate to the friend's friends
	 *
	 * This method implements findFriendsOfFriends to find the friend's friends.
	 *
	 * @param src The student
	 * @param dest The teacher
	 * @return Response message to pass back for HTTP request
	 */
	public String event2(T src, T dest) {
		if (!hasVertex(src) || !hasVertex(dest)) {
			return "NV";
		}
		Vertex<T, N> temp = head;
		ArrayList<T> passed = new ArrayList<>();
		passed.add(src);
		passed.add(dest);
		int rep;
		boolean goodOrBad;
		int tempVal = getTeachingRep(dest, src);
		System.out.println(tempVal);
		switch (tempVal) {
			case 0:
				System.out.println(dest + " never taught " + src + " before!");
				return "NT";
			case 1:
				goodOrBad = true;
				changeTeachingRep(dest, src);
				break;
			case -1:
				goodOrBad = false;
				changeTeachingRep(dest, src);
				break;
			default:
				System.out.println("Event 2 between " + src + " and " + dest + " has been run through before.");
				return "AT";
		}

		if (goodOrBad) {
			rep = (int) (getEdgeWeight(src, dest) * 0.5);
		} else {
			rep = getEdgeWeight(src, dest) * -1;
		}
		// Traverse to the source vertex
		while (temp != null) {
			if (temp.getVertexInfo() == src) {
				// Initiate propagation (Start chitchat between friends, and friends of friends)
				System.out.println("Event 2");
				findFriendsOfFriends(src, dest, rep, passed);
				// End loop after everything is done
				return "good";
			}
			temp = temp.getNextVertex();
		}
		return "bad";
	}

	/**
	 * This method is used to find maximum rep a person can get from eating
	 * lunch with other person. This method implements formAllPath to find all
	 * the possible people to eat with.
	 *
	 * @param gloryPerson The person which want to eat lunch with others to
	 * increase reputation.
	 * @return Response message to pass back for HTTP request
	 */
	public String event3(T gloryPerson) {
		if (!hasVertex(gloryPerson)) {
			return "NV";
		}
		ArrayList<ArrayList<T>> lunchList = formAllPath(gloryPerson);
		System.out.println("Event 3");
		System.out.println("All of the Possible Path: " + lunchList);
		int maxFriend = Integer.MIN_VALUE;
		int IofMaxSize = 0;
		// Use a for loop to see which path is longest
		for (int i = 0; i < lunchList.size(); i++) {
			if (lunchList.get(i).size() > maxFriend) {
				maxFriend = lunchList.get(i).size();
				IofMaxSize = i;
			}
		}
		String query;
		session = driver.session();
		int repCount = 0;
		try (Transaction tx = session.beginTransaction()) {
			// Use a for loop to increase rep of the element in arraylist to gloryPerson or addEdge if originally do not have edge between them
			for (T friend : lunchList.get(IofMaxSize)) {
				if (!hasEdge(gloryPerson, friend)) {
					// Update the database with new relationship
					query = "MATCH (a:Person), (b:Person) WHERE a.name = \'" + friend + "\' AND b.name = \'" + gloryPerson + "\' CREATE (a)-[r:FRIEND {reputation: " + 1 + "}]-> (b)";
					tx.run(query);
					repCount++;
					addEdge(friend, 1, gloryPerson);
				} else {
					if (head == null) {
						System.out.println("Graph is empty!");
					}
					Vertex<T, N> newFriend = head;
					while (newFriend != null) {
						if (newFriend.getVertexInfo().equals(friend)) {
							Edge<T, N> temp = newFriend.getFirstEdge();
							while (temp != null) {
								if (temp.getDestination().getVertexInfo().compareTo(gloryPerson) == 0) {
									temp.setRep(temp.getRep() + 1);
									repCount++;
									// Update the database with new reputation value 
									// Between this new friend and gloryPerson
									query = "MATCH (:Person {name: \'" + newFriend.getVertexInfo() + "\'})-[rel:FRIEND]-(:Person {name: \'" + gloryPerson + "\'}) SET rel.reputation = " + temp.getRep();
									tx.run(query);
								}
								temp = temp.getNextEdge();
							}
						}
						newFriend = newFriend.getNextVertex();
					}
				}
			}
			System.out.println("Total reputation increased: " + repCount);
			tx.commit();
			tx.close();
		}
		session.close();
		return String.valueOf(repCount);
	}

	/**
	 * This method implements an application that arrange books until all the
	 * books in the stack is in descending order.
	 *
	 * This method get number of books and a String of height of all books as
	 * input.
	 *
	 * The output of this method is the number of rounds to arrange the book
	 * until it reaches the requirement.
	 *
	 * @param numOfBook The number of books
	 * @param books The height of each book
	 * @return Response message to pass back for HTTP request
	 */
	public String event4(int numOfBook, int[] books) {
		Stack<Integer> bookStack = new Stack<>();
		for (int i = 0; i < numOfBook; i++) {
			bookStack.add(books[i]);
		}
		int bookCount;
		Stack<Integer> arrange = new Stack<>();
		System.out.println("Event 4");
		int round = 0;
		boolean havePop = true;
		while (havePop) {
			// Clone a stack from the original stack for every round
			// to see whethere there is changes at the end of the loop
			bookCount = bookStack.size();
			Stack<Integer> toCompare = new Stack<>();
			toCompare.addAll(bookStack);

			havePop = false;
			// Check every book in the bookStack and for every book that
			// are shorter or same height with the book after it,
			// pop the book and store into another stack to proceed to next round,
			// else, pop only
			for (int i = 0; i < bookCount - 1; i++) {
				int temp = bookStack.pop();

				if (temp <= bookStack.peek()) {
					arrange.add(temp);
					havePop = true;
				}
			}
			// Break the loop if there is no book popped from the original Stack,
			// else increment round by one and pop back all the books to the original
			// stack for next round of arranging 
			if (arrange.isEmpty()) {
				break;
			} else {
				arrange.add(bookStack.pop());
				round++;
				while (!arrange.isEmpty()) {
					bookStack.add(arrange.pop());
				}
			}
			System.out.println("Bookstack: " + bookStack.toString());
			// Terminate this loop if the new bookStack is same as the original
			// bookStack that we cloned because this means that there is no changes 
			// in the arrangement of books after this round
			if (bookStack.equals(toCompare)) {
				round--;
				break;
			}
		}

		return String.valueOf(round);
	}

	/**
	 * This method is to determine if a rumours spread from a stranger, are we
	 * able to stop the rumours before it reaches our crush where the rumours
	 * propagates at one jump per day.
	 *
	 * This method implement the method findAllPath to find all the possible
	 * path for the rumours to reach the crush and compare the days of every
	 * path.
	 *
	 * Precondition: Rumours and crush cannot be friends
	 *
	 * @param rumours The vertex where the rumours start spreading
	 * @param crush The vertex where we should stop the rumours from reaching it
	 * @return Response message to pass back for HTTP request
	 */
	public String event5(T rumours, T crush) {
		if (!hasVertex(rumours) || !hasVertex(crush)) {
			return "NV";
		}
		ArrayList<T> passed = new ArrayList<>();
		boolean print = true;
		// Check if the one who spread the rumours is in same group as crush
		passed.add(rumours);
		// If there are not in the same group, it is imposibble for your crush 
		// to hear about the rumours
		if (!isGroup(rumours, crush, passed)) {
			print = true;
		} else {
			// Determine all the possible paths from the rumours to crush
			findAllPaths(rumours, crush);
			// Compare the length for every path in the 2d ArrayList
			for (int i = 0; i < path2d.size(); i++) {
				for (int j = 0; j < path2d.size(); j++) {
					if (j != i) {
						if (path2d.get(i).size() == path2d.get(j).size()) {
							print = false;
							break;
						}
					}

				}
			}
		}
		System.out.println("Path: " + path2d.toString());
		path2d.clear();

		if (print) {
			return "good";
		} else {
			return "bad";
		}
	}

	/**
	 * Find the total number of unique ways the friendship can be formed.
	 *
	 * @param line The number of relationships exist
	 * @param idividual Each entity in the relationship
	 * @return Response message to pass back for HTTP request and all
	 * friendships
	 */
	public String event6(int line, int[] individual) {
		System.out.println("\n--------Event 6--------");
		// Find total number of the vertices
		int max = individual[0];
		for (int i = 0; i < individual.length; i++) {
			if (individual[i] > max) {
				max = individual[i];
			}
		}
		Graph friendship = new Graph(max + 1);
		// Add edge
		for (int i = 0; i < individual.length; i += 2) {
			friendship.addUndirectedEdge(individual[i], individual[i + 1]);
		}
		// Print the friendship can be formed
		for (int i = 1; i < max; i++) {
			for (int j = i + 1; j <= max; j++) {
				printAllPaths(i, j);

			}
		}
		String toReturn = String.valueOf(relationship) + "%" + result;
		relationship = 0;
		result = "";
		return toReturn;
	}
}
