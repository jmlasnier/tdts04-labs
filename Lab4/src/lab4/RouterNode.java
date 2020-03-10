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
  private boolean poisonReverse = false;

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
		  boolean change;
		  for (int n = 0; n < num_nodes; n++) {
			  change = false;
			  
			  if (n == myID) {
				  continue;
			  }
			  
			  int newCost = distances[route[n]][n] + distances[myID][route[n]];
			  
			  change = (distances[myID][n] != newCost);
			  distances[myID][n] = newCost;			  
			  
			  if (findNewRoute(n) || change) {
				  broadcast();
			  }
		  }
	  }
	  
	  printDistanceTable();
  }
  

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    
	  if (poisonReverse) {
		  for (int i = 0; i < num_nodes; i++) {
			  if (distances[myID][i] == pkt.destid) {
				  pkt.mincost[i] = RouterSimulator.INFINITY;
			  }
		  }
	  }
	  sim.toLayer2(pkt);
  }
  
  private void broadcast() {
	  for (int i = 0; i < num_nodes; i++) {
		  if (costs[i] != myID && i != myID) {
			  sendUpdate(new RouterPacket(myID, i, distances[myID]));
		  }
	  }
  }
  
//--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
	  costs[dest] = newcost;
	  System.out.println("Link cost for route " + myID + "->" + dest + "=" + newcost);
	  
	  if (route[dest] == dest) {
		  distances[myID][dest] = newcost;
	  }
	  
	  findNewRoute(dest);
	  
	  
	  
  }
  
  private boolean findNewRoute(int dest) {
	  boolean change = false;
	  
	  if (distances[myID][dest] > costs[dest]) {
		  distances[myID][dest] = costs[dest];
		  route[dest] = dest;
		  change = true;
	  }
		
	  for (int i = 0; i < num_nodes; i++) {
		  int routeCost = distances[myID][dest] + 
				  distances[dest][i];
		  if (distances[myID][i] > routeCost) {
			  distances[myID][i] = routeCost;
			  route[i] = route[dest];
			  change = true;
		  }
		  
	  }
	  
	  return change;
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
		  routeLine += "  " + String.format("%3d", route[i]);
		  chartTop += ("    " + i);
		  horizBars += "-----";
		  if (i != myID) {
			  nbrLines[n] = " nbr  " + n + "_|";
			  for (int j = 0; j < costs.length; j++) {
				  nbrLines[n] += "  " + String.format("%3d", distances[i][j]);
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
	  myGUI.println(routeLine);
	  
  }

  
}
