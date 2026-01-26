import java.io.*;
import java.util.*;

public class ELUIM_preprocess_strategy1_strategy2 {
private final Set<Long> preSet = new HashSet<>();   // 指纹集合
    Generator3 generator;
    int max_utility;
    BufferedWriter writer=null;
    long runtime ;
    int patternCount;
    long candidatesCount;
    private static long finger64(int[] a) {
        long h = 0x345678L;
        long GOLDEN_RATIO = 0x9E3779B97F4A7C15L;

        for (int v : a) {
            long x = v & 0xFFFFFFFFL;
            h ^= x + (h << 6) + (h >> 2);
            h *= GOLDEN_RATIO;
        }
        h ^= (h >> 33);
        h *= GOLDEN_RATIO;
        h ^= (h >> 29);

        return h;
    }
    public void runAlgorithm(String input, int max_utility,String output) throws IOException {
        this.max_utility=max_utility;
        long startTime=System.currentTimeMillis();
        loadFile(input);

        //sortTrans(input);
        String delPrex=null;
//        res is MaxDisConSet
        List<int[]> res=genContain(input);
        writer = new BufferedWriter(new FileWriter(output));
        for (int i = 0; i < res.size(); i++) {
            LUM(delPrex, res.get(i));
            preSet.add(finger64(res.get(i)));
        }
        runtime=System.currentTimeMillis()-startTime;
        writer.close();
    }

    public void LUM(String delPre,int[] itemset) throws IOException {
        long key = finger64(itemset);
        if (!preSet.add(key)) {
            return; // has processed, return
        }
        Pattern pattern;
        if(delPre==null){
            pattern = generator.getPattern(itemset);
        }
        else{
            String[] delPreArr=delPre.split("&");
            int lastItem=Integer.valueOf(delPreArr[delPreArr.length-1]);
            pattern = generator.getPattern2(itemset,lastItem);
        }
        TwoListContainer twoListContainer=pattern.utilityInfo;
        if(itemset.length!=pattern.filteredItemset.length){
            itemset=pattern.filteredItemset;
            long key2 = finger64(itemset);
            if (!preSet.add(key2)) {
                return;
            }
        }
        candidatesCount++;
        // obtain the utility of the current itemset
        int uOfRoot= twoListContainer.listB.get(0);
        if (uOfRoot<=max_utility&&uOfRoot>0) {
            myprint(itemset,uOfRoot);
            patternCount++;
        }
        // no need to consider extensions of the itemset
        if (itemset.length==1){
            return;
        }

        int lbOfExD;
        if (delPre!=null){
            String[] delPreArr2=delPre.split("&");
            int lastItem=Integer.valueOf(delPreArr2[delPreArr2.length-1]);
            // obtain the lower bound for depth pruning; if it exceeds maxUtil, no extension is considered
            // compute the leaf shrinkage of the itemset, i.e., the sum of utilities of items preceding the current item
            lbOfExD=getlbOfExD(itemset,lastItem,twoListContainer);
        }else {
            // obtain the lower bound for depth pruning; if it exceeds maxUtil, no further extension is considered
            lbOfExD= twoListContainer.listB.get(1);
        }
        MemoryLogger.getInstance().checkMemory();
        // consider possible extensions
        // perform depth pruning
        if (lbOfExD<=max_utility){
            if (delPre!=null){
                // only elements greater than lastItem can be removed
                String[] delPreArr=delPre.split("&");
                int lastItem=Integer.valueOf(delPreArr[delPreArr.length-1]);
                for (int i = 0; i < itemset.length; i++) {
                    if (itemset[i]<lastItem){
                        continue;
                    }
                    // obtain the lower bound for width pruning; if it exceeds maxUtil, the itemset and its extensions are pruned
                    int lbOfExB=getlbOfExD(itemset,itemset[i],twoListContainer);;
                    if (lbOfExB<=max_utility){
                    List<Integer> deleteList = pruneItem(lbOfExB,itemset,itemset[i],twoListContainer);
                    int[] extendsion;
                    if(deleteList.size()==0){
                        extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    }else{
                        if(deleteList.contains(itemset[i])){
                            extendsion=new int[itemset.length-deleteList.size()];
                            for (int j = 0,k = 0; j < itemset.length; j++) {
                                if((!deleteList.contains(itemset[j]))){
                                    extendsion[k++]=itemset[j];
                                }
                            }
                        }else{
                            extendsion=new int[itemset.length-deleteList.size()-1];
                            for (int j = 0,k = 0; j < itemset.length; j++) {
                                if((!deleteList.contains(itemset[j]))&&(j!=i)){
                                    extendsion[k++]=itemset[j];
                                }
                            }
                        }
                    }

                        if (extendsion.length>0){
                            LUM(delPre+"&"+itemset[i],extendsion);
                        }

                    }
                }
            }else //delpre==null
            {
                for (int i = 0; i < itemset.length; i++) {
                    // obtain the lower bound for width pruning; if it exceeds maxUtil, both the current itemset and its extensions are pruned
                    int lbOfExB= twoListContainer.getLBofExD(i);
                    int[] extendsion=delOneOfItemset(itemset,i,itemset[i]);
                    if (lbOfExB<=max_utility){
                        if (extendsion.length>0){
                            LUM(String.valueOf(itemset[i]),extendsion);
                        }
                    }
                }
            }


        }
    }
    public int getlbOfExD(int[] itemset,int lastDelItem,TwoListContainer twoListContainer){
        for(int i=0;i<itemset.length;i++){
            if(itemset[i]>=lastDelItem){
                return twoListContainer.getLBofExD(i);
            }
        }
        return twoListContainer.getLBofExD(itemset.length);
    }

    private List<Integer> pruneItem(int utilOfFront, int[] itemset, int lastItem,TwoListContainer twoListContainer){
        List<Integer> deleteList=new ArrayList<>();
        if(itemset[0]>=lastItem){
            for (int i = 0; i < itemset.length; i++) {
                if(itemset[i]<lastItem){
                    continue;
                }
                int utility = twoListContainer.listA.get(i);
                if (utility>max_utility){
                    deleteList.add(itemset[i]);
                }
            }
            return deleteList;
        }
        for (int i = 0; i < itemset.length; i++) {
            if(itemset[i]<lastItem){
                continue;
            }
            int utility = twoListContainer.listA.get(i);
            int utilOfFrontAndItem=utilOfFront+utility;
            if (utilOfFrontAndItem>max_utility){
                deleteList.add(itemset[i]);
            }
        }
        return deleteList;
    }
    private void myprint(int[] itemset, int uOfRoot) throws IOException {
        StringBuffer buffer=new StringBuffer();
        for (int i = 0; i < itemset.length; i++) {
            if (i==itemset.length-1){
                // System.out.print(itemset[i]+":");
                buffer.append(itemset[i]+":");
            }else {
                // System.out.print(itemset[i]+" ");
                buffer.append(itemset[i]+" ");
            }
        }
        buffer.append(uOfRoot);
        writer.write(buffer.toString());
        writer.newLine();
        writer.flush();
    }
    private int[] delOneOfItemset(int[] itemset,int index, int item) {
        int[] res=new int[itemset.length-1];
        if (itemset[index]==item){
            //int i = 0, j = 0;
            for (int i = 0, j = 0; i < itemset.length&&j < res.length; i++) {
                if (i==index){
                    continue;
                }else {
                    res[j++]=itemset[i];
                }
            }}else {
            System.out.println("del item error");
        }
        return res;
    }

    static class IntArrayWrapper {
        final int[] data;

        IntArrayWrapper(int[] data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IntArrayWrapper)) return false;
            return Arrays.equals(data, ((IntArrayWrapper) o).data);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(data);
        }
    }

    private boolean containsAll(int[] seq1, int[] seq2) {
        if (seq1.length < seq2.length) return false;
        int i = 0, j = 0;
        while (i < seq1.length && j < seq2.length) {
            if (seq1[i] == seq2[j]) {
                j++;
            }
            i++;
        }
        return j == seq2.length;
    }



    public void printStats(List<Double> runTimelist,List<Double> memorylist,List<Long> candidateslist,List<Integer> patternlist) {

        runTimelist.add((double)runtime/1000);
        memorylist.add(MemoryLogger.getInstance().getMaxMemory());
        candidateslist.add(candidatesCount);
        patternlist.add(patternCount);

    }
    /**
     * Read the original file and store the mapping of items to utilities
     */
    public void loadFile(String path) throws IOException {
        Set<Integer> setOfItems=new TreeSet<>();
        Map<Integer,String[]> mapTidToItems = new LinkedHashMap<>();
        Map<Integer,String[]> mapTidToUtilities=new LinkedHashMap<>();
        int tidCount=0;
        String thisLine;
        BufferedReader myInput = null;
        try {
            myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))));
            // for each transaction (line) in the input file
            while ((thisLine = myInput.readLine()) != null) {
                // if the line is  a comment, is  empty or is a
                // kind of metadata
                if (thisLine.isEmpty() == true ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%'
                        || thisLine.charAt(0) == '@') {
                    continue;
                }
                tidCount++;
                String[] partions=thisLine.split(":");
                // int transactionUtility = Integer.parseInt(partions[1]);

                String[] items = partions[0].split(" ");
                int[] itemsInt=new int[items.length];
                for (int i = 0; i < items.length; i++) {
                    itemsInt[i]=Integer.valueOf(items[i]);
                    setOfItems.add(itemsInt[i]);
                }
                mapTidToItems.put(tidCount,items);
                String[] utilities = partions[2].split(" ");
                mapTidToUtilities.put(tidCount,utilities);
            }
        } catch (Exception e) {
            // catch exceptions
            e.printStackTrace();
        }finally {
            if(myInput != null){
                // close the file
                myInput.close();
            }
        }
        generator=new Generator3(setOfItems,tidCount);
        generator.setGenerator(mapTidToItems,mapTidToUtilities);
        generator.max_utility=this.max_utility;
    }

    /**
     * Read the file and generate transactions, performing deduplication and containment pruning
     */

    List<int[]> genContain(String input) throws IOException {
        Set<IntArrayWrapper> uniqueSet = new LinkedHashSet<>();

        try (BufferedReader myInput = new BufferedReader(new InputStreamReader(new FileInputStream(new File(input))))) {
            String thisLine;

            while ((thisLine = myInput.readLine()) != null) {
                if (thisLine.trim().isEmpty() ||
                        thisLine.charAt(0) == '#' || thisLine.charAt(0) == '%' ||
                        thisLine.charAt(0) == '@') {
                    continue;
                }

                String[] parts = thisLine.trim().split(":");
                if (parts.length < 3) {
                    System.err.println("⚠️ [Warning] Invalid line: " + thisLine);
                    continue;
                }

                String[] items = parts[0].trim().split("\\s+");
                String[] utilities = parts[2].trim().split("\\s+");

                int len = Math.min(items.length, utilities.length);
                int[] validItems = new int[len]; // 预分配数组
                int validCount = 0;

                // Strategy 1 filter: utility <= max_utility
                for (int i = 0; i < len; i++) {
                    if (items[i].isEmpty() || utilities[i].isEmpty()) continue;
                    try {
                        int item = Integer.parseInt(items[i]);
                        int util = Integer.parseInt(utilities[i]);

                        if (util <= max_utility) {
                            validItems[validCount++] = item;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ [Warning] Number format error: " + thisLine);
                    }
                }

                if (validCount > 0) {
                    // Create a compact array and sort it
                    int[] transaction = Arrays.copyOf(validItems, validCount);
                    Arrays.sort(transaction);
                    uniqueSet.add(new IntArrayWrapper(transaction));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert to a list for containment pruning
        List<int[]> uniqueList = new ArrayList<>();
        for (IntArrayWrapper wrapper : uniqueSet) {
            uniqueList.add(wrapper.data);
        }

        // Containment pruning - directly compare arrays
        List<int[]> result = new ArrayList<>();
        while (!uniqueList.isEmpty()) {
            int[] last = uniqueList.remove(uniqueList.size() - 1);

            // Safely remove using an iterator
            Iterator<int[]> iterator = uniqueList.iterator();
            while (iterator.hasNext()) {
                int[] candidate = iterator.next();
                if (containsAll(last, candidate)) {
                    iterator.remove();
                }
            }

            result.add(last);
        }

        return result;
    }


}
