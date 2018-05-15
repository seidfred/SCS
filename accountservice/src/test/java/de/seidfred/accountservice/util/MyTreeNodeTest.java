package de.seidfred.accountservice.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.junit.Test;

import antlr.TreeParserSharedInputState;

public class MyTreeNodeTest {

	@Test
	public void test() {
		MyTreeNode<String> root = new MyTreeNode<>("Root");

		MyTreeNode<String> child1 = new MyTreeNode<>("Child1");
		child1.addChild("Grandchild1");
		child1.addChild("Grandchild2");

		MyTreeNode<String> child2 = new MyTreeNode<>("Child2");
		child2.addChild("Grandchild3");

		root.addChild(child1);
		root.addChild(child2);
		root.addChild("Child3");

		root.addChildren(Arrays.asList(
		        new MyTreeNode<>("Child4"),
		        new MyTreeNode<>("Child5"),
		        new MyTreeNode<>("Child6")
		));

		for(MyTreeNode node : root.getChildren()) {
		    System.out.println(node.getData());
		}
	}

	@Test
	public void testTreeSetMap(){
		List<String> list1 = Arrays.asList("ACC","ID","abc-100");
		List<String> list2 = Arrays.asList("ACC","STS","INF","info");
		List<String> list3 = Arrays.asList("ACC","STS","STSE","accept");
		
		TreeSet<String> testTree = new TreeSet<String>();
		
		testTree.addAll(list1);
		testTree.addAll(list2);
		testTree.addAll(list3);
		
		Iterator<String> iterator = testTree.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
			
		}
		
		
	}
}
