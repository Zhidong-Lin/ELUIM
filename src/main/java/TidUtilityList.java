 public class TidUtilityList {
    int[] tids;   // 该 item 出现在哪些 tid 中
    int[] utils;  // 对应的 utility 值
     public TidUtilityList(int[] tids, int[] utils) {
        this.tids = tids;
        this.utils = utils;
    }
}