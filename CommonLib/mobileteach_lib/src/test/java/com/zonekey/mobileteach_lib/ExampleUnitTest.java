package com.zonekey.mobileteach_lib;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testSpilt() {
        String s = "111111";
        String[] split = s.split(",");
        assertEquals(1, split.length);
    }

    @Test
    public void testSort() {        //测试模拟UDP消息的排序...
        ArrayList<String> lists = new ArrayList<>();
        lists.add("0");
        lists.add("1");
        lists.add("2");
        lists.add("6");
        lists.add("7");
        lists.add("8");

        int receiveIndex = 4;   //远程收到的index
        String start = "3";
        String ctrl = "4";
        String end = "5";
        try {
            sortPoint(receiveIndex, lists, start, ctrl, end);
        } catch (Exception e) {
            assertEquals("1", "2");
        }
        int receiveIndex2 = 10;
        String start2 = "9";
        String ctrl2 = "10";
        String end2 = "11";
        try {
            sortPoint(receiveIndex2, lists, start2, ctrl2, end2);
        } catch (Exception e) {
            assertEquals("1", "2");
        }
        assertEquals(lists.size(), 12);
        for (int i = 0; i < lists.size(); i++) {
            assertEquals(lists.get(i), i + "");
        }
    }

    private void sortPoint(int receiveIndex, ArrayList<String> lists, String start, String ctrl, String end) throws Exception {
        if (lists.size() < receiveIndex) {   //集合长度 < 收到点的index,正常在后面追加
            lists.add(start);
            lists.add(ctrl);
            lists.add(end);
        } else if (lists.size() > receiveIndex) {    //集合长度 > 收到点的index,插入到index位置去
            lists.add(receiveIndex - 1, end);
            lists.add(receiveIndex - 1, ctrl);
            lists.add(receiveIndex - 1, start);
        } else {        //正常情况下不存在
            throw new Exception("the index == receiveIndex receiveIndex = " + receiveIndex);
        }
    }

    @Test
    public void testByteBuffer(){
        byte b = 0x00;
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.put(b);
        bb.put(b);
        bb.put(b);
        bb.put(b);
        bb.put(b);
        bb.put(b);

        byte[] tempB1 = bb.array();
        bb.put(b);
        byte[] tempB2 = bb.array();
        int i = 0;
    }

    @Test
    public void testStringLength(){
        String s = "一二三";
        int l1 = s.length();
        int l2 = 0;
        try {
            l2 = s.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        assertEquals(l1,l2);
    }
}