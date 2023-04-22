import java.io.Serializable;
public class MinWghtOGE implements Serializable
{
	int trNdUnqID;
	int wgt;
	int nonTrNdUnqID;
	
	MinWghtOGE()
	{
		this.trNdUnqID =0;
		this.wgt=Integer.MAX_VALUE;
		this.nonTrNdUnqID=0;
	}
}