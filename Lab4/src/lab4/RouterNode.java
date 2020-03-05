package lab4;

import java.util.Arrays;
import java.util.HashMap;

import javax.swing.*;        

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int[] costs = new int[RouterSimulator.NUM_NODES];
  private int[][] distances = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
  private HashMap<Integer, Integer> route = new HashMap<>();
  private boolean reversePoison = false;

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator routerSimulator, int[] costs) {
    myID = ID;
    this.sim = routerSimulator;
    myGUI = new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
    
    for (int i[] : distances) {
    	Arrays.fill(i, RouterSimulator.INFINITY);
    }
    
    distances[myID][myID] = 0;
    
    for (int n = 0; n < costs.length; n++) {
    	distances[myID][n] = costs[n];
    	if (costs[n] != RouterSimulator.INFINITY && n != myID) {
    		route.put(n, n);
    	}
    }
    
    printDistanceTable();
  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
	  printDistanceTable();
  }
  

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);
  }
  

  //--------------------------------------------------
  public void printDistanceTable() {
	  
	  String chartTop = "    dst |";
	  String horizBars = "---------";
	  
	  String nbrLines[] = new String[costs.length - 1]; // TO IMPLEMENT LATER
	  String nbrLinesTotal = "";
	  String costLine = " cost   |";
	  String routeLine = " route  |"; // TO IMPLEMENT AFTER ALGORITHM
	  int n = 0;
	  for (int i = 0; i < costs.length; i++) {
		  costLine += "  " + String.format("%3d", costs[i]);
		  chartTop += ("    " + i);
		  horizBars += "-----";
		  if (i != myID) {
			  nbrLines[n] = " nbr  " + n + "_|";
			  for (int j = 0; j < costs.length; j++) {
				  nbrLines[n] += "  " + String.format("%3d", costs[j]);
			  }
			  nbrLinesTotal += (nbrLines[n] + "\n");
			  n++;
		  }
	  }
	  myGUI.println("Current table for " + myID + "  at time " + sim.getClocktime());
	  myGUI.println("\nDistancetable:");
	  myGUI.println(chartTop);
	  myGUI.println(horizBars);
	  myGUI.println(nbrLinesTotal);
	  
	  myGUI.println("\nOur distance vector and routes:");
	  myGUI.println(chartTop);
	  myGUI.println(horizBars);
	  myGUI.println(costLine);
	  
  }

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
	  costs[dest] = newcost;
  }

}
