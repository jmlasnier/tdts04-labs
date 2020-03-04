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
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");

    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);

  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {

  }
  

  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);

  }
  

  //--------------------------------------------------
  public void printDistanceTable() {
	  
	  String chartTop = "    dst |";
	  String horizBars = "---------";
	  for (int i = 0; i < costs.length; i++) {
		  chartTop += ("    " + i);
		  horizBars += "-----";
		  
	  }
	  
	  String nbrLines[] = new String[costs.length - 1]; // TO IMPLEMENT LATER
	  String costLine = " cost   |";
	  String routeLine = " route  |"; // TO IMPLEMENT AFTER ALGORITHM
	  for (int i = 0; i < costs.length; i++) {
		  costLine += "  " + String.format("%3d", costs[i]);
		  
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
