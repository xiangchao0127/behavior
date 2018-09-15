package com.handge.bigdata;

import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeRangeSet;
import com.handge.bigdata.tree.TreeNode;
import com.handge.bigdata.tree.multinode.ArrayMultiTreeNode;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.baidu.com/s?word=%E5%B0%8F%E7%B1%B3%E9%9B%86%E5%9B%A2%E5%87%BA%E7%8E%B0%E6%8A%BD%E9%A3%9E&sa=re_dl_seError_1&tn=SE_fengyunbangS_fs9rizg7&rsv_dl=fyb_n_erro";

        System.out.println(url.contains("baidu"));

        System.out.println(url.isEmpty());
        List<NameValuePair> kvs = null;
        try {
            kvs = URLEncodedUtils.parse(new URI(url), "UTF-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        for (NameValuePair kv: kvs){
            System.out.println(kv.getName()  + "  == "+ kv.getValue());
        }

        TreeNode<String> root = new ArrayMultiTreeNode<>("root");
        // Creating the tree nodes
        TreeNode<String> n1 = new ArrayMultiTreeNode<>("n1_a");
        TreeNode<String> n2 = new ArrayMultiTreeNode<>("n1_b");
        TreeNode<String> n3 = new ArrayMultiTreeNode<>("n2_a");
        TreeNode<String> n4 = new ArrayMultiTreeNode<>("n2_b");
        TreeNode<String> n5 = new ArrayMultiTreeNode<>("n3_a");
        TreeNode<String> n6 = new ArrayMultiTreeNode<>("n4_b");
        TreeNode<String> n7 = new ArrayMultiTreeNode<>("n4_a");

        root.add(n1);
        root.add(n2);
        n2.add(n3);
        n1.add(n4);
        n3.add(n5);
        n5.add(n6);
        n5.add(n7);




        System.out.println(root);
        System.out.println(root.find("n4_b").parent().parent().parent().parent());

//
//        XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject("test").startObject("properties");
//        builder.startObject("appName").field("type", "string").endObject();
//        builder.startObject("appProtocol").field("type", "string").endObject();
//        builder.startObject("appType").field("type", "string").endObject();
//        builder.startObject("appTags").field("type", "string").endObject();
//        builder.startObject("desIp").field("type", "string").endObject();
//        builder.startObject("info").field("type", "string").endObject();
//        builder.startObject("device").field("type", "string").endObject();
//        builder.startObject("received").field("type", 10).endObject();
//        builder.startObject("send").field("type", 10).endObject();
//        builder.startObject("startTime").field("type", 12).endObject();
//        builder.startObject("endTime").field("type", 12).endObject();
//        builder.startObject("url").field("type", "string").endObject();
//        builder.startObject("method").field("type", "string").endObject();
//        builder.startObject("clentType").field("type", "string").endObject();
//        builder.endObject().endObject().endObject();
//
//        System.out.println(builder.string());
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }
}
