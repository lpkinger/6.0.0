package com.uas.erp.core;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class TreeParser {
	private List<Node> nodes;

	public TreeParser() {
		super();
		nodes = new ArrayList<Node>();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Node> getNodes(int depth) {
		List<Node> list = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getDepth() == depth)
				list.add(n);
		}
		return list;
	}

	public Node getNodeByCode(String code) {
		if (code != null) {
			for (Node n : nodes) {
				if (code.equals(n.getCode())) {
					return n;
				}
			}
		}
		return null;
	}

	public int getSize() {
		return this.nodes.size();
	}

	/**
	 * @return 根节点
	 */
	public List<Node> getTreeRoot() {
		return this.getNodes(1);
	}

	private boolean isExist(Node node) {
		String key = node.getCode();
		for (Node s : nodes) {
			if (key.equals(s.getCode())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param str
	 * <br>
	 *            形如a,b;a,c;a,d;a,e;b,f;b,g,b,h;f,i;f,j;c,f;c,k;
	 * @return
	 */
	public List<Node> parseTree(String str) {
		nodes = new ArrayList<Node>();
		String[] strs = str.split(";");
		for (String s : strs) {
			if (s != null) {
				String[] ns = s.split(",");
				Node preNode = null;
				Node node = null;
				for (int i = 0, len = ns.length; i < len; i++) {
					node = new Node();
					node.setCode(ns[i]);
					if (!isExist(node)) {
						nodes.add(node);
					} else {
						node = getNodeByCode(node.getCode());
					}
					if (preNode != null) {
						preNode.addChild(node);
						preNode.setLeaf(Node.FALSE);
						node.setDepth(preNode.getDepth() + 1);
					} else {
						if(node.getDepth() == 0)
							node.setDepth(1);
					}
					preNode = node;
				}
			}
		}
		return this.nodes;
	}
	
	public class Node {

		private int leaf = TRUE;
		public final static int TRUE = 1;
		public final static int FALSE = 0;
		private String code;
		private int depth = 0;
		private List<Node> children;
		private JSONObject data;
		private int id;
		private int pid = 0;
		private String number;

		public int getDepth() {
			return depth;
		}

		public void setDepth(int depth) {
			this.depth = depth;
		}

		public boolean isLeaf() {
			return this.leaf == TRUE;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}

		public int getLeaf() {
			return leaf;
		}

		public void setLeaf(int leaf) {
			this.leaf = leaf;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public int getPid() {
			return pid;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		public JSONObject getData() {
			return data;
		}

		public void setData(JSONObject data) {
			this.data = data;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public List<Node> getChildren() {
			return this.children;
		}

		public void addChild(Node node) {
			List<Node> nodes = getChildren();
			if (nodes == null)
				nodes = new ArrayList<Node>();
			String key = node.getCode();
			boolean isExist = false;
			for (Node n : nodes) {
				if (key.equals(n.getCode()))
					isExist = true;
			}
			if (!isExist) {
				nodes.add(node);
				setChildren(nodes);
			}
		}
		
		private String toString(String str) {
			if (str == null) {
				str = "";
			} else {
				str = str + ",";
			}
			if(this.isLeaf()) {
				return str + code;
			}
			List<Node> nodes = getChildren();
			StringBuffer sb = new StringBuffer();
			for(Node n:nodes) {
				if(sb.length() > 0)
					sb.append(";");
				sb.append(n.toString(str + code));
			}
			return sb.toString();
		}
		
		@Override
		public String toString() {
			return this.toString(null);
		}
	}
}