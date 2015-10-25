import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class tuple implements Comparable<tuple>
{
	int x, y;
	char desc[] = new char [500];
	public int compareTo(tuple t) {
		long hdiff= getHilbertValue(x,y) - getHilbertValue(t.x,t.y);
		if(hdiff==0)
			return 0;
		else if(hdiff>0)
			return 1;
		else 
			return -1;
	}
	int getx()
	{
		return x;
	}
	int gety()
	{
		return y;
	}
	static long getHilbertValue(int x1, int x2) {
		long res = 0;
		int BITS_PER_DIM = 16;
		for (int ix = BITS_PER_DIM - 1; ix >= 0; ix--) {
			long h = 0;
			long b1 = (x1 & (1 << ix)) >> ix;
			long b2 = (x2 & (1 << ix)) >> ix;
			if (b1 == 0 && b2 == 0) {
				h = 0;
			} else if (b1 == 0 && b2 == 1) {
				h = 1;
			} else if (b1 == 1 && b2 == 0) {
				h = 3;
			} else if (b1 == 1 && b2 == 1) {
				h = 2;
			}
			res += h << (2 * ix);
		}
		return res;
	}
}

class dataPage
{
	ArrayList <tuple>t;
	int pageid;
	int minx, miny, maxx, maxy;
	public dataPage()
	{
		t = new ArrayList<tuple>();
	}
	void addTuple(tuple temp)
	{
		t.add(temp);
	}
	tuple gettuple(int i)
	{
		return t.get(i);
	}
	void setindex(int i)
	{
		pageid = i;
	}
	int size()
	{
		return t.size();
	}
	void setminmax()
	{
		minx = minx();
		miny = miny();
		maxx = maxx();
		maxy = maxy();
	}
	int minx()
	{
		int temp = t.get(0).x;
		for(int i=0; i<t.size(); i++)
			if(t.get(i).x < temp)
				temp = t.get(i).x;
		return temp;

	}
	int miny()
	{
		int temp = t.get(0).y;
		for(int i=0; i<t.size(); i++)
			if(t.get(i).y < temp)
				temp = t.get(i).y;
		return temp;
				
	}
	int maxx()
	{
		int temp = t.get(0).x;
		for(int i=0; i<t.size(); i++)
			if(t.get(i).x > temp)
				temp = t.get(i).x;
		return temp;
				
	}
	int maxy()
	{
		int temp = t.get(0).y;
		for(int i=0; i<t.size(); i++)
			if(t.get(i).y > temp)
				temp = t.get(i).y;
		return temp;
				
	}
}


class bound
{
	int minx, miny, maxx, maxy;
	int pointer;
}

class indexPage
{
	ArrayList <bound>box;
	int minx, miny, maxx, maxy;
	public indexPage()
	{
		box = new ArrayList<bound>();
	}
	void addbound(bound b)
	{
		box.add(b);
	}
	bound getbound(int i)
	{
		return box.get(i);
	}
	int size()
	{
		return box.size();
	}
	void setminmax()
	{
		minx = minx();
		miny = miny();
		maxx = maxx();
		maxy = maxy();
	}
	
	int minx()
	{
		int temp = box.get(0).minx;
		for(int i=0; i<box.size(); i++)
			if(box.get(i).minx < temp)
				temp = box.get(i).minx;
		return temp;
				
	}
	int miny()
	{
		int temp = box.get(0).miny;
		for(int i=0; i<box.size(); i++)
			if(box.get(i).miny < temp)
				temp = box.get(i).miny;
		return temp;
				
	}
	int maxx()
	{
		int temp = box.get(0).maxx;
		for(int i=0; i<box.size(); i++)
			if(box.get(i).maxx > temp)
				temp = box.get(i).maxx;
		return temp;
				
	}
	int maxy()
	{
		int temp = box.get(0).maxy;
		for(int i=0; i<box.size(); i++)
			if(box.get(i).maxy > temp)
				temp = box.get(i).maxy;
		return temp;
	}
}

class R3
{
	
	static ArrayList <tuple>file;
	static ArrayList <dataPage>dplist;
	static ArrayList <indexPage>iplist;
	indexPage root;
	int pointdisc;
	int rangedisc;
	float max;
	public R3()
	{
		file = new ArrayList<tuple>();
		dplist = new ArrayList<dataPage>();
		iplist = new ArrayList<indexPage>();
		root = new indexPage();
		pointdisc = 0;
		rangedisc = 0;
		max = 0;
	}
	
	void readfile()
	{
		try (BufferedReader br = new BufferedReader(new FileReader("project3dataset30K.txt")))
		{
			String sCurrentLine;
			String splitString[];	
			while ((sCurrentLine = br.readLine()) != null) {
				tuple t = new tuple();
				splitString = sCurrentLine.split(",");
				t.x = Integer.parseInt(splitString[0]);
				t.y = Integer.parseInt(splitString[1]);
				String s = UUID.randomUUID().toString();
				t.desc = s.toCharArray();
	//			System.out.println(file[i].x);
	//			System.out.println(file[i].y);
				file.add(t);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sort()
	{
		Collections.sort(file);
	}
	void bulkLoad()
	{		
		int index = 0;
		do
		{
			dataPage dp = new dataPage();
			for(int l=0; l<4 & !file.isEmpty(); l++)
				dp.addTuple(file.remove(0));
			dp.setminmax();
			dplist.add(dp);
			dp.pageid = index;
			index++;
		}while(!file.isEmpty());
		index = 0;
		
		while(index < dplist.size())
		{
			
			indexPage ip = new indexPage();
			for(int i=0; i<204 & index < dplist.size(); i++)
			{
				bound b = new bound();
				dataPage p = new dataPage(); 
				p = dplist.get(index);
				b.minx=p.minx;
				b.miny=p.miny;
				b.maxx=p.maxx;
				b.maxy=p.maxy;
				b.pointer=p.pageid;
				ip.addbound(b);
				index++;
			}
			ip.setminmax();
			iplist.add(ip);
		}
		
		for(int i=0 ; i<iplist.size(); i++)
		{
			bound b = new bound();
			indexPage p = iplist.get(i);
			b.minx=p.minx;
			b.miny=p.miny;
			b.maxx=p.maxx;
			b.maxy=p.maxy;
			b.pointer=i;
			root.addbound(b);
		}
	}
	ArrayList <tuple> pointsearch(int x, int y)
	{
		pointdisc = 0;
		ArrayList <tuple>a = new ArrayList<tuple>();
		for(int i=0; i<root.size(); i++)
		{
			bound b = new bound(); 
			b = root.getbound(i);

			if(x >= b.minx & y >= b.miny & x <= b.maxx & y <= b.maxy )
			{
				indexPage ip1= new indexPage(); 
				ip1 = iplist.get(b.pointer);
				pointdisc ++;
				if(x >= ip1.minx & y >= ip1.miny & x <= ip1.maxx & y <= ip1.maxy )
				{
					for(int j=0; j<ip1.size(); j++)
					{
						int p = ip1.getbound(j).pointer;
						dataPage temp = new dataPage(); 
						temp = dplist.get(p);
						pointdisc++;
						if(x >= temp.minx & y >= temp.miny & x >= temp.minx & y >= temp.miny)
						{
							if(temp.gettuple(0).x == x & temp.gettuple(0).y==y)
								a.add(temp.gettuple(0));
							if(temp.gettuple(1).x == x & temp.gettuple(1).y==y)
								a.add(temp.gettuple(1));
							if(temp.gettuple(2).x == x & temp.gettuple(2).y==y)
								a.add( temp.gettuple(2));
							if(temp.gettuple(3).x == x & temp.gettuple(3).y==y)
								a.add(temp.gettuple(3));
						}
					}
				}
			}
		}
		return a;
		
	}
	ArrayList <tuple> rangesearch(int x1, int y1, int x2, int y2)
	{
		rangedisc = 0;
		ArrayList <tuple>a = new ArrayList<tuple>();
		for(int i=0; i<root.size(); i++)
		{
			bound b = new bound();
			b = root.getbound(i);
			if((b.minx >= x1 & b.miny >= y1 & b.minx <= x2 & b.miny <= y2)
					| (b.maxx >= x1 & b.maxy >= y1 & b.maxx <= x2 & b.maxy <= y2)
					| (x1 >= b.minx & y1 >= b.miny & x2 <= b.maxx & y2 <= b.maxy))
			{
				indexPage ip1 = new indexPage(); 
				ip1 = iplist.get(i);
				rangedisc++;
			//System.out.println(ip1.minx() +","+ ip1.miny() +"," +ip1.maxx() +","+ ip1.maxy());
				if(( ip1.minx >= x1 & ip1.miny >= y1 & ip1.minx <= x2 & ip1.miny <= y2)
					| (ip1.maxx >= x1 & ip1.maxy >= y1 & ip1.maxx <= x2 & ip1.maxy <= y2)
					| (x1 >= ip1.minx & y1 >= ip1.miny & x2 <= ip1.maxx & y2 <= ip1.maxy))
				{
				//System.out.println("HERE");
					for(int j=0; j<ip1.size(); j++)
					{
						int p = ip1.getbound(j).pointer;
						rangedisc++;
						dataPage temp = new dataPage(); 
						temp = dplist.get(p);
						if((temp.minx >= x1 & temp.miny >= y1 & temp.minx <= x2 & temp.miny <= y2)
							| (temp.maxx >= x1 & temp.maxy >= y1 & temp.maxx <= x2 & temp.maxy <= y2)
							| (x1 >= temp.minx & y1 >= temp.miny & x2 <= temp.maxx & y2 <= temp.maxy))
						{
							if((temp.gettuple(0).x >= x1 & temp.gettuple(0).y >= y1)
								& (temp.gettuple(0).x <= x2 & temp.gettuple(0).y <= y2))
								a.add(temp.gettuple(0));
							if((temp.gettuple(1).x >= x1 & temp.gettuple(1).y >= y1)
								& (temp.gettuple(1).x <= x2 & temp.gettuple(1).y <= y2))
								a.add(temp.gettuple(1));
							if((temp.gettuple(2).x >= x1 & temp.gettuple(2).y >= y1)
								& (temp.gettuple(2).x <= x2 & temp.gettuple(2).y <= y2))
								a.add(temp.gettuple(2));
							if((temp.gettuple(3).x >= x1 & temp.gettuple(3).y >= y1)
								& (temp.gettuple(3).x <= x2 & temp.gettuple(3).y <= y2))
								a.add(temp.gettuple(3));
						}
					}
				}
			}
		}
		return a;
	}
	tuple maximize(float alpha, float beta)
	{
		float cmin = 100000;
		tuple t = new tuple();
		for(int i=0; i < root.size(); i++)
		{
			int p1 = root.getbound(i).pointer;
			indexPage ip1= new indexPage(); 
			ip1 = iplist.get(p1);
			if((ip1.minx * alpha + ip1.miny * beta) < cmin)
				cmin = ip1.minx * alpha + ip1.miny * beta;
			if(cmin > (ip1.maxx * alpha + ip1.maxx * beta))
				continue;
			for(int j=0; j<ip1.size(); j++)
			{
				bound b = new bound();
				b = ip1.getbound(j);
				int p2 = b.pointer;

				dataPage temp = new dataPage(); 
				temp = dplist.get(p2);
				for(int k=0; k <4; k++)
				{
					if((temp.gettuple(k).x * alpha + temp.gettuple(k).y * beta) > max)
					{
						t = new tuple();
						t= temp.gettuple(k);
						max = t.x * alpha + t.y * beta;
					}
				}
			}
		}
		return t;
	}
}

class db
{
	public static void main(String args[])
	{
		tuple t = new tuple();
		ArrayList <tuple>s = new ArrayList<tuple>();
		ArrayList <tuple>l = new ArrayList<tuple>();
		R3 x = new R3();
		x.readfile();
		x.sort();
		x.bulkLoad();
		Scanner reader = new Scanner(System.in);
//		x.printData();
		int flag =1;
		while(flag==1)
		{
			
			System.out.println("Enter: 1 for pointsearch, 2 for rangesearch, 3 for maximize, 4 for exit");
			int ch = reader.nextInt();
			switch(ch)
			{
			case 1:
				System.out.println("Enter the point (x,y)");
		//get user input for a
				int px=reader.nextInt();
				int py=reader.nextInt();
			
				s = x.pointsearch(px,py);
				if(t == null)
					System.out.println("Point Not found");
				else
					for(int i=0; i< s.size(); i++)
						System.out.println(s.get(i).x + ", " + s.get(i).y + ", " + 
								String.valueOf(s.get(i).desc));
					System.out.println("\nThe number of disc access " + x.pointdisc);
				break;
			case 2:
				System.out.println("Enter the range (x1,y1), (x2,y2)");
				int px1=reader.nextInt();
				int py1=reader.nextInt();
				int px2=reader.nextInt();
				int py2=reader.nextInt();
				l = x.rangesearch(px1, py1, px2, py2);
				if(l.isEmpty())
					System.out.println("Range Not found");
				else
					for(int i=0; i< l.size(); i++)
						System.out.println(l.get(i).x + "," + l.get(i).y + "," + 
								String.valueOf(l.get(i).desc));
				System.out.println("\nThe number of disc access " + x.rangedisc);
				break;
			case 3:
				System.out.println("Enter alpha and beta");
				Float p1=reader.nextFloat();
				Float p2=reader.nextFloat();
				t = x.maximize(p1, p2);
				System.out.println(t.x + ", " + t.y + ", " + String.valueOf(t.desc) + "\nMaximum value of f " + x.max);
				break;
			case 4:
				flag = 0;
				break;
			}		
		}
		reader.close();
	}
}
