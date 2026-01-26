import java.util.*;

public class Generator3 {
    int max_utility;
    int itemsCount_row;
    int tidCount_column;
   // boolean[][] bitOfItem;
    BitSet[] bitOfItem;
   // int[][] uOfItem;

    Map<Integer,Map<Integer,Integer>> uMapOfItemToTidU=new HashMap<>();
    //<item,<tid,utility>>
    //Map<Integer,Map<Integer,Integer>> uMapOfTidToItemU=new HashMap<>();
    Map<Integer,Integer> mapItemtoIndex=new HashMap<>();
    //items 为按照字典序或其他顺序排列的item总数，tidcount为事务总数
    public Generator3(Set<Integer> items, int tidCount) {
        itemsCount_row = items.size();
        this.tidCount_column = tidCount;
        bitOfItem = new BitSet[itemsCount_row];
        int i = 0;
        while (i < itemsCount_row) {
            bitOfItem[i] = new BitSet(tidCount_column);
            i++;
        }
        //uOfItem=new int[itemsCount_row][tidCount_column];
        int index=0;
        for (Integer item:items) {
            mapItemtoIndex.put(item,index++);
        }
//        System.out.println("输出mapItemtoIndex的类型");
//        System.out.println(mapItemtoIndex.getClass().toString());
//        for (Map.Entry<Integer, Integer> entry : mapItemtoIndex.entrySet()) {
//            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//        }

    }
    //items 的顺序要按照字典序或其他固定顺序
    public void setGeneratorOne(int tid,String[] items, String[] utils){
//        Map<Integer,Integer> mapItemToU=uMapOfTidToItemU.get(tid);
//        if (mapItemToU==null){
//            mapItemToU=new HashMap<>();
//            uMapOfTidToItemU.put(tid,mapItemToU);
//        }
        for (int k = 0; k < items.length; k++) {
            int index=mapItemtoIndex.get(Integer.valueOf(items[k]));
            bitOfItem[index].set(tid-1);
           // bitOfItem[index][tid-1]=true;
           // uOfItem[index][tid-1]=Integer.valueOf(utils[k]);
//            uMapOfItemToTidU对应某一项所在的序列和效用
            Map<Integer,Integer> mapTidToU=uMapOfItemToTidU.get(index);
            if (mapTidToU==null){
                mapTidToU=new HashMap<>();
                uMapOfItemToTidU.put(index,mapTidToU);
            }
            mapTidToU.put(tid-1,Integer.valueOf(utils[k]));
//            System.out.println("输出mapTidToU:"+mapTidToU.getClass().toString());
//            System.out.println("输出index:"+index);
//            for(Map.Entry<Integer, Integer> entry : mapTidToU.entrySet()){
//                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//            }
           // mapItemToU.put(index,Integer.valueOf(utils[k]));
        }
    }
//    public Pattern getPattern(int[] itemset){
//        List<Integer> listSumAndMin=new ArrayList<>();
//        List<Integer> listItemToUB=new ArrayList<>();
//        TwoListContainer twoListContainer = new TwoListContainer(listItemToUB,listSumAndMin);
//        List<Integer> deleteList=new ArrayList<>();
//        int sum=0;
//        int min=Integer.MAX_VALUE;
//        //找到同时包含itemset里面所有item的tid,求每一个item在这些tid里的utility
//        //所有位设置为1：111111111
//        BitSet resBit=new BitSet(tidCount_column);
//        resBit.set(0,tidCount_column);
//        for (int i = 0; i < itemset.length; i++) {
//            //获取item的索引
//            Integer index = mapItemtoIndex.get(itemset[i]);
////            System.out.println("itemset[i]:"+itemset[i]);
////            System.out.println("index:"+index);
//            //generator中不含某个item(通常前部分有，后部分没有),这种情况LB通常是0，因为没有tid同时含有这个itemset
//            if (index == null) {
//                //没有这个item,直接返回
//                listItemToUB.add(0);
//                listSumAndMin.add(Integer.MAX_VALUE);
//                listSumAndMin.add(0);
//                return new Pattern(twoListContainer,itemset);
//            }
////            bitOfitem是某个项所在的序列号
////            System.out.println("输出bitOfItem:"+bitOfItem[index]);
////            System.out.println("输出resBit1:");
////            System.out.println(resBit);
//            //每个tid
////          得到该模式所在的序列号
//            resBit.and(bitOfItem[index]);
//        }
//        for (int j = 0; j < itemset.length; j++) {
//            int item = itemset[j];
//            int index = mapItemtoIndex.get(item);
//            Integer currentUtility = 0;
//
//            // 遍历所有包含该模式的tid
//            for (int tid = resBit.nextSetBit(0); tid <= tidCount_column-1 && tid >= 0; tid = resBit.nextSetBit(tid+1)) {
//                // 获取当前项在当前tid中的效用值
//                Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
//                if (utilityInTid != null) {
//                    currentUtility += utilityInTid;
//                }
//            }
//            if(currentUtility>=max_utility){
//                deleteList.add(j);
//            }else{
//                min = Math.min(min, currentUtility);
//                sum+=currentUtility;
//                // 将计算好的效用值存入map
//                listItemToUB.add(currentUtility);
//            }
//        }
//        listSumAndMin.add(sum);
//        listSumAndMin.add(min);
//        if(deleteList.size()>0){
//            int[] newItemset=new int[itemset.length-deleteList.size()];
//            for(int i=0,j=0;i<itemset.length;i++){
//                if(!deleteList.contains(i)){
//                    newItemset[j++]=itemset[i];
//                }
//            }
//            listSumAndMin=new ArrayList<>();
//            listItemToUB=new ArrayList<>();
//            twoListContainer = new TwoListContainer(listItemToUB,listSumAndMin);
//            sum=0;
//            min=Integer.MAX_VALUE;
//            resBit.set(0,tidCount_column);
////            if(newItemset.length==0){
////                System.out.println(1111);
////            }
//            for (int i = 0; i < newItemset.length; i++) {
//                //获取item的索引
//                Integer index = mapItemtoIndex.get(newItemset[i]);
////            System.out.println("itemset[i]:"+itemset[i]);
////            System.out.println("index:"+index);
//                //generator中不含某个item(通常前部分有，后部分没有),这种情况LB通常是0，因为没有tid同时含有这个itemset
//                if (index == null) {
//                    //没有这个item,直接返回
//                    listItemToUB.add(0);
//                    listSumAndMin.add(Integer.MAX_VALUE);
//                    listSumAndMin.add(0);
//                    return new Pattern(twoListContainer,newItemset);
//                }
////            bitOfitem是某个项所在的序列号
////            System.out.println("输出bitOfItem:"+bitOfItem[index]);
////            System.out.println("输出resBit1:");
////            System.out.println(resBit);
//                //每个tid
////          得到该模式所在的序列号
//                resBit.and(bitOfItem[index]);
//            }
//            for (int j = 0; j < newItemset.length; j++) {
//                int item = newItemset[j];
//                int index = mapItemtoIndex.get(item);
//                Integer currentUtility = 0;
//                // 遍历所有包含该模式的tid
//                for (int tid = resBit.nextSetBit(0); tid <= tidCount_column-1 && tid >= 0; tid = resBit.nextSetBit(tid+1)) {
//                    // 获取当前项在当前tid中的效用值
//                    Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
//                    if (utilityInTid != null) {
//                        currentUtility += utilityInTid;
//                    }
//                }
//                    min = Math.min(min, currentUtility);
//                    sum+=currentUtility;
//                    // 将计算好的效用值存入map
//                    listItemToUB.add(currentUtility);
//            }
//            listSumAndMin.add(sum);
//            listSumAndMin.add(min);
//            return new Pattern(twoListContainer,newItemset);
//        }
//        else{
//            return new Pattern(twoListContainer,itemset);
//        }
//
//        //map<item,totalUtility>
////        twoMapContainer=new TwoMapContainer(mapItemToUB,mapSumAndMin);
////        return twoMapContainer;
//    }
public Pattern getPattern(int[] itemset) {
    // --- 初始化 ---
    int len = itemset.length;
    int sum = 0;
    int min = Integer.MAX_VALUE;

    List<Integer> listSumAndMin = new ArrayList<>(2);
    List<Integer> listItemToUB = new ArrayList<>(len);
    TwoListContainer twoListContainer = new TwoListContainer(listItemToUB, listSumAndMin);

    // BitSet 初始化：假设全部包含
    BitSet resBit = new BitSet(tidCount_column);
    resBit.set(0, tidCount_column);

    // --- 求 itemset 的交集 ---
    for (int item : itemset) {
        Integer index = mapItemtoIndex.get(item);
        if (index == null) {
            listItemToUB.add(0);
            listSumAndMin.add(Integer.MAX_VALUE);
            listSumAndMin.add(0);
            return new Pattern(twoListContainer, itemset);
        }
        resBit.and(bitOfItem[index]);
    }

    // --- 计算每个 item 的 utility 并决定是否删除 ---
    boolean[] isDeleted = new boolean[len];
    int deletedCount = 0;

    for (int j = 0; j < len; j++) {
        int item = itemset[j];
        int index = mapItemtoIndex.get(item);
        int currentUtility = 0;
        for (int tid = resBit.nextSetBit(0); tid >= 0 && tid < tidCount_column; tid = resBit.nextSetBit(tid + 1)) {
            Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
            if (utilityInTid != null) currentUtility += utilityInTid;
        }
        if (currentUtility > max_utility) {
            isDeleted[j] = true;
            deletedCount++;
        } else {
            if(deletedCount==0){
                min = Math.min(min, currentUtility);
                sum += currentUtility;
                listItemToUB.add(currentUtility);
            }
        }
    }
    if (deletedCount == 0) {
        // --- 如果没有删除项，直接返回 ---
        listSumAndMin.add(sum);
        listSumAndMin.add(min);
        return new Pattern(twoListContainer, itemset);
    }
    // --- 构建 newItemset ---
    int[] newItemset = new int[len - deletedCount];
    for (int i = 0, j = 0; i < len; i++) {
        if (!isDeleted[i]) newItemset[j++] = itemset[i];
    }

    // --- 重新计算 utility（精准） ---
//    listSumAndMin = new ArrayList<>(2);
    listItemToUB = new ArrayList<>(newItemset.length);
    twoListContainer = new TwoListContainer(listItemToUB, listSumAndMin);

    sum = 0;
    min = Integer.MAX_VALUE;
    resBit.set(0, tidCount_column);

    for (int item : newItemset) {
        Integer index = mapItemtoIndex.get(item);
        if (index == null) {
            listItemToUB.add(0);
            listSumAndMin.add(Integer.MAX_VALUE);
            listSumAndMin.add(0);
            return new Pattern(twoListContainer, newItemset);
        }
        resBit.and(bitOfItem[index]);
    }
    for (int item : newItemset) {
        int index = mapItemtoIndex.get(item);
        int currentUtility = 0;
        for (int tid = resBit.nextSetBit(0); tid >= 0 && tid < tidCount_column; tid = resBit.nextSetBit(tid + 1)) {
            Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
            if (utilityInTid != null) currentUtility += utilityInTid;
        }
        min = Math.min(min, currentUtility);
        sum += currentUtility;
        listItemToUB.add(currentUtility);
    }
    listSumAndMin.add(sum);
    listSumAndMin.add(min);
    return new Pattern(twoListContainer, newItemset);
}


    public Pattern getPattern2(int[] itemset,int lastItem) {
        // --- 初始化 ---
        int len = itemset.length;
        int sum = 0;
        int min = Integer.MAX_VALUE;

        List<Integer> listSumAndMin = new ArrayList<>(2);
        List<Integer> listItemToUB = new ArrayList<>(len);
        TwoListContainer twoListContainer = new TwoListContainer(listItemToUB, listSumAndMin);

        // BitSet 初始化：假设全部包含
        BitSet resBit = new BitSet(tidCount_column);
        resBit.set(0, tidCount_column);

        // --- 求 itemset 的交集 ---
        for (int item : itemset) {
            Integer index = mapItemtoIndex.get(item);
            if (index == null) {
                listItemToUB.add(0);
                listSumAndMin.add(Integer.MAX_VALUE);
                listSumAndMin.add(0);
                return new Pattern(twoListContainer, itemset);
            }
            resBit.and(bitOfItem[index]);
        }

        // --- 计算每个 item 的 utility 并决定是否删除 ---
        boolean[] isDeleted = new boolean[len];
        int deletedCount = 0;

        for (int j = 0; j < len; j++) {
            int item = itemset[j];
            int index = mapItemtoIndex.get(item);
            int currentUtility = 0;
            for (int tid = resBit.nextSetBit(0); tid >= 0 && tid < tidCount_column; tid = resBit.nextSetBit(tid + 1)) {
                Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
                if (utilityInTid != null) currentUtility += utilityInTid;
            }
            if(deletedCount==0){
                min = Math.min(min, currentUtility);
                sum += currentUtility;
                listItemToUB.add(currentUtility);
            }
            if(item>lastItem){
                if (currentUtility > max_utility) {
                    isDeleted[j] = true;
                    deletedCount++;
                }
            }
//            if (currentUtility >= max_utility) {
//                isDeleted[j] = true;
//                deletedCount++;
//            } else {
//                if(deletedCount==0){
//                    min = Math.min(min, currentUtility);
//                    sum += currentUtility;
//                    listItemToUB.add(currentUtility);
//                }
//            }
        }
        if (deletedCount == 0) {
            // --- 如果没有删除项，直接返回 ---
            listSumAndMin.add(sum);
            listSumAndMin.add(min);
            return new Pattern(twoListContainer, itemset);
        }
        // --- 构建 newItemset ---
        int[] newItemset = new int[len - deletedCount];
        for (int i = 0, j = 0; i < len; i++) {
            if (!isDeleted[i]) newItemset[j++] = itemset[i];
        }

        // --- 重新计算 utility（精准） ---
//    listSumAndMin = new ArrayList<>(2);
        listItemToUB = new ArrayList<>(newItemset.length);
        twoListContainer = new TwoListContainer(listItemToUB, listSumAndMin);

        sum = 0;
        min = Integer.MAX_VALUE;
        resBit.set(0, tidCount_column);

        for (int item : newItemset) {
            Integer index = mapItemtoIndex.get(item);
            if (index == null) {
                listItemToUB.add(0);
                listSumAndMin.add(Integer.MAX_VALUE);
                listSumAndMin.add(0);
                return new Pattern(twoListContainer, newItemset);
            }
            resBit.and(bitOfItem[index]);
        }
        for (int item : newItemset) {
            int index = mapItemtoIndex.get(item);
            int currentUtility = 0;
            for (int tid = resBit.nextSetBit(0); tid >= 0 && tid < tidCount_column; tid = resBit.nextSetBit(tid + 1)) {
                Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
                if (utilityInTid != null) currentUtility += utilityInTid;
            }
            min = Math.min(min, currentUtility);
            sum += currentUtility;
            listItemToUB.add(currentUtility);
        }
        listSumAndMin.add(sum);
        listSumAndMin.add(min);
        return new Pattern(twoListContainer, newItemset);
    }
    public Pattern getPattern3(int[] itemset) {
        /* 1. 事务交集 */
        BitSet resBit = new BitSet(tidCount_column);
        resBit.set(0, tidCount_column);
        for (int item : itemset) {
            Integer idx = mapItemtoIndex.get(item);
            if (idx == null)                       // 商品不存在
                return newEmptyPattern(itemset);
            resBit.and(bitOfItem[idx]);
        }

        /* 2. 算每件效用 + 累加 sum / min */
        int[] utils = new int[itemset.length];
        int sum = 0, min = Integer.MAX_VALUE;
        for (int j = 0; j < itemset.length; j++) {
            int idx  = mapItemtoIndex.get(itemset[j]);
            int util = 0;
            for (int tid = resBit.nextSetBit(0); tid >= 0; tid = resBit.nextSetBit(tid + 1)) {
                Integer u = uMapOfItemToTidU.get(idx).get(tid);
                if (u != null) util += u;
            }
            utils[j] = util;
            sum += util;
            min = Math.min(min, util);
        }

        /* 3. 直接返回，无删除、无第二轮 */
        return new Pattern(
                new TwoListContainer(
                        java.util.Arrays.stream(utils).boxed().collect(java.util.stream.Collectors.toList()),
                        java.util.Arrays.asList(sum, min)),
                itemset);
    }

    /* 工具方法：商品不存在时的空结果 */
    private Pattern newEmptyPattern(int[] itemset) {
        List<Integer> zero = java.util.Collections.nCopies(itemset.length, 0);
        return new Pattern(new TwoListContainer(zero, java.util.Arrays.asList(0, Integer.MAX_VALUE)), itemset);
    }    public Pattern getPattern4(int[] itemset, int lastItem) {
        int len = itemset.length;
        int sum = 0;
        int min = Integer.MAX_VALUE;

        List<Integer> listItemToUB = new ArrayList<>(len);
        BitSet resBit = new BitSet(tidCount_column);
        resBit.set(0, tidCount_column);

        /* 1. 求事务交集 */
        for (int item : itemset) {
            Integer idx = mapItemtoIndex.get(item);
            if (idx == null) {          // 商品不存在
                listItemToUB.clear();
                listItemToUB.addAll(Collections.nCopies(len, 0));
                return new Pattern(
                        new TwoListContainer(listItemToUB,
                                Arrays.asList(0, Integer.MAX_VALUE)),
                        itemset);
            }
            resBit.and(bitOfItem[idx]);
        }

        /* 2. 算每件商品效用，同时累加 sum / min */
        for (int j = 0; j < len; j++) {
            int idx  = mapItemtoIndex.get(itemset[j]);
            int util = 0;
            for (int tid = resBit.nextSetBit(0); tid >= 0; tid = resBit.nextSetBit(tid + 1)) {
                Integer u = uMapOfItemToTidU.get(idx).get(tid);
                if (u != null) util += u;
            }
            min = Math.min(min, util);
            sum += util;
            listItemToUB.add(util);
        }

        /* 3. 直接封装返回，不再做第二轮 */
        List<Integer> listSumAndMin = Arrays.asList(sum, min);
        return new Pattern(new TwoListContainer(listItemToUB, listSumAndMin), itemset);
    }


//public Pattern getPattern(int[] itemset) {
//    // 参数校验
//    if(itemset == null || itemset.length == 0) {
//        return new Pattern(new TwoListContainer(new ArrayList<>(), new ArrayList<>()), itemset);
//    }
//    // 初始化数据结构
//    TwoListContainer twoListContainer = new TwoListContainer(
//            new ArrayList<>(itemset.length),
//            new ArrayList<>(2)
//    );
//
//    // 1. 计算交集TID
//    BitSet resBit = new BitSet(tidCount_column);
//    resBit.set(0, tidCount_column);
//    for (int item : itemset) {
//        Integer index = mapItemtoIndex.get(item);
//        if (index == null) {
//            twoListContainer.listA.add(0);
//            twoListContainer.listB.add(Integer.MAX_VALUE);
//            twoListContainer.listB.add(0);
//            return new Pattern(twoListContainer, itemset);
//        }
//        resBit.and(bitOfItem[index]);
//    }
//    // 2. 预计算活跃TID列表
//    int[] activeTids = new int[resBit.cardinality()];
//    int pos = 0;
//    for (int tid = resBit.nextSetBit(0); tid >= 0; tid = resBit.nextSetBit(tid+1)) {
//        activeTids[pos++] = tid;
//    }
//    // 3. 第一轮计算和过滤
//    boolean[] isDeleted = new boolean[itemset.length];
//    int deletedCount = 0;
//    int sum = 0;
//    int min = Integer.MAX_VALUE;
//
//    for (int j = 0; j < itemset.length; j++) {
//        int index = mapItemtoIndex.get(itemset[j]);
//        int currentUtility = 0;
//
//        // 使用预计算的activeTids加速计算
//        Map<Integer, Integer> uMap = uMapOfItemToTidU.get(index);
//        for (int tid : activeTids) {
//            Integer util = uMap.get(tid);
//            if (util != null) currentUtility += util;
//        }
//
//        if (currentUtility >= max_utility) {
//            isDeleted[j] = true;
//            deletedCount++;
//        } else if (deletedCount == 0) { // 只有未删除时才计算
//            min = Math.min(min, currentUtility);
//            sum += currentUtility;
//            twoListContainer.listA.add(currentUtility);
//        }
//    }
//    // 无删除直接返回
//    if (deletedCount == 0) {
//        twoListContainer.listB.add(sum);
//        twoListContainer.listB.add(min);
//        return new Pattern(twoListContainer, itemset);
//    }
//    // 4. 构建新项集
//    int[] newItemset = new int[itemset.length - deletedCount];
//    for (int i = 0, j = 0; i < itemset.length; i++) {
//        if (!isDeleted[i]) newItemset[j++] = itemset[i];
//    }
//    // 5. 精确重新计算
//    twoListContainer = new TwoListContainer(
//            new ArrayList<>(newItemset.length),
//            new ArrayList<>(2)
//    );
//    // 重新计算交集TID
//    resBit.set(0, tidCount_column);
//    for (int item : newItemset) {
//        resBit.and(bitOfItem[mapItemtoIndex.get(item)]);
//    }
//    // 更新activeTids
//    pos = 0;
//    for (int tid = resBit.nextSetBit(0); tid >= 0; tid = resBit.nextSetBit(tid+1)) {
//        if(pos >= activeTids.length) {
//            activeTids = Arrays.copyOf(activeTids, activeTids.length * 2);
//        }
//        activeTids[pos++] = tid;
//    }
//    // 最终计算
//    sum = 0;
//    min = Integer.MAX_VALUE;
//    for (int item : newItemset) {
//        int index = mapItemtoIndex.get(item);
//        int currentUtility = 0;
//        Map<Integer, Integer> uMap = uMapOfItemToTidU.get(index);
//        for (int i = 0; i < pos; i++) {
//            Integer util = uMap.get(activeTids[i]);
//            if (util != null) currentUtility += util;
//        }
//        min = Math.min(min, currentUtility);
//        sum += currentUtility;
//        twoListContainer.listA.add(currentUtility);
//    }
//    twoListContainer.listB.add(sum);
//    twoListContainer.listB.add(min);
//    return new Pattern(twoListContainer, newItemset);
//}
//    // 初始化 BitSet
//    BitSet resBit = new BitSet(tidCount_column);
//    resBit.set(0, tidCount_column);


    public TwoListContainer getTwoMapContainer2(int[] itemset){
        List<Integer> listSumAndMin=new ArrayList<>();
        List<Integer> listItemToUB=new ArrayList<>();
        TwoListContainer twoListContainer = new TwoListContainer(listItemToUB,listSumAndMin);
        boolean[] deleteList=new boolean[itemset.length];
        int deletedCount = 0;
        int sum=0;
        int min=Integer.MAX_VALUE;
        //找到同时包含itemset里面所有item的tid,求每一个item在这些tid里的utility
        //所有位设置为1：111111111
        BitSet resBit=new BitSet(tidCount_column);
        resBit.set(0,tidCount_column);
        for (int i = 0; i < itemset.length; i++) {
            //获取item的索引
            Integer index = mapItemtoIndex.get(itemset[i]);
//            System.out.println("itemset[i]:"+itemset[i]);
//            System.out.println("index:"+index);
            //generator中不含某个item(通常前部分有，后部分没有),这种情况LB通常是0，因为没有tid同时含有这个itemset
            if (index == null) {
                //没有这个item,直接返回
                listItemToUB.add(0);
                listSumAndMin.add(Integer.MAX_VALUE);
                listSumAndMin.add(0);
                return twoListContainer;
            }
//            bitOfitem是某个项所在的序列号
//            System.out.println("输出bitOfItem:"+bitOfItem[index]);
//            System.out.println("输出resBit1:");
//            System.out.println(resBit);
            //每个tid
//          得到该模式所在的序列号
            resBit.and(bitOfItem[index]);
        }
        for (int j = 0; j < itemset.length; j++) {
            int item = itemset[j];
            int index = mapItemtoIndex.get(item);
            Integer currentUtility = 0;

            // 遍历所有包含该模式的tid
            for (int tid = resBit.nextSetBit(0); tid <= tidCount_column-1 && tid >= 0; tid = resBit.nextSetBit(tid+1)) {
                // 获取当前项在当前tid中的效用值
                Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
                if (utilityInTid != null) {
                    currentUtility += utilityInTid;
                }
            }
            if(currentUtility>=max_utility){
                deleteList[j]=true;
                deletedCount++;
            }else{
                min = Math.min(min, currentUtility);
                sum+=currentUtility;
                // 将计算好的效用值存入map
                listItemToUB.add(currentUtility);
            }
        }
        listSumAndMin.add(sum);
        listSumAndMin.add(min);
        if(deletedCount>0){
            int[] newItemset=new int[itemset.length-deletedCount];
            for(int i=0,j=0;i<itemset.length;i++){
                if(!deleteList[i]){
                    newItemset[j++]=itemset[i];
                }
            }
            itemset=newItemset;
            listSumAndMin=new ArrayList<>();
            listItemToUB=new ArrayList<>();
            twoListContainer = new TwoListContainer(listItemToUB,listSumAndMin);
            sum=0;
            min=Integer.MAX_VALUE;
            resBit.set(0,tidCount_column);
//            if(newItemset.length==0){
//                System.out.println(1111);
//            }
            for (int i = 0; i < newItemset.length; i++) {
                //获取item的索引
                Integer index = mapItemtoIndex.get(newItemset[i]);
//            System.out.println("itemset[i]:"+itemset[i]);
//            System.out.println("index:"+index);
                //generator中不含某个item(通常前部分有，后部分没有),这种情况LB通常是0，因为没有tid同时含有这个itemset
                if (index == null) {
                    //没有这个item,直接返回
                    listItemToUB.add(0);
                    listSumAndMin.add(Integer.MAX_VALUE);
                    listSumAndMin.add(0);
                    return twoListContainer;
                }
//            bitOfitem是某个项所在的序列号
//            System.out.println("输出bitOfItem:"+bitOfItem[index]);
//            System.out.println("输出resBit1:");
//            System.out.println(resBit);
                //每个tid
//          得到该模式所在的序列号
                resBit.and(bitOfItem[index]);
            }
            for (int j = 0; j < itemset.length; j++) {
                int item = itemset[j];
                int index = mapItemtoIndex.get(item);
                Integer currentUtility = 0;
                // 遍历所有包含该模式的tid
                for (int tid = resBit.nextSetBit(0); tid <= tidCount_column-1 && tid >= 0; tid = resBit.nextSetBit(tid+1)) {
                    // 获取当前项在当前tid中的效用值
                    Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
                    if (utilityInTid != null) {
                        currentUtility += utilityInTid;
                    }
                }
                min = Math.min(min, currentUtility);
                sum+=currentUtility;
                // 将计算好的效用值存入map
                listItemToUB.add(currentUtility);
            }
            listSumAndMin.add(sum);
            listSumAndMin.add(min);
            return twoListContainer;
        }
        else{
            return twoListContainer;
        }

        //map<item,totalUtility>
//        twoMapContainer=new TwoMapContainer(mapItemToUB,mapSumAndMin);
//        return twoMapContainer;
    }
    public TwoListContainer getTwoMapContainer(int[] itemset){
        List<Integer> listSumAndMin=new ArrayList<>();
        List<Integer> listItemToUB=new ArrayList<>();
        TwoListContainer twoListContainer = new TwoListContainer(listItemToUB,listSumAndMin);
        int sum=0;
        int min=Integer.MAX_VALUE;
        //找到同时包含itemset里面所有item的tid,求每一个item在这些tid里的utility
        //所有位设置为1：111111111
        BitSet resBit=new BitSet(tidCount_column);
        resBit.set(0,tidCount_column);
        for (int i = 0; i < itemset.length; i++) {
            //获取item的索引
            Integer index = mapItemtoIndex.get(itemset[i]);
//            System.out.println("itemset[i]:"+itemset[i]);
//            System.out.println("index:"+index);
            //generator中不含某个item(通常前部分有，后部分没有),这种情况LB通常是0，因为没有tid同时含有这个itemset
            if (index == null) {
                //没有这个item,直接返回
                listItemToUB.add(0);
                listSumAndMin.add(Integer.MAX_VALUE);
                listSumAndMin.add(0);
                return twoListContainer;
            }
//            bitOfitem是某个项所在的序列号
//            System.out.println("输出bitOfItem:"+bitOfItem[index]);
//            System.out.println("输出resBit1:");
//            System.out.println(resBit);
            //每个tid
//          得到该模式所在的序列号
            resBit.and(bitOfItem[index]);
        }
        for (int j = 0; j < itemset.length; j++) {
            int item = itemset[j];
            int index = mapItemtoIndex.get(item);
            Integer currentUtility = 0;

            // 遍历所有包含该模式的tid
            for (int tid = resBit.nextSetBit(0); tid <= tidCount_column-1 && tid >= 0; tid = resBit.nextSetBit(tid+1)) {
                // 获取当前项在当前tid中的效用值
                Integer utilityInTid = uMapOfItemToTidU.get(index).get(tid);
                if (utilityInTid != null) {
                    currentUtility += utilityInTid;
                }
            }
                min = Math.min(min, currentUtility);
                sum+=currentUtility;
                // 将计算好的效用值存入map
                listItemToUB.add(currentUtility);

        }
        listSumAndMin.add(sum);
        listSumAndMin.add(min);
        return twoListContainer;

        //map<item,totalUtility>
//        twoMapContainer=new TwoMapContainer(mapItemToUB,mapSumAndMin);
//        return twoMapContainer;
    }
    public ULB getUBofItemset(int[] itemset){
//        System.out.print("输出itemset:");
//        for(int i=0;i<itemset.length;i++)
//        {
//            System.out.print(itemset[i]);
//        }
//        System.out.println(" ");
        ULB res=new ULB();
        res.setItemset(itemset);
        Map<Integer,Integer> mapItemToUB=new LinkedHashMap<>();
        res.setMapItemToLB(mapItemToUB);
        //所有位设置为1：111111111
        BitSet resBit=new BitSet(tidCount_column);
        resBit.set(0,tidCount_column);
//        boolean[] bitTidsOfItemset=new boolean[tidCount_column];
//        for (int i = 0; i < bitTidsOfItemset.length; i++) {
//            bitTidsOfItemset[i]=true;
//        }
        //每个 item
        for (int i = 0; i < itemset.length; i++) {
            //获取item的索引
            Integer index=mapItemtoIndex.get(itemset[i]);
//            System.out.println("itemset[i]:"+itemset[i]);
//            System.out.println("index:"+index);
            //generator中不含某个item(通常前部分有，后部分没有),这种情况LB通常是0，因为没有tid同时含有这个itemset
            if (index==null){
                return res;
            }
//            bitOfitem是某个项所在的序列号
//            System.out.println("输出bitOfItem:"+bitOfItem[index]);
//            System.out.println("输出resBit1:");
//            System.out.println(resBit);
            //每个tid
//          得到该模式所在的序列号
            resBit.and(bitOfItem[index]);
//            System.out.println("输出resBit2:");
//            System.out.println(resBit);
//            for (int j = 0; j < bitTidsOfItemset.length; j++) {
//                bitTidsOfItemset[j]=bitTidsOfItemset[j]&bitOfItem[index][j];
//            }
        }

        //resBit.stream().forEach(e -> System.out.println(e));
        for (int tid = resBit.nextSetBit(0); tid <= tidCount_column-1&&tid>=0; tid = resBit.nextSetBit(tid+1)) {
            // 输出tid
            //System.out.println(tid);
            for (int j = 0; j < itemset.length; j++) {
                int index=mapItemtoIndex.get(itemset[j]);
//                System.out.println("itemset[j]:"+itemset[j]);
//                System.out.println("index:"+index);
                Integer old= mapItemToUB.get(itemset[j]);
//                System.out.println("old:"+old);
//                mapItemToUB存储模式中各项的总的效用
                //正常情况下，uMapOfItemToTidU.get(index).get(tid)不会为空
                if (old!=null){
                    mapItemToUB.put(itemset[j],old+uMapOfItemToTidU.get(index).get(tid));
                }else {
                    mapItemToUB.put(itemset[j],uMapOfItemToTidU.get(index).get(tid));
//                    System.out.println(itemset[j]+":"+uMapOfItemToTidU.get(index).get(tid));
                }
            }
        }
        return res;
    }

    public void setGenerator(Map<Integer, String[]> mapTidToItems, Map<Integer, String[]> mapTidToUtilities) {
        for (Integer tid: mapTidToItems.keySet()) {
            setGeneratorOne(tid,mapTidToItems.get(tid),mapTidToUtilities.get(tid));
//            System.out.println("tid:"+tid+" items:"+mapTidToItems.get(tid));
        }
    }

}