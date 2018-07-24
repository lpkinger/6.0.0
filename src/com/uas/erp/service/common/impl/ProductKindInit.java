package com.uas.erp.service.common.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.InitData;
import com.uas.erp.service.common.AbstractInit;

/**
 * 导入数据--><br>
 * <font color=red>物料编码规则</font>
 * <hr>
 * 
 * @author yingp
 * 
 */
public class ProductKindInit extends AbstractInit {

	private Map<String, JSONObject> stackDatas;

	private List<String> sqls;

	public ProductKindInit(List<InitData> datas) {
		super(datas);
		stackDatas = new HashMap<String, JSONObject>();
		sqls = new ArrayList<String>();
	}

	@Override
	public synchronized void toFormal() {
		String treeStr = getStackString();
		if (treeStr.length() > 0) {
			TreeParser parser = new TreeParser();
			parser.parseTree(treeStr);
			setConfig(parser.getSize(), "ProductKind", "pk_id");
			List<TreeParser.Node> nodes = parser.getTreeRoot();
			for (TreeParser.Node node : nodes) {
				setDefaults(node, null);
			}
			String errors = getDB()
					.executeWithCheck(sqls, null,
							"select wm_concat(pk_name) from (select distinct pk_name from productkind group by pk_subof,pk_name having count(1)>1)");
			if(errors != null && errors != "null")					
				BaseUtil.showError("种类名称出现重复: " + errors);
		}
	}

	private String getStackString() {
		JSONObject data = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer su = null;
		Object code = null;
		for (InitData d : datas) {
			data = JSONObject.fromObject(d.getId_data());
			code = data.get("pk_code1");
			if (code != null && !"".equals(code.toString().trim())) {
				su = new StringBuffer();
				su.append(code);
				stackDatas.put(su.toString(), data);
				code = data.get("pk_code2");
				if (code != null && !"".equals(code.toString().trim())) {
					su.append(",");
					su.append(code);
					stackDatas.put(su.toString(), data);
					code = data.get("pk_code3");
					if (code != null && !"".equals(code.toString().trim())) {
						su.append(",");
						su.append(code);
						stackDatas.put(su.toString(), data);
						code = data.get("pk_code4");
						if (code != null && !"".equals(code.toString().trim())) {
							su.append(",");
							su.append(code);
							stackDatas.put(su.toString(), data);
						}
					}
				}
				sb.append(su).append(";");
			}
		}
		return sb.toString();
	}

	private void setDefaults(TreeParser.Node node, Integer id) {
		if (id != null) {
			node.setPid(id);
		}
		id = getSeq();
		node.setId(id);
		node.setNumber(getCode());
		JSONObject d = stackDatas.get(node.getKey(null));
		if (d != null) {
			node.setData(d);
		}
		this.sqls.add(getSql(node));
		if (!node.isLeaf()) {
			List<TreeParser.Node> children = node.getChildren();
			for (TreeParser.Node n : children) {
				setDefaults(n, id);
			}
		}
	}

	private static final String SQL_STR = "INSERT INTO ProductKind (pk_id, pk_level, pk_leaf, pk_subof, pk_number,pk_name,pk_code) VALUES (";

	private static final String SQL_STR_LEAF = "INSERT INTO ProductKind (pk_id, pk_level, pk_leaf, pk_subof, pk_number,pk_name,pk_code,pk_length,pk_nameeg,pk_namerule,pk_speceg,pk_specrule,pk_parametereg,pk_parameterrule,pk_manutype,pk_dhzc,pk_supplytype,pk_whcode,pk_whname,pk_stockcatecode,pk_stockcate,pk_acceptmethod,pk_wccode,pk_wcname,pk_material,pk_priority,pk_testlossrate,pk_lossrate,pk_exportlossrate,pk_ifzeroqty,pk_purclossrate,pk_aql,pr_plzl,pk_period,pk_ltinstock,pk_leadtime,pk_validdays,pk_purcmergedays,pk_purchasedays,pk_ltwarndays,pk_prname,pk_effective,pk_location,pk_qualmethod,pk_incomecate,pk_costcatecode,pk_costcate,pk_incomecatecode,pk_serial,pk_cop,pk_maxnum,pk_cggdycode,pk_cggdy,pk_buyername,pk_buyercode,pk_self) VALUES (";

	private String getSql(TreeParser.Node node) {
		StringBuffer sb = new StringBuffer();
		if (node.isLeaf()) {
			JSONObject d = node.getData();
			sb.append(SQL_STR_LEAF).append(node.getId()).append(",").append(node.getDepth()).append(",'T',")
					.append(node.getPid()).append(",'").append(node.getNumber()).append("','")
					.append(d.get("pk_name" + node.getDepth())).append("','").append(node.getCode()).append("'");
			if (d != null) {
				Object obj = d.get("pk_length");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",3");
				} else {
					sb.append(",").append(obj);
				}
				obj = d.get("pk_nameeg");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_namerule");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_speceg");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_specrule");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_parametereg");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_parameterrule");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_manutype");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					//sb.append(",'").append(obj).append("'");
					sb.append(",'").append(String.valueOf(obj).replace("制造","MAKE").replace("委外","OSMAKE").replace("外购","PURCHASE").replace("客供","CUSTOFFER")).append("'");
				}
				obj = d.get("pk_dhzc");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_supplytype");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					//sb.append(",'").append(obj).append("'");
					sb.append(",'").append(String.valueOf(obj).replace("推式","PUSH").replace("拉式","PULL").replace("虚拟件","VIRTUAL")).append("'");					
				}
				obj = d.get("pk_whcode");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_whname");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_stockcatecode");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_stockcate");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_acceptmethod");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append("检验".equals(obj)?0:1).append("'");
				}
				obj = d.get("pk_wccode");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_wcname");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_material");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_priority");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_testlossrate");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_lossrate");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_exportlossrate");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_ifzeroqty");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append("是".equals(obj) ? -1 : 0).append("'");
				}
				obj = d.get("pk_purclossrate");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_aql");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pr_plzl");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_period");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_ltinstock");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",0");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_leadtime");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",0");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_validdays");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",0");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_purcmergedays");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",0");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_purchasedays");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",0");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_ltwarndays");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",0");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_prname");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}
				obj = d.get("pk_effective");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",'有效'");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_location");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_qualmethod");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_incomecate");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_costcatecode");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_costcate");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_incomecatecode");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_serial");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_cop");
				if (obj == null || obj.toString().trim().length() == 0) {
						sb.append(",null");
				} else {
						sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_maxnum");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",").append(obj);
				}obj = d.get("pk_cggdycode");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_cggdy");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_buyername");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_buyercode");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",null");
				} else {
					sb.append(",'").append(obj).append("'");
				}obj = d.get("pk_self");
				if (obj == null || obj.toString().trim().length() == 0) {
					sb.append(",0");
				} else {
					sb.append(",'").append("是".equals(obj) ? -1 : 0).append("'");
				}
			}
			sb.append(")");
		} else {
			JSONObject d = node.getData();
			sb.append(SQL_STR).append(node.getId()).append(",").append(node.getDepth()).append(",'F',")
					.append(node.getPid()).append(",'").append(node.getNumber()).append("','")
					.append(d.get("pk_name" + node.getDepth())).append("','").append(node.getCode()).append("')");
		}
		return sb.toString();
	}
}

class TreeParser {
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
		String key = node.getKey(null);
		for (Node s : nodes) {
			if (key.equals(s.getKey(null))) {				
				return true;
			}
		}
		return false;
	}

	/**
	 * @param str
	 * <br>
	 *            形如a,b,c;a,b,d;a,e,f;aa,bb,cc;aa,bb,dd
	 * @return
	 */
	public List<Node> parseTree(String str) {
		nodes = new ArrayList<Node>();
		String[] strs = str.split(";");
		for (String s : strs) {
			if (s != null) {
				String[] ns = s.split(",");
				Node node1 = null;
				Node node2 = null;
				for (int i = 0, len = ns.length; i < len; i++) {
					node2 = new Node();
					node2.setDepth(i + 1);
					node2.setCode(ns[i]);
					if (i < ns.length - 1)
						node2.setLeaf(Node.FALSE);
					if (i > 0)
						node2.setParent(node1);
					if (!isExist(node2)) {
						System.out.println("key:"+s);
						nodes.add(node2);
					}
					node1 = node2;
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
		private Node parent;
		private int depth = 1;
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

		public Node getParent() {
			return parent;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public int getLeaf() {
			return leaf;
		}

		public void setLeaf(int leaf) {
			this.leaf = leaf;
		}

		public String getKey(String str) {
			if (str == null) {
				str = "";
			} else {
				str = "," + str;
			}
			Node p = getParent();
			if (p == null) {
				return this.code + str;
			}
			return p.getKey(this.code + str);
		}

		public Node getRoot() {
			Node p = this.getParent();
			if (p == null)
				return this;
			return p.getRoot();
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
			List<Node> ns = getNodes(this.getDepth() + 1);
			List<Node> list = new ArrayList<Node>();
			String key = getKey(null);
			for (Node n : ns) {
				if (key.equals(n.getParent().getKey(null)))
					list.add(n);
			}
			return list;
		}
	}
}
