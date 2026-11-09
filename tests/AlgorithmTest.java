import java.util.*;
public class AlgorithmTest {
    static final Random R = new Random(250304);
    static void check(boolean ok) { if (!ok) throw new AssertionError(); }
    static String dna(int n) { StringBuilder s = new StringBuilder(); while(n-- > 0) s.append("ACGT".charAt(R.nextInt(4))); return s.toString(); }
    static int edit(String a, String b, int i, int j) {
        if (i == a.length()) return b.length()-j;
        if (j == b.length()) return a.length()-i;
        return Math.min(edit(a,b,i+1,j+1)+(a.charAt(i)==b.charAt(j)?0:1), 1+Math.min(edit(a,b,i+1,j),edit(a,b,i,j+1)));
    }
    static boolean equal(String a,int i,String b,int j,int k) { for(int t=0;t<k;t++) if(a.charAt(i+t)!=b.charAt(j+t)) return false; return true; }
    public static void main(String[] args) {
        for(int trial=0;trial<150;trial++) {
            String a=dna(R.nextInt(6)), b=dna(R.nextInt(6));
            check(Strains.editDistance(a,b)==edit(a,b,0,0));
            int best=0;
            for(int mask=0;mask<(1<<a.length());mask++) {
                int pos=0, matched=0;
                for(int i=0;i<a.length();i++) if((mask&(1<<i))!=0) {
                    while(pos<b.length() && b.charAt(pos)!=a.charAt(i)) pos++;
                    if(pos<b.length()) { pos++; matched++; } else { matched=-1; break; }
                }
                best=Math.max(best,matched);
            }
            check(Strains.lcsLength(a,b)==best);
            String text=dna(30), p=dna(1+R.nextInt(6));
            List<Integer> hits=new ArrayList<>();
            for(int i=0;i+p.length()<=text.length();i++) if(equal(text,i,p,0,p.length())) hits.add(i);
            check(Arrays.equals(Marker.findAll(text,p),hits.stream().mapToInt(x->x).toArray()));
            int[] pi=Marker.prefixFunction(p);
            for(int i=0;i<p.length();i++) { int border=0; for(int k=1;k<=i;k++) if(equal(p,0,p,i+1-k,k)) border=k; check(pi[i]==border); }
            a=dna(30); b=dna(30); int k=1+R.nextInt(12), words=0;
            for(int i=0;i+k<=a.length();i++) for(int j=0;j+k<=b.length();j++) if(equal(a,i,b,j,k)) { words++; break; }
            check(Marker.sharedWords(a,b,k)==words);
            int n=1+R.nextInt(10); int[] parent=new int[n], cost=new int[n],degree=new int[n];
            for(int i=1;i<n;i++) { parent[i]=R.nextInt(i); degree[i]++; degree[parent[i]]++; }
            int[][] nb=new int[n][]; for(int i=0;i<n;i++) { nb[i]=new int[degree[i]]; cost[i]=R.nextInt(20); }
            Arrays.fill(degree,0);
            for(int i=1;i<n;i++) { nb[i][degree[i]++]=parent[i];nb[parent[i]][degree[parent[i]]++]=i; }
            long opt=Long.MAX_VALUE;
            for(int mask=0;mask<(1<<n);mask++) {
                boolean valid=true;long total=0;
                for(int i=0;i<n;i++) if((mask&(1<<i))!=0) total+=cost[i];
                for(int i=1;i<n;i++) if((mask&(1<<i))==0 && (mask&(1<<parent[i]))==0) valid=false;
                if(valid) opt=Math.min(opt,total);
            }
            boolean[] take=Outbreak.cheapestTests(n,nb,cost);long total=0;
            for(int i=0;i<n;i++) if(take[i]) total+=cost[i];
            for(int i=1;i<n;i++) check(take[i]||take[parent[i]]);
            check(total==opt);
            int diameter=0;
            for(int start=0;start<n;start++) {
                int[] dist=new int[n];Arrays.fill(dist,-1);dist[start]=0;ArrayDeque<Integer> q=new ArrayDeque<>();q.add(start);
                while(!q.isEmpty()) {int v=q.remove();for(int u:nb[v]) if(dist[u]<0) {dist[u]=dist[v]+1;diameter=Math.max(diameter,dist[u]);q.add(u);}}
            }
            check(Outbreak.longestChain(n,nb)==diameter);
        }
        check(Arrays.equals(Marker.findAll("AAAAA","AAA"),new int[]{0,1,2}));
        check(Marker.sharedWords("AAAAA","AAAA",3)==3);
        System.out.println("150 randomized trials passed: exhaustive edit/LCS/tree-cover oracles, naive matching and all-pairs tree distances.");
    }
}
