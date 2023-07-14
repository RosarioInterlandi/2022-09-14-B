package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private Graph<Album, DefaultEdge> grafo;
	private ItunesDAO dao;
	private List<Album> allNodes;
	private Map<Integer, Album> albumIdMap;
	
	
	public Model() {
		this.dao = new ItunesDAO();
	}
	

	
	public void BuidGraph(String durata) {
		//1s = 1000 ms, unità del database 
		int durataInMillis = Integer.parseInt(durata)*1000;

		this.allNodes = new ArrayList<>();
		this.albumIdMap = new HashMap<>();
		
		this.grafo = new SimpleGraph<>(DefaultEdge.class);
		this.allNodes = this.dao.getVertici(durataInMillis);
		Graphs.addAllVertices(this.grafo, allNodes);
		for (Album nodo : allNodes) {
			this.albumIdMap.put(nodo.getAlbumId(), nodo);
		}
		//Aggiungo vertici
		for (Album nodo1 : allNodes) {
			for (Album nodo2: allNodes) {
				if (nodo1 != nodo2) {
					Set<Integer> intersezione = new HashSet(dao.getPlayList(nodo1.getAlbumId()));
					Set<Integer> playlist2 = new HashSet<>(dao.getPlayList(nodo2.getAlbumId()));
					intersezione.retainAll(playlist2);
					if (intersezione.size()>0) {
						this.grafo.addEdge(nodo2, nodo1);
					}
				}
			}
		}
	}
	
	public Set<Album> getComponente(Album a1) {
		ConnectivityInspector<Album, DefaultEdge> ci =
				new ConnectivityInspector<>(this.grafo) ;
		return ci.connectedSetOf(a1) ;
	}
	
	public List<Album> getVertici() {
		List<Album> result = new ArrayList<>(this.grafo.vertexSet());
				
		Collections.sort(result);
		return result;
	}
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
}
