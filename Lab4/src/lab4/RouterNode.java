package lab4;

import java.util.Arrays;       

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int num_nodes = RouterSimulator.NUM_NODES;
  private int[] costs = new int[RouterSimulator.NUM_NODES];
  private int[][] distances = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
  private int[] route = new int[RouterSimulator.NUM_NODES];
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
    
    System.arraycopy(costs, 0, distances[myID], 0, RouterSimulator.NUM_NODES);
    System.arraycopy(costs, 0, route, 0, RouterSimulator.NUM_NODES);
    
    
    printDistanceTable();
  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
	  if (!distances[pkt.sourceid].equals(pkt.mincost)) {
		  System.arraycopy(pkt.mincost.clone(), 0, distances[pkt.sourceid], 
				  0, RouterSimulator.NUM_NODES);
		  boolean change = false;
		  for (int n = 0; n < num_nodes; n++) {
			  if (n == myID) {
				  continue;
			  }
			  
			  int newCost = distances[route[n]][n] + distances[myID][route[n]];
			  
			  change = (distances[myID][n] != newCost);
			  distances[myID][n] = newCost;
			  
			  if (distances[myID][n] > costs[n]) {
				  distances[myID][n] = costs[n];
				  route[n] = n;
				  change = true;
			  }
				
			  for (int i = 0; i < num_nodes; i++) {
				  int routeCost = distances[myID][n] + 
						  distances[n][i];
				  if (distances[myID][i] > routeCost) {
					  distances[myID][i] = routeCost;
					  route[i] = route[n];
					  change = true;
				  }
				  
			  }
			  if (change) {
				  //asdf
			  }
		  }
	  }
	  
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
