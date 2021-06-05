/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assignment.thesociopath;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author XuanEr
 */
@RestController
public class EventController {
	// Vertex
	Integer[] person = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
	// Adjacency matrix with weight
	int[][] friendlist = {
		{0, 5, 0, 0, 0, 0, 4, 0, 0, 0},
		{8, 0, 5, 0, 6, 9, 0, 0, 0, 0},
		{0, 4, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 7, 0, 7},
		{0, 2, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 7, 0, 0, 0, 0, 0, 0, 0, 0},
		{3, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 10, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 5},
		{0, 0, 0, 7, 0, 0, 0, 0, 6, 0},};
	// Create graph
	Graph<Integer, Integer> friendGraph = new Graph<>(person, friendlist);
	
	/**
	 * This method is used to clear database when a HTTP POST request is mapped
	 * at "/api/clearDb/".
	 * 
	 * @param request hold the request data
	 * @return response message of success clearance
	 */
	@RequestMapping(value = "/api/clearDb/", method = RequestMethod.POST)
	public ResponseEntity clearDatabase(@RequestBody Map<String,Object> request){
		Map<String,Object> payload = new HashMap<>();
		Map<String,Object> response = new HashMap<>();
		friendGraph.clear();
		friendGraph = new Graph<>(person, friendlist);
		payload.put("status", "good");
		response.put("success", true);
		response.put("payload", payload);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * This method is used to perform event 1 when a HTTP POST request is mapped 
	 * at "/api/event1".
	 * 
	 * @param request hold the request data
	 * @return response message of whether succeed or failed
	 */
	@RequestMapping(value = "/api/event1/", method = RequestMethod.POST)
	public ResponseEntity event1(@RequestBody Map<String, Object> request) {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		int teacher, student;
		// Number Format Exception handler
		try {
			teacher = Integer.parseInt(request.get("teacher").toString());
			student = Integer.parseInt(request.get("student").toString());
		} catch (NumberFormatException e) {
			payload.put("status", "NFE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		if(teacher == student){
			payload.put("status", "NE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		boolean proceedEvent2 = Boolean.parseBoolean(request.get("proceedEvent2").toString());
		String msg = friendGraph.event1(teacher, student);
		if (proceedEvent2) {
			String temp = friendGraph.event2(student, teacher);
			payload.put("status", msg);
			payload.put("event2", temp);
			response.put("success", true);
			response.put("payload", payload);
		} else {
			payload.put("status", msg);
			response.put("success", true);
			response.put("payload", payload);
		}
		System.out.println("Event 1: " + msg);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * This method is used to perform event 2 when a HTTP POST request is mapped 
	 * at "/api/event2/".
	 * 
	 * @param request
	 * @return response message of whether succeed or failed
	 */
	@RequestMapping(value = "/api/event2/", method = RequestMethod.POST)
	public ResponseEntity event2(@RequestBody Map<String, Object> request) {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		int teacher, student;
		// Number Formation Exception handler
		try {
			teacher = Integer.parseInt(request.get("teacher").toString());
			student = Integer.parseInt(request.get("student").toString());
		} catch (NumberFormatException e) {
			payload.put("status", "NFE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		if(teacher == student){
			payload.put("status", "NE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		String msg = friendGraph.event2(student, teacher);
		System.out.println("Event 2: " + msg);
		payload.put("status", msg);
		response.put("success", true);
		response.put("payload", payload);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * This method is used to perform event 3 when a HTTP POST request is mapped 
	 * at "/api/event3/".
	 * 
	 * @param request
	 * @return response message of whether succeed or failed
	 */
	@RequestMapping(value = "/api/event3/", method = RequestMethod.POST)
	public ResponseEntity event3(@RequestBody Map<String, Object> request) {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		int person;
		// Number Format Exception handler
		try {
			person = Integer.parseInt(request.get("person").toString());
		} catch (NumberFormatException e) {
			payload.put("status", "NFE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		String msg = friendGraph.event3(person);
		payload.put("status", "good");
		payload.put("answer", msg);
		response.put("success", true);
		response.put("payload", payload);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * This method is used to perform event 4 when a HTTP POST request is mapped 
	 * at "/api/event4/".
	 * 
	 * @param request
	 * @return response message of whether succeed or failed
	 */
	@RequestMapping(value = "/api/event4/", method = RequestMethod.POST)
	public ResponseEntity event4(@RequestBody Map<String, Object> request) {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		int numOfBooks;
		String[] temp = request.get("books").toString().split(" ");
		int[] books = new int[temp.length];
		// Number Format Exception handler
		try {
			numOfBooks = Integer.parseInt(request.get("numOfBooks").toString());
			for (int i = 0; i < books.length; i++) {
				books[i] = Integer.parseInt(temp[i]);
			}
		} catch (NumberFormatException e) {
			payload.put("status", "NFE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		// Index Out Of Bounds handler
		if (numOfBooks != books.length) {
			payload.put("status", "EL");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		String msg = friendGraph.event4(numOfBooks, books);
		System.out.println("Event 4: " + msg);
		payload.put("status", msg);
		response.put("success", true);
		response.put("payload", payload);

		return ResponseEntity.ok(response);
	}
	
	/**
	 * This method is used to perform event 5 when a HTTP POST request is mapped 
	 * at "/api/event5/".
	 * 
	 * @param request
	 * @return response message of whether succeed or failed
	 */
	@RequestMapping(value = "api/event5/", method = RequestMethod.POST)
	public ResponseEntity event5(@RequestBody Map<String, Object> request) {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		boolean randomize = Boolean.parseBoolean(request.get("randomize").toString());
		int crush, rumour;
		String msg;
		// Randomize crush and rumour
		if (randomize) {
			Random r = new Random();
			do {
				crush = r.nextInt(friendGraph.getSize()) + 1;
				rumour = r.nextInt(friendGraph.getSize()) + 1;
			} while (friendGraph.getNeighbours(crush).contains(rumour) || crush == rumour || friendGraph.getNeighbours(rumour).contains(crush));
		} else {
		// Value given by user
			// Number Format Exception handler
			try {
				rumour = Integer.parseInt(request.get("rumour").toString());
				crush = Integer.parseInt(request.get("crush").toString());
				// Crush cannot be neighbour with rumour
				if(friendGraph.getNeighbours(crush).contains(rumour) || friendGraph.getNeighbours(rumour).contains(crush)){
					payload.put("status", "NN");
					response.put("success", true);
					response.put("payload", payload);

					return ResponseEntity.ok(response);
				}
				// Crush cannot be equal to rumour
				if(crush == rumour){
					payload.put("status", "NE");
					response.put("success", true);
					response.put("payload", payload);

					return ResponseEntity.ok(response);
				}
			} catch (NumberFormatException e) {
				payload.put("status", "NFE");
				response.put("success", true);
				response.put("payload", payload);

				return ResponseEntity.ok(response);
			}
		}
		System.out.println("Event 5");
		System.out.println("rumour: " + rumour + "\tCrush: " + crush);
		msg = friendGraph.event5(rumour, crush);
		System.out.println("Event 5: " + msg);
		payload.put("status", msg);
		if(randomize){
			payload.put("rumour", rumour);
			payload.put("crush", crush);
		}
		response.put("success", true);
		response.put("payload", payload);

		return ResponseEntity.ok(response);
	}
	
	/**
	 * This method is used to perform event 6 when a HTTP POST request is mapped 
	 * at "/api/event6/".
	 * 
	 * @param request
	 * @return response message of whether succeed or failed
	 */
	@RequestMapping(value = "/api/event6/", method = RequestMethod.POST)
	public ResponseEntity event6(@RequestBody Map<String, Object> request) {
		Map<String, Object> payload = new HashMap<>();
		Map<String, Object> response = new HashMap<>();
		int line;
		// Number Format Exception handler for numOfRelationships
		try {
			line = Integer.parseInt(request.get("numOfRelationships").toString());
		} catch (NumberFormatException e) {
			payload.put("status", "NFE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		String placeHolder = request.get("relationships").toString();
		placeHolder = placeHolder.substring(1, placeHolder.length() - 1);
		boolean printPath = Boolean.parseBoolean(request.get("printPath").toString());
		int[] individual = new int[line * 2];
		placeHolder = placeHolder.replaceAll(",", "");
		String[] temp = placeHolder.split(" ");
		// Index Out Of Bounds handler
		if (temp.length != line * 2) {
			payload.put("status", "EL");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		// Number Format Exception handler for when parsing each entity
		// into individual array
		try {
			for (int i = 0; i < individual.length; i++) {
				individual[i] = Integer.parseInt(temp[i]);
			}
		} catch (NumberFormatException e) {
			payload.put("status", "NFE");
			response.put("success", true);
			response.put("payload", payload);

			return ResponseEntity.ok(response);
		}
		String msg = friendGraph.event6(line, individual);
		int index = msg.indexOf("%");
		System.out.println("Event 6: " + msg.substring(0, index));
		payload.put("status", msg.substring(0, index));
		if(printPath) {
			payload.put("path", msg.substring(index+1));
		}
		response.put("success", true);
		response.put("payload", payload);

		return ResponseEntity.ok(response);
	}
}
