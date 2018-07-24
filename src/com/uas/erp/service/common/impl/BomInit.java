package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;
import com.uas.erp.model.InitData;
import com.uas.erp.service.common.AbstractInit;

/**
 * 导入数据--><br>
 * <font color=red>BOM资料</font>
 * <hr>
 * 
 * @author yingp
 * 
 */
public class BomInit extends AbstractInit {

	private Map<String, JSONObject> stackDatas;

	private Employee employee;

	private List<String> sqls;

	public BomInit(List<InitData> datas, Employee employee) {
		super(datas);
		stackDatas = new HashMap<String, JSONObject>();
		this.employee = employee;
		sqls = new ArrayList<String>();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, timeout = 20)
	public synchronized void toFormal() {
		String treeStr = getStackString();
		if (treeStr.length() > 0) {
			BomTreeParser parser = new BomTreeParser();
			parser.parseTree(treeStr);
			List<BomTreeParser.Node> nodes = parser.getParentNodes();
			setConfig(nodes.size(), "BOM", "bo_id");
			List<Integer> ids = new ArrayList<Integer>();
			for (BomTreeParser.Node node : nodes) {
				node.setId(getSeq());
				node.setSid(node.getId());
				node.setData(stackDatas.get(node.getCode()));
				sqls.add(getSql(node, null));
				ids.add(node.getId());
			}
			setConfig(parser.getChildNodes().size(), "BOMDetail", "bd_id");
			for (BomTreeParser.Node node : nodes) {
				List<BomTreeParser.Node> children = node.getChildren();
				int detno = 1;
				for (BomTreeParser.Node n : children) {
					n.setId(getSeq());
					n.setPid(node.getSid());
					n.setData(stackDatas.get(node.getCode() + "," + n.getCode()));
					sqls.add(getSql(n, detno++));
					// 替代料
					Object replace = n.getData().get("bd_replace");
					if (replace != null && replace.toString().trim().length() > 0) {
						String[] repCodes = replace.toString().split(",");
						int j = 1;
						for (String s : repCodes) {
							sqls.add(insertReplace(n, s, j++));
						}
					}
					/*
					 * // 位号 Object location = n.getData().get("bd_location"); if (location != null && location.toString().trim().length() > 0) { String[] locCodes = location.toString().replace("","").split(","); int j = 1; for (String s : locCodes) { if (s != null && s.trim().length() > 0) { sqls.add(insertLocation(n, s, j++, detno)); } } }
					 */
				}
			}
			getDB().execute(sqls);
			getDB().execute("update bomdetail set bd_ifrep=-1 where bd_id in (select pre_bdid from prodreplace )");
			getDB().execute(
					"update bomdetail set bd_sonbomid=(select max(bo_id) from bom where bo_mothercode=bd_soncode) where bd_sonbomid=0 and bd_soncode in (select bo_mothercode from bom)");
			getDB().callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "BOM", "bo_id" });
			getDB().callProcedure("SYS_CHECK_SEQUENCE", new Object[] { "BOMDetail", "bd_id" });
		}
	}

	private String getStackString() {
		JSONObject data = null;
		StringBuffer sb = new StringBuffer();
		Object code = null;
		for (InitData d : datas) {
			data = JSONObject.fromObject(d.getId_data());
			code = data.get("bo_mothercode");
			if (code != null && !"".equals(code.toString().trim())) {
				sb.append(code);
				stackDatas.put(code.toString(), data);
				code = data.get("bd_soncode");
				if (code != null && !"".equals(code.toString().trim())) {
					sb.append(",");
					sb.append(code);
					stackDatas.put(data.get("bo_mothercode") + "," + code, data);
				}
				sb.append(";");
			}
		}
		return sb.toString();
	}

	private static final String SQL_BOM = "INSERT INTO BOM (bo_id,bo_mothercode,bo_statuscode,bo_status,bo_ispast,bo_validstatuscode,bo_validstatus,bo_updateman,bo_recorder) VALUES (";

	private static final String SQL_DETAIL = "INSERT INTO BOMDetail (bd_id,bd_bomid,bd_soncode,bd_detno,bd_baseqty,bd_mothercode,bd_remark,bd_location,bd_ifrep,bd_repcode) VALUES (";

	private String getSql(BomTreeParser.Node node, Integer detno) {
		StringBuffer sb = new StringBuffer();
		if (detno == null) {
			JSONObject d = node.getData();
			String em_name = "";
			if (employee != null)
				em_name = employee.getEm_name();
			sb.append(SQL_BOM).append(node.getId()).append(",'").append(node.getCode()).append("','ENTERING','在录入',")
					.append("是".equals(d.get("bo_ispast")) ? -1 : 0).append(",'UNVALID','无效','").append(em_name).append("','")
					.append(em_name).append("')");
		} else {
			JSONObject d = node.getData();
			int ifrep = (d.get("bd_replace") == null || d.get("bd_replace").toString().trim().length() == 0) ? 0 : -1;
			Object bd_location = d.get("bd_location");
			if (bd_location != null) {
				bd_location = bd_location.toString().replace("，", ",");// 替换位号的全角逗号为半角
			}
			sb.append(SQL_DETAIL).append(node.getId()).append(",").append(node.getPid()).append(",'").append(node.getCode()).append("',")
					.append(detno).append(",").append(d.get("bd_baseqty")).append(",'").append(d.get("bo_mothercode")).append("','")
					.append(d.get("bd_remark")).append("','").append(bd_location).append("',").append(ifrep).append(",'")
					.append(d.get("bd_replace")).append("')");
		}
		return sb.toString();
	}

	private static final String SQL_REPLACE = "INSERT INTO ProdReplace(pre_id, pre_detno, pre_soncode, pre_repcode,pre_bdid, pre_bomid) VALUES(";

	private String insertReplace(BomTreeParser.Node node, String replaceCode, int detno) {
		StringBuffer sb = new StringBuffer();
		sb.append(SQL_REPLACE).append("PRODREPLACE_SEQ.nextval,").append(detno).append(",'").append(node.getCode()).append("','")
				.append(replaceCode).append("',").append(node.getId()).append(",").append(node.getSid()).append(")");
		return sb.toString();
	}
}

class BomTreeParser {
	private List<Node> nodes;

	private Map<String, Node> nodeMaps;

	public BomTreeParser() {
		super();
		nodes = new ArrayList<Node>();
		nodeMaps = new HashMap<String, Node>();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @param depth
	 *            深度、权值、层级
	 * @return
	 */
	public List<Node> getNodes(int depth) {
		List<Node> list = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getDepth() == depth)
				list.add(n);
		}
		return list;
	}

	/**
	 * @return 非叶节点
	 */
	public List<Node> getParentNodes() {
		List<Node> list = new ArrayList<Node>();
		for (Node n : nodes) {
			if (!n.isLeaf())
				list.add(n);
		}
		return list;
	}

	/**
	 * @return 非根节点
	 */
	public List<Node> getChildNodes() {
		List<Node> list = new ArrayList<Node>();
		for (Node n : nodes) {
			if (n.getDepth() > 1)
				list.add(n);
		}
		return list;
	}

	/**
	 * @param code
	 * @return
	 */
	public Node getNodeByCode(String code) {
		if (code != null) {
			return nodeMaps.get(code);
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

	/**
	 * 节点是否存在于数结构中
	 * 
	 * @param node
	 * @return
	 */
	private boolean isExist(Node node) {
		String key = node.getCode();
		return nodeMaps.get(key) != null;
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
						nodeMaps.put(ns[i], node);
					} else {
						node = getNodeByCode(node.getCode());
					}
					if (preNode != null) {
						preNode.addChild(node);
						preNode.setLeaf(Node.FALSE);
						node.setDepth(preNode.getDepth() + 1);
					} else {
						if (node.getDepth() == 0)
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
		private int sid;

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

		public int getSid() {
			return sid;
		}

		public void setSid(int sid) {
			this.sid = sid;
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
			if (this.isLeaf()) {
				return str + code;
			}
			List<Node> nodes = getChildren();
			StringBuffer sb = new StringBuffer();
			for (Node n : nodes) {
				if (sb.length() > 0)
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