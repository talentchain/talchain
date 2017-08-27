package org.talchain.net.dht;

import java.util.List;

public class DHTUtils {

    public static void printAllLeafs(Bucket root){
        Bucket.SaveLeaf saveLeaf = new Bucket.SaveLeaf();
        root.traverseTree(saveLeaf);

        for (Bucket bucket : saveLeaf.getLeafs())
            System.out.println(bucket);
    }

    public static List<Bucket> getAllLeafs(Bucket root){
        Bucket.SaveLeaf saveLeaf = new Bucket.SaveLeaf();
        root.traverseTree(saveLeaf);

        return saveLeaf.getLeafs();
    }
}
