public class Wght {
	int UnqID1;
	int UnqID2;
	int wgt;
	public Wght(int unqid1, int unqid2, int w)
	{
		if(unqid1<unqid2)
		{
			this.UnqID1=unqid1;
			this.UnqID2=unqid2;
		}
		else
		{
			this.UnqID2=unqid1;
			this.UnqID1=unqid2;
		}
		this.wgt=w;
	}
}
