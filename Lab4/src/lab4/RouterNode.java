package lab4;

import javax.swing.*;        

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int[] costs = new int[RouterSimulator.NUM_NODES];

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator routerSimulator, int[] costs) {
    myID = ID;
    this.sim = routerSimulator;
    for (int i = 0; i < costs.length; i++) {
    	costs[i] = RouterSimulator.INFINITY;
    }
    
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");

    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
    printDistanceTable();
  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
	  printDistanceTable();
  }
  

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);
    printDistanceTable();
  }
  

  //--------------------------------------------------
  public void printDistanceTable() {
	  
	  String chartTop = "    dst |";
	  String horizBars = "---------";
	  
	  String nbrLines[] = new String[costs.length - 1]; // TO IMPLEMENT LATER
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
			  n++;
		  }
	  }
	  myGUI.println("Current table for " + myID + "  at time " + sim.getClocktime());
	  myGUI.println("\nDistancetable:");
	  myGUI.println(chartTop);
	  myGUI.println(horizBars);
	  myGUI.println();
	  
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
